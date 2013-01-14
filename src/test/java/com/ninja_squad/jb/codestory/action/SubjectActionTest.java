package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpParameters;
import com.ninja_squad.jb.codestory.HttpRequest;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests for SubjectAction
 * @author JB
 */
public class SubjectActionTest {
    @Test
    public void shouldReturnNoSubjectThenWellReceivedThenSubject() throws IOException {
        SubjectAction.reset();
        HttpRequest getRequest = HttpRequest.get("/subject");
        assertThat(new SubjectAction().execute(getRequest).getBodyAsString(StandardCharsets.UTF_8))
            .isEqualTo("Aucun sujet poste");

        HttpRequest postRequest = new HttpRequest(HttpRequest.Method.POST,
                                                  "/",
                                                  HttpParameters.NO_PARAMETER,
                                                  HttpHeaders.PLAIN_ASCII_TEXT,
                                                  "The subject".getBytes(StandardCharsets.US_ASCII));
        assertThat(new SubjectAction().execute(postRequest).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("Bien recu");

        assertThat(new SubjectAction().execute(getRequest).getBodyAsString(StandardCharsets.UTF_8))
            .isEqualTo("The subject");
    }
}
