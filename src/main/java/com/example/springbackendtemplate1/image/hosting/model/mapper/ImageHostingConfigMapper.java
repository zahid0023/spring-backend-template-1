package com.example.springbackendtemplate1.image.hosting.model.mapper;

import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.CreateImageHostingConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.ImageHostingConfigRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingconfig.UpdateImageHostingConfigRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingConfigDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingConfigEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageHostingConfigMapper {

    public ImageHostingConfigEntity create(CreateImageHostingConfigRequest request) {
        ImageHostingConfigEntity entity = new ImageHostingConfigEntity();
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(ImageHostingConfigEntity entity, UpdateImageHostingConfigRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(ImageHostingConfigEntity entity, ImageHostingConfigRequest request) {
        entity.setName(request.getName());
        entity.setConfig(request.getConfig());
    }

    public ImageHostingConfigDto.ImageHostingConfigDtoBuilder toDto(ImageHostingConfigEntity entity) {
        return ImageHostingConfigDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .config(entity.getConfig());
    }
}
