package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests for RootAction
 * @author JB
 */
public class RootActionTest {
    @Test
    public void shouldReturnBadRequestForUnknownQuestion() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Comment+ca+va");
        assertThat(new RootAction().execute(request).getStatus()).isEqualTo(HttpResponse.Status._400_BAD_REQUEST);
    }

    @Test
    public void getActionShouldReturnAddressEmailActionForStep1Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Quelle+est+ton+adresse+email");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("jb+codestory@ninja-squad.com");
    }

    @Test
    public void shouldReturnYesForStep2Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Es+tu+abonne+a+la+mailing+list(OUI/NON)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("OUI");
    }

    @Test
    public void shouldReturnYesForStep3Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Es+tu+heureux+de+participer(OUI/NON)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("OUI");
    }

    @Test
    public void shouldReturnYesForStep4Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Es+tu+pret+a+recevoir+une+enonce+au+format+markdown+par+http+post(OUI/NON)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("OUI");
    }

    @Test
    public void shouldReturnnoForStep5Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Est+ce+que+tu+reponds+toujours+oui(OUI/NON)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("NON");
    }

    @Test
    public void shouldReturnnoForStep6Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=As+tu+bien+recu+le+premier+enonce(OUI/NON)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("OUI");
    }
}
