package com.javadev.bod.painless.request.script.ast.nodes.initialization;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import com.javadev.bod.painless.request.script.ast.nodes.AstNode;
import com.javadev.bod.painless.request.script.ast.nodes.ExpressionNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Helper structure. Not real AST node.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapEntryNode implements AstNode {

    private ExpressionNode key;
    private ExpressionNode value;

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitMapEntry(this);
    }

}