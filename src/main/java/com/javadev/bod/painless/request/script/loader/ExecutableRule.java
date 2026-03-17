package com.javadev.bod.painless.request.script.loader;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.commands.Command;
import com.javadev.bod.painless.request.script.ast.conditions.Condition;

public class ExecutableRule {
    private final Condition filter;
    private final Command command;

    public ExecutableRule(Condition filter, Command command) {
        this.filter = filter;
        this.command = command;
    }

    public void execute(ExecutionContext ctx) {
        if (filter.test(ctx)) {
            command.execute(ctx);
        }
    }
}
