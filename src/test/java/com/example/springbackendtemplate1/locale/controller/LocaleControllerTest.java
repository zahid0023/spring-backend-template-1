package com.example.springbackendtemplate1.locale.controller;

import com.example.springbackendtemplate1.support.ApiIntegrationTestBase;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full integration test for the Locale CRUD API — real HTTP via MockMvc, real JWT
 * auth via {@link ApiIntegrationTestBase}, against an in-memory H2 database.
 */
@DisplayName("Locale API")
class LocaleControllerTest extends ApiIntegrationTestBase {

    private String createLocaleJson(String code, String name, int sortOrder) {
        return """
                { "code": "%s", "name": "%s", "sort_order": %d }
                """.formatted(code, name, sortOrder);
    }

    private Long createLocale(String code, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/locales")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLocaleJson(code, name, 1)))
                .andExpect(status().isCreated())
                .andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    @Test
    @DisplayName("Test: Create Locale")
    void create_shouldPersistLocale() throws Exception {
        mockMvc.perform(post("/api/v1/locales")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLocaleJson("xx1", "Test Locale One", 1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Test: Create Locale with Duplicate Code Returns 409")
    void create_duplicateCode_shouldReturn409Conflict() throws Exception {
        createLocale("xx2", "Test Locale Two");

        mockMvc.perform(post("/api/v1/locales")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLocaleJson("xx2", "Duplicate", 2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test
    @DisplayName("Test: Create Locale with Missing Required Field Returns 400")
    void create_missingRequiredField_shouldReturn400InvalidArgument() throws Exception {
        String body = """
                { "sort_order": 1 }
                """;

        mockMvc.perform(post("/api/v1/locales")
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
    }

    @Test
    @DisplayName("Test: Get Locale by Id")
    void getById_shouldReturnLocale() throws Exception {
        Long id = createLocale("xx3", "Test Locale Three");

        mockMvc.perform(get("/api/v1/locales/{id}", id)
                        .with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("xx3"))
                .andExpect(jsonPath("$.data.name").value("Test Locale Three"));
    }

    @Test
    @DisplayName("Test: Get Locale by Unknown Id Returns 404")
    void getById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/locales/{id}", 9_999_999L)
                        .with(asSuperAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ENTITY_NOT_FOUND"));
    }

    @Test
    @DisplayName("Test: List Locales Filters by Code")
    void getAll_filtersByCodePartialMatch() throws Exception {
        createLocale("xx4", "Test Locale Four");

        mockMvc.perform(get("/api/v1/locales")
                        .with(asSuperAdmin())
                        .param("code", "xx4")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].code").value("xx4"));
    }

    @Test
    @DisplayName("Test: Update Locale Modifies Fields but Not Code")
    void update_shouldModifyNameAndSortOrder_butNotCode() throws Exception {
        Long id = createLocale("xx5", "Before Update");

        mockMvc.perform(put("/api/v1/locales/{id}", id)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "After Update", "sort_order": 9 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/v1/locales/{id}", id)
                        .with(asSuperAdmin()))
                .andExpect(jsonPath("$.data.code").value("xx5"))
                .andExpect(jsonPath("$.data.name").value("After Update"))
                .andExpect(jsonPath("$.data.sort_order").value(9));
    }

    @Test
    @DisplayName("Test: Update Unknown Locale Returns 404")
    void update_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(put("/api/v1/locales/{id}", 9_999_999L)
                        .with(asSuperAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Nowhere", "sort_order": 1 }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test: Delete Locale Soft-Deletes and Hides from Reads")
    void delete_shouldSoftDeleteAndHideFromFutureReads() throws Exception {
        Long id = createLocale("xx6", "To Delete");

        mockMvc.perform(delete("/api/v1/locales/{id}", id)
                        .with(asSuperAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/v1/locales/{id}", id)
                        .with(asSuperAdmin()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/locales")
                        .with(asSuperAdmin())
                        .param("code", "xx6")
                        .param("sortBy", "id"))
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
