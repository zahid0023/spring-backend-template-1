package com.example.springbackendtemplate1.image.hosting.specification;

import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.ImageHostingConfigFilterRequest;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingConfigEntity;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class ImageHostingConfigSpecification {

    public Specification<@NonNull ImageHostingConfigEntity> filter(ImageHostingConfigFilterRequest request, Long localeId) {
        return SpecificationUtils.build(request, localeId);
    }
}
