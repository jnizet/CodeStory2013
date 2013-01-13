package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * The action for the first question of CodeStory : Quelle est ton adresse amail
 * @author JB
 */
public class RootAction implements Action {

    public static final String EMAIL_ADDRESS_QUESTION = "Quelle est ton adresse email";

    public static final String EMAIL_ADDRESS_ANSWER = "jb+codestory@ninja-squad.com";
    public static final String BAD_REQUEST_ANSWER = "Could you repeat the question?";

    @Override
    public HttpResponse execute(HttpRequest request) throws IOException {
        if (request.getMethod() == HttpRequest.Method.GET
            && EMAIL_ADDRESS_QUESTION.equals(request.getParameters().getSingleParameter("q").orNull())) {
            return new HttpResponse(HttpResponse.Status._200_OK,
                                    HttpHeaders.PLAIN_ASCII_TEXT,
                                    EMAIL_ADDRESS_ANSWER.getBytes(StandardCharsets.US_ASCII));
        }
        return new HttpResponse(HttpResponse.Status._400_BAD_REQUEST,
                                HttpHeaders.PLAIN_ASCII_TEXT,
                                BAD_REQUEST_ANSWER.getBytes(StandardCharsets.US_ASCII));
    }
}
