package com.javadev.bod.painless;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ScriptFunctions;
import com.javadev.bod.painless.request.script.ast.commands.Command;
import com.javadev.bod.painless.request.script.ast.parser.AstToRuntimeCompiler;
import com.javadev.bod.painless.request.script.loader.LocalPainlessCompiler;
import com.javadev.bod.painless.request.script.loader.RuleDefinition;
import com.javadev.bod.painless.request.script.loader.YamlRuleLoader;
import com.javadev.bod.painless.schema.SchemaValidator;
import com.javadev.bod.painless.schema.ValidationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies the end-to-end execution flow for an update-by-query rule:
 *
 * <ol>
 *     <li>Loads an update-by-query YAML definition from test resources.</li>
 *     <li>Extracts and compiles the embedded Painless script.</li>
 *     <li>Validates the input JSON document against the configured JSON Schema.</li>
 *     <li>Executes the compiled script against the input document.</li>
 *     <li>Validates the mutated document again against the same JSON Schema.</li>
 *     <li>Compares the actual mutated document with the expected JSON document.</li>
 * </ol>
 *
 * <p>This test is intentionally focused on script execution and schema safety.
 * Query matching is assumed to be covered by separate update-by-query executor tests.</p>
 */
class UpdateByQueryScriptSchemaIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private YamlRuleLoader yamlRuleLoader;
    private ScriptFunctions scriptFunctions;
    private LocalPainlessCompiler compiler;
    private SchemaValidator schemaValidator;

    /**
     * Initializes test dependencies before each test execution.
     */
    @BeforeEach
    void setUp() {
        this.yamlRuleLoader = new YamlRuleLoader();
        this.scriptFunctions = new ScriptFunctions();
        this.compiler = new LocalPainlessCompiler(new AstToRuntimeCompiler());

        ValidationProperties props = new ValidationProperties();
        props.setInputSchema("schemas/document-index.schema.yaml");

        this.schemaValidator = new SchemaValidator(props);
    }

    /**
     * Verifies script execution for the fully populated valid input document.
     *
     * @throws Exception if resource loading, JSON parsing, compilation, or execution fails
     */
    @Test
    void shouldValidateCompileExecuteAndCompareFullDocument() throws Exception {
        assertScriptExecutionScenario(
                "update-by-query/scripts/script-1.yml",
                "update-by-query/input/sample-1.json",
                "update-by-query/expected/sample-1.json"
        );
    }

    /**
     * Verifies script execution for the minimal valid input document.
     *
     * <p>This scenario is especially important because it confirms that the interpreter
     * correctly creates missing nested objects during path assignment when needed.</p>
     *
     * @throws Exception if resource loading, JSON parsing, compilation, or execution fails
     */
    @Test
    void shouldValidateCompileExecuteAndCompareMinimalDocument() throws Exception {
        assertScriptExecutionScenario(
                "update-by-query/scripts/script-1.yml",
                "update-by-query/input/sample-required-minimal.json",
                "update-by-query/expected/sample-required-minimal.json"
        );
    }

    /**
     * Executes the complete validation and script-application flow for one scenario.
     *
     * @param rulePath         classpath path to the update-by-query YAML rule
     * @param inputJsonPath    classpath path to the input JSON document
     * @param expectedJsonPath classpath path to the expected JSON document
     * @throws Exception if any part of the scenario fails
     */
    private void assertScriptExecutionScenario(
            String rulePath,
            String inputJsonPath,
            String expectedJsonPath
    ) throws Exception {

        RuleDefinition rule = yamlRuleLoader.load(rulePath);

        assertNotNull(rule, "Rule must not be null");
        assertNotNull(rule.getScript(), "Rule script must not be null");
        assertNotNull(rule.getScript().getSource(), "Rule script source must not be null");
        assertFalse(rule.getScript().getSource().isBlank(), "Rule script source must not be blank");

        String inputJson = readClasspathResourceAsString(inputJsonPath);

        schemaValidator.validateJson(inputJson);

        Map<String, Object> actualDocument = objectMapper.readValue(
                inputJson,
                new TypeReference<LinkedHashMap<String, Object>>() {}
        );

        Command command = compiler.compile(rule.getScript().getSource());

        ExecutionContext context = new ExecutionContext(actualDocument, scriptFunctions);
        if (rule.getScript().getParams() != null) {
            context.setVariable("params", new LinkedHashMap<>(rule.getScript().getParams()));
        }
        command.execute(context);

        String actualJson = objectMapper.writeValueAsString(actualDocument);

        schemaValidator.validateJson(actualJson);

        Map<String, Object> expectedDocument = loadJsonAsMap(expectedJsonPath);

        JsonNode expectedNode = objectMapper.readTree(
                objectMapper.writeValueAsString(loadJsonAsMap(expectedJsonPath))
        );

        JsonNode actualNode = objectMapper.readTree(
                objectMapper.writeValueAsString(actualDocument)
        );

        assertEquals(
                expectedNode,
                actualNode,
                () -> {
                    try {
                        return """
                        Actual document does not match expected document.
        
                        Expected:
                        %s
        
                        Actual:
                        %s
                        """.formatted(
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedNode),
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualNode)
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    /**
     * Reads a UTF-8 text resource from the classpath.
     *
     * @param path classpath-relative resource path
     * @return resource contents as a string
     */
    private String readClasspathResourceAsString(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Classpath resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read classpath resource: " + path, e);
        }
    }

    /**
     * Loads a JSON resource from the classpath into a mutable map representation.
     *
     * @param path classpath-relative JSON resource path
     * @return parsed JSON document as a linked hash map
     * @throws Exception if the resource cannot be loaded or parsed
     */
    private Map<String, Object> loadJsonAsMap(String path) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Classpath resource not found: " + path);
            }
            return objectMapper.readValue(
                    is,
                    new TypeReference<LinkedHashMap<String, Object>>() {}
            );
        }
    }
}