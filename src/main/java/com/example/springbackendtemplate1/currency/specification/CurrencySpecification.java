package com.example.springbackendtemplate1.currency.specification;

import com.example.springbackendtemplate1.currency.dto.request.currency.CurrencyFilterRequest;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class CurrencySpecification {

    public Specification<@NonNull CurrencyEntity> filter(CurrencyFilterRequest request, Long localeId) {
        return SpecificationUtils.build(request, localeId);
    }
}
