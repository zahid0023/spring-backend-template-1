package com.example.springbackendtemplate1.imagehosting.strategy;

import com.example.springbackendtemplate1.imagehosting.enums.ImageHostingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ImageHostingStrategyRegistry {

    private final Map<ImageHostingProvider, ImageHostingStrategy> strategies;

    public ImageHostingStrategyRegistry(List<ImageHostingStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(ImageHostingStrategy::provider, s -> s));
        log.info("Registered image hosting strategies: {}", this.strategies.keySet());
    }

    public ImageHostingStrategy get(ImageHostingProvider provider) {
        ImageHostingStrategy strategy = strategies.get(provider);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No strategy registered for provider: " + provider
                            + ". Available: " + strategies.keySet());
        }
        return strategy;
    }
}
