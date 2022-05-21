package com.thecout.lox.Parser.Expr;

import com.thecout.lox.Token;

import java.util.List;
import java.util.stream.Collectors;

public class Call extends Expr {
    public Call(Expr callee, List<Expr> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }


    public final Expr callee;
    public final List<Expr> arguments;

    @Override
    public String print() {
        String args = arguments.stream().map(Expr::print).collect(Collectors.joining(" "));
        return "(%s %s)".formatted(callee.print(), args);
    }

    @Override
    public <R> R accept(ExprVisitor<R> exprVisitor) {
        return exprVisitor.visitCallExpr(this);
    }
}
