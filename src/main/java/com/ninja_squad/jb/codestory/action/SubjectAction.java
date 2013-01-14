package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Action used to receive (and display) if method is GET a subject
 * @author jbnizet.ext
 */
public class SubjectAction implements Action {
    public static final String WELL_RECEIVED = "Bien recu";


    private static final AtomicReference<HttpRequest> LAST_POST_REQUEST = new AtomicReference<>(null);

    @Override
    public HttpResponse execute(HttpRequest request) throws IOException {
        if (request.getMethod() == HttpRequest.Method.GET) {
            HttpRequest subject = LAST_POST_REQUEST.get();
            return doGet(subject);
        }
        else {
            return doPost(request);
        }
    }

    public static void reset() {
        LAST_POST_REQUEST.set(null);
    }

    private HttpResponse doPost(HttpRequest request) {
        LAST_POST_REQUEST.set(request);
        String subject = new String(request.getBody(), request.getContentCharset());
        System.out.println("Subject = " + subject);
        return new HttpResponse(HttpResponse.Status._201_CREATED,
                                HttpHeaders.PLAIN_ASCII_TEXT,
                                WELL_RECEIVED.getBytes(StandardCharsets.US_ASCII));
    }

    private HttpResponse doGet(HttpRequest subject) {
        if (subject == null) {
            return new HttpResponse(HttpResponse.Status._200_OK,
                                    HttpHeaders.PLAIN_ASCII_TEXT,
                                    "Aucun sujet poste".getBytes(StandardCharsets.US_ASCII));
        }
        else {
            return new HttpResponse(HttpResponse.Status._200_OK,
                                    HttpHeaders.builder().setContentType("text/plain", subject.getContentCharset()).build(),
                                    subject.getBody());
        }
    }
}
