package com.javadev.bod.painless.request.script.ast;

import com.javadev.bod.painless.request.ScriptNode;
import com.javadev.bod.painless.request.script.ast.commands.Command;
import com.javadev.bod.painless.request.script.ast.nodes.*;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.CastNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.ListInitializationNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.MapEntryNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.MapInitializationNode;
import com.javadev.bod.painless.request.script.ast.nodes.literal.*;

/**
 * FOr printing, validating AST. COnverting AST into runtime {@link Command Command} tree
 *
 * @param <T>
 */
public interface AstVisitor<T> {
    T visitScript(ScriptNode node);

    T visitBlockStatement(BlockStatementNode node);
    T visitIfStatement(IfStatementNode node);
    T visitReturnStatement(ReturnStatementNode node);
    T visitAssignmentStatement(AssignmentStatementNode node);
    T visitCompoundAssignmentStatement(CompoundAssignmentNode node);
    T visitVariableDeclaration(VariableDeclarationNode node);
    T visitExpressionStatement(ExpressionStatementNode node);

    T visitVariableTarget(VariableTargetNode node);
    T visitPathTarget(PathTargetNode node);
    T visitIndexTarget(IndexTargetNode node);

    T visitNullLiteral(NullLiteralNode node);
    T visitBooleanLiteral(BooleanLiteralNode node);
    T visitStringLiteral(StringLiteralNode node);
    T visitNumericalLiteral(NumericLiteralNode node);
    T visitLiteral(LiteralExpressionNode node);

    T visitIntegerLiteral(IntegerLiteralNode node);
    T visitDecimalLiteral(DecimalLiteralNode node);
    T visitEmptyMapLiteral(EmptyMapLiteralNode node);

    T visitVariableReference(VariableReferenceNode node);
    T visitPathReference(PathReferenceNode node);
    T visitIndexAccess(IndexAccessNode node);
    T visitPropertyAccess(PropertyAccessNode node);

    T visitFunctionCall(FunctionCallNode node);
    T visitMethodCall(MethodCallNode node);

    T visitBinaryExpression(BinaryExpressionNode node);
    T visitUnaryExpression(UnaryExpressionNode node);
    T visitCast(CastNode node);
    T visitInstanceOf(InstanceOfNode node);
    T visitUpdateExpression(UpdateExpressionNode node);

    T visitListInitialization(ListInitializationNode node);
    T visitMapInitialization(MapInitializationNode node);
    T visitMapEntry(MapEntryNode node);
}
