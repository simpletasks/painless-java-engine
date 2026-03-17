package com.javadev.bod.painless.schema;

import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.networknt.schema.SpecificationVersion.DRAFT_2020_12;

public class SchemaValidator {

    private final ValidationProperties props;
    private final ClassLoader classLoader;

    public SchemaValidator(ValidationProperties props) {
        this(props, Thread.currentThread().getContextClassLoader());
    }

    public SchemaValidator(ValidationProperties props, ClassLoader classLoader) {
        this.props = Objects.requireNonNull(props, "props must not be null");
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader must not be null");
    }

    public void validateJson(String jsonPayload) {
        if (jsonPayload == null) {
            throw new IllegalArgumentException("jsonPayload must not be null");
        }

        String schemaPath = props.getInputSchema();
        if (schemaPath == null || schemaPath.isBlank()) {
            throw new IllegalStateException("Missing config property: app.validation.input-schema");
        }

        validate(jsonPayload, schemaPath);
    }

    private void validate(String jsonPayload, String schemaPath) {
        try {
            String normalizedPath = normalizeClasspathLocation(schemaPath);

            String schemaContent;
            try (InputStream is = classLoader.getResourceAsStream(normalizedPath)) {
                if (is == null) {
                    throw new IllegalArgumentException("Schema resource does not exist on classpath: " + schemaPath);
                }
                schemaContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            SchemaRegistry registry = SchemaRegistry.withDefaultDialect(DRAFT_2020_12);

            InputFormat schemaFormat = detectSchemaFormat(normalizedPath);

            Schema schema = registry.getSchema(schemaContent, schemaFormat);
            List<Error> errors = schema.validate(jsonPayload, InputFormat.JSON);

            if (!errors.isEmpty()) {
                String msg = errors.stream()
                        .map(Error::toString)
                        .sorted()
                        .collect(Collectors.joining("\n- ", "Schema validation failed:\n- ", ""));
                throw new IllegalArgumentException(msg);
            }

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed validating JSON against schema from classpath: " + schemaPath, e);
        }
    }

    private static String normalizeClasspathLocation(String path) {
        String p = path.trim();

        if (p.startsWith("classpath:")) {
            p = p.substring("classpath:".length());
        }

        while (p.startsWith("/")) {
            p = p.substring(1);
        }

        return p;
    }

    private static InputFormat detectSchemaFormat(String path) {
        String lower = path.toLowerCase();

        if (lower.endsWith(".yaml") || lower.endsWith(".yml")) {
            return InputFormat.YAML;
        }
        if (lower.endsWith(".json")) {
            return InputFormat.JSON;
        }

        throw new IllegalArgumentException("Unsupported schema file extension for path: " + path + ". Supported: .yaml, .yml, .json");
    }
}