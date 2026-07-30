package com.example.springbackendtemplate1.address.specification;

import com.example.springbackendtemplate1.address.dto.request.city.CityFilterRequest;
import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class CitySpecification {

    public Specification<@NonNull CityEntity> filter(CityFilterRequest request) {
        return SpecificationUtils.build(request);
    }
}
