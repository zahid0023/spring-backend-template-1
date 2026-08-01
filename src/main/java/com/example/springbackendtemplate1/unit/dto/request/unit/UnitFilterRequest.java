package com.example.springbackendtemplate1.unit.dto.request.unit;

import com.example.springbackendtemplate1.unit.model.enums.UnitSearchField;
import com.example.springbackendtemplate1.unit.model.enums.UnitSortField;
import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.commons.utils.LocaleJoinSortInfo;
import com.example.springbackendtemplate1.commons.utils.LocaleRequiredFilterable;
import com.example.springbackendtemplate1.commons.utils.LocaleSortable;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UnitFilterRequest extends PaginatedRequest implements LocaleRequiredFilterable, LocaleSortable {

    private String code;
    private Long unitTypeId;
    private Boolean isBaseUnit;
    private String name;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId) {
        List<Predicate> predicates = SpecificationUtils.buildSearchPredicates(this, UnitSearchField.values(), root, query, cb, localeId);
        if (unitTypeId != null) {
            predicates.add(cb.equal(root.get("unitTypeEntity").get("id"), unitTypeId));
        }
        if (isBaseUnit != null) {
            predicates.add(cb.equal(root.get("isBaseUnit"), isBaseUnit));
        }
        return predicates;
    }

    @Override
    public LocaleJoinSortInfo getLocaleSortInfo(Long localeId) {
        return buildLocaleSortInfo("unitLocaleEntities", getSortBy(), getSortDir(), localeId, UnitSortField.localeSortFields());
    }
}
