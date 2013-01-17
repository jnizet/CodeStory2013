package com.ninja_squad.jb.codestory;

import com.google.common.io.ByteStreams;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
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
public class HttpServerError500Test {
    private static final int PORT = 56789;
    private static final String ADDRESS = "http://localhost:" + PORT + "/";
    public static final String FAKE_EXCEPTION_WITH_ACCENTS = "Fake exception with accents: יא";

    private static HttpServer httpServer;

    @BeforeClass
    public static void beforeClass() throws IOException {
        httpServer = new HttpServer(PORT);
        httpServer.start(new ActionFactory() {
            @Override
            public Action getAction(HttpRequest request) {
                return new Action() {
                    @Override
                    public com.ninja_squad.jb.codestory.HttpResponse execute(HttpRequest request) {
                        throw new IllegalStateException(FAKE_EXCEPTION_WITH_ACCENTS);
                    }
                };
            }
        });
    }

    @AfterClass
    public static void afterClass() {
        httpServer.stop();
    }

    @Test
    public void serverShouldAnswerWithError500COntainingStackTrace() throws IOException {
        HttpResponse response = Request.Get(ADDRESS).execute().returnResponse();
        String text = new String(ByteStreams.toByteArray(response.getEntity().getContent()), StandardCharsets.UTF_8);
        assertThat(text).contains("IllegalStateException");
        assertThat(text).contains(FAKE_EXCEPTION_WITH_ACCENTS);
        assertThat(text).contains("at");
    }
}
