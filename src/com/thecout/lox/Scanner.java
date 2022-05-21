package com.thecout.lox;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    public Scanner(String source) {
        this.source = source;
    }


    public List<Token> scanLine(String line, int lineNumber) {
        List<Token> returnToken = new ArrayList<>();

        //find all matching comments and replace with word "comment"
        String[] commentMatches = Pattern.compile("\\/\\/.*")
                .matcher(line)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);

        Counter commentCounter = new Counter(0, commentMatches.length);
        line = line.replaceAll("\\/\\/.*", "comment");

        //Find all matching Strings and replace with word "string"
        String[] stringMatches = Pattern.compile("\"(.*?)\"")
                .matcher(line)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);

        for (int i = 0; i < stringMatches.length; i++)
        {
            stringMatches[i] = stringMatches[i].replaceAll("\"", "");
        }
        Counter stringCounter = new Counter(0, stringMatches.length);
        line = line.replaceAll("\"(.*?)\"", "string");

        String [] lexems = prepareInput(line);
        matchInput(lexems, lineNumber, returnToken, commentMatches, stringMatches, stringCounter, commentCounter);

/*
        System.out.println("****returnTokens****");
        for (Token token : returnToken)
        {
            System.out.println(token.toString());
        }
*/

        return returnToken;
    }

    private void matchInput(String[] lexems, int lineNumber, List returnToken, String[] commentMatches, String[] stringMatches, Counter stringCounter, Counter commentCounter)
    {
        for (String lexem : lexems)
        {
            Token token = null;

            switch (lexem) {
                case "(":
                    token = new Token(TokenType.LEFT_PAREN, "(", "(", lineNumber);
                    break;
                case ")":
                    token = new Token(TokenType.RIGHT_PAREN, ")", ")", lineNumber);
                    break;
                case "{":
                    token = new Token(TokenType.LEFT_BRACE, "{", "{", lineNumber);
                    break;
                case "}":
                    token = new Token(TokenType.RIGHT_BRACE, "}", "}", lineNumber);
                    break;
                case ",":
                    token = new Token(TokenType.COMMA, ",", ",", lineNumber);
                    break;
                case ".":
                    token = new Token(TokenType.DOT, ".", ".", lineNumber);
                    break;
                case "-":
                    token = new Token(TokenType.MINUS, "-", "-", lineNumber);
                    break;
                case "+":
                    token = new Token(TokenType.PLUS, "+", "+", lineNumber);
                    break;
                case ";":
                    token = new Token(TokenType.SEMICOLON, ";", ";", lineNumber);
                    break;
                case "/":
                    token = new Token(TokenType.SLASH, "/", "/", lineNumber);
                    break;
                case "*":
                    token = new Token(TokenType.STAR, "*", "*", lineNumber);
                    break;
                case "bang":
                    token = new Token(TokenType.BANG, "!", "!", lineNumber);
                    break;
                case "bangequal":
                    token = new Token(TokenType.BANG_EQUAL, "!=", "!=", lineNumber);
                    break;
                case "equal":
                    token = new Token(TokenType.EQUAL, "=", "=", lineNumber);
                    break;
                case "equalequal":
                    token = new Token(TokenType.EQUAL_EQUAL, "==", "==", lineNumber);
                    break;
                case "greater":
                    token = new Token(TokenType.GREATER, ">", ">", lineNumber);
                    break;
                case "greaterequal":
                    token = new Token(TokenType.GREATER_EQUAL, ">=", ">=", lineNumber);
                    break;
                case "less":
                    token = new Token(TokenType.LESS, "<", "<", lineNumber);
                    break;
                case "lessequal":
                    token = new Token(TokenType.LESS_EQUAL, "<=", "<=", lineNumber);
                    break;
                case "and":
                    token = new Token(TokenType.AND, "and", "and", lineNumber);
                    break;
                case "else":
                    token = new Token(TokenType.ELSE, "else", "else", lineNumber);
                    break;
                case "false":
                    token = new Token(TokenType.FALSE, "false", "false", lineNumber);
                    break;
                case "fun":
                    token = new Token(TokenType.FUN, "fun", "fun", lineNumber);
                    break;
                case "for":
                    token = new Token(TokenType.FOR, "for", "for", lineNumber);
                    break;
                case "if":
                    token = new Token(TokenType.IF, "if", "if", lineNumber);
                    break;
                case "nil":
                    token = new Token(TokenType.NIL, "nil", "nil", lineNumber);
                    break;
                case "or":
                    token = new Token(TokenType.OR, "or", "or", lineNumber);
                    break;
                case "print":
                    token = new Token(TokenType.PRINT, "print", "print", lineNumber);
                    break;
                case "return":
                    token = new Token(TokenType.RETURN, "return", "return", lineNumber);
                    break;
                case "true":
                    token = new Token(TokenType.TRUE, "true", "true", lineNumber);
                    break;
                case "var":
                    token = new Token(TokenType.VAR, "var", "var", lineNumber);
                    break;
                case "while":
                    token = new Token(TokenType.WHILE, "while", "while", lineNumber);
                    break;
                case "string":
                    token = addNewStringToken(stringMatches, stringCounter, lineNumber);
                    break;
                case "comment":
                    token =addNewCommentToken(commentMatches, commentCounter, lineNumber);
                    break;
                default:
                    if (lexem.matches("([0-9]*[.])?[0-9]+"))
                    {
                        token = new Token(TokenType.NUMBER, lexem, Double.parseDouble(lexem), lineNumber);
                        break;
                    }
                    else if (lexem.matches("[a-zA-Z]+"))
                    {
                        token = new Token(TokenType.IDENTIFIER, lexem, lexem, lineNumber);
                        break;
                    }
                    System.out.println("Error: No matching token found for lexem " + lexem + ".");
            }

            if (token != null)
            {
                returnToken.add(token);
            }
        }
    }


    private String[] prepareInput(String line)
    {
        // Find all matching left parens and add spaces at each end
        line = line.replaceAll("\\(", " ( ");

        // Find all matching right parens and add spaces at each end
        line = line.replaceAll("\\)", " ) ");

        // Find all matching left braces and add spaces at each end
        line = line.replaceAll("\\{", " { ");

        // Find all matching right braces and add spaces at each end
        line = line.replaceAll("\\}", " } ");

        // Find all matching commas and add spaces at each end
        line = line.replaceAll(",", " , ");

        // Find all matching minus and add spaces at each end
        line = line.replaceAll("-", " - ");

        // Find all matching plus and add spaces at each end
        line = line.replaceAll("\\+", " + ");

        // Find all matching semicolon and add spaces at each end
        line = line.replaceAll(";", " ; ");

        // Find all matching slashes and add spaces at each end
        line = line.replaceAll("/", " / ");

        // Find all matching stars and add spaces at each end
        line = line.replaceAll("\\*", " * ");

        // Find all matching bangs and add spaces at each end
        line = line.replaceAll("!", " bang ");

        // Find all matching bang equals and add spaces at each end
        line = line.replaceAll(" bang =", " bangequal ");

        // Find all matching greater and add spaces at each end
        line = line.replaceAll(">", " greater ");

        // Find all matching greater equal and add spaces at each end
        line = line.replaceAll(" greater =", " greaterequal ");

        // Find all matching less and add spaces at each end
        line = line.replaceAll("<", " less ");

        // Find all matching less equal and add spaces at each end
        line = line.replaceAll(" less =", " lessequal ");

        // Find all matching equal equal and add spaces at each end
        line = line.replaceAll("==", " equalequal ");

        // Handle equals
        line = line.replaceAll("=", " equal ");

        //Remove multiple spaces in a row
        line = line.replaceAll("\\s+", " ");
        line = line.trim();

        //split line into single lexems
        return line.split(" ");
    }

    private Token addNewCommentToken(String[] stringMatches, Counter counter, int lineNumber)
    {
        Token token;
        token = new Token(TokenType.COMMENT, stringMatches[counter.getValue()], stringMatches[counter.getValue()], lineNumber);
        counter.setValue(counter.getValue() + 1);
        return token;
    }

    private Token addNewStringToken(String[] commentMatches, Counter counter, int lineNumber)
    {
        Token token;
        token = new Token(TokenType.STRING, commentMatches[counter.getValue()], commentMatches[counter.getValue()], lineNumber);
        counter.setValue(counter.getValue() + 1);
        return token;
    }

    public List<Token> scan() {
        String[] lines = source.split("\n");
        for (int i = 0; i < lines.length; i++) {
            tokens.addAll(scanLine(lines[i], i));
        }
        tokens.add(new Token(TokenType.EOF, "", "", lines.length));
        return tokens;
    }

}