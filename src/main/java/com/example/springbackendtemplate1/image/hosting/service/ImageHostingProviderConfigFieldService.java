package com.example.springbackendtemplate1.image.hosting.service;

import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.CreateImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.UpdateImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingProviderConfigFieldDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigFieldEntity;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;

import java.util.List;

public interface ImageHostingProviderConfigFieldService {

    SuccessResponse create(CreateImageHostingProviderConfigFieldRequest request,
                           ImageHostingProviderEntity providerEntity);

    ImageHostingProviderConfigFieldEntity getEntityById(Long imageHostingProviderId, Long id);

    List<ImageHostingProviderConfigFieldDto> getAll(Long imageHostingProviderId);

    SuccessResponse update(ImageHostingProviderConfigFieldEntity entity, UpdateImageHostingProviderConfigFieldRequest request);

    SuccessResponse delete(ImageHostingProviderConfigFieldEntity entity);
}
