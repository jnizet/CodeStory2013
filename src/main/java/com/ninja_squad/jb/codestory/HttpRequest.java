package com.ninja_squad.jb.codestory;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ListMultimap;
import com.google.common.io.ByteStreams;
import com.google.common.io.LineProcessor;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * An HTTP request
 * @author JB
 */
public class HttpRequest {

    public static final byte[] NO_BODY = new byte[0];

    public static enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        HEAD
    }

    private Method method;
    private String path;
    private String queryString;
    private HttpParameters parameters;
    private HttpHeaders headers;
    private byte[] body = NO_BODY;

    public HttpRequest(Method method,
                       String path,
                       String queryString,
                       HttpParameters parameters,
                       HttpHeaders headers,
                       @Nullable byte[] body) {
        this.method = Preconditions.checkNotNull(method);
        this.path = Preconditions.checkNotNull(path);
        this.queryString = Preconditions.checkNotNull(queryString);
        this.parameters = Preconditions.checkNotNull(parameters);
        this.headers = Preconditions.checkNotNull(headers);
        this.body = body == null ? NO_BODY : body;
    }

    private HttpRequest() {
    }

    public static HttpRequest parse(InputStream in) throws IOException {
        return new RequestParser().parse(in);
    }

    public static HttpRequest get(String pathAndQueryString) {
        return getBuilder(pathAndQueryString).build();
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HttpParameters getParameters() {
        return parameters;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyAsString() {
        return new String(body, getContentCharset());
    }

    public String getPathAndQueryString() {
        if (Strings.isNullOrEmpty(queryString)) {
            return path;
        }
        else {
            return path + '?' + queryString;
        }
    }

    public Charset getContentCharset() {
        if (headers.getContentType().isPresent()) {
            return headers.getContentType().get().getCharset();
        }
        return HttpHeaders.ContentType.DEFAULT_CHARSET;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("method", method)
                      .add("path", path)
                      .add("queryString", queryString)
                      .add("parameters", parameters)
                      .add("headers", headers)
                      .add("body", Arrays.toString(body))
                      .toString();
    }

    public static Builder getBuilder(String pathAndQueryString) {
        return builder(Method.GET, pathAndQueryString);
    }

    public static Builder postBuilder(String pathAndQueryString) {
        return builder(Method.POST, pathAndQueryString);
    }

    public static Builder builder(Method method, String pathAndQueryString) {
        return new Builder(method, pathAndQueryString);
    }

    private static class RequestParser {
        public HttpRequest parse(InputStream in) throws IOException {
            HttpRequest request = new HttpRequest();
            parseHead(in, request);
            parseBody(in, request);
            return request;
        }

        private void parseHead(InputStream in, final HttpRequest request) throws IOException {
            HttpLineReader lineReader = new HttpLineReader(in, StandardCharsets.US_ASCII);
            lineReader.readLines(new LineProcessor<Void>() {
                private boolean firstLine = true;
                private final HttpHeaders.Builder builder = HttpHeaders.builder();

                @Override
                public boolean processLine(String line) throws IOException {
                    if (firstLine) {
                        parseFirstLine(line, request);
                        firstLine = false;
                        return true;
                    }
                    else if (line.isEmpty()) {
                        return false;
                    }
                    else {
                        parseHeader(line, builder);
                        return true;
                    }
                }

                @Override
                public Void getResult() {
                    request.headers = builder.build();
                    return null;
                }
            });
        }

        private void parseHeader(String line, HttpHeaders.Builder builder) {
            Iterator<String> parts = Splitter.on(':').trimResults().split(line).iterator();
            builder.add(parts.next(), parts.next());
        }

        private void parseFirstLine(String line, HttpRequest request) {
            Iterator<String> parts = Splitter.on(' ').split(line).iterator();
            request.method = Method.valueOf(parts.next());
            String pathAndQueryString = parts.next();
            parsePathAndQueryString(pathAndQueryString, request);
        }

        private void parsePathAndQueryString(String pathAndQueryString, HttpRequest request) {
            try {
                Iterator<String> parts = Splitter.on('?').split(pathAndQueryString).iterator();
                request.path = parts.next();
                request.queryString = parts.hasNext() ? parts.next() : "";
                parseQueryString(request, request.queryString, StandardCharsets.ISO_8859_1);
            }
            catch (UnsupportedEncodingException e) {
                throw Throwables.propagate(e);
            }
        }

        private void parseQueryString(HttpRequest request,
                                      String queryString,
                                      Charset charset) throws UnsupportedEncodingException {
            ListMultimap<String, String> parameters = ArrayListMultimap.create();
            Splitter nameValuePairSplitter = Splitter.on('=');
            for (String nameValuePair : Splitter.on('&').omitEmptyStrings().split(queryString)) {
                List<String> pair = FluentIterable.from(nameValuePairSplitter.split(nameValuePair)).toList();
                parameters.put(pair.get(0), pair.size() < 2 ? "" : URLDecoder.decode(pair.get(1), charset.name()));
            }
            request.parameters = new HttpParameters(parameters);
        }

        private void parseBody(InputStream in, HttpRequest request) throws IOException {
            int contentLength = Integer.parseInt(request.getHeaders().getHeader(HttpHeaders.CONTENT_LENGTH).or("0"));
            Optional<HttpHeaders.ContentType> contentType = request.getHeaders().getContentType();
            byte[] body = NO_BODY;
            if (contentLength > 0) {
                body = new byte[contentLength];
                ByteStreams.readFully(in, body);
                if (contentType.isPresent() && contentType.get().getName().equals("application/x-www-form-urlencoded")) {
                    Charset charset = contentType.get().getCharset();
                    parseQueryString(request,
                                     new String(body, charset),
                                     charset);
                }
            }
            request.body = body;
        }
    }

    public static final class Builder {
        private final Method method;
        private final String pathAndQueryString;
        private final HttpHeaders.Builder headers = HttpHeaders.builder();
        private byte[] body = NO_BODY;

        public Builder(Method method, String pathAndQueryString) {
            this.method = Preconditions.checkNotNull(method);
            this.pathAndQueryString = Preconditions.checkNotNull(pathAndQueryString);
        }

        public Builder header(String name, String value) {
            headers.add(name, value);
            return this;
        }

        public Builder contentType(String name, Charset charset) {
            headers.setContentType(name, charset);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = Preconditions.checkNotNull(body);
            this.headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.length));
            return this;
        }

        /**
         * Sets the body as a string.
         * @param body the body, as a string. If this method is called before setting the content type
         * via {@link #contentType(String, java.nio.charset.Charset)} of {@link #header(String, String)},
         * then the default charset is used (ISO-8859-1) to encode the string. Else, the specified charset is used.
         */
        public Builder body(String body) {
            Preconditions.checkNotNull(body);
            Charset charset = HttpHeaders.ContentType.DEFAULT_CHARSET;
            if (headers.getContentType().isPresent()) {
                charset = headers.getContentType().get().getCharset();
            }
            return this.body(body.getBytes(charset));
        }

        public HttpRequest build() {
            HttpRequest request = new HttpRequest();
            request.method = method;
            RequestParser parser = new RequestParser();
            parser.parsePathAndQueryString(pathAndQueryString, request);
            request.headers = headers.build();
            try {
                parser.parseBody(new ByteArrayInputStream(body), request);
            }
            catch (IOException e) {
                // impossible
                throw new IllegalStateException(e);
            }
            return request;
        }
    }
}
