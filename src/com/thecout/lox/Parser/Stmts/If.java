package com.thecout.lox.Parser.Stmts;


import com.thecout.lox.Parser.Expr.Expr;

public class If extends Stmt {
    public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }


    public final Expr condition;
    public final Stmt thenBranch;
    public final Stmt elseBranch;

    @Override
    public String print() {
        return "(if %s %s %s)".formatted(condition.print(), thenBranch.print(), elseBranch.print());
    }

    @Override
    public <R> R accept(StmtVisitor<R> stmtVisitor) {
        return stmtVisitor.visitIfStmt(this);
    }
}
