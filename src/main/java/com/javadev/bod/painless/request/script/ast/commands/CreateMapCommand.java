package com.javadev.bod.painless.request.script.ast.commands;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.LinkedHashMap;

public class CreateMapCommand implements Command {
    private final String variableName;

    public CreateMapCommand(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public void execute(ExecutionContext ctx) {
        ctx.setVariable(variableName, new LinkedHashMap<>());
    }
}
