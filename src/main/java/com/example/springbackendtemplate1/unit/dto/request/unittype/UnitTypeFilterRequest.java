package com.example.springbackendtemplate1.unit.dto.request.unittype;

import com.example.springbackendtemplate1.unit.model.enums.UnitTypeSearchField;
import com.example.springbackendtemplate1.unit.model.enums.UnitTypeSortField;
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
public class UnitTypeFilterRequest extends PaginatedRequest implements LocaleRequiredFilterable, LocaleSortable {

    private String code;
    private String name;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId) {
        return SpecificationUtils.buildSearchPredicates(this, UnitTypeSearchField.values(), root, query, cb, localeId);
    }

    @Override
    public LocaleJoinSortInfo getLocaleSortInfo(Long localeId) {
        return buildLocaleSortInfo("unitTypeLocaleEntities", getSortBy(), getSortDir(), localeId, UnitTypeSortField.localeSortFields());
    }
}
