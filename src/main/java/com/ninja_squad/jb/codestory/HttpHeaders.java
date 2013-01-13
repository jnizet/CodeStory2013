package com.ninja_squad.jb.codestory;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * The headers of a request, stored in lower-case
 * @author JB
 */
public final class HttpHeaders {
    public static final String CONTENT_LENGTH = "content-length";
    public static final String CONTENT_TYPE = "content-type";

    public static final HttpHeaders NO_HEADER = new HttpHeaders(Collections.<String, String>emptyMap());
    public static final HttpHeaders PLAIN_ASCII_TEXT =
        new HttpHeaders(ImmutableMap.of(HttpHeaders.CONTENT_TYPE, "text/plain; charset=us-ascii"));

    private final Map<String, String> map;
    private final Optional<ContentType> contentType;

    public HttpHeaders(Map<String, String> map) {
        this.map = ImmutableMap.copyOf(map);
        String contentTypeAsString = map.get(CONTENT_TYPE);
        if (contentTypeAsString == null) {
            this.contentType = Optional.absent();
        }
        else {
            this.contentType = Optional.of(ContentType.parse(contentTypeAsString));
        }
    }

    private HttpHeaders(Builder builder) {
        this.map = ImmutableMap.copyOf(builder.map);
        this.contentType = Optional.fromNullable(builder.contentType);
    }

    public Optional<String> getHeader(String key) {
        return Optional.fromNullable(map.get(key));
    }

    public Map<String, String> asMap() {
        return map;
    }

    public Optional<ContentType> getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("map", map).add("contentType", contentType).toString();
    }

    public static class ContentType {
        private final String name;
        private final Charset charset;

        private ContentType(String name, Charset charset) {
            this.name = name;
            this.charset = charset;
        }

        public static ContentType parse(String line) {
            Iterator<String> parts = Splitter.on(';').trimResults().split(line).iterator();
            String name = parts.next();
            Charset charset = StandardCharsets.ISO_8859_1;
            if (parts.hasNext()) {
                String charsetDefinition = parts.next();
                Iterator<String> charsetParts = Splitter.on('=').trimResults().split(charsetDefinition).iterator();
                String key = charsetParts.next();
                if (key.equals("charset") && charsetParts.hasNext()) {
                    charset = Charset.forName(charsetParts.next());
                }
            }
            return new ContentType(name, charset);
        }

        public String getName() {
            return name;
        }

        public Charset getCharset() {
            return charset;
        }

        public String toHeaderValue() {
            return name + "; charset=" + charset.name();
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("name", name).add("charset", charset).toString();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder for HTTP headers
     */
    public static final class Builder {
        private Map<String, String> map = Maps.newHashMap();
        private ContentType contentType;

        private Builder() {
        }

        public Builder set(String name, String value) {
            map.put(name, value);
            return this;
        }

        public Builder setContentType(String type, Charset charset) {
            contentType = new ContentType(type, charset);
            map.put(HttpHeaders.CONTENT_TYPE, contentType.toHeaderValue());
            return this;
        }

        public HttpHeaders build() {
            return new HttpHeaders(this);
        }
    }
}
