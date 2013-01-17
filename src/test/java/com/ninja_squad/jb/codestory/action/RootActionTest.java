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
    public void shouldReturnNoForStep5Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=Est+ce+que+tu+reponds+toujours+oui(OUI/NON)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("NON");
    }

    @Test
    public void shouldReturnYesForStep6Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=As+tu+bien+recu+le+premier+enonce(OUI/NON)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("OUI");
    }

    @Test
    public void shouldReturnBofForGoodNightQuestion() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=As+tu+passe+une+bonne+nuit+malgre+les+bugs+de+l+etape+precedente(PAS_TOP/BOF/QUELS_BUGS)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("BOF");
    }

    @Test
    public void shouldReturnYesForSubject2Question() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=As+tu+bien+recu+le+second+enonce(OUI/NON)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("OUI");
    }

    @Test
    public void shouldReturnNoForNDeloofQuestion() throws IOException {
        HttpRequest request = HttpRequest.get("/?q=As+tu+copie+le+code+de+ndeloof(OUI/NON/JE_SUIS_NICOLAS)");
        assertThat(new RootAction().execute(request).getBodyAsString(StandardCharsets.US_ASCII))
            .isEqualTo("NON");
    }
}
