package com.thecout.lox.Parser.Expr;

import com.thecout.lox.Token;

public class Logical extends Expr {
    public Logical(Expr left, Token operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


    public final Expr left;
    public final Token operator;
    public final Expr right;

    @Override
    public String print() {
        return "(%s %s %s)".formatted(operator.lexeme, left.print(), right.print());
    }

    @Override
    public <R> R accept(ExprVisitor<R> exprVisitor) {
        return exprVisitor.visitLogicalExpr(this);
    }
}
