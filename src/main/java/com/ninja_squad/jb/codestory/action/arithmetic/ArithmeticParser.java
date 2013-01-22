package com.ninja_squad.jb.codestory.action.arithmetic;

import java.math.BigDecimal;
import java.util.List;

/**
 * A parser of arithmetic expressions
 * @author JB
 */
public class ArithmeticParser {
    private List<Token> tokens;
    private int index = 0;

    public ArithmeticParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public BigDecimal parse() {
        BigDecimal result = expression();
        Token token = tokens.get(index);
        if (token.getType() != TokenType.EOF) {
            throw new RuntimeException("unexpected token: " + token);
        }
        return result;
    }

    private BigDecimal expression() {
        BigDecimal value = multiplication();
        while (true) {
            Token token = tokens.get(index);
            if (token.getType() == TokenType.PLUS) {
                index++;
                BigDecimal operand2 = multiplication();
                value = value.add(operand2);
            }
            else if (token.getType() == TokenType.MINUS) {
                index++;
                BigDecimal operand2 = multiplication();
                value = value.subtract(operand2);
            }
            else {
                break;
            }
        }
        return value;
    }

    private BigDecimal multiplication() {
        BigDecimal value = factor();
        while (true) {
            Token token = tokens.get(index);
            if (token.getType() == TokenType.MULTIPLY) {
                index++;
                BigDecimal operand2 = factor();
                value = value.multiply(operand2);
            }
            else if (token.getType() == TokenType.DIVIDE) {
                index++;
                BigDecimal operand2 = factor();
                value = value.divide(operand2, 10, BigDecimal.ROUND_UP);
            }
            else {
                break;
            }
        }
        return value;
    }


    private BigDecimal factor() {
        Token token = tokens.get(index);
        switch (token.getType()) {
            case MINUS:
                index++;
                return atom().negate();
            default:
                return atom();
        }
    }

    private BigDecimal atom() {
        Token token = tokens.get(index);
        switch (token.getType()) {
            case FLOAT:
                index++;
                return new BigDecimal(token.getValue());
            case LEFT_PAREN:
                index++;
                BigDecimal result = expression();
                Token nextToken = tokens.get(index);
                if (nextToken.getType() != TokenType.RIGHT_PAREN) {
                    throw new RuntimeException("Unexpected token: " + token);
                }
                index++;
                return result;
            default:
                throw new RuntimeException("Unexpected token: " + token);
        }
    }
}
