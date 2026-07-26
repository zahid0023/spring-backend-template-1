package com.example.springbackendtemplate1.commons.utils;

import org.springframework.data.domain.Sort;

public record LocaleJoinSortInfo(
        String collectionField,   // @OneToMany field on root entity, e.g. "countryLocaleEntities"
        String targetField,       // field on locale child, e.g. "name"
        String localeEntityField, // FK to LocaleEntity on locale child, e.g. "localeEntity"
        Long localeId,            // optional — null means any locale
        Sort.Direction direction
) {}
