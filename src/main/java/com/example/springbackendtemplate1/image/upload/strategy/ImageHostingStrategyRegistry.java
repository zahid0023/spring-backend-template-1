package com.example.springbackendtemplate1.image.upload.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ImageHostingStrategyRegistry {

    private final Map<String, ImageHostingStrategy> strategies;

    public ImageHostingStrategyRegistry(List<ImageHostingStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(ImageHostingStrategy::providerCode, s -> s));
        log.info("Registered image hosting strategies: {}", this.strategies.keySet());
    }

    public ImageHostingStrategy get(String providerCode) {
        ImageHostingStrategy strategy = strategies.get(providerCode);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No upload strategy registered for provider: " + providerCode
                            + ". Available: " + strategies.keySet());
        }
        return strategy;
    }
}
