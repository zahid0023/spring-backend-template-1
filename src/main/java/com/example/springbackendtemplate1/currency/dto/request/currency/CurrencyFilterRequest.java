package com.example.springbackendtemplate1.currency.dto.request.currency;

import com.example.springbackendtemplate1.currency.model.enums.CurrencySearchField;
import com.example.springbackendtemplate1.currency.model.enums.CurrencySortField;
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
public class CurrencyFilterRequest extends PaginatedRequest implements LocaleRequiredFilterable, LocaleSortable {

    private String code;
    private String numericCode;
    private Long countryId;
    private Boolean isDefault;
    private String name;
    private String shortName;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId) {
        List<Predicate> predicates = SpecificationUtils.buildSearchPredicates(this, CurrencySearchField.values(), root, query, cb, localeId);
        if (countryId != null) {
            predicates.add(cb.equal(root.get("countryEntity").get("id"), countryId));
        }
        if (isDefault != null) {
            predicates.add(cb.equal(root.get("isDefault"), isDefault));
        }
        return predicates;
    }

    @Override
    public LocaleJoinSortInfo getLocaleSortInfo(Long localeId) {
        return buildLocaleSortInfo("currencyLocaleEntities", getSortBy(), getSortDir(), localeId, CurrencySortField.localeSortFields());
    }
}
