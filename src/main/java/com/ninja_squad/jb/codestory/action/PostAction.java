package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;

import java.io.IOException;

/**
 * Action which stores the latest post bodies in a cache, for later retrieval
 * @author JB
 */
public class PostAction implements Action {

    @Override
    public HttpResponse execute(HttpRequest request) throws IOException {
        PostBodyCache.INSTANCE.addBody(request.getBody());
        return new HttpResponse(404, HttpHeaders.NO_HEADER, null);
    }
}
