package com.ninja_squad.jb.codestory.action;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.ActionFactory;
import com.ninja_squad.jb.codestory.ContentTypes;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import com.ninja_squad.jb.codestory.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The action factory for CodeStory.
 * @author JB
 */
public class CodeStoryActionFactory implements ActionFactory {

    public static final String DEFAULT_ANSWER = "What?";
    private static final Pattern ARITHMETIC_EXPRESSION_PATTERN = Pattern.compile("/\\?q=[(,\\d].*");

    /**
     * The default action. Returns a 404.
     */
    private static final Action DEFAULT_ACTION = new Action() {
        @Override
        public HttpResponse execute(HttpRequest request) {
            return HttpResponse.builder()
                               .status(HttpStatus._404_NOT_FOUND)
                               .contentType(ContentTypes.TEXT_PLAIN, StandardCharsets.US_ASCII)
                               .body(DEFAULT_ANSWER)
                               .build();
        }
    };

    private static final Map<String, Supplier<Action>> ACTIONS_BY_PATH =
        ImmutableMap.<String, Supplier<Action>>builder()
                    .put("/",
                         new Supplier<Action>() {
                             @Override
                             public Action get() {
                                 return new RootAction();
                             }
                         })
                    .put("/subject",
                         new Supplier<Action>() {
                             @Override
                             public Action get() {
                                 return new SubjectAction();
                             }
                         })
                    .build();

    /**
     * Gets the appropriate answerer based on the given request path
     */
    @Override
    public Action getAction(HttpRequest request) {
        if (request.getMethod() == HttpRequest.Method.POST
            && request.getPath().equals("/jajascript/optimize")) {
            return new JajascriptAction();
        }
        if (request.getMethod() == HttpRequest.Method.POST) {
            return new SubjectAction();
        }
        if (request.getMethod() == HttpRequest.Method.GET
            && request.getPath().startsWith("/scalaskel/change/")) {
            return new SkalaskelAction();
        }
        if (request.getMethod() == HttpRequest.Method.GET
            && request.getParameters().getSingleParameter("q").isPresent()
            && ARITHMETIC_EXPRESSION_PATTERN.matcher(request.getPathAndQueryString()).matches()) {
            return new ArithmeticAction();
        }
        Supplier<Action> supplier = ACTIONS_BY_PATH.get(request.getPath());
        if (supplier == null) {
            return DEFAULT_ACTION;
        }
        return supplier.get();
    }
}
