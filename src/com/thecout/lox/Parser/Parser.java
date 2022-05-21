package com.thecout.lox.Parser;


import com.thecout.lox.Parser.Expr.*;
import com.thecout.lox.Parser.Stmts.*;
import com.thecout.lox.Token;
import com.thecout.lox.TokenType;

import java.util.ArrayList;
import java.util.List;

import static com.thecout.lox.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        try {
            while (!isAtEnd()) {
                statements.add(declaration());
                //System.out.println(statements.get(statements.size()-1).print());
            }
        }
        catch (ParseError e)
        {
            System.out.println(e.getMessage());
        }

        return statements;
    }

    private Expr expression() {
        return assignment();
    }

    private Stmt declaration() {
            if (match(FUN)) return function();
            if (match(VAR)) return varDeclaration();

            return statement();
    }

    private Stmt statement() {
        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return new Block(block());

        return expressionStatement();
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN,"Expected '('");
        List<Stmt> returnStatements = new ArrayList<>();
        if(SortedTokenTypes.VAR_DECL.containsTokenType(peek().type)){
            consume(VAR,"Expected 'var'");
            returnStatements.add(varDeclaration());
        }else if(SortedTokenTypes.EXPR_STATEMENT.containsTokenType(peek().type)){
            returnStatements.add(expressionStatement());
        }else{
            consume(SEMICOLON,"Expected ';'");
        }
        Expr condition = null;
        if(SortedTokenTypes.EXPR.containsTokenType(peek().type)){
            condition = expression();
        }
        consume(SEMICOLON,"Expected ';'");
        Expr mutator = null;
        if(SortedTokenTypes.EXPR.containsTokenType(peek().type)){
            mutator = expression();
        }

        if(condition == null)
        {
            condition = new Literal(true);
        }
        consume(RIGHT_PAREN, "Expect ')'");
        Stmt blockStmt = new Block(List.of(statement(),new Expression(mutator)));
        Stmt whileStmt = new While(condition, blockStmt);
        returnStatements.add(whileStmt);

        return new Block(returnStatements);

    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition."); // [parens]

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ; at end of print statement.");
        return new Print(expr);
    }

    private Stmt returnStatement() {
        Expr expr = new Literal(null);
        try
        {
            expr = expression();
        }
        catch(Exception e) {}
        consume(SEMICOLON, "Expect semicolon at end of return statement.");
        return new Return(expr);
    }

    private Stmt varDeclaration() {
        Token name = null;
        Expr expression = new Literal(null);
        if(match(IDENTIFIER)) {
            name = previous();
            try {
                consume(EQUAL, "Expect equal for declaration.");
                expression = expression();
            }
            catch(Exception e) {}
            consume(SEMICOLON, "Expect semicolon at end of declaration.");
        }
        return new Var(name, expression);
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect ( at start of while statement.");
        Expr expr = expression();
        consume(RIGHT_PAREN, "Expect ) after expression.");
        Stmt statement = statement();
        return new While(expr, statement);
    }

    private Stmt expressionStatement() {
        Expr expression = expression();
        consume(SEMICOLON, "Expect semicolon at end of expression statement.");
        return new Expression(expression);
    }

    private Function function() {
        List<Token> parameters = new ArrayList<>();
        Token name = null;
        if(match(IDENTIFIER)) {
            name = previous();
            consume(LEFT_PAREN, "Expect ( at start of function declaration.");
            while (!peekMatch(RIGHT_PAREN)){
                parameters.add(consume(IDENTIFIER,"Expected identifier"));
                if(!match(COMMA)) break;
            }
        }
        consume(RIGHT_PAREN, "Expected ).");
        consume(LEFT_BRACE,"Expected {.");
        List<Stmt> block = block();
        return new Function(name, parameters, block);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<Stmt>();
        while(!match(RIGHT_BRACE))
        {
            Stmt statement = statement();
            statements.add(statement);
        }
        return statements;
    }

    private Expr assignment() {
        if(check(IDENTIFIER))
        {
            Token name = consume(IDENTIFIER, "Expected identifier.");
            if(match(EQUAL))
            {
                Expr assignment = assignment();
                return new Assign(name, assignment);
            }
            moveCounterToPrevious();
        }
        return or();
    }

    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();
        while(match(AND)){
            Token operator = previous();
            Expr right = equality();
            expr = new Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();
        while(match(BANG_EQUAL, EQUAL_EQUAL)){
            Token operator = previous();
            Expr right = comparison();
            expr = new Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();
        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
            Token operator = previous();
            Expr right = addition();
            expr = new Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();
        while(match(PLUS,MINUS)){
            Token operator = previous();
            Expr right = multiplication();
            expr = new Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();
        while(match(STAR,SLASH)){
            Token operator = previous();
            Expr right = unary();
            expr = new Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if(match(BANG,MINUS)){
            Token operator = previous();
            if(SortedTokenTypes.CALL.containsTokenType(operator.type)){
                return new Unary(operator, call());
            }
            return new Unary(operator, unary());
        }else if(SortedTokenTypes.CALL.containsTokenType(peek().type)){
            return call();
        }
        throw error(previous(),"Expected 'unary operator'");
    }

    private Expr call() {
        Expr expr = primary();
        List<Expr> arguments = new ArrayList<>();
        if(match(LEFT_PAREN)){
            while (!peekMatch(RIGHT_PAREN)){
                arguments.add(expression());
                if(!match(COMMA)) break;
            }
            consume(RIGHT_PAREN,"Expected ).");
            return new Call(expr,arguments);
        }
        return expr;
    }

    private Expr primary() {
        if(match(TRUE)) return new Literal(true);
        if(match(FALSE)) return new Literal(true);
        if(match(NIL)) return new Literal(null);
        if(check(NUMBER)) return new Literal(consume(NUMBER,"Expected Number").literal);
        if(check(STRING)) return new Literal(consume(STRING,"Expected String").literal);
        if(check(IDENTIFIER)) return new Variable(consume(IDENTIFIER, "Expected Identifier"));
        if(match(LEFT_PAREN)){
            Expr expr = expression();
            consume(RIGHT_PAREN,"Expected ')'");
            return expr;
        }

        throw new ParseError();
    }

    private boolean peekMatch(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                return true;
            }
        }

        return false;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) return false;
        return peek().type == tokenType;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        ParserError.error(token, message);
        return new ParseError();
    }

    private void moveCounterToPrevious()
    {
        if(current == 0)
        {
            throw new ParseError();
        }
        current--;
    }
}
