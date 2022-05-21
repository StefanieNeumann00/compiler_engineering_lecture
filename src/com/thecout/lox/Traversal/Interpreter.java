package com.thecout.lox.Traversal;


import com.thecout.lox.Parser.Expr.*;
import com.thecout.lox.Parser.Stmts.*;
import com.thecout.lox.Traversal.InterpreterUtils.*;

import java.util.ArrayList;
import java.util.List;

public class Interpreter implements ExprVisitor<Object>,
        StmtVisitor<Void> {

    public final Environment globals = new Environment();
    private Environment environment = globals;




    public Interpreter() {
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            error.printStackTrace();
        }
    }

    public void executeBlock(List<Stmt> statements,
                             Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    public void execute(Stmt stmt) {
        stmt.accept(this);
    }


    @Override
    public Object visitAssignExpr(Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return expr.value;
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        if(left instanceof Literal) left = ((Literal) left).value;
        if(right instanceof Literal) right = ((Literal) right).value;

        return switch (expr.operator.type){
            case STAR -> ((Double) left) * ((Double) right);
            case SLASH -> ((Double) left) / ((Double) right);
            case PLUS -> ((Double) left) + ((Double) right);
            case MINUS -> ((Double) left) - ((Double) right);
            default -> throw new IllegalStateException("Unexpected value: " + expr.operator.type);
        };
    }

    @Override
    public Object visitCallExpr(Call expr) {
        Object callee  = evaluate(expr.callee);
        if (callee instanceof LoxCallable) {
            var args = new ArrayList<Object>(expr.arguments);
            return ((LoxCallable) callee).call(this, args);
        }
        throw new RuntimeError(((Variable) expr.callee).name, "Can only call expressions of type function.");
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        if(left instanceof Literal) left = ((Literal) left).value;
        if(right instanceof Literal) right = ((Literal) right).value;
        return switch (expr.operator.type){
            case EQUAL_EQUAL -> left == right;
            case LESS_EQUAL -> ((Double) left) <= ((Double) right);
            case GREATER_EQUAL -> ((Double) left) >= ((Double) right);
            case LESS -> ((Double) left) < ((Double) right);
            case GREATER -> ((Double) left) > ((Double) right);
            default -> throw new IllegalStateException("Unexpected value: " + expr.operator.type);
        };
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object o = evaluate(expr.right);
        return switch (expr.operator.type){
            case MINUS -> -(Double) o;
            case BANG -> ! (Boolean) o;
            default -> throw new IllegalStateException("Unexpected value: " + expr.operator.type);
        };
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.statements,new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        environment.define(stmt.name.lexeme, new LoxFunction(stmt,environment));
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        boolean condition = (boolean) evaluate(stmt.condition);
        if(condition){
            execute(stmt.thenBranch);
        }else {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        System.out.println(evaluate(stmt.expression));
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        throw new LoxReturn(evaluate(stmt.value));
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        environment.define(stmt.name.lexeme, evaluate(stmt.initializer));
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        while ((boolean) evaluate(stmt.condition)){
            execute(stmt.body);
        }
        return null;
    }

}