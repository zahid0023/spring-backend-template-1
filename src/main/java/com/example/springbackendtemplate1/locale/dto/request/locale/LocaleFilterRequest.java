package com.example.springbackendtemplate1.locale.dto.request.locale;

import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.commons.utils.Filterable;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import com.example.springbackendtemplate1.locale.model.enums.LocaleSearchField;
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
public class LocaleFilterRequest extends PaginatedRequest implements Filterable {
    private String code;
    private String name;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for (LocaleSearchField field : LocaleSearchField.values()) {
            String value = field.getValueExtractor().apply(this);
            switch (field.getSearchType()) {
                case LIKE  -> SpecificationUtils.addLikeFilter(predicates, root, cb, field.getFieldName(), value);
                case EXACT -> SpecificationUtils.addEqualFilter(predicates, root, cb, field.getFieldName(), value);
            }
        }
        return predicates;
    }
}
