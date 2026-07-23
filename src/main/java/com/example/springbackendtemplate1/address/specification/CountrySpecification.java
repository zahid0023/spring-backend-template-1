package com.example.springbackendtemplate1.address.specification;

import com.example.springbackendtemplate1.address.dto.request.country.CountryFilterRequest;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class CountrySpecification {

    public Specification<@NonNull CountryEntity> filter(CountryFilterRequest request) {
        return SpecificationUtils.build(request);
    }
}
