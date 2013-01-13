package com.ninja_squad.jb.codestory;

/**
 * An action factory, which returns the appropriate action for a request
 * @author JB
 */
public interface ActionFactory {
    Action getAction(HttpRequest request);
}
