package com.thecout.lox.Parser.Expr;

import com.thecout.lox.Token;

public class Assign extends Expr {
    public Assign(Token name, Expr value) {
        this.name = name;
        this.value = value;
    }

    public final Token name;
    public final Expr value;

    @Override
    public String print() {
        return "(= %s %s)".formatted(name.lexeme, value.print());
    }

    @Override
    public <R> R accept(ExprVisitor<R> exprVisitor) {
        return exprVisitor.visitAssignExpr(this);
    }
}
