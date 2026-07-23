package com.example.springbackendtemplate1.commons.utils;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityValidator {

    private EntityValidator() {
    }

    public static <T> void validateAllFound(Set<Long> requestedIds, List<T> entities, Function<T, Long> idExtractor, String entityName) {
        Set<Long> foundIds = entities.stream().map(idExtractor).collect(Collectors.toSet());
        Set<Long> missingIds = requestedIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());

        if (!missingIds.isEmpty()) {
            throw new EntityNotFoundException(entityName + " not found with ids: " + missingIds);
        }
    }
}
