-- ============================================================
-- Scenario tests for the audit-history / integrity triggers
-- added across V2, V5, V8, V11.
--
-- NOT part of the Maven test suite: ./mvnw test runs against H2
-- with Flyway disabled (see src/test/resources/application.yaml),
-- and H2 cannot execute PL/pgSQL (DO $$ ... $$, LANGUAGE plpgsql
-- trigger functions). This script only runs manually against a
-- real Postgres database that already has these migrations
-- applied (e.g. the local docker-compose Postgres).
--
-- Each block: performs a real action, then asserts the expected
-- outcome with RAISE EXCEPTION on failure. A silent run with only
-- "NOTICE" lines printed means everything passed.
--
-- Cleans up its own test rows on the happy path, but the safest
-- way to leave zero footprint regardless of outcome is to wrap
-- the whole file in a transaction you roll back, e.g. in psql:
--
--   BEGIN;
--   \i sql-scripts/trigger_scenario_tests.sql
--   ROLLBACK;
--
-- or in a JDBC-based console (IntelliJ, DataGrip, etc.), just
-- start a transaction, run this whole file, then roll back instead
-- of committing.
-- ============================================================

-- ============================================================
-- SETUP: a throwaway top-level category and child to test against
-- ============================================================

DO $$
DECLARE
    sys_id     bigint;
    parent_id  bigint;
    child_id   bigint;
    other_id   bigint;
    history_count int;
BEGIN
    SELECT id INTO sys_id FROM users WHERE username = 'system';

    -- ============================================================
    -- TEST 1: INSERT into service_categories writes an audit row
    -- ============================================================
    INSERT INTO service_categories (parent_id, code, name, description, sort_order, created_by, updated_by)
    VALUES (null, 'TEST_PARENT', 'Test Parent', 'scenario test row', 100, sys_id, sys_id)
    RETURNING id INTO parent_id;

    SELECT count(*) INTO history_count
    FROM service_category_history
    WHERE service_category_id = parent_id AND operation = 'INSERT';

    IF history_count <> 1 THEN
        RAISE EXCEPTION 'TEST 1 FAILED: expected exactly 1 INSERT history row for category %, found %', parent_id, history_count;
    END IF;
    RAISE NOTICE 'TEST 1 PASSED: INSERT wrote an audit history row';

    -- ============================================================
    -- TEST 2: UPDATE writes an audit row reflecting the new value
    -- ============================================================
    UPDATE service_categories SET name = 'Test Parent Renamed', updated_by = sys_id WHERE id = parent_id;

    IF NOT EXISTS (
        SELECT 1 FROM service_category_history
        WHERE service_category_id = parent_id AND operation = 'UPDATE' AND name = 'Test Parent Renamed'
    ) THEN
        RAISE EXCEPTION 'TEST 2 FAILED: expected an UPDATE history row with the new name for category %', parent_id;
    END IF;
    RAISE NOTICE 'TEST 2 PASSED: UPDATE wrote an audit history row with the new value';

    -- ============================================================
    -- TEST 3: history rows cannot be edited or deleted
    -- ============================================================
    BEGIN
        UPDATE service_category_history SET name = 'tampered' WHERE service_category_id = parent_id;
        RAISE EXCEPTION 'TEST 3 FAILED: was able to UPDATE a service_category_history row, immutability trigger did not fire';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM LIKE '%append-only%' THEN
            RAISE NOTICE 'TEST 3a PASSED: UPDATE on history row correctly blocked (%))', SQLERRM;
        ELSE
            RAISE EXCEPTION 'TEST 3a FAILED with unexpected error: %', SQLERRM;
        END IF;
    END;

    BEGIN
        DELETE FROM service_category_history WHERE service_category_id = parent_id;
        RAISE EXCEPTION 'TEST 3 FAILED: was able to DELETE service_category_history rows, immutability trigger did not fire';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM LIKE '%append-only%' THEN
            RAISE NOTICE 'TEST 3b PASSED: DELETE on history rows correctly blocked (%)', SQLERRM;
        ELSE
            RAISE EXCEPTION 'TEST 3b FAILED with unexpected error: %', SQLERRM;
        END IF;
    END;

    -- ============================================================
    -- TEST 4: a category cannot be its own parent
    -- ============================================================
    BEGIN
        UPDATE service_categories SET parent_id = id WHERE id = parent_id;
        RAISE EXCEPTION 'TEST 4 FAILED: self-parenting was allowed';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM LIKE '%cannot reference itself%' THEN
            RAISE NOTICE 'TEST 4 PASSED: self-parenting correctly blocked (%)', SQLERRM;
        ELSE
            RAISE EXCEPTION 'TEST 4 FAILED with unexpected error: %', SQLERRM;
        END IF;
    END;

    -- ============================================================
    -- TEST 5: a cycle (child becomes parent's parent) is blocked
    -- ============================================================
    INSERT INTO service_categories (parent_id, code, name, description, sort_order, created_by, updated_by)
    VALUES (parent_id, 'TEST_CHILD', 'Test Child', 'scenario test row', 101, sys_id, sys_id)
    RETURNING id INTO child_id;

    BEGIN
        UPDATE service_categories SET parent_id = child_id WHERE id = parent_id;
        RAISE EXCEPTION 'TEST 5 FAILED: a two-node cycle was allowed';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM LIKE '%would create a cycle%' THEN
            RAISE NOTICE 'TEST 5 PASSED: cycle correctly blocked (%)', SQLERRM;
        ELSE
            RAISE EXCEPTION 'TEST 5 FAILED with unexpected error: %', SQLERRM;
        END IF;
    END;

    -- ============================================================
    -- TEST 6: a category cannot be parented under a soft-deleted one
    -- ============================================================
    INSERT INTO service_categories (parent_id, code, name, description, sort_order, is_deleted, created_by, updated_by)
    VALUES (null, 'TEST_DELETED_PARENT', 'Test Deleted Parent', 'scenario test row', 102, true, sys_id, sys_id)
    RETURNING id INTO other_id;

    BEGIN
        UPDATE service_categories SET parent_id = other_id WHERE id = child_id;
        RAISE EXCEPTION 'TEST 6 FAILED: parenting under a soft-deleted category was allowed';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM LIKE '%soft-deleted and cannot be used as a parent%' THEN
            RAISE NOTICE 'TEST 6 PASSED: soft-deleted parent correctly blocked (%)', SQLERRM;
        ELSE
            RAISE EXCEPTION 'TEST 6 FAILED with unexpected error: %', SQLERRM;
        END IF;
    END;

    -- ============================================================
    -- TEST 7: a category cannot be parented under an ARCHIVED one
    -- ============================================================
    UPDATE service_categories SET is_deleted = false, status = 'ARCHIVED' WHERE id = other_id;

    BEGIN
        UPDATE service_categories SET parent_id = other_id WHERE id = child_id;
        RAISE EXCEPTION 'TEST 7 FAILED: parenting under an archived category was allowed';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM LIKE '%archived and cannot be used as a parent%' THEN
            RAISE NOTICE 'TEST 7 PASSED: archived parent correctly blocked (%)', SQLERRM;
        ELSE
            RAISE EXCEPTION 'TEST 7 FAILED with unexpected error: %', SQLERRM;
        END IF;
    END;

    -- ============================================================
    -- TEST 8: hard DELETE now succeeds and is logged (post-FK-fix)
    -- ============================================================
    DELETE FROM service_categories WHERE id = child_id;

    IF NOT EXISTS (
        SELECT 1 FROM service_category_history WHERE service_category_id = child_id AND operation = 'DELETE'
    ) THEN
        RAISE EXCEPTION 'TEST 8 FAILED: hard DELETE did not write a DELETE history row (FK fix regression?)';
    END IF;
    RAISE NOTICE 'TEST 8 PASSED: hard DELETE succeeded and was logged, even though the parent row is now gone';

    -- cleanup remaining test rows
    DELETE FROM service_categories WHERE id IN (parent_id, other_id);

    RAISE NOTICE '=== service_categories: ALL TESTS PASSED ===';
END $$;


-- ============================================================
-- TEST 9: locales — 'en' cannot be deactivated or soft-deleted
-- ============================================================
DO $$
BEGIN
    BEGIN
        UPDATE locales SET is_active = false WHERE code = 'en';
        RAISE EXCEPTION 'TEST 9a FAILED: deactivating the en locale was allowed';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM LIKE '%fallback locale%' THEN
            RAISE NOTICE 'TEST 9a PASSED: deactivating en correctly blocked (%)', SQLERRM;
        ELSE
            RAISE EXCEPTION 'TEST 9a FAILED with unexpected error: %', SQLERRM;
        END IF;
    END;

    BEGIN
        UPDATE locales SET is_deleted = true WHERE code = 'en';
        RAISE EXCEPTION 'TEST 9b FAILED: soft-deleting the en locale was allowed';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM LIKE '%fallback locale%' THEN
            RAISE NOTICE 'TEST 9b PASSED: soft-deleting en correctly blocked (%)', SQLERRM;
        ELSE
            RAISE EXCEPTION 'TEST 9b FAILED with unexpected error: %', SQLERRM;
        END IF;
    END;

    BEGIN
        DELETE FROM locales WHERE code = 'en';
        RAISE EXCEPTION 'TEST 9c FAILED: hard-deleting the en locale was allowed';
    EXCEPTION WHEN OTHERS THEN
        IF SQLERRM LIKE '%fallback locale%' THEN
            RAISE NOTICE 'TEST 9c PASSED: hard-deleting en correctly blocked (%)', SQLERRM;
        ELSE
            RAISE EXCEPTION 'TEST 9c FAILED with unexpected error: %', SQLERRM;
        END IF;
    END;

    RAISE NOTICE '=== locales: ALL TESTS PASSED ===';
END $$;


-- ============================================================
-- TEST 10: currencies — only one active default currency allowed
-- ============================================================
DO $$
DECLARE
    sys_id  bigint;
    bd_id   bigint;
    test_id bigint;
BEGIN
    SELECT id INTO sys_id FROM users WHERE username = 'system';
    SELECT id INTO bd_id FROM countries WHERE code = 'BD';

    INSERT INTO currencies (country_id, code, numeric_code, symbol, decimal_places, is_default, sort_order,
                             created_by, updated_by)
    VALUES (bd_id, 'TST', '999', 'T', 2, true, 999, sys_id, sys_id)
    RETURNING id INTO test_id;

    RAISE EXCEPTION 'TEST 10 FAILED: was able to insert a second is_default=true currency while BDT is already default';
EXCEPTION WHEN unique_violation THEN
    RAISE NOTICE 'TEST 10 PASSED: second default currency correctly blocked (%)', SQLERRM;
    -- test_id was never assigned since the INSERT itself failed; nothing to clean up
END $$;

-- ============================================================
-- If every block above printed only NOTICE lines (no unhandled
-- exception propagated out of this script), all scenarios passed.
-- ============================================================
