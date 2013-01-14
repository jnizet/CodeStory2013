package com.ninja_squad.jb.codestory.action;

import com.google.common.collect.ImmutableMap;
import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * The action for the first question of CodeStory : Quelle est ton adresse amail
 * @author JB
 */
public class RootAction implements Action {


    public static final String EMAIL_ADDRESS_QUESTION = "Quelle est ton adresse email";
    public static final String EMAIL_ADDRESS_ANSWER = "jb+codestory@ninja-squad.com";

    public static final String MAILING_LIST_QUESTION = "Es tu abonne a la mailing list(OUI/NON)";
    public static final String MAILING_LIST_ANSWER = "OUI";

    public static final String ARE_YOU_HAPPY_QUESTION = "Es tu heureux de participer(OUI/NON)";
    public static final String ARE_YOU_HAPPY_ANSWER = "OUI";

    public static final String READY_FOR_POST_QUESTION = "Es tu pret a recevoir une enonce au format markdown par http post(OUI/NON)";
    public static final String READY_FOR_POST_ANSWER = "OUI";

    public static final String ALWAYS_YES_QUESTION = "Est ce que tu reponds toujours oui(OUI/NON)";
    public static final String ALWAYS_YES_ANSWER = "NON";

    public static final String SUBJECT_RECEIVED_QUESTION = "As tu bien recu le premier enonce(OUI/NON)";
    public static final String SUBJECT_RECEIVED_ANSWER = "OUI";

    public static final String BAD_REQUEST_ANSWER = "Could you repeat the question?";

    public static final Map<String, String> ANSWERS_BY_QUESTION =
        ImmutableMap.<String, String>builder().put(EMAIL_ADDRESS_QUESTION, EMAIL_ADDRESS_ANSWER)
                                              .put(MAILING_LIST_QUESTION, MAILING_LIST_ANSWER)
                                              .put(ARE_YOU_HAPPY_QUESTION, ARE_YOU_HAPPY_ANSWER)
                                              .put(READY_FOR_POST_QUESTION, READY_FOR_POST_ANSWER)
                                              .put(ALWAYS_YES_QUESTION, ALWAYS_YES_ANSWER)
                                              .put(SUBJECT_RECEIVED_QUESTION, SUBJECT_RECEIVED_ANSWER)
                                              .build();

    @Override
    public HttpResponse execute(HttpRequest request) throws IOException {
        if (request.getMethod() == HttpRequest.Method.GET) {
            String answer = ANSWERS_BY_QUESTION.get(request.getParameters().getSingleParameter("q").orNull());
            if (answer != null) {
                return new HttpResponse(HttpResponse.Status._200_OK,
                                        HttpHeaders.PLAIN_ASCII_TEXT,
                                        answer.getBytes(StandardCharsets.US_ASCII));
            }
        }
        return new HttpResponse(HttpResponse.Status._400_BAD_REQUEST,
                                HttpHeaders.PLAIN_ASCII_TEXT,
                                BAD_REQUEST_ANSWER.getBytes(StandardCharsets.US_ASCII));
    }
}
