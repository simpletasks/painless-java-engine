package com.javadev.bod.painless.request.script.ast.commands;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.List;

public class BlockCommand implements Command {

    private final List<Command> commands;

    public BlockCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(ExecutionContext ctx) {
        for (Command command : commands) {
            command.execute(ctx);
        }
    }
}
