package com.example.springbackendtemplate1.image.hosting.specification;

import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.ImageHostingProviderFilterRequest;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class ImageHostingProviderSpecification {

    public Specification<@NonNull ImageHostingProviderEntity> filter(ImageHostingProviderFilterRequest request, Long localeId) {
        return SpecificationUtils.build(request, localeId);
    }
}
