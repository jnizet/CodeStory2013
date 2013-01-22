package com.ninja_squad.jb.codestory.action.arithmetic;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * A lexer of arithmetic expressions
 * @author JB
 */
public class ArithmeticLexer {

    private String input;
    private StringBuilder buffer = new StringBuilder();
    private List<Token> tokens = Lists.newArrayList();

    public ArithmeticLexer(String input) {
        this.input = input;
    }

    public List<Token> parse() {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '+' :
                    addFloat();
                    tokens.add(new Token(TokenType.PLUS));
                    break;
                case '-' :
                    addFloat();
                    tokens.add(new Token(TokenType.MINUS));
                    break;
                case '*' :
                    addFloat();
                    tokens.add(new Token(TokenType.MULTIPLY));
                    break;
                case '/' :
                    addFloat();
                    tokens.add(new Token(TokenType.DIVIDE));
                    break;
                case '(' :
                    addFloat();
                    tokens.add(new Token(TokenType.LEFT_PAREN));
                    break;
                case ')' :
                    addFloat();
                    tokens.add(new Token(TokenType.RIGHT_PAREN));
                    break;
                default :
                    buffer.append(c);
                    break;
            }
        }
        addFloat();
        tokens.add(new Token(null, TokenType.EOF));
        return tokens;
    }

    private void addFloat() {
        if (buffer.length() > 0) {
            new BigDecimal(buffer.toString());
            tokens.add(new Token(buffer.toString(), TokenType.FLOAT));
            buffer = new StringBuilder();
        }
    }
}
