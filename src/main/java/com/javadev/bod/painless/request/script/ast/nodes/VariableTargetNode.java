package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class VariableTargetNode implements AssignableNode {
    private final String variableName;

    public VariableTargetNode(String variableName) {
        this.variableName = variableName;
    }


    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitVariableTarget(this);
    }
    @Override
    public String toString() {
        return "VariableTargetNode{" +
                "variableName='" + variableName + '\'' +
                '}';
    }
}
