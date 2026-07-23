package com.example.springbackendtemplate1.address.dto.request.country;

import com.example.springbackendtemplate1.address.model.enums.CountrySearchField;
import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.commons.utils.Filterable;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
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

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for (CountrySearchField field : CountrySearchField.values()) {
            SpecificationUtils.addLikeFilter(predicates, root, cb,
                    field.getFieldName(), field.getValueExtractor().apply(this));
        }
        return predicates;
    }
}
