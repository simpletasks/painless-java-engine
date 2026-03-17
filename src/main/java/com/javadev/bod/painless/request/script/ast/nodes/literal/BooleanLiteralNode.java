package com.javadev.bod.painless.request.script.ast.nodes.literal;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import com.javadev.bod.painless.request.script.ast.nodes.ExpressionNode;

public class BooleanLiteralNode implements ExpressionNode {

    private final boolean value;

    public BooleanLiteralNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitBooleanLiteral(this);
    }
}