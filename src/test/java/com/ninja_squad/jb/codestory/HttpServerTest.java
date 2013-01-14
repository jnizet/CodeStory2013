package com.ninja_squad.jb.codestory;

import com.google.common.io.ByteStreams;
import com.ninja_squad.jb.codestory.action.CodeStoryActionFactory;
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
    public void serverShouldAnswerWithWellReceivedAndStoreSUbjectForSubject() throws IOException {
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
    public void serverShouldAnswerWith400WhenNoQuestion() throws IOException {
        HttpResponse response = Request.Get(ADDRESS).execute().returnResponse();
        check400(response);
    }

    @Test
    public void serverShouldAnswerWith404WhenNotHandlesPath() throws IOException {
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
