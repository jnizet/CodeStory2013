package com.ninja_squad.jb.codestory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * An HTTP response
 * @author JB
 */
public class HttpResponse {
    private static final byte[] NO_BODY = new byte[0];

    private final int status;
    private final HttpHeaders headers;
    private final byte[] body;

    public HttpResponse(int status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body == null ? NO_BODY : body;
    }

    public int getStatus() {
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
        Writer writer = new OutputStreamWriter(out);
        writer.write("HTTP/1.1 ");
        writer.write(String.valueOf(status));
        writer.write(EOL);
        writer.write("Server: JB's CodeStory Server");
        writer.write(EOL);
        if (body.length > 0) {
            writer.write("Content-Length:");
            writer.write(body.length);
            writer.write(EOL);
        }

        for (Map.Entry<String, String> header : headers.asMap().entrySet()) {
            writer.write(header.getKey());
            writer.write(':');
            writer.write(header.getValue());
            writer.write(EOL);
        }
        writer.write(EOL);
        writer.flush();
        out.write(body);
    }
}
