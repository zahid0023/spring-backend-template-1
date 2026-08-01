package com.example.springbackendtemplate1.commons.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public interface LocaleRequiredFilterable extends Filterable {

    @Override
    default List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " requires a localeId — use toPredicates(root, query, cb, localeId)");
    }

    @Override
    List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId);
}
