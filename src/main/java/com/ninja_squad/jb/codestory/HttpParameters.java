package com.ninja_squad.jb.codestory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;

/**
 * Structure containing HTTP parameters
 * @author JB
 */
public class HttpParameters {
    private final ListMultimap<String, String> map;

    public HttpParameters(ListMultimap<String, String> map) {
        this.map = ImmutableListMultimap.copyOf(map);
    }

    public Optional<String> getSingleParameter(String name) {
        List<String> values = map.get(name);
        return Optional.fromNullable(values.isEmpty() ? null : values.get(0));
    }

    public List<String> getMultipleParameters(String name) {
        return map.get(name);
    }

    public ListMultimap<String, String> asMap() {
        return map;
    }
}
