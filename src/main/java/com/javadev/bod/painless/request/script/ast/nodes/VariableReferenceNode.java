package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class VariableReferenceNode implements ExpressionNode {
    private final String variableName;

    public VariableReferenceNode(String variableName) {
        this.variableName = variableName;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitVariableReference(this);
    }

    @Override
    public String toString() {
        return "VariableReferenceNode{" +
                "variableName='" + variableName + '\'' +
                '}';
    }
}
