package com.javadev.bod.painless.request.script.ast.nodes.initialization;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import com.javadev.bod.painless.request.script.ast.nodes.ExpressionNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapInitializationNode implements ExpressionNode {

    private List<MapEntryNode> entries = new ArrayList<>();

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitMapInitialization(this);
    }
}
