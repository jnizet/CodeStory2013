package com.ninja_squad.jb.codestory;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.LineProcessor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An HTTP request
 * @author JB
 */
public class HttpRequest {
    private static final byte[] NO_BODY = new byte[0];

    public static enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        HEAD
    }

    private Method method;
    private String path;
    private String pathAndQueryString;
    private HttpParameters parameters;
    private HttpHeaders headers;
    private byte[] body = NO_BODY;

    public HttpRequest(@Nonnull Method method,
                       @Nonnull String path,
                       @Nonnull HttpParameters parameters,
                       @Nonnull HttpHeaders headers,
                       byte[] body) {
        this.method = Preconditions.checkNotNull(method);
        this.path = Preconditions.checkNotNull(path);
        this.parameters = Preconditions.checkNotNull(parameters);
        this.headers = Preconditions.checkNotNull(headers);
        this.body = body == null ? NO_BODY : body;
    }

    private HttpRequest() {
    }

    public static HttpRequest parse(InputStream in) throws IOException {
        return new RequestParser().parse(in);
    }

    public static HttpRequest get(@Nonnull String pathAndQueryString) {
        Preconditions.checkNotNull(pathAndQueryString);
        HttpRequest request = new HttpRequest();
        request.method = Method.GET;
        request.pathAndQueryString = pathAndQueryString;
        new RequestParser().parsePathAndQueryString(pathAndQueryString, request);
        request.headers = HttpHeaders.NO_HEADER;
        request.body = NO_BODY;
        return request;
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
        return pathAndQueryString;
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
                      .add("pathAndQueryString", pathAndQueryString)
                      .add("parameters", parameters)
                      .add("headers", headers)
                      .add("body", Arrays.toString(body))
                      .toString();
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
                private final Map<String, String> headers = Maps.newHashMap();

                @Override
                public boolean processLine(String line) throws IOException {
                    if (firstLine) {
                        parseFirstLine(line, request);
                        firstLine = false;
                        return true;
                    }
                    else if (line.isEmpty()) {
                        request.headers = new HttpHeaders(headers);
                        return false;
                    }
                    else {
                        parseHeader(line, headers);
                        return true;
                    }
                }

                @Override
                public Void getResult() {
                    return null;
                }
            });
        }

        private void parseHeader(String line, Map<String, String> headers) {
            Iterator<String> parts = Splitter.on(':').trimResults().split(line).iterator();
            headers.put(parts.next().toLowerCase(), parts.next());
        }

        private void parseFirstLine(String line, HttpRequest request) {
            Iterator<String> parts = Splitter.on(' ').split(line).iterator();
            request.method = Method.valueOf(parts.next());
            request.pathAndQueryString = parts.next();
            parsePathAndQueryString(request.pathAndQueryString, request);
        }

        private void parsePathAndQueryString(String pathAndQueryString, HttpRequest request) {
            try {
                Iterator<String> parts = Splitter.on('?').split(pathAndQueryString).iterator();
                request.path = parts.next();
                String queryString = parts.hasNext() ? parts.next() : "";
                parseQueryString(request, queryString, StandardCharsets.ISO_8859_1);
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
}
