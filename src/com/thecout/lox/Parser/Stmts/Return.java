package com.thecout.lox.Parser.Stmts;

import com.thecout.lox.Parser.Expr.Expr;

public class Return extends Stmt {
    public Return( Expr value) {
        this.value = value;
    }

    public final Expr value;

    @Override
    public String print() {
        return "(return %s)".formatted(value.print());
    }

    @Override
    public <R> R accept(StmtVisitor<R> stmtVisitor) {
        return stmtVisitor.visitReturnStmt(this);
    }
}
