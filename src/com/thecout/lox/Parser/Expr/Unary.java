package com.thecout.lox.Parser.Expr;

import com.thecout.lox.Token;

public class Unary extends Expr {
    public Unary(Token operator, Expr right) {
        this.operator = operator;
        this.right = right;
    }


    public final Token operator;
    public final Expr right;

    @Override
    public String print() {
        return "%s%s".formatted(operator.lexeme, right.print());
    }

    @Override
    public <R> R accept(ExprVisitor<R> exprVisitor) {
        return exprVisitor.visitUnaryExpr(this);
    }
}
