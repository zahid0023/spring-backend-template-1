package com.example.springbackendtemplate1.imagehosting.enums;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public enum ImageHostingProvider {

    S3(List.of("bucket", "region", "access_key", "secret_key")),
    CLOUDINARY(List.of("cloud_name", "api_key", "api_secret"));

    private final List<String> requiredKeys;

    ImageHostingProvider(List<String> requiredKeys) {
        this.requiredKeys = requiredKeys;
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
