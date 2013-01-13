package com.ninja_squad.jb.codestory.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

/**
 * Cache of the latest post bodies
 * @author JB
 */
public final class PostBodyCache {

    public static final PostBodyCache INSTANCE = new PostBodyCache();

    private static final int MAX_SIZE = 100;

    private final LinkedList<byte[]> bodies = Lists.newLinkedList();

    public synchronized void addBody(byte[] body) {
        if (bodies.size() == MAX_SIZE) {
            bodies.removeFirst();
        }
        bodies.add(body);
    }

    public synchronized List<byte[]> getBodies() {
        return ImmutableList.copyOf(bodies);
    }
}
