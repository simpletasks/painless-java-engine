package com.javadev.bod.painless.request.script.loader;

import lombok.Getter;

/**
 * Internal control-flow exception used to implement the {@code return} statement
 * in the interpreter.
 *
 * <p>In a scripting language interpreter, a {@code return} statement must
 * immediately terminate the execution of the current script or function,
 * regardless of how deeply nested the statement is inside blocks,
 * conditionals, or loops.</p>
 *
 * <p>For example:</p>
 *
 * <pre>
 * if (a > 5) {
 *     if (b > 10) {
 *         return 42;
 *     }
 * }
 * </pre>
 *
 * <p>The {@code return} statement above must stop execution not only of the
 * innermost block but of the entire script. Propagating such control flow
 * through every interpreter layer would require every command to explicitly
 * check and propagate return states.</p>
 *
 * <p>Instead, the interpreter uses this exception as a <b>control-flow signal</b>.
 * When a {@code return} statement is executed, this exception is thrown and
 * propagates through the Java call stack until it is caught by the top-level
 * script executor.</p>
 *
 * <p>This approach keeps individual command implementations simple because they
 * do not need to explicitly handle return propagation.</p>
 *
 * <p>This exception is optimized for control-flow usage:</p>
 *
 * <ul>
 *   <li>No message</li>
 *   <li>No cause</li>
 *   <li>No writable stack trace</li>
 *   <li>No stack trace generation</li>
 * </ul>
 *
 * <p>These optimizations avoid the overhead normally associated with exceptions,
 * since this class is used as a lightweight jump mechanism rather than for
 * error reporting.</p>
 *
 * <p>The optional {@code returnValue} carries the value returned by a
 * {@code return expression;} statement.</p>
 */
@Getter
public class ScriptReturnException extends RuntimeException {
    private final Object returnValue;

    public ScriptReturnException() {
        this(null);
    }

    public ScriptReturnException(Object returnValue) {
        super(null, null, false, false);
        this.returnValue = returnValue;
    }
}
