package com.example.springbackendtemplate1.image.hosting.model.mapper;

import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.CreateImageHostingProviderConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.ImageHostingProviderConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingproviderconfig.UpdateImageHostingProviderConfigRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingProviderConfigDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageHostingProviderConfigMapper {

    public ImageHostingProviderConfigEntity create(CreateImageHostingProviderConfigRequest request) {
        ImageHostingProviderConfigEntity entity = new ImageHostingProviderConfigEntity();
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(ImageHostingProviderConfigEntity entity, UpdateImageHostingProviderConfigRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(ImageHostingProviderConfigEntity entity, ImageHostingProviderConfigRequest request) {
        entity.setName(request.getName());
        entity.setConfig(request.getConfig());
    }

    public ImageHostingProviderConfigDto.ImageHostingProviderConfigDtoBuilder toDto(ImageHostingProviderConfigEntity entity) {
        return ImageHostingProviderConfigDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .config(entity.getConfig());
    }
}
