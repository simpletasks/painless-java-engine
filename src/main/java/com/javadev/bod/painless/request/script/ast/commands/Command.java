package com.javadev.bod.painless.request.script.ast.commands;

import com.javadev.bod.painless.request.ExecutionContext;

public interface Command {

    void execute(ExecutionContext ctx);
}
