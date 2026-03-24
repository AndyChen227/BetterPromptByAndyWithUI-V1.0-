package com.promptopt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Loads simple rule lists from JSON files in src/main/resources.
 *
 * This keeps editable text data out of Java code so strategy classes stay small.
 */
public class RuleLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Loads a JSON array of strings from the classpath.
     *
     * Example file shape:
     * ["phrase one", "phrase two"]
     */
    public List<String> loadStringList(String resourcePath) {
        ClassPathResource resource = new ClassPathResource(resourcePath);

        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load rules from: " + resourcePath, e);
        }
    }

    /**
     * Loads a JSON object into a simple Java class.
     *
     * This is useful when rule files need more structure than a plain string list.
     */
    public <T> T loadJson(String resourcePath, Class<T> type) {
        ClassPathResource resource = new ClassPathResource(resourcePath);

        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, type);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load rules from: " + resourcePath, e);
        }
    }
}
