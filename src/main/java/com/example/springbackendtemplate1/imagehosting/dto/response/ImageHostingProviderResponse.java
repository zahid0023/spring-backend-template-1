package com.example.springbackendtemplate1.imagehosting.dto.response;

import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageHostingProviderResponse {
    private ImageHostingProvider provider;
    private String label;
    private List<ImageHostingProviderKeyDto> requiredKeys;

    public static ImageHostingProviderResponse from(ImageHostingProvider provider) {
        List<ImageHostingProviderKeyDto> keys = provider.getKeyLabels().entrySet().stream()
                .map(e -> new ImageHostingProviderKeyDto(e.getKey(), e.getValue()))
                .toList();
        return ImageHostingProviderResponse.builder()
                .provider(provider)
                .label(provider.getLabel())
                .requiredKeys(keys)
                .build();
    }
}
