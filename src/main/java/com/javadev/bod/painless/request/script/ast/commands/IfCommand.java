package com.javadev.bod.painless.request.script.ast.commands;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.conditions.Condition;

import java.util.List;

public class IfCommand implements Command {
    private final Condition condition;
    private final List<Command> thenCommands;
    private final List<Command> elseCommands;

    public IfCommand(Condition condition, List<Command> thenCommands, List<Command> elseCommands) {
        this.condition = condition;
        this.thenCommands = thenCommands;
        this.elseCommands = elseCommands;
    }

    @Override
    public void execute(ExecutionContext ctx) {
        List<Command> branch = condition.test(ctx) ? thenCommands : elseCommands;
        for (Command command : branch) {
            command.execute(ctx);
        }
    }
}
