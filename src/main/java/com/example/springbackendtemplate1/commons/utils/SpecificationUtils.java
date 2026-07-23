package com.example.springbackendtemplate1.commons.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class SpecificationUtils {

    public <T> Specification<@NonNull T> build(Filterable filterable) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addActiveFilter(predicates, root, cb);
            predicates.addAll(filterable.toPredicates(root, cb));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public <T> void addActiveFilter(List<Predicate> predicates, Root<T> root, CriteriaBuilder cb) {
        predicates.add(cb.equal(root.get("isActive"), true));
        predicates.add(cb.equal(root.get("isDeleted"), false));
    }

    public <T> void addLikeFilter(List<Predicate> predicates, Root<T> root, CriteriaBuilder cb,
                                  String field, String value) {
        if (value != null && !value.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%"));
        }
    }
}
