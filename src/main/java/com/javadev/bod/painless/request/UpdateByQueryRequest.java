package com.javadev.bod.painless.request;

import com.javadev.bod.painless.request.query.ast.QueryNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class UpdateByQueryRequest {

    private final QueryNode query;
    private final ScriptNode script;
    private final Map<String, Object> params;
}
