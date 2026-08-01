package com.example.springbackendtemplate1;

import com.example.springbackendtemplate1.support.ApiIntegrationTestBase;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A single, continuous, realistic application journey — everything through real HTTP
 * with a real JWT (no direct repository seeding, no security-test shortcuts):
 *
 * <ol>
 *   <li>Superadmin is bootstrapped and logged in for real (see {@link ApiIntegrationTestBase}).</li>
 *   <li>Superadmin creates the Locale reference data most other entities depend on.</li>
 *   <li>Superadmin creates a Country that references those real, just-created locale IDs.</li>
 *   <li>Superadmin manages that Country's locale sub-resource (add / update / delete a translation).</li>
 * </ol>
 *
 * <p>This lives in one {@code @Test} method rather than several {@code @Order}ed ones:
 * {@code @Transactional} rolls back the database after every test <em>method</em>, so
 * splitting the journey across methods would mean step 2 never actually sees step 1's data.
 * City is intentionally not included — it has no REST controller yet.
 */
@DisplayName("End-to-End Application Flow")
class EndToEndFlowTest extends ApiIntegrationTestBase {

    @Test
    @DisplayName("Test: Superadmin Creates Locales, then Country, then Manages Country Locales")
    void fullFlow_superAdmin_thenLocales_thenCountry_thenCountryLocales() throws Exception {

        // ---- Step 1: superadmin is already bootstrapped + logged in via @BeforeEach ----
        mockMvc.perform(get("/api/v1/locales")
                        .with(asSuperAdmin()))
                .andExpect(status().isOk());

        // ---- Step 2: create the Locale reference data (real HTTP, real JWT) ----
        Long englishLocaleId = createLocale("en", "English");
        Long bengaliLocaleId = createLocale("bn", "Bengali");

        mockMvc.perform(get("/api/v1/locales/{id}", englishLocaleId)
                        .with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("en"));

        // ---- Step 3: create a Country that references the real locale IDs from step 2 ----
        String createCountryBody = """
                {
                  "code": "FL",
                  "iso3_code": "FLW",
                  "phone_code": "44",
                  "sort_order": 1,
                  "locales": [
                    { "locale_id": %d, "name": "Flowland", "description": "End-to-end test country", "sort_order": 1 },
                    { "locale_id": %d, "name": "ফ্লো ল্যান্ড", "description": "", "sort_order": 2 }
                  ]
                }
                """.formatted(englishLocaleId, bengaliLocaleId);

        MvcResult createCountryResult = mockMvc.perform(post("/api/v1/countries")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCountryBody))
                .andExpect(status().isCreated())
                .andReturn();
        Long countryId = ((Number) JsonPath.read(
                createCountryResult.getResponse().getContentAsString(), "$.id")).longValue();

        // Locale-scoped list view: Accept-Language: bn should surface the Bengali translation
        mockMvc.perform(get("/api/v1/countries")
                        .with(asSuperAdmin())
                        .param("iso3Code", "FLW")
                        .param("sortBy", "code")
                        .header("Accept-Language", "bn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].locales[0].locale.code").value("bn"));

        // getById returns every translation, regardless of Accept-Language
        mockMvc.perform(get("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("FL"))
                .andExpect(jsonPath("$.data.locales.length()").value(2));

        // ---- Step 4: manage the Country's locale sub-resource ----
        Long thirdLocaleId = createLocale("fr", "French");

        MvcResult addLocaleResult = mockMvc.perform(post("/api/v1/countries/{countryId}/locales", countryId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "locale_id": %d, "name": "Flowterre", "description": "", "sort_order": 3 }
                                """.formatted(thirdLocaleId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long frenchCountryLocaleId = ((Number) JsonPath.read(
                addLocaleResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(get("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(jsonPath("$.data.locales.length()").value(3));

        mockMvc.perform(put("/api/v1/countries/{countryId}/locales/{id}", countryId, frenchCountryLocaleId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Flowterre (updated)", "description": "updated", "sort_order": 3 }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(jsonPath("$.data.locales[?(@.locale.code == 'fr')].name")
                        .value("Flowterre (updated)"));

        mockMvc.perform(delete("/api/v1/countries/{countryId}/locales/{id}", countryId, frenchCountryLocaleId)
                        .with(asSuperAdmin()))
                .andExpect(status().isOk());

        // ---- Step 5: final state — back down to 2 translations, country itself untouched ----
        mockMvc.perform(get("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(jsonPath("$.data.code").value("FL"))
                .andExpect(jsonPath("$.data.locales.length()").value(2));
    }

    private Long createLocale(String code, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/locales")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "code": "%s", "name": "%s", "sort_order": 1 }
                                """.formatted(code, name)))
                .andExpect(status().isCreated())
                .andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }
}
