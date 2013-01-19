package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.ContentTypes;
import com.ninja_squad.jb.codestory.HttpRequest;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.fest.assertions.Assertions.*;

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

        HttpRequest postRequest = HttpRequest.postBuilder("/")
                                             .contentType(ContentTypes.TEXT_PLAIN, StandardCharsets.US_ASCII)
                                             .body("The subject")
                                             .build();
        assertThat(new SubjectAction().execute(postRequest).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("Bien recu");

        assertThat(new SubjectAction().execute(getRequest).getBodyAsString(StandardCharsets.UTF_8))
            .isEqualTo("The subject");
    }
}
