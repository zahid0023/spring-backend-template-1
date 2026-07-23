package com.example.springbackendtemplate1.imagehosting.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum ImageHostingProvider {

    S3("Amazon S3", new LinkedHashMap<>() {{
        put("bucket",     "Bucket Name");
        put("region",     "AWS Region");
        put("access_key", "Access Key ID");
        put("secret_key", "Secret Access Key");
    }}),
    CLOUDINARY("Cloudinary", new LinkedHashMap<>() {{
        put("cloud_name", "Cloud Name");
        put("api_key",    "API Key");
        put("api_secret", "API Secret");
    }});

    private final String label;
    private final Map<String, String> keyLabels;
    private final List<String> requiredKeys;

    ImageHostingProvider(String label, Map<String, String> keyLabels) {
        this.label = label;
        this.keyLabels = keyLabels;
        this.requiredKeys = List.copyOf(keyLabels.keySet());
    }

    /**
     * Validates that the given config map contains all required keys with non-blank values.
     * Throws {@link IllegalArgumentException} listing any missing or blank keys.
     */
    public void validate(Map<String, String> config) {
        if (config == null) {
            throw new IllegalArgumentException(
                    "Config must not be null. Required keys for " + name() + ": " + requiredKeys);
        }
        List<String> missing = requiredKeys.stream()
                .filter(key -> {
                    String value = config.get(key);
                    return value == null || value.isBlank();
                })
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing or blank required config keys for provider " + name() + ": " + missing
                            + ". Required keys: " + requiredKeys);
        }
    }
}
