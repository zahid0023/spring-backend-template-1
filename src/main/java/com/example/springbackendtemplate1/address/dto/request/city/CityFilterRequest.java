package com.example.springbackendtemplate1.address.dto.request.city;

import com.example.springbackendtemplate1.address.model.enums.CitySearchField;
import com.example.springbackendtemplate1.address.model.enums.CitySortField;
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
public class CityFilterRequest extends PaginatedRequest implements LocaleRequiredFilterable, LocaleSortable {

    private String code;
    private Long countryId;
    private String name;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId) {
        List<Predicate> predicates = SpecificationUtils.buildSearchPredicates(this, CitySearchField.values(), root, query, cb, localeId);
        if (countryId != null) {
            predicates.add(cb.equal(root.get("countryEntity").get("id"), countryId));
        }
        return predicates;
    }

    @Override
    public LocaleJoinSortInfo getLocaleSortInfo(Long localeId) {
        return buildLocaleSortInfo("cityLocaleEntities", getSortBy(), getSortDir(), localeId, CitySortField.localeSortFields());
    }
}
