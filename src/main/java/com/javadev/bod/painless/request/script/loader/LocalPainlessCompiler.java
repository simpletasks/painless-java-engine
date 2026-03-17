package com.javadev.bod.painless.request.script.loader;


import com.javadev.bod.painless.request.script.ast.AstPrinterVisitor;
import com.javadev.bod.painless.request.script.ast.commands.Command;
import com.javadev.bod.painless.request.ScriptNode;
import com.javadev.bod.painless.request.script.ast.parser.AstToRuntimeCompiler;
import com.javadev.bod.painless.request.script.ast.parser.PainlessSubsetAstParser;
import com.javadev.bod.painless.request.script.ast.tokenizer.Tokenizer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalPainlessCompiler {
    private final Tokenizer tokenizer = new Tokenizer();
    private final AstToRuntimeCompiler astToRuntimeCompiler;

    public LocalPainlessCompiler(AstToRuntimeCompiler astToRuntimeCompiler) {
        this.astToRuntimeCompiler = astToRuntimeCompiler;
    }

    public Command compile(String script) {
        ScriptNode ast = parseToAst(script);
        String dump = ast.accept(new AstPrinterVisitor());
        log.info(dump);
        return astToRuntimeCompiler.compile(ast);
    }

    public ScriptNode parseToAst(String script) {
        return new PainlessSubsetAstParser(tokenizer.tokenize(script)).parseScript();
    }
}
