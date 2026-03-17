package com.javadev.bod.painless;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javadev.bod.painless.request.QueryMapParser;
import com.javadev.bod.painless.request.ScriptNode;
import com.javadev.bod.painless.request.UpdateByQueryRequest;
import com.javadev.bod.painless.request.query.QueryAstToRuntimeCompiler;
import com.javadev.bod.painless.request.query.UpdateByQueryExecutor;
import com.javadev.bod.painless.request.query.ast.QueryNode;
import com.javadev.bod.painless.request.script.ScriptFunctions;
import com.javadev.bod.painless.request.script.ast.parser.AstToRuntimeCompiler;
import com.javadev.bod.painless.request.script.ast.parser.PainlessSubsetAstParser;
import com.javadev.bod.painless.request.script.loader.LocalPainlessCompiler;
import com.javadev.bod.painless.request.script.loader.RuleDefinition;
import com.javadev.bod.painless.request.script.loader.YamlRuleLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UpdateByQueryExecutorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private YamlRuleLoader yamlRuleLoader;
    private QueryAstToRuntimeCompiler queryCompiler;
    private AstToRuntimeCompiler scriptCompiler;
    private ScriptFunctions scriptFunctions;
    private UpdateByQueryExecutor updateByQueryExecutor;
    private final LocalPainlessCompiler compiler = new LocalPainlessCompiler(new AstToRuntimeCompiler());

    @BeforeEach
    void setUp() {
        this.yamlRuleLoader = new YamlRuleLoader();
        this.queryCompiler = new QueryAstToRuntimeCompiler();
        this.scriptCompiler = new AstToRuntimeCompiler();
        this.scriptFunctions = new ScriptFunctions();
        this.updateByQueryExecutor = new UpdateByQueryExecutor(
                queryCompiler,
                scriptCompiler,
                scriptFunctions
        );
    }

    @Test
    void shouldExecuteScriptOnlyForDocumentsMatchingUpdateByQueryCondition() throws Exception {

        // given
        RuleDefinition rule =
                yamlRuleLoader.load("scripts/resolve-some.yml");

        assertNotNull(rule);
        assertNotNull(rule.getQuery());
        assertNotNull(rule.getScript());

        QueryNode queryNode = QueryMapParser.parse(rule.getQuery());
        ScriptNode scriptNode = compiler.parseToAst(rule.getScript().getSource());

        UpdateByQueryRequest request = new UpdateByQueryRequest(queryNode, scriptNode, rule.getScript().getParams());

        TestDocument matchingDoc = buildIndexedDocument();

        TestDocument nonMatchingDoc = buildIndexedDocument();
        // no-match condition
        nonMatchingDoc.getSome().setResolved(true);
        nonMatchingDoc.setStatus("DONE");

        Map<String, Object> matchingDocMap = objectMapper.convertValue(
                matchingDoc,
                new TypeReference<LinkedHashMap<String, Object>>() {
                }
        );

        Map<String, Object> nonMatchingDocMap = objectMapper.convertValue(
                nonMatchingDoc,
                new TypeReference<LinkedHashMap<String, Object>>() {
                }
        );

        List<Map<String, Object>> documents = List.of(matchingDocMap, nonMatchingDocMap);

        // when
        List<Map<String, Object>> actualDocuments = updateByQueryExecutor.execute(request, documents);

        // then
        assertNotNull(actualDocuments);
        assertEquals(2, actualDocuments.size());

        Map<String, Object> actualMatchingDoc = actualDocuments.get(0);
        Map<String, Object> actualNonMatchingDoc = actualDocuments.get(1);

        Map<String, Object> expectedMatchingDoc = loadExpectedJson("expected/resolve-some-expected.json");

        Map<String, Object> expectedNonMatchingDoc = objectMapper.convertValue(
                nonMatchingDoc,
                new TypeReference<LinkedHashMap<String, Object>>() {
                }
        );

        assertEquals(expectedMatchingDoc, actualMatchingDoc);
        assertEquals(expectedNonMatchingDoc, actualNonMatchingDoc);
    }

    @Test
    void shouldExecuteUpdateByQueryWhenQueryMatches() throws Exception {
        RuleDefinition rule =
                yamlRuleLoader.load("update-by-query/scripts/resolve-some-update-by-query-match.yml");

        QueryNode queryNode = QueryMapParser.parse(rule.getQuery());
        ScriptNode scriptNode = compiler.parseToAst(rule.getScript().getSource());

        UpdateByQueryRequest request = new UpdateByQueryRequest(queryNode, scriptNode, rule.getScript().getParams());

        TestDocument actualIndexedDoc = buildIndexedDocument();

        Map<String, Object> actualIndexedDocMap = objectMapper.convertValue(
                actualIndexedDoc,
                new TypeReference<LinkedHashMap<String, Object>>() {
                }
        );

        List<Map<String, Object>> actualDocuments =
                updateByQueryExecutor.execute(request, List.of(actualIndexedDocMap));

        Map<String, Object> actualMap = actualDocuments.get(0);
        Map<String, Object> expectedMap =
                loadExpectedJson("update-by-query/expected/resolve-some-update-by-query-match-expected.json");

        assertEquals(expectedMap, actualMap);
    }

    @Test
    void shouldNotExecuteUpdateByQueryWhenQueryDoesNotMatch() throws Exception {
        RuleDefinition rule =
                yamlRuleLoader.load("update-by-query/scripts/resolve-some-update-by-query-no-match.yml");

        QueryNode queryNode = QueryMapParser.parse(rule.getQuery());
        ScriptNode scriptNode = compiler.parseToAst(rule.getScript().getSource());

        UpdateByQueryRequest request = new UpdateByQueryRequest(queryNode, scriptNode, rule.getScript().getParams());

        TestDocument actualIndexedDoc = buildIndexedDocument();

        Map<String, Object> actualIndexedDocMap = objectMapper.convertValue(
                actualIndexedDoc,
                new TypeReference<LinkedHashMap<String, Object>>() {
                }
        );

        List<Map<String, Object>> actualDocuments =
                updateByQueryExecutor.execute(request, List.of(actualIndexedDocMap));

        Map<String, Object> actualMap = actualDocuments.get(0);
        Map<String, Object> expectedMap =
                loadExpectedJson("update-by-query/expected/resolve-some-update-by-query-no-match-expected.json");

        assertEquals(expectedMap, actualMap);
    }


    private TestDocument buildIndexedDocument() {
        TestDocument doc = new TestDocument();
        doc.setStatus("NEW");
        doc.setResolution(null);

        Meta meta = new Meta();
        meta.setTimestamp("2024-01-15T10:15:30Z");
        doc.setMeta(meta);

        Payload some = new Payload();
        some.setId("obj-123");
        some.setResolved(false);
        doc.setSome(some);

        return doc;
    }

    private Map<String, Object> loadExpectedJson(String path) throws Exception {
        return objectMapper.readValue(
                getClass().getClassLoader().getResourceAsStream(path),
                new TypeReference<LinkedHashMap<String, Object>>() {
                }
        );
    }
}
