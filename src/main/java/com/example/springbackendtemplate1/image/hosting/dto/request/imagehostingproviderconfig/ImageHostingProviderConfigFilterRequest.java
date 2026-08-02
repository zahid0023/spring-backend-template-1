package com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig;

import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.commons.utils.Filterable;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import com.example.springbackendtemplate1.image.hosting.model.enums.ImageHostingProviderConfigSearchField;
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
public class ImageHostingProviderConfigFilterRequest extends PaginatedRequest implements Filterable {
    private String name;
    private Long imageHostingProviderId;

    @Override
    public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for (ImageHostingProviderConfigSearchField field : ImageHostingProviderConfigSearchField.values()) {
            String value = field.getValueExtractor().apply(this);
            switch (field.getSearchType()) {
                case LIKE  -> SpecificationUtils.addLikeFilter(predicates, root, cb, field.getFieldName(), value);
                case EXACT -> SpecificationUtils.addEqualFilter(predicates, root, cb, field.getFieldName(), value);
            }
        }
        if (imageHostingProviderId != null) {
            predicates.add(cb.equal(root.get("imageHostingProviderEntity").get("id"), imageHostingProviderId));
        }
        return predicates;
    }
}
