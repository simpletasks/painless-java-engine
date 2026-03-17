package com.javadev.bod.painless.request.script.ast.commands;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.expressions.Expression;
import com.javadev.bod.painless.request.script.loader.ScriptReturnException;
import lombok.RequiredArgsConstructor;

/**
 * Command representing a {@code return expression;} statement.
 *
 * <p>This command evaluates the provided expression and terminates script
 * execution by throwing a {@link ScriptReturnException} containing the
 * evaluated return value.</p>
 *
 * <p>The exception acts as a control-flow mechanism that unwinds the interpreter
 * execution stack until the script executor catches it and extracts the
 * returned value.</p>
 *
 * <p>Using an exception for this purpose avoids the need for explicit return
 * propagation through every command implementation in the interpreter.</p>
 */
@RequiredArgsConstructor
public class ReturnWithValueCommand implements Command {

    private final Expression expression;

    @Override
    public void execute(ExecutionContext ctx) {
        Object value = expression.evaluate(ctx);
        throw new ScriptReturnException(value);
    }
}
