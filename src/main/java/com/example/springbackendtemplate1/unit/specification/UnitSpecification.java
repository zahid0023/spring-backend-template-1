package com.example.springbackendtemplate1.unit.specification;

import com.example.springbackendtemplate1.unit.dto.request.unit.UnitFilterRequest;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class UnitSpecification {

    public Specification<@NonNull UnitEntity> filter(UnitFilterRequest request, Long unitTypeId) {
        Specification<UnitEntity> spec = SpecificationUtils.build(request);
        if (unitTypeId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("unitTypeEntity").get("id"), unitTypeId));
        }
        return spec;
    }
}
