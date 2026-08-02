package com.example.springbackendtemplate1.image.hosting.model.mapper;

import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.CreateImageHostingProviderRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.ImageHostingProviderRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.UpdateImageHostingProviderRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingProviderDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageHostingProviderMapper {

    public ImageHostingProviderEntity create(CreateImageHostingProviderRequest request) {
        ImageHostingProviderEntity entity = new ImageHostingProviderEntity();
        entity.setCode(request.getCode());
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(ImageHostingProviderEntity entity, UpdateImageHostingProviderRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(ImageHostingProviderEntity entity, ImageHostingProviderRequest request) {
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setSortOrder(request.getSortOrder());
    }

    public ImageHostingProviderDto.ImageHostingProviderDtoBuilder toDto(ImageHostingProviderEntity entity) {
        return ImageHostingProviderDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .sortOrder(entity.getSortOrder());
    }
}
