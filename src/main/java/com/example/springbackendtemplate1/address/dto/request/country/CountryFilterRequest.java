package com.example.springbackendtemplate1.address.dto.request.country;

import com.example.springbackendtemplate1.address.model.enums.CountrySearchField;
import com.example.springbackendtemplate1.address.model.enums.CountrySortField;
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
public class CountryFilterRequest extends PaginatedRequest implements LocaleRequiredFilterable, LocaleSortable {

    private String code;
    private String iso3Code;
    private String phoneCode;
    private String name;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId) {
        return SpecificationUtils.buildSearchPredicates(this, CountrySearchField.values(), root, query, cb, localeId);
    }

    @Override
    public LocaleJoinSortInfo getLocaleSortInfo(Long localeId) {
        return buildLocaleSortInfo("countryLocaleEntities", getSortBy(), getSortDir(), localeId, CountrySortField.localeSortFields());
    }
}
