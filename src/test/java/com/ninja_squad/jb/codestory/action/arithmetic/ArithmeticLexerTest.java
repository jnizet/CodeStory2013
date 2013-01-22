package com.ninja_squad.jb.codestory.action.arithmetic;

import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.*;

/**
 * Test for {@link com.ninja_squad.jb.codestory.action.arithmetic.ArithmeticLexer}
 * @author JB
 */
public class ArithmeticLexerTest {
    @Test(expected = RuntimeException.class)
    public void randomStringShouldCauseException() throws Exception {
        new ArithmeticLexer("hello").parse();
    }

    @Test(expected = RuntimeException.class)
    public void spacesShouldCauseException() throws Exception {
        new ArithmeticLexer("1 + 1").parse();
    }

    @Test(expected = RuntimeException.class)
    public void incorrectFloatShouldCauseException() throws Exception {
        new ArithmeticLexer("1,1+1").parse();
    }

    @Test
    public void correctStringShouldParseFine() throws Exception {
        ArithmeticLexer lexer = new ArithmeticLexer("(-1.1+2*(3/-4)");
        List<Token> tokens = lexer.parse();
        assertThat(tokens).hasSize(13)
                          .containsExactly(new Token(TokenType.LEFT_PAREN),
                                           new Token(TokenType.MINUS),
                                           new Token("1.1", TokenType.FLOAT),
                                           new Token(TokenType.PLUS),
                                           new Token("2", TokenType.FLOAT),
                                           new Token(TokenType.MULTIPLY),
                                           new Token(TokenType.LEFT_PAREN),
                                           new Token("3", TokenType.FLOAT),
                                           new Token(TokenType.DIVIDE),
                                           new Token(TokenType.MINUS),
                                           new Token("4", TokenType.FLOAT),
                                           new Token(TokenType.RIGHT_PAREN),
                                           new Token(TokenType.EOF));
    }
}
