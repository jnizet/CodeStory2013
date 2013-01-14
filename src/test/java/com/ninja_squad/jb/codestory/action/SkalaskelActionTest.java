package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.ninja_squad.jb.codestory.action.SkalaskelAction.*;
import static org.fest.assertions.Assertions.*;

/**
 * Tests form Skalaskel action
 * @author JB
 */
public class SkalaskelActionTest {

    @Test
    public void shouldReturnBadRequestForNoAmount() throws IOException {
        checkBadRequest("");
    }

    @Test
    public void shouldReturnBadRequestForBadAmount() throws IOException {
        checkBadRequest("hello");
    }

    @Test
    public void shouldReturnJSONRequestForAmount() throws IOException {
        SkalaskelAction action = new SkalaskelAction();
        HttpRequest request = HttpRequest.get("/scalaskel/change/7");
        HttpResponse result = action.execute(request);
        assertThat(result.getStatus()).isEqualTo(HttpResponse.Status._200_OK);
        assertThat(result.getHeaders().getContentType().get().getName()).isEqualTo("application/json");
        assertThat(result.getBodyAsString(StandardCharsets.US_ASCII)).isIn(
            "[{'baz':'0';'qix':'0';'bar':'1';'foo':'0'},{'baz':'0';'qix':'0';'bar':'0';'foo':'7'}]".replace("'", "\""),
            "[{'baz':'0';'qix':'0';'bar':'0';'foo':'7'},{'baz':'0';'qix':'0';'bar':'1';'foo':'0'}]".replace("'", "\""));
    }

    private void checkBadRequest(String badAmount) throws IOException {
        SkalaskelAction action = new SkalaskelAction();
        HttpRequest request = HttpRequest.get("/scalaskel/change/" + badAmount);
        HttpResponse result = action.execute(request);
        assertThat(result.getStatus()).isEqualTo(HttpResponse.Status._400_BAD_REQUEST);
        assertThat(result.getBodyAsString(StandardCharsets.US_ASCII)).isEqualTo("Bad amount: " + badAmount);
    }

    @Test
    public void testChange() throws Exception {
        SkalaskelAction action = new SkalaskelAction();
        assertThat(action.change(1)).containsOnly(new Change(0, 0, 0, 1));
        assertThat(action.change(6)).containsOnly(new Change(0, 0, 0, 6));
        assertThat(action.change(7)).containsOnly(new Change(0, 0, 1, 0), new Change(0, 0, 0, 7));
        assertThat(action.change(8)).containsOnly(new Change(0, 0, 1, 1), new Change(0, 0, 0, 8));
        assertThat(action.change(14)).containsOnly(new Change(0, 1, 0, 3),
                                                   new Change(0, 0, 2, 0),
                                                   new Change(0, 0, 1, 7),
                                                   new Change(0, 0, 0, 14));
        assertThat(action.change(21)).containsOnly(new Change(1, 0, 0, 0),
                                                   new Change(0, 1, 1, 3),
                                                   new Change(0, 1, 0, 10),
                                                   new Change(0, 0, 3, 0),
                                                   new Change(0, 0, 2, 7),
                                                   new Change(0, 0, 1, 14),
                                                   new Change(0, 0, 0, 21));
    }
}
