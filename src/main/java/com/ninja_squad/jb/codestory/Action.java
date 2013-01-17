package com.ninja_squad.jb.codestory;

/**
 * An action, which consists in creating a response for a request
 * @author JB
 */
public interface Action {
    HttpResponse execute(HttpRequest request);
}
