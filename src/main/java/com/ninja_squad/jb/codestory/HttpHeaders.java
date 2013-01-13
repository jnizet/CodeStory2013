package com.ninja_squad.jb.codestory;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

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
    private Optional<ContentType> contentType;

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

        private ContentType(String name, String charset) {
            this.name = name;
            this.charset = charset == null ? StandardCharsets.ISO_8859_1 : Charset.forName(charset);
        }

        public static ContentType parse(String line) {
            Iterator<String> parts = Splitter.on(';').trimResults().split(line).iterator();
            String name = parts.next();
            String charset = null;
            if (parts.hasNext()) {
                String charsetDefinition = parts.next();
                Iterator<String> charsetParts = Splitter.on('=').trimResults().split(charsetDefinition).iterator();
                String key = charsetParts.next();
                if (key.equals("charset") && charsetParts.hasNext()) {
                    charset = charsetParts.next();
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

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("name", name).add("charset", charset).toString();
        }
    }
}
