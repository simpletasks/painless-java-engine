package com.javadev.bod.painless.request.script.ast.commands;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.expressions.Expression;

public class AssignVariableCommand implements Command {
    private final String variableName;
    private final Expression expression;

    public AssignVariableCommand(String variableName, Expression expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    @Override
    public void execute(ExecutionContext ctx) {
        ctx.setVariable(variableName, expression.evaluate(ctx));
    }
}
