package com.example.springbackendtemplate1.image.hosting.model.mapper;

import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.CreateImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.ImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.dto.request.imagehostingprovider.configfield.UpdateImageHostingProviderConfigFieldRequest;
import com.example.springbackendtemplate1.image.hosting.model.dto.ImageHostingProviderConfigFieldDto;
import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigFieldEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageHostingProviderConfigFieldMapper {

    public ImageHostingProviderConfigFieldEntity create(CreateImageHostingProviderConfigFieldRequest request) {
        ImageHostingProviderConfigFieldEntity entity = new ImageHostingProviderConfigFieldEntity();
        entity.setKey(request.getKey());
        applyCommonFields(entity, request);
        return entity;
    }

    public void update(ImageHostingProviderConfigFieldEntity entity, UpdateImageHostingProviderConfigFieldRequest request) {
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(ImageHostingProviderConfigFieldEntity entity, ImageHostingProviderConfigFieldRequest request) {
        entity.setLabel(request.getLabel());
        entity.setFieldType(request.getFieldType());
        entity.setPlaceholder(request.getPlaceholder());
        entity.setDefaultValue(request.getDefaultValue());
        entity.setIsRequired(request.getIsRequired());
        entity.setSortOrder(request.getSortOrder());
    }

    public ImageHostingProviderConfigFieldDto.ImageHostingProviderConfigFieldDtoBuilder toDto(ImageHostingProviderConfigFieldEntity entity) {
        return ImageHostingProviderConfigFieldDto.builder()
                .id(entity.getId())
                .key(entity.getKey())
                .label(entity.getLabel())
                .fieldType(entity.getFieldType())
                .placeholder(entity.getPlaceholder())
                .defaultValue(entity.getDefaultValue())
                .isRequired(entity.getIsRequired())
                .sortOrder(entity.getSortOrder());
    }
}
