package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.ContentTypes;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import com.ninja_squad.jb.codestory.HttpStatus;

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
            return doGet();
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
        return HttpResponse.builder()
                           .status(HttpStatus._201_CREATED)
                           .contentType(ContentTypes.TEXT_PLAIN, StandardCharsets.US_ASCII)
                           .body(WELL_RECEIVED)
                           .build();
    }

    private HttpResponse doGet() {
        HttpRequest subject = LAST_POST_REQUEST.get();
        if (subject == null) {
            return StandardResponses.ok(NO_SUBJECT_POSTED);
        }
        else {
            return HttpResponse.builder()
                               .status(HttpStatus._200_OK)
                               .contentType(ContentTypes.TEXT_PLAIN, subject.getContentCharset())
                               .body(subject.getBody())
                               .build();
        }
    }
}
