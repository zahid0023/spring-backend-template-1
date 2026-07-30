package com.example.springbackendtemplate1.commons.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public interface Filterable {
    List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb);

    default List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId) {
        return toPredicates(root, query, cb);
    }
}