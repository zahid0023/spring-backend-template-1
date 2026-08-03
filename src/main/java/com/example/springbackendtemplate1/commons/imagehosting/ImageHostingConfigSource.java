package com.example.springbackendtemplate1.commons.imagehosting;

import java.util.Map;

public interface ImageHostingConfigSource {

    String getProviderCode();

    Map<String, Object> getConfig();
}
