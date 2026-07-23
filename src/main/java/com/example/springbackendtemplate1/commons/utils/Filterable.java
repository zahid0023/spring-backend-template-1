package com.example.springbackendtemplate1.commons.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public interface Filterable {
    List<Predicate> toPredicates(Root<?> root, CriteriaBuilder cb);
}
