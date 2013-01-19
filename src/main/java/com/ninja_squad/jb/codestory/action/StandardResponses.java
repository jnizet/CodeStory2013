package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.ContentTypes;
import com.ninja_squad.jb.codestory.HttpResponse;
import com.ninja_squad.jb.codestory.HttpStatus;

import java.nio.charset.StandardCharsets;

/**
 * Contains methods to construct standard responses
 * @author JB
 */
public final class StandardResponses {
    private StandardResponses() {
    }

    public static HttpResponse badRequest(String body) {
        return HttpResponse.builder()
                           .status(HttpStatus._400_BAD_REQUEST)
                           .contentType(ContentTypes.TEXT_PLAIN, StandardCharsets.US_ASCII)
                           .body(body)
                           .build();
    }

    public static HttpResponse ok(String body) {
        return HttpResponse.builder()
                           .status(HttpStatus._200_OK)
                           .contentType(ContentTypes.TEXT_PLAIN, StandardCharsets.US_ASCII)
                           .body(body)
                           .build();
    }
}
