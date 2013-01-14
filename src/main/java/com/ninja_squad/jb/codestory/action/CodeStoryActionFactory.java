package com.ninja_squad.jb.codestory.action;

import com.google.common.collect.ImmutableMap;
import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.ActionFactory;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * The action factory for CodeStory.
 * @author JB
 */
public class CodeStoryActionFactory implements ActionFactory {

    public static final String DEFAULT_ANSWER = "What?";

    /**
     * The default action. Returns a 404.
     */
    private static final Action DEFAULT_ACTION = new Action() {
        @Override
        public HttpResponse execute(HttpRequest request) throws IOException {
            return new HttpResponse(HttpResponse.Status._404_NOT_FOUND,
                                    HttpHeaders.NO_HEADER,
                                    DEFAULT_ANSWER.getBytes(StandardCharsets.US_ASCII));
        }
    };

    private static final Action SUBJECT_ACTION = new SubjectAction();

    private static final Map<String, Action> ACTIONS_BY_PATH =
        ImmutableMap.of("/", new RootAction(), "/subject", SUBJECT_ACTION);

    /**
     * Gets the appropriate answerer based on the given request path
     */
    @Override
    public Action getAction(HttpRequest request) {
        if (request.getMethod() == HttpRequest.Method.POST) {
            return SUBJECT_ACTION;
        }
        Action action = ACTIONS_BY_PATH.get(request.getPath());
        if (action == null) {
            action = DEFAULT_ACTION;
        }
        return action;
    }
}
