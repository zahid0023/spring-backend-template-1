package com.example.springbackendtemplate1.commons.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class SpecificationUtils {

    public <T> Specification<@NonNull T> build(Filterable filterable) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addActiveFilter(predicates, root, cb);
            predicates.addAll(filterable.toPredicates(root, query, cb));
            if (filterable instanceof LocaleSortable ls) {
                LocaleJoinSortInfo info = ls.getLocaleSortInfo();
                if (info != null) {
                    addJoinSort(root, query, cb, info);
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public <T> void addJoinSort(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb,
                                LocaleJoinSortInfo info) {
        Join<T, ?> join = root.join(info.collectionField(), JoinType.LEFT);
        if (info.localeId() != null) {
            join.on(cb.equal(join.get(info.localeEntityField()).get("id"), info.localeId()));
        }
        query.distinct(true);
        query.orderBy(info.direction() == Sort.Direction.ASC
                ? cb.asc(join.get(info.targetField()))
                : cb.desc(join.get(info.targetField())));
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

    public <T> void addEqualFilter(List<Predicate> predicates, Root<T> root, CriteriaBuilder cb,
                                   String field, Object value) {
        if (value != null) {
            predicates.add(cb.equal(root.get(field), value));
        }
    }

    public <T> void addJoinLikeFilter(List<Predicate> predicates, Root<T> root,
                                      CriteriaQuery<?> query, CriteriaBuilder cb,
                                      String collectionField, String targetField, String value,
                                      Long localeId, String localeEntityField) {
        if (value == null || value.isBlank()) return;
        query.distinct(true);
        Join<T, ?> join = root.join(collectionField, JoinType.LEFT);
        if (localeId != null) {
            join.on(cb.equal(join.get(localeEntityField).get("id"), localeId));
        }
        predicates.add(cb.like(cb.lower(join.get(targetField)), "%" + value.toLowerCase() + "%"));
    }

    public <T> void addJoinEqualFilter(List<Predicate> predicates, Root<T> root,
                                       CriteriaQuery<?> query, CriteriaBuilder cb,
                                       String collectionField, String targetField, Object value,
                                       Long localeId, String localeEntityField) {
        if (value == null) return;
        query.distinct(true);
        Join<T, ?> join = root.join(collectionField, JoinType.LEFT);
        if (localeId != null) {
            join.on(cb.equal(join.get(localeEntityField).get("id"), localeId));
        }
        predicates.add(cb.equal(join.get(targetField), value));
    }
}
