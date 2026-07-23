package com.example.springbackendtemplate1.address.dto.request.city;

import com.example.springbackendtemplate1.address.model.enums.CitySearchField;
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
public class CityFilterRequest extends PaginatedRequest implements Filterable {
    private String code;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for (CitySearchField field : CitySearchField.values()) {
            SpecificationUtils.addLikeFilter(predicates, root, cb,
                    field.getFieldName(), field.getValueExtractor().apply(this));
        }
        return predicates;
    }
}
