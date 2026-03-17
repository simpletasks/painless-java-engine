package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a method call on a target expression.
 *
 * <p>Example Painless source:</p>
 *
 * <pre>{@code
 * obj.containsKey('id')
 * }</pre>
 *
 * <p>AST:</p>
 *
 * <pre>{@code
 * MethodCallNode
 * ├─ target
 * │  └─ VariableReferenceNode
 * │     └─ variableName: "obj"
 * ├─ methodName: "containsKey"
 * └─ arguments
 *    └─ StringLiteralNode
 *       └─ value: "id"
 * }</pre>
 */
@Getter
public class MethodCallNode implements ExpressionNode {

    private final ExpressionNode target;
    private final String methodName;
    private final List<ExpressionNode> arguments;

    public MethodCallNode(ExpressionNode target, String methodName, List<ExpressionNode> arguments) {
        this.target = target;
        this.methodName = methodName;
        this.arguments = arguments == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(arguments));
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitMethodCall(this);
    }

    @Override
    public String toString() {
        return "MethodCallNode{" +
                "target=" + target +
                ", methodName='" + methodName + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
