package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.ContentTypes;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Action used to receive (and display) if method is GET a subject
 * @author jbnizet.ext
 */
public class SubjectAction implements Action {
    public static final String WELL_RECEIVED = "Bien recu";
    public static final String NO_SUBJECT_POSTED = "Aucun sujet poste";

    private static final AtomicReference<HttpRequest> LAST_POST_REQUEST = new AtomicReference<>(null);

    @Override
    public HttpResponse execute(HttpRequest request) {
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
        String subject = request.getBodyAsString();
        System.out.println("Sujet = " + subject);
        return new HttpResponse(HttpResponse.Status._201_CREATED,
                                HttpHeaders.PLAIN_ASCII_TEXT,
                                WELL_RECEIVED.getBytes(StandardCharsets.US_ASCII));
    }

    private HttpResponse doGet(HttpRequest subject) {
        if (subject == null) {
            return HttpResponse.ok(NO_SUBJECT_POSTED);
        }
        else {
            return new HttpResponse(HttpResponse.Status._200_OK,
                                    HttpHeaders.builder().setContentType(ContentTypes.TEXT_PLAIN,
                                                                         subject.getContentCharset()).build(),
                                    subject.getBody());
        }
    }
}
