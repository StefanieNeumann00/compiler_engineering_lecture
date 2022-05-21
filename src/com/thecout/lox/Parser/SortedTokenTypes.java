package com.thecout.lox.Parser;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.thecout.lox.TokenType;


public enum SortedTokenTypes {

    PRIMARY(TokenType.TRUE, TokenType.FALSE, TokenType.NIL, TokenType.NUMBER, TokenType.STRING, TokenType.IDENTIFIER, TokenType.LEFT_PAREN),
    CALL(List.of(PRIMARY.tokenTypes)),
    UNARY(TokenType.BANG,TokenType.MINUS),

    VAR_DECL(TokenType.VAR),
    FUN_DECL(TokenType.FUN),
    ASSIGNMENT(TokenType.IDENTIFIER),
    EXPR(List.of(ASSIGNMENT.tokenTypes)),
    EXPR_STATEMENT(List.of(EXPR.tokenTypes)),
    STATEMENT(TokenType.IDENTIFIER,TokenType.FUN,TokenType.FUN,TokenType.FOR,TokenType.IF,TokenType.WHILE,TokenType.PRINT,TokenType.RETURN,TokenType.LEFT_BRACE),;


    private final List<TokenType> tokenTypes;

    SortedTokenTypes(TokenType... tokenTypes){
        this.tokenTypes = List.of(tokenTypes);
    }

    SortedTokenTypes(List<List<TokenType>> firstTokens){
        this.tokenTypes = firstTokens.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public boolean containsTokenType(TokenType tokenType){
        return tokenTypes.contains(tokenType);
    }

}
