package com.javadev.bod.painless.request.script.ast.commands;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.PathRef;
import com.javadev.bod.painless.request.script.ast.expressions.Expression;

public class AssignPathCommand implements Command {

    private final PathRef target;
    private final Expression expression;

    public AssignPathCommand(PathRef target, Expression expression) {
        this.target = target;
        this.expression = expression;
    }

    @Override
    public void execute(ExecutionContext ctx) {
        Object value = expression.evaluate(ctx);
        ctx.setPathValue(target, value);
    }
}
