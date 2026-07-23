package com.example.springbackendtemplate1.locale.specification;

import com.example.springbackendtemplate1.locale.dto.request.locale.LocaleFilterRequest;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class LocaleSpecification {

    public Specification<@NonNull LocaleEntity> filter(LocaleFilterRequest request) {
        return SpecificationUtils.build(request);
    }
}
