package com.example.springbackendtemplate1.address.dto.request.country;

import com.example.springbackendtemplate1.address.model.enums.CountrySearchField;
import com.example.springbackendtemplate1.address.model.enums.CountrySortField;
import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.commons.utils.Filterable;
import com.example.springbackendtemplate1.commons.utils.LocaleJoinSortInfo;
import com.example.springbackendtemplate1.commons.utils.LocaleSortable;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CountryFilterRequest extends PaginatedRequest implements Filterable, LocaleSortable {

    private String code;
    private String iso3Code;
    private String phoneCode;
    private String name;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        throw new UnsupportedOperationException("CountryFilterRequest requires a localeId — use toPredicates(root, query, cb, localeId)");
    }

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId) {
        List<Predicate> predicates = new ArrayList<>();
        for (CountrySearchField field : CountrySearchField.values()) {
            String value = field.getValueExtractor().apply(this);
            if (field.isLocaleField()) {
                switch (field.getSearchType()) {
                    case LIKE  -> SpecificationUtils.addJoinLikeFilter(predicates, root, query, cb,
                            field.getCollectionField(), field.getFieldName(), value, localeId, "localeEntity");
                    case EXACT -> SpecificationUtils.addJoinEqualFilter(predicates, root, query, cb,
                            field.getCollectionField(), field.getFieldName(), value, localeId, "localeEntity");
                }
            } else {
                switch (field.getSearchType()) {
                    case LIKE  -> SpecificationUtils.addLikeFilter(predicates, root, cb, field.getFieldName(), value);
                    case EXACT -> SpecificationUtils.addEqualFilter(predicates, root, cb, field.getFieldName(), value);
                }
            }
        }
        return predicates;
    }

    @Override
    public LocaleJoinSortInfo getLocaleSortInfo() {
        throw new UnsupportedOperationException("CountryFilterRequest requires a localeId — use getLocaleSortInfo(localeId)");
    }

    @Override
    public LocaleJoinSortInfo getLocaleSortInfo(Long localeId) {
        if (!CountrySortField.localeSortFields().contains(getSortBy())) {
            return null;
        }
        return new LocaleJoinSortInfo("countryLocaleEntities", getSortBy(), "localeEntity", localeId, getSortDir());
    }

}
