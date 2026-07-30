package com.example.springbackendtemplate1.address.dto.request.city;

import com.example.springbackendtemplate1.address.model.enums.CitySearchField;
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
public class CityFilterRequest extends PaginatedRequest implements Filterable {

    private String code;
    private Long countryId;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for (CitySearchField field : CitySearchField.values()) {
            String value = field.getValueExtractor().apply(this);
            switch (field.getSearchType()) {
                case LIKE  -> SpecificationUtils.addLikeFilter(predicates, root, cb, field.getFieldName(), value);
                case EXACT -> SpecificationUtils.addEqualFilter(predicates, root, cb, field.getFieldName(), value);
            }
        }
        if (countryId != null) {
            predicates.add(cb.equal(root.get("countryEntity").get("id"), countryId));
        }
        return predicates;
    }
}
