package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.HttpRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.fest.assertions.Assertions.*;

/**
 * Tests for CodeStoryActionFactory
 * @author JB
 */
public class CodeStoryActionFactoryTest {
    private CodeStoryActionFactory actionFactory;

    @Before
    public void setUp() {
        actionFactory = new CodeStoryActionFactory();
    }

    @Test
    public void getActionShouldReturnDefaultAnswererForUnknownQuestion() throws IOException {
        HttpRequest request = HttpRequest.get("/q=Comment+ca+va");
        assertThat(actionFactory.getAction(request).execute(request).getStatus())
            .isEqualTo(404);
    }

    @Test
    public void getActionShouldReturnAddressEmailAnswererForStep1Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Quelle+est+ton+adresse+email");
        assertThat(actionFactory.getAction(request).execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo(RootAction.EMAIL_ADDRESS_ANSWER);
    }
}
