package com.alirizakaygusuz.gymcrm.seed;


import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility component for reading and deserializing JSON seed files.
 *
 * <p>This class uses Jackson's {@link ObjectMapper} to parse JSON content from
 * specified resource paths into Java objects of the desired type.</p>
 */
@Component
public class JsonSeedReader {

    private final ObjectMapper mapper;
    private final ResourceLoader resourceLoader;

    public JsonSeedReader(ObjectMapper mapper, ResourceLoader resourceLoader) {
        this.mapper = mapper;
        this.resourceLoader = resourceLoader;
    }

    public <T> T read(String resourcePath, TypeReference<T> type) {
        Resource resource = resourceLoader.getResource(resourcePath);
        try (InputStream is = resource.getInputStream()) {
            return mapper.readValue(is, type);
        } catch (IOException e) {
            throw new IllegalStateException("Seed file could not be read: " + resourcePath, e);
        }
    }
}
