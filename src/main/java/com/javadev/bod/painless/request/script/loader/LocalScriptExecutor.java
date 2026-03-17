package com.javadev.bod.painless.request.script.loader;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.commands.Command;

public class LocalScriptExecutor {
    public void execute(Command command, ExecutionContext ctx) {
        try {
            command.execute(ctx);
        } catch (ScriptReturnException ignored) {
            // normal return
        }
    }

    // no usages
    public Object executeAndReturn(Command command, ExecutionContext ctx) {
        try {
            command.execute(ctx);
            return null;
        } catch (ScriptReturnException e) {
            // return with value
            return e.getReturnValue();
        }
    }
}
