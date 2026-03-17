package com.javadev.bod.painless;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ScriptFunctions;
import com.javadev.bod.painless.request.script.ast.commands.Command;
import com.javadev.bod.painless.request.script.ast.parser.AstToRuntimeCompiler;
import com.javadev.bod.painless.request.script.loader.LocalPainlessCompiler;
import com.javadev.bod.painless.request.script.loader.LocalScriptExecutor;
import com.javadev.bod.painless.request.script.loader.RuleDefinition;
import com.javadev.bod.painless.request.script.loader.YamlRuleLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class LocalPainlessStateTest {
    private YamlRuleLoader yamlRuleLoader = new YamlRuleLoader();

    private LocalPainlessCompiler compiler = new LocalPainlessCompiler(new AstToRuntimeCompiler());

    private LocalScriptExecutor executor = new LocalScriptExecutor();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldInterpretYamlPainlessAndMatchExpectedState() throws Exception {
        // load painless script
        RuleDefinition painlessScript = yamlRuleLoader.load("scripts/resolve-some.yml");

        assertNotNull(painlessScript);
        assertNotNull(painlessScript.getScript());
        assertEquals("painless", painlessScript.getScript().getLang());

        Command command = compiler.compile(painlessScript.getScript().getSource());
        assertNotNull(command);

        // Indexed document - initial state
        TestDocument actualIndexedDoc = buildIndexedDocument();

        Map<String, Object> actualIndexedDocMap = objectMapper.convertValue(actualIndexedDoc, new TypeReference<LinkedHashMap<String, Object>>() {
        });

        // when
        ExecutionContext executionContext = new ExecutionContext(actualIndexedDocMap, new ScriptFunctions());

        executor.execute(command, executionContext);

        // then
        Map<String, Object> actualMap = actualIndexedDocMap;
        Map<String, Object> expectedMap = loadExpectedJson("expected/resolve-some-expected.json");

        assertEquals(expectedMap, actualMap);
        log.info("Interpret Yaml Painless ");
    }

    private TestDocument buildIndexedDocument() {
        Meta meta = new Meta();
        meta.setTimestamp("2024-01-15T10:15:30Z");

        Payload payload = new Payload();
        payload.setId("obj-123");
        payload.setResolved(false);

        TestDocument doc = new TestDocument();
        doc.setMeta(meta);
        doc.setSome(payload);
        doc.setResolution(null);
        doc.setStatus(null);

        return doc;
    }

    private Map<String, Object> loadExpectedJson(String classpathLocation) throws Exception {
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(classpathLocation)) {

            if (is == null) {
                throw new IllegalStateException("Expected JSON not found: " + classpathLocation);
            }

            return objectMapper.readValue(is, new TypeReference<LinkedHashMap<String, Object>>() {
            });
        }
    }
}
