package com.javadev.bod.painless.request.script.ast.nodes.literal;

import com.javadev.bod.painless.request.script.ast.AstVisitor;

public class NullLiteralNode extends LiteralExpressionNode {

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitNullLiteral(this);
    }

    @Override
    public String toString() {
        return "NullLiteralNode{}";
    }
}
