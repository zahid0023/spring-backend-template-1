package com.example.springbackendtemplate1.image.hosting.service;

import com.example.springbackendtemplate1.commons.dto.response.PaginatedResponse;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.CreateImageHostingProviderConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.ImageHostingProviderConfigFilterRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.UpdateImageHostingProviderConfigRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingProviderConfigDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigEntity;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;

public interface ImageHostingProviderConfigService {

    SuccessResponse create(CreateImageHostingProviderConfigRequest request,
                           ImageHostingProviderEntity providerEntity);

    ImageHostingProviderConfigEntity getEntityById(Long imageHostingProviderId, Long id);

    ImageHostingProviderConfigEntity getEntityById(Long id);

    PaginatedResponse<ImageHostingProviderConfigDto> getAll(ImageHostingProviderConfigFilterRequest request);

    SuccessResponse update(ImageHostingProviderConfigEntity entity, UpdateImageHostingProviderConfigRequest request);

    SuccessResponse delete(ImageHostingProviderConfigEntity entity);
}
