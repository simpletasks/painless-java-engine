package com.javadev.bod.painless.request.script.loader;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class YamlRuleLoader {
    @SuppressWarnings("unchecked")
    public RuleDefinition load(String classpathLocation) {

        try (InputStream is = classpath(classpathLocation)) {

            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + classpathLocation);
            }

            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(is);

            RuleDefinition def = new RuleDefinition();
            def.setQuery((Map<String, Object>) root.get("query"));

            Map<String, Object> scriptMap = (Map<String, Object>) root.get("script");
            if (scriptMap == null) {
                throw new IllegalStateException("Missing script section");
            }

            ScriptSection script = new ScriptSection();
            script.setLang((String) scriptMap.get("lang"));
            script.setSource((String) scriptMap.get("source"));
            script.setParams((Map<String, Object>) scriptMap.get("params"));

            def.setScript(script);

            return def;

        } catch (Exception e) {
            throw new IllegalStateException("Cannot load YAML rule: " + classpathLocation, e);
        }
    }

    public static InputStream classpath(String path) {
        InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);

        if (is == null) {
            throw new IllegalArgumentException("Classpath resource not found: " + path);
        }

        return is;
    }
}
