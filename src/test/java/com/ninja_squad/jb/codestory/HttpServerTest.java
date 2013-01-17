package com.ninja_squad.jb.codestory;

import com.google.common.io.ByteStreams;
import com.ninja_squad.jb.codestory.action.CodeStoryActionFactory;
import com.ninja_squad.jb.codestory.action.JajascriptActionTest;
import com.ninja_squad.jb.codestory.action.RootAction;
import com.ninja_squad.jb.codestory.action.SubjectAction;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.fest.assertions.Assertions.*;

/**
 * Tests for HttpServer
 * @author JB
 */
public class HttpServerTest {
    private static final int PORT = 56789;
    private static final String ADDRESS = "http://localhost:" + PORT + "/";
    //private static final String ADDRESS = "http://codestory.jnizet.cloudbees.net/";

    private static HttpServer httpServer;

    @BeforeClass
    public static void beforeClass() throws IOException {
        httpServer = new HttpServer(PORT);
        httpServer.start(new CodeStoryActionFactory());
    }

    @AfterClass
    public static void afterClass() {
        httpServer.stop();
    }

    @Test
    public void serverShouldAnswerWithEmailAddressForStep1() throws IOException {
        String result = Request.Get(ADDRESS + "?q=Quelle+est+ton+adresse+email")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo(RootAction.EMAIL_ADDRESS_ANSWER);
    }

    @Test
    public void serverShouldAnswerWithYesForStep2() throws IOException {
        String result = Request.Get(ADDRESS + "?q=Es+tu+abonne+a+la+mailing+list(OUI/NON)")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo(RootAction.MAILING_LIST_ANSWER);
    }

    @Test
    public void serverShouldAnswerWithYesForStep3() throws IOException {
        String result = Request.Get(ADDRESS + "?q=Es+tu+heureux+de+participer(OUI/NON)")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo(RootAction.ARE_YOU_HAPPY_ANSWER);
    }

    @Test
    public void serverShouldAnswerWithYesForStep4() throws IOException {
        String result = Request.Get(ADDRESS + "?q=Es+tu+pret+a+recevoir+une+enonce+au+format+markdown+par+http+post(OUI/NON)")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo(RootAction.READY_FOR_POST_ANSWER);
    }

    @Test
    public void serverShouldAnswerWithNoForStep5() throws IOException {
        String result = Request.Get(ADDRESS + "?q=Est+ce+que+tu+reponds+toujours+oui(OUI/NON)")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo(RootAction.ALWAYS_YES_ANSWER);
    }

    @Test
    public void serverShouldAnswerWithYesForStep6() throws IOException {
        String result = Request.Get(ADDRESS + "?q=As+tu+bien+recu+le+premier+enonce(OUI/NON)")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo(RootAction.SUBJECT_RECEIVED_ANSWER);
    }

    @Test
    public void serverShouldAnswerWithBofForGoodNight() throws IOException {
        String result = Request.Get(ADDRESS + "?q=As+tu+passe+une+bonne+nuit+malgre+les+bugs+de+l+etape+precedente(PAS_TOP/BOF/QUELS_BUGS)")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo(RootAction.GOOD_NIGHT_ANSWER);
    }

    @Test
    public void serverShouldAnswerWithNoForNDeloof() throws IOException {
        String result = Request.Get(ADDRESS + "?q=As+tu+copie+le+code+de+ndeloof(OUI/NON/JE_SUIS_NICOLAS)")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo(RootAction.NDELOOF_ANSWER);
    }

    @Test
    public void serverShouldAnswerWithYesForSubject2() throws IOException {
        String result = Request.Get(ADDRESS + "?q=As+tu+bien+recu+le+second+enonce(OUI/NON)")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo(RootAction.SUBJECT2_RECEIVED_ANSWER);
    }

    @Test
    public void serverShouldAnswerWithWellReceivedAndStoreSubjectForSubject() throws IOException {
        SubjectAction.reset();
        HttpResponse response = Request.Post(ADDRESS)
                                  .bodyString("The subject", ContentType.TEXT_PLAIN)
                                  .execute()
                                  .returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(com.ninja_squad.jb.codestory.HttpResponse.Status._201_CREATED.getCode());
        assertThat(new String(ByteStreams.toByteArray(response.getEntity().getContent()),
                              StandardCharsets.US_ASCII)).isEqualTo(SubjectAction.WELL_RECEIVED);

        String result = Request.Get(ADDRESS + "subject")
                        .execute()
                        .returnContent()
                        .asString();
        assertThat(result).isEqualTo("The subject");
    }

    @Test
    public void serverShouldAnswerWithJSONForSkalaskel() throws IOException {
        String result = Request.Get(ADDRESS + "scalaskel/change/100")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).startsWith("[{")
                          .endsWith("}]");
    }

    @Test
    public void serverShouldAnswerWith2For1Plus1() throws IOException {
        String result = Request.Get(ADDRESS + "?q=1+1")
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).isEqualTo("2");
        result = Request.Get(ADDRESS + "/?q=((1,1+2)+3,14+4+(5+6+7)+(8+9+10)*4267387833344334647677634)/2*553344300034334349999000")
                        .execute()
                        .returnContent()
                        .asString();
        assertThat(result).isEqualTo("31878018903828899277492024491376690701584023926880");
    }

    @Test
    public void serverShouldAnswerWithJSONForJajascript() throws IOException {
        String json = JajascriptActionTest.generateJajascriptJSON(10000);
        String result = Request.Post(ADDRESS + "jajascript/optimize")
                               .bodyString(json, ContentType.APPLICATION_JSON)
                               .execute()
                               .returnContent()
                               .asString();
        assertThat(result).startsWith("{")
            .endsWith("}");
    }

    @Test
    public void serverShouldAnswerWith400WhenNoQuestion() throws IOException {
        HttpResponse response = Request.Get(ADDRESS).execute().returnResponse();
        check400(response);
    }

    @Test
    public void serverShouldAnswerWith404WhenNotHandledPath() throws IOException {
        HttpResponse response = Request.Get(ADDRESS + "/hello").execute().returnResponse();
        check404(response);
    }

    private void check404(HttpResponse response) throws IOException {
        assertThat(response.getStatusLine().getStatusCode() == 404);
        assertThat(new String(ByteStreams.toByteArray(response.getEntity().getContent()),
                              StandardCharsets.US_ASCII)).isEqualTo(CodeStoryActionFactory.DEFAULT_ANSWER);
    }

    private void check400(HttpResponse response) throws IOException {
        assertThat(response.getStatusLine().getStatusCode() == 400);
        assertThat(new String(ByteStreams.toByteArray(response.getEntity().getContent()),
                              StandardCharsets.US_ASCII)).isEqualTo(RootAction.BAD_REQUEST_ANSWER);
    }

    @Test
    public void serverShouldAskToRepeatTheQuestionWhenBadQUestion() throws IOException {
        HttpResponse response = Request.Get(ADDRESS + "?q=Comment+va").execute().returnResponse();
        check400(response);
    }
}
