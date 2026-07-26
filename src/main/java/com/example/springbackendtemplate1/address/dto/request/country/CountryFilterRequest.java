package com.example.springbackendtemplate1.address.dto.request.country;

import com.example.springbackendtemplate1.address.model.enums.CountrySearchField;
import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.commons.utils.Filterable;
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
public class CountryFilterRequest extends PaginatedRequest implements Filterable {

    private String code;
    private String iso3Code;
    private String phoneCode;
    private String name;
    private Long localeId;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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

}
