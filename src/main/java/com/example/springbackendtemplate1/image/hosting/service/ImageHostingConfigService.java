package com.example.springbackendtemplate1.image.hosting.service;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.CreateImageHostingConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.ImageHostingConfigFilterRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.UpdateImageHostingConfigRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingConfigDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingConfigEntity;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;

public interface ImageHostingConfigService {

    SuccessResponse create(CreateImageHostingConfigRequest request, ImageHostingProviderEntity providerEntity);

    ImageHostingConfigEntity getEntityById(Long id);

    PaginatedResponse<ImageHostingConfigDto> getAll(ImageHostingConfigFilterRequest request);

    SuccessResponse update(ImageHostingConfigEntity entity, UpdateImageHostingConfigRequest request);

    SuccessResponse delete(ImageHostingConfigEntity entity);
}
