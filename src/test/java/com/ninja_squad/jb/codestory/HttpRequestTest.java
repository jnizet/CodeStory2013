package com.ninja_squad.jb.codestory;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.fest.assertions.Assertions.*;

/**
 * Tests for Http
 * @author JB
 */
public class HttpRequestTest {
    @Test
    public void parseWithGetShouldWorkFine() throws IOException {
        byte[] request =
            ("GET /somePath?a=1&b=param+with+spaces&a=3 HTTP/1.1\r\n"
             + "Host: localhost\r\n"
             + "User-Agent: test\r\n"
             + "\r\n").getBytes(StandardCharsets.US_ASCII);
        HttpRequest result = HttpRequest.parse(new ByteArrayInputStream(request));
        assertThat(result.getBody()).hasSize(0);
        assertThat(result.getHeaders().getHeader("host").get()).isEqualTo("localhost");
        assertThat(result.getHeaders().getHeader("user-agent").get()).isEqualTo("test");
        assertThat(result.getPath()).isEqualTo("/somePath");
        assertThat(result.getParameters().getSingleParameter("b").get()).isEqualTo("param with spaces");
        assertThat(result.getParameters().getMultipleParameters("a")).containsExactly("1", "3");
        assertThat(result.getMethod()).isEqualTo(HttpRequest.Method.GET);
    }

    @Test
    public void parseWithPostWithoutFormShouldWorkFine() throws IOException {
        String body = "Hello world";
        byte[] request =
            ("POST /somePath HTTP/1.1\r\n"
             + "Host: localhost\r\n"
             + "Content-Length:" + body.length() + "\r\n"
             + "\r\n"
            + body).getBytes(StandardCharsets.US_ASCII);
        HttpRequest result = HttpRequest.parse(new ByteArrayInputStream(request));
        assertThat(new String(result.getBody(), StandardCharsets.US_ASCII)).isEqualTo(body);
        assertThat(result.getHeaders().getHeader("host").get()).isEqualTo("localhost");
        assertThat(result.getPath()).isEqualTo("/somePath");
        assertThat(result.getParameters().asMap().asMap()).isEmpty();
        assertThat(result.getMethod()).isEqualTo(HttpRequest.Method.POST);
    }

    @Test
    public void parseWithPostWitFormShouldWorkFine() throws IOException {
        String body = "a=1&b=param+with+spaces&a=3";
        byte[] request =
            ("POST /somePath HTTP/1.1\r\n"
             + "Host: localhost\r\n"
             + "Content-Length:" + body.length() + "\r\n"
             + "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
             + "\r\n"
             + body).getBytes(StandardCharsets.US_ASCII);
        HttpRequest result = HttpRequest.parse(new ByteArrayInputStream(request));
        assertThat(new String(result.getBody(), StandardCharsets.US_ASCII)).isEqualTo(body);
        assertThat(result.getHeaders().getHeader("host").get()).isEqualTo("localhost");
        assertThat(result.getPath()).isEqualTo("/somePath");
        assertThat(result.getParameters().getSingleParameter("b").get()).isEqualTo("param with spaces");
        assertThat(result.getParameters().getMultipleParameters("a")).containsExactly("1", "3");
        assertThat(result.getMethod()).isEqualTo(HttpRequest.Method.POST);
    }
}
