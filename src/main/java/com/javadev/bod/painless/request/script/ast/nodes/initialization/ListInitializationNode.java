package com.javadev.bod.painless.request.script.ast.nodes.initialization;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import com.javadev.bod.painless.request.script.ast.nodes.ExpressionNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ListInitializationNode implements ExpressionNode {

    private List<ExpressionNode> elements = new ArrayList<>();

    public ListInitializationNode(List<ExpressionNode> elements) {
        this.elements = elements;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitListInitialization(this);
    }
}
