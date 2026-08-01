package com.example.springbackendtemplate1.address.controller;

import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.locale.repository.LocaleRepository;
import com.example.springbackendtemplate1.support.ApiIntegrationTestBase;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full integration test for the Country CRUD API + its CountryLocale sub-resource.
 * Extends {@link ApiIntegrationTestBase} for real HTTP + real JWT auth (superadmin).
 * Locale fixtures (en/bn) are seeded directly via the repository here since Locale's
 * own create flow is already covered end-to-end by LocaleControllerTest — this class's
 * job is Country, not re-proving Locale creation.
 */
@DisplayName("Country API")
class CountryControllerTest extends ApiIntegrationTestBase {

    @Autowired
    private LocaleRepository localeRepository;

    private Long enLocaleId;
    private Long bnLocaleId;

    @BeforeEach
    void seedLocales() {
        LocaleEntity en = new LocaleEntity();
        en.setCode("en");
        en.setName("English");
        en.setSortOrder(1);
        enLocaleId = localeRepository.save(en).getId();

        LocaleEntity bn = new LocaleEntity();
        bn.setCode("bn");
        bn.setName("Bengali");
        bn.setSortOrder(2);
        bnLocaleId = localeRepository.save(bn).getId();
    }

    private String createCountryJson(String code, String iso3, String phone, int sortOrder,
                                      Long localeId, String name) {
        return """
                {
                  "code": "%s",
                  "iso3_code": "%s",
                  "phone_code": "%s",
                  "sort_order": %d,
                  "locales": [
                    { "locale_id": %d, "name": "%s", "description": "", "sort_order": 1 }
                  ]
                }
                """.formatted(code, iso3, phone, sortOrder, localeId, name);
    }

    private Long createCountry(String code, String iso3, String phone) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/countries")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCountryJson(code, iso3, phone, 1, enLocaleId, code + " Land")))
                .andExpect(status().isCreated())
                .andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    // ---- Create ----

    @Test
    @DisplayName("Test: Create Country with Locales")
    void create_shouldPersistCountryWithLocales() throws Exception {
        mockMvc.perform(post("/api/v1/countries")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCountryJson("T1", "AAA", "11", 5, enLocaleId, "Testland")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Test: Create Country with Duplicate Code Returns 409")
    void create_duplicateCode_shouldReturn409Conflict() throws Exception {
        createCountry("T2", "AAB", "12");

        mockMvc.perform(post("/api/v1/countries")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCountryJson("T2", "AAC", "19", 1, enLocaleId, "Duplicate")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test
    @DisplayName("Test: Create Country with Missing Required Field Returns 400")
    void create_missingRequiredField_shouldReturn400InvalidArgument() throws Exception {
        String body = """
                {
                  "iso3_code": "AAD",
                  "phone_code": "13",
                  "sort_order": 1,
                  "locales": [ { "locale_id": %d, "name": "Testland", "description": "", "sort_order": 1 } ]
                }
                """.formatted(enLocaleId);

        mockMvc.perform(post("/api/v1/countries")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
    }

    @Test
    @DisplayName("Test: Create Country without Locales (Empty Array)")
    void create_emptyLocalesArray_shouldReturn400InvalidArgument() throws Exception {
        String body = """
                {
                  "code": "ZE",
                  "iso3_code": "AAZ",
                  "phone_code": "1",
                  "sort_order": 1,
                  "locales": []
                }
                """;

        mockMvc.perform(post("/api/v1/countries")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
    }

    @Test
    @DisplayName("Test: Create Country without Locales (Field Omitted)")
    void create_omittedLocalesField_shouldReturn400InvalidArgument() throws Exception {
        String body = """
                {
                  "code": "ZO",
                  "iso3_code": "AAY",
                  "phone_code": "1",
                  "sort_order": 1
                }
                """;

        mockMvc.perform(post("/api/v1/countries")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
    }

    // ---- Get by id ----

    @Test
    @DisplayName("Test: Get Country by Id Returns All Locale Translations")
    void getById_shouldReturnAllLocaleTranslations() throws Exception {
        Long countryId = createCountry("T4", "AAE", "14");
        mockMvc.perform(post("/api/v1/countries/{countryId}/locales", countryId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "locale_id": %d, "name": "টেস্ট", "description": "", "sort_order": 2 }
                                """.formatted(bnLocaleId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("T4"))
                .andExpect(jsonPath("$.data.locales.length()").value(2));
    }

    @Test
    @DisplayName("Test: Get Country by Unknown Id Returns 404")
    void getById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/countries/{id}", 9_999_999L)
                        .with(asSuperAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ENTITY_NOT_FOUND"));
    }

    // ---- Get all / search ----

    @Test
    @DisplayName("Test: List Countries without sortBy Returns 400")
    void getAll_missingSortBy_shouldReturn400InvalidSortField() throws Exception {
        mockMvc.perform(get("/api/v1/countries")
                        .with(asSuperAdmin())
                        .header("Accept-Language", "en"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
    }

    @Test
    @DisplayName("Test: List Countries Scopes Locale by Accept-Language")
    void getAll_scopesLocaleByAcceptLanguage() throws Exception {
        Long countryId = createCountry("T5", "AAF", "15");
        mockMvc.perform(post("/api/v1/countries/{countryId}/locales", countryId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "locale_id": %d, "name": "T5-Bengali", "description": "", "sort_order": 2 }
                                """.formatted(bnLocaleId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/countries")
                        .with(asSuperAdmin())
                        .param("iso3Code", "AAF")
                        .param("sortBy", "code")
                        .header("Accept-Language", "bn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].locales[0].locale.code").value("bn"))
                .andExpect(jsonPath("$.data[0].locales.length()").value(1));

        mockMvc.perform(get("/api/v1/countries")
                        .with(asSuperAdmin())
                        .param("iso3Code", "AAF")
                        .param("sortBy", "code")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].locales[0].locale.code").value("en"));
    }

    @Test
    @DisplayName("Test: List Countries Missing Accept-Language Returns 400")
    void getAll_missingAcceptLanguage_shouldReturn400InvalidArgument() throws Exception {
        mockMvc.perform(get("/api/v1/countries")
                        .with(asSuperAdmin())
                        .param("sortBy", "code"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
    }

    @Test
    @DisplayName("Test: List Countries Filters by ISO3 Code")
    void getAll_filtersByIso3CodePartialMatch() throws Exception {
        createCountry("T6", "ZZZ", "16");

        mockMvc.perform(get("/api/v1/countries")
                        .with(asSuperAdmin())
                        .param("iso3Code", "zz")
                        .param("sortBy", "code")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].code").value("T6"));
    }

    // ---- Update ----

    @Test
    @DisplayName("Test: Update Country Modifies Fields but Not Code")
    void update_shouldModifyIso3PhoneAndSortOrder_butNotCode() throws Exception {
        Long countryId = createCountry("T7", "AAG", "17");

        mockMvc.perform(put("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "iso3_code": "UPD", "phone_code": "99", "sort_order": 42 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(jsonPath("$.data.code").value("T7"))
                .andExpect(jsonPath("$.data.iso3_code").value("UPD"))
                .andExpect(jsonPath("$.data.phone_code").value("99"))
                .andExpect(jsonPath("$.data.sort_order").value(42));
    }

    @Test
    @DisplayName("Test: Update Unknown Country Returns 404")
    void update_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(put("/api/v1/countries/{id}", 9_999_999L)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "iso3_code": "XXX", "phone_code": "1", "sort_order": 1 }
                                """))
                .andExpect(status().isNotFound());
    }

    // ---- Delete ----

    @Test
    @DisplayName("Test: Delete Country Soft-Deletes and Hides from Reads")
    void delete_shouldSoftDeleteAndHideFromFutureReads() throws Exception {
        Long countryId = createCountry("T8", "AAH", "18");

        mockMvc.perform(delete("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/countries")
                        .with(asSuperAdmin())
                        .param("iso3Code", "AAH")
                        .param("sortBy", "code")
                        .header("Accept-Language", "en"))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ---- Country Locale sub-resource ----

    @Test
    @DisplayName("Test: Create Country Locale")
    void countryLocale_create_shouldReturn201() throws Exception {
        Long countryId = createCountry("T9", "AAI", "19");

        mockMvc.perform(post("/api/v1/countries/{countryId}/locales", countryId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "locale_id": %d, "name": "T9-Bengali", "description": "", "sort_order": 2 }
                                """.formatted(bnLocaleId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Test: Create Duplicate Country Locale Returns 409")
    void countryLocale_create_duplicateLocaleForCountry_shouldReturn409DataIntegrityViolation() throws Exception {
        Long countryId = createCountry("TA", "AAJ", "20"); // already has an 'en' translation

        mockMvc.perform(post("/api/v1/countries/{countryId}/locales", countryId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "locale_id": %d, "name": "Duplicate English", "description": "", "sort_order": 1 }
                                """.formatted(enLocaleId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DATA_INTEGRITY_VIOLATION"));
    }

    @Test
    @DisplayName("Test: Create Country Locale with Unknown Locale Id Returns 404")
    void countryLocale_create_unknownLocaleId_shouldReturn404() throws Exception {
        Long countryId = createCountry("TB", "AAK", "21");

        mockMvc.perform(post("/api/v1/countries/{countryId}/locales", countryId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "locale_id": 9999999, "name": "Nowhere", "description": "", "sort_order": 1 }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DisplayName("Test: Update Country Locale")
    void countryLocale_update_shouldModifyNameDescriptionSortOrder() throws Exception {
        Long countryId = createCountry("TC", "AAL", "22");
        MvcResult createResult = mockMvc.perform(post("/api/v1/countries/{countryId}/locales", countryId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "locale_id": %d, "name": "Before", "description": "before", "sort_order": 2 }
                                """.formatted(bnLocaleId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long localeRowId = ((Number) JsonPath.read(
                createResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(put("/api/v1/countries/{countryId}/locales/{id}", countryId, localeRowId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "After", "description": "after", "sort_order": 3 }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(jsonPath("$.data.locales[?(@.locale.code == 'bn')].name").value("After"));
    }

    @Test
    @DisplayName("Test: Delete Country Locale Soft-Deletes and Hides from Parent")
    void countryLocale_delete_shouldSoftDeleteAndHideFromGetById() throws Exception {
        Long countryId = createCountry("TD", "AAM", "23");
        MvcResult createResult = mockMvc.perform(post("/api/v1/countries/{countryId}/locales", countryId)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "locale_id": %d, "name": "ToDelete", "description": "", "sort_order": 2 }
                                """.formatted(bnLocaleId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long localeRowId = ((Number) JsonPath.read(
                createResult.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(delete("/api/v1/countries/{countryId}/locales/{id}", countryId, localeRowId)
                        .with(asSuperAdmin()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/countries/{id}", countryId)
                        .with(asSuperAdmin()))
                .andExpect(jsonPath("$.data.locales.length()").value(1));
    }
}
