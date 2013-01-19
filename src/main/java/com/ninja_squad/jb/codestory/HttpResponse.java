package com.ninja_squad.jb.codestory;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An HTTP response
 * @author JB
 */
public final class HttpResponse {

    private final HttpStatus status;
    private final HttpHeaders headers;
    private final byte[] body;

    private HttpResponse(Builder builder) {
        this.status = Preconditions.checkNotNull(builder.status);
        this.headers = builder.headers.build();
        this.body = builder.body;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyAsString(Charset charset) {
        return new String(body, charset);
    }

    public void send(OutputStream out) throws IOException {
        String EOL = "\r\n";
        Writer writer = new OutputStreamWriter(out, StandardCharsets.US_ASCII);

        // status line
        writer.write("HTTP/1.1 ");
        writer.write(String.valueOf(status.getCode()));
        writer.write(" ");
        writer.write(status.getReason());
        writer.write(EOL);

        // always there headers
        writer.write("Server: JB's CodeStory Server");
        writer.write(EOL);
        if (body.length > 0) {
            writer.write("Content-Length:");
            writer.write(String.valueOf(body.length));
            writer.write(EOL);
        }

        // additional headers, including content type and charset
        headers.writeTo(writer);

        // end of headers separation line
        writer.write(EOL);
        writer.flush();

        // body
        out.write(body);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private static final byte[] NO_BODY = new byte[0];

        private HttpStatus status;
        private byte[] body = NO_BODY;
        private final HttpHeaders.Builder headers = HttpHeaders.builder();

        private Builder() {
        }

        public Builder status(HttpStatus status) {
            this.status = Preconditions.checkNotNull(status);
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.add(name, value);
            return this;
        }

        /**
         * Sets the content type and charset of the response. If this method is called after
         * {@link #body(String)} has been called, and if the charset is not the default charset (
         * ISO-8859-1), then the response will be incorrect, so this method should be called before
         * setting the body as a String.
         */
        public Builder contentType(String contentTypeName, Charset charset) {
            headers.setContentType(contentTypeName, charset);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = Preconditions.checkNotNull(body);
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
            this.body = body.getBytes(charset);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
