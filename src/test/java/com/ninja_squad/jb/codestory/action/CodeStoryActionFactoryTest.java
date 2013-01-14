package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpParameters;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
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
    public void getActionShouldReturnNotFoundForUnknownPath() throws IOException {
        HttpRequest request = HttpRequest.get("/foo?q=Quelle+est+ton+adresse+email");
        assertThat(actionFactory.getAction(request).execute(request).getStatus()).isEqualTo(HttpResponse.Status._404_NOT_FOUND);
    }

    @Test
    public void getActionShouldReturnBadRequestForUnknownQuestion() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Comment+ca+va");
        assertThat(actionFactory.getAction(request).execute(request).getStatus()).isEqualTo(HttpResponse.Status._400_BAD_REQUEST);
    }

    @Test
    public void getActionShouldReturnAddressEmailActionForStep1Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Quelle+est+ton+adresse+email");
        assertThat(actionFactory.getAction(request).execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("jb+codestory@ninja-squad.com");
    }

    @Test
    public void getActionShouldReturnYesForStep2Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Es+tu+abonne+a+la+mailing+list(OUI/NON)");
        assertThat(actionFactory.getAction(request).execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("OUI");
    }

    @Test
    public void getActionShouldReturnYesForStep3Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Es+tu+heureux+de+participer(OUI/NON)");
        assertThat(actionFactory.getAction(request).execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("OUI");
    }

    @Test
    public void getActionShouldReturnYesForStep4Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Es+tu+pret+a+recevoir+une+enonce+au+format+markdown+par+http+post(OUI/NON)");
        assertThat(actionFactory.getAction(request).execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("OUI");
    }

    @Test
    public void getActionShouldReturnWellReceivedForSubjectPost() throws IOException {
        SubjectAction.reset();
        HttpRequest getRequest = HttpRequest.get("/subject");
        assertThat(actionFactory.getAction(getRequest).execute(getRequest).getBodyAsString(StandardCharsets.UTF_8))
            .isEqualTo("Aucun sujet poste");

        HttpRequest postRequest = new HttpRequest(HttpRequest.Method.POST,
                                                 "/",
                                                  HttpParameters.NO_PARAMETER,
                                                  HttpHeaders.PLAIN_ASCII_TEXT,
                                                  "The subject".getBytes(StandardCharsets.US_ASCII));
        assertThat(actionFactory.getAction(postRequest).execute(postRequest).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("Bien recu");

        assertThat(actionFactory.getAction(getRequest).execute(getRequest).getBodyAsString(StandardCharsets.UTF_8))
            .isEqualTo("The subject");
    }
}
