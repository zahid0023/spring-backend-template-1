package com.example.springbackendtemplate1.commons.model.entity;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Static utility class for managing bidirectional JPA relationships.
 *
 * <p>JPA does not automatically synchronise both sides of a bidirectional
 * association. If only the owning side is updated, the in-memory object graph
 * becomes inconsistent and can cause subtle bugs (e.g. stale first-level cache,
 * incorrect cascade behaviour). These helpers keep both sides in sync with a
 * single call.
 *
 * <p>All methods accept an <em>unbound</em> method reference of the form
 * {@code BiConsumer<C, P>}, where {@code C} is the child type and {@code P} is
 * the parent type. This allows the same reference (e.g.
 * {@code CityEntity::setCountry}) to be applied to any child instance in a loop,
 * which is the key reason {@link java.util.function.Consumer Consumer&lt;P&gt;}
 * would be insufficient for bulk operations.
 *
 * <p>Intended usage inside concrete entity methods:
 * <pre>{@code
 * import static com.example.springbackendtemplate1.commons.model.entity.EntityRelationshipHelper.*;
 *
 * public void addCity(CityEntity city) {
 *     addChild(this.cities, city, CityEntity::setCountry, this);
 * }
 *
 * public void removeCity(CityEntity city) {
 *     removeChild(this.cities, city, CityEntity::setCountry);
 * }
 *
 * public void replaceCities(Collection<CityEntity> newCities) {
 *     replaceChildren(this.cities, newCities, CityEntity::setCountry, this);
 * }
 *
 * public void clearCities() {
 *     clearChildren(this.cities, CityEntity::setCountry);
 * }
 * }</pre>
 *
 * <p>This class is not instantiable.
 */
public final class EntityRelationshipHelper {

    private EntityRelationshipHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Adds {@code child} to {@code children} and wires its back-reference to
     * {@code parent}, keeping both sides of a bidirectional association in sync.
     *
     * <p>The child is only added when {@link Collection#add} returns {@code true},
     * so the duplicate-rejection behaviour of {@link java.util.Set Set}
     * implementations is respected — the parent setter is never called twice for
     * the same child.
     *
     * @param <P>          the parent entity type
     * @param <C>          the child entity type
     * @param children     the parent-side collection that owns the relationship
     * @param child        the child instance to add; must not be {@code null}
     * @param parentSetter unbound method reference on the child that sets its
     *                     parent field (e.g. {@code CityEntity::setCountry})
     * @param parent       the parent instance to assign to {@code child}
     * @return {@code true} if {@code child} was added to the collection;
     *         {@code false} if the collection already contained it
     * @throws NullPointerException if {@code child} is {@code null}
     */
    public static <P, C> boolean addChild(
            Collection<C> children,
            C child,
            BiConsumer<C, P> parentSetter,
            P parent) {

        Objects.requireNonNull(child, "Child cannot be null.");

        if (!children.add(child)) {
            return false;
        }

        parentSetter.accept(child, parent);

        return true;
    }

    /**
     * Removes {@code child} from {@code children} and nulls out its
     * back-reference to the parent, keeping both sides of a bidirectional
     * association in sync.
     *
     * <p>If {@code child} is not present in {@code children}, this method is a
     * no-op — no exception is thrown and the setter is not called.
     *
     * @param <P>          the parent entity type
     * @param <C>          the child entity type
     * @param children     the parent-side collection that owns the relationship
     * @param child        the child instance to remove
     * @param parentSetter unbound method reference on the child that sets its
     *                     parent field (e.g. {@code CityEntity::setCountry});
     *                     called with {@code null} to clear the back-reference
     */
    public static <P, C> void removeChild(
            Collection<C> children,
            C child,
            BiConsumer<C, P> parentSetter) {

        if (children.remove(child)) {
            parentSetter.accept(child, null);
        }
    }

    /**
     * Replaces the entire {@code children} collection with {@code newChildren},
     * maintaining correct bidirectional wiring throughout.
     *
     * <p>The existing children have their parent references nulled before the
     * collection is cleared. Then every element of {@code newChildren} is added
     * and its parent reference is set to {@code parent}.
     *
     * <p>Passing {@code null} for {@code newChildren} is equivalent to calling
     * {@link #clearChildren} — all existing children are unwired and the
     * collection is emptied.
     *
     * @param <P>          the parent entity type
     * @param <C>          the child entity type
     * @param children     the parent-side collection to replace
     * @param newChildren  the replacement collection; may be {@code null}
     * @param parentSetter unbound method reference on the child that sets its
     *                     parent field (e.g. {@code CityEntity::setCountry})
     * @param parent       the parent instance to assign to each new child
     */
    public static <P, C> void replaceChildren(
            Collection<C> children,
            Collection<C> newChildren,
            BiConsumer<C, P> parentSetter,
            P parent) {

        for (C child : children) {
            parentSetter.accept(child, null);
        }

        children.clear();

        if (newChildren == null) {
            return;
        }

        for (C child : newChildren) {
            children.add(child);
            parentSetter.accept(child, parent);
        }
    }

    /**
     * Removes all children from {@code children}, nulling each child's
     * back-reference to the parent before clearing the collection.
     *
     * <p>Prefer this over calling {@code collection.clear()} directly. A plain
     * {@code clear()} leaves every child's parent field pointing to the parent
     * entity, producing an inconsistent in-memory object graph that can cause
     * surprising behaviour during the same JPA session.
     *
     * @param <P>          the parent entity type
     * @param <C>          the child entity type
     * @param children     the parent-side collection to clear
     * @param parentSetter unbound method reference on the child that sets its
     *                     parent field (e.g. {@code CityEntity::setCountry});
     *                     called with {@code null} for each child
     */
    public static <P, C> void clearChildren(
            Collection<C> children,
            BiConsumer<C, P> parentSetter) {

        for (C child : children) {
            parentSetter.accept(child, null);
        }

        children.clear();
    }
}
