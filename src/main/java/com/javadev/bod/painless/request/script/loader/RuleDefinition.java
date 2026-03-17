package com.javadev.bod.painless.request.script.loader;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RuleDefinition {
    private Map<String, Object> query;
    private ScriptSection script;
}
