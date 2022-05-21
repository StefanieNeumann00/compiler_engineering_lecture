package com.thecout.lox.Parser.Stmts;

import com.thecout.lox.Parser.Expr.Expr;

public class Print extends Stmt {
    public Print(Expr expression) {
        this.expression = expression;
    }


    public final Expr expression;

    @Override
    public String print() {
        return "(print %s)".formatted(expression.print());
    }

    @Override
    public <R> R accept(StmtVisitor<R> stmtVisitor) {
        return stmtVisitor.visitPrintStmt(this);
    }
}
