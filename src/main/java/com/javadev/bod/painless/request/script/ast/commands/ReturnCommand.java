package com.javadev.bod.painless.request.script.ast.commands;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.loader.ScriptReturnException;

/**
 * Command representing a {@code return;} statement without a return value.
 *
 * <p>When executed, this command terminates the script execution by throwing
 * a {@link ScriptReturnException}. The exception propagates through the
 * interpreter call stack until it is caught by the top-level script executor.</p>
 *
 * <p>This mechanism allows the interpreter to implement non-local control flow
 * without requiring every command in the execution chain to explicitly check
 * for return conditions.</p>
 *
 * <p>The thrown exception does not include a return value, indicating that the
 * script returned without an explicit expression.</p>
 */
public class ReturnCommand implements Command {
    @Override
    public void execute(ExecutionContext ctx) {
        throw new ScriptReturnException();
    }
}
