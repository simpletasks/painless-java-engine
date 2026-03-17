package com.javadev.bod.painless.request.script.loader;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ScriptSection {
    private String lang;
    private String source;
    private Map<String, Object> params;

}
