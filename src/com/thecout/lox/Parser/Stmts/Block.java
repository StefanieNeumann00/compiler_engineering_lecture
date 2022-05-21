package com.thecout.lox.Parser.Stmts;

import java.util.List;
import java.util.stream.Collectors;

public class Block extends Stmt {
    public Block(List<Stmt> statements) {
        this.statements = statements;
    }

    public final List<Stmt> statements;

    @Override
    public String print() {
        return "(%s)".formatted(statements.stream().map(Stmt::print).collect(Collectors.joining("\n")));
    }

    @Override
    public <R> R accept(StmtVisitor<R> stmtVisitor) {
        return stmtVisitor.visitBlockStmt(this);
    }
}
