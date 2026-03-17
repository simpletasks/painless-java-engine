package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Covers only global function. Not methods on objects.
 *
 * <pre>{@code
 * Painless script:
 * formatUtcNow(formatUtcNow('yyyy'))
 *
 * AST:
 *
 * FunctionCallNode
 * ├─ functionName: "formatUtcNow"
 * └─ arguments:
 *    └─ FunctionCallNode
 *       ├─ functionName: "formatUtcNow"
 *       └─ arguments:
 *          └─ StringLiteralNode("yyyy")
 * }</pre>
 *
 * <pre> {@code
 * Painless script:
 * myFunc(ctx._source['some'])
 *
 * AST:
 *
 * FunctionCallNode
 * ├─ functionName: "myFunc"
 * └─ arguments
 *    └─ IndexAccessNode
 *       ├─ target
 *       │  └─ PathReferenceNode
 *       │     └─ path: ["ctx", "_source"]
 *       └─ index
 *          └─ StringLiteralNode
 *             └─ value: "some"
 * }</pre>
 */
@Getter
public class FunctionCallNode implements ExpressionNode {
    private final String functionName;
    private final List<ExpressionNode> arguments;

    public FunctionCallNode(String functionName, List<ExpressionNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(arguments));
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitFunctionCall(this);
    }

    @Override
    public String toString() {
        return "FunctionCallNode{" +
                "functionName='" + functionName + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
