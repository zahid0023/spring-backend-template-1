package com.example.springbackendtemplate1.commons.utils;

import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.locale.service.LocaleService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocaleUtils {

    public static <T> Map<Long, LocaleEntity> resolveLocaleMap(
            List<T> localeRequests,
            Function<T, Long> localeIdExtractor,
            LocaleService localeService) {
        return resolveEntityMap(localeRequests, localeIdExtractor, localeService::getAll, LocaleEntity::getId);
    }

    public static <T, E> Map<Long, E> resolveEntityMap(
            List<T> requests,
            Function<T, Long> idExtractor,
            Function<Set<Long>, List<E>> fetchAll,
            Function<E, Long> entityIdExtractor) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> ids = requests.stream()
                .map(idExtractor)
                .collect(Collectors.toSet());
        return fetchAll.apply(ids).stream()
                .collect(Collectors.toMap(entityIdExtractor, e -> e));
    }
}
