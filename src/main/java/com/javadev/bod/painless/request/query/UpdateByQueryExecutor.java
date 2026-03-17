package com.javadev.bod.painless.request.query;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.UpdateByQueryRequest;
import com.javadev.bod.painless.request.script.ScriptFunctions;
import com.javadev.bod.painless.request.script.ast.commands.Command;
import com.javadev.bod.painless.request.script.ast.parser.AstToRuntimeCompiler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpdateByQueryExecutor {

    private final QueryAstToRuntimeCompiler queryCompiler;
    private final AstToRuntimeCompiler scriptCompiler;
    private final ScriptFunctions scriptFunctions;

    public UpdateByQueryExecutor(QueryAstToRuntimeCompiler queryCompiler, AstToRuntimeCompiler scriptCompiler, ScriptFunctions scriptFunctions) {
        this.queryCompiler = queryCompiler;
        this.scriptCompiler = scriptCompiler;
        this.scriptFunctions = scriptFunctions;
    }

    public List<Map<String, Object>> execute(UpdateByQueryRequest request, List<Map<String, Object>> documents) {
        QueryPredicate queryPredicate = queryCompiler.compile(request.getQuery());
        Command scriptCommand = scriptCompiler.compile(request.getScript());

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> document : documents) {
            QueryEvaluationContext queryContext = new QueryEvaluationContext(document);

            if (queryPredicate.matches(queryContext)) {
                ExecutionContext executionContext = new ExecutionContext(document, scriptFunctions);
                if (request.getParams() != null && !request.getParams().isEmpty()) {
                    executionContext.setVariable("params", new LinkedHashMap<>(request.getParams()));
                }
                scriptCommand.execute(executionContext);
            }

            result.add(document);
        }

        return result;
    }
}
