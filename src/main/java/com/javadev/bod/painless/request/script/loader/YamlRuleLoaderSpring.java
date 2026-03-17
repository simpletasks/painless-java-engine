package com.javadev.bod.painless.request.script.loader;

//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Component;


//@Component
public class YamlRuleLoaderSpring {
    @SuppressWarnings("unchecked")
    public RuleDefinition load(String classpathLocation) {
//        try (InputStream is = new ClassPathResource(classpathLocation).getInputStream()) {
//            Yaml yaml = new Yaml();
//            Map<String, Object> root = yaml.load(is);
//
//            RuleDefinition def = new RuleDefinition();
//            def.setQuery((Map<String, Object>) root.get("query"));
//
//            Map<String, Object> scriptMap = (Map<String, Object>) root.get("script");
//            if (scriptMap == null) {
//                throw new IllegalStateException("Missing script section");
//            }
//
//            ScriptSection script = new ScriptSection();
//            script.setLang((String) scriptMap.get("lang"));
//            script.setSource((String) scriptMap.get("source"));
//            def.setScript(script);
//
//            return def;
//        } catch (Exception e) {
//            throw new IllegalStateException("Cannot load YAML rule: " + classpathLocation, e);
//        }
        return null;
    }
}
