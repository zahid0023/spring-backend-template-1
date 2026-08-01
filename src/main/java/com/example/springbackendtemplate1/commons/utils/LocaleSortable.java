package com.example.springbackendtemplate1.commons.utils;

import org.springframework.data.domain.Sort;

import java.util.Set;

public interface LocaleSortable {

    default LocaleJoinSortInfo getLocaleSortInfo() {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " requires a localeId — use getLocaleSortInfo(localeId)");
    }

    LocaleJoinSortInfo getLocaleSortInfo(Long localeId);

    default LocaleJoinSortInfo buildLocaleSortInfo(String collectionField, String sortBy, Sort.Direction sortDir,
                                                    Long localeId, Set<String> localeSortFields) {
        if (!localeSortFields.contains(sortBy)) {
            return null;
        }
        return new LocaleJoinSortInfo(collectionField, sortBy, "localeEntity", localeId, sortDir);
    }
}
