package com.ninja_squad.jb.codestory.action.arithmetic;

import java.util.Objects;

/**
* A token of the arithmetic grammar
* @author JB
*/
public class Token {
    private String value;
    private TokenType type;

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    public Token(TokenType type) {
        this(null, type);
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Token{" +
               "value='" + value + '\'' +
               ", type=" + type +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Token token = (Token) o;

        return Objects.equals(value, token.value)
               && Objects.equals(type, token.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }
}
