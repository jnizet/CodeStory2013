package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpParameters;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
    public void getActionShouldReturnNotFoundForUnknownPath() throws IOException {
        HttpRequest request = HttpRequest.get("/unknown");
        assertThat(actionFactory.getAction(request).execute(request).getStatus()).isEqualTo(HttpResponse.Status._404_NOT_FOUND);
    }

    @Test
    public void getActionShouldReturnRootActionForRootAndGet() throws IOException {
        HttpRequest request = HttpRequest.get("/");
        assertThat(actionFactory.getAction(request)).isInstanceOf(RootAction.class);
    }

    @Test
    public void getActionShouldReturnRootActionForArithmeticExpression() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=1+2");
        assertThat(actionFactory.getAction(request)).isInstanceOf(ArithmeticAction.class);
        request = HttpRequest.get("/?q=(1+2)");
        assertThat(actionFactory.getAction(request)).isInstanceOf(ArithmeticAction.class);
    }

    @Test
    public void getActionShouldReturnSubjectActionForPost() throws IOException {
        HttpRequest request = new HttpRequest(HttpRequest.Method.POST,
                                              "/",
                                              HttpParameters.NO_PARAMETER,
                                              HttpHeaders.NO_HEADER,
                                              null);
        assertThat(actionFactory.getAction(request)).isInstanceOf(SubjectAction.class);
    }

    @Test
    public void getActionShouldReturnSubjectActionForSubjectAndGet() throws IOException {
        HttpRequest request = HttpRequest.get("/subject");
        assertThat(actionFactory.getAction(request)).isInstanceOf(SubjectAction.class);
    }
}
