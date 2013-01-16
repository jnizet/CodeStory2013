package com.ninja_squad.jb.codestory.action;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpParameters;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.ninja_squad.jb.codestory.action.JajascriptAction.*;
import static org.fest.assertions.Assertions.*;

/**
 * Tests for Jajascript action
 * @author jbnizet.ext
 */
public class JajascriptActionTest {

    @Test
    public void shouldReturnBestPathinJSON() throws ParseException {
        String json = ("[{'VOL': 'A', 'DEPART': '0', 'DUREE': 5, 'PRIX': 10}"
                       + ", {'VOL': 'B', 'DEPART': 6, 'DUREE': 4, 'PRIX': 21}"
                       + ", {'VOL': 'C', 'DEPART': 8, 'DUREE': '1', 'PRIX': 10}"
                       + ", {'VOL': 'D', 'DEPART': 9, 'DUREE': 2, 'PRIX': 10}"
                       + ", {'VOL': 'E', 'DEPART': 12, 'DUREE': 1, 'PRIX': 10}"
                       + ", {'VOL': 'F', 'DEPART': 4, 'DUREE': 9, 'PRIX': 40}]").replace('\'', '"');


        HttpRequest request = new HttpRequest(HttpRequest.Method.POST,
                                              "/jajascript/optimize",
                                              HttpParameters.NO_PARAMETER,
                                              HttpHeaders.PLAIN_ASCII_TEXT,
                                              json.getBytes(StandardCharsets.US_ASCII));
        HttpResponse response = new JajascriptAction().execute(request);
        assertThat(response.getStatus()).isEqualTo(HttpResponse.Status._201_CREATED);
        assertThat(response.getHeaders().getContentType().get().getName()).isEqualTo("application/json");
        new JSONParser().parse(response.getBodyAsString(StandardCharsets.US_ASCII));
    }

    @Test
    public void shouldReturnBestPath() {
        List<JajascriptAction.Flight> flights = Lists.newArrayList(new Flight("A", 0, 5, 10),
                                                                   new Flight("B", 6, 4, 21),
                                                                   new Flight("C", 8, 1, 10),
                                                                   new Flight("D", 9, 2, 10),
                                                                   new Flight("E", 12, 1, 10),
                                                                   new Flight("F", 4, 9, 40));
        Path bestPath = new JajascriptAction().findBestPath(flights.toArray(new Flight[flights.size()]));
        assertThat(bestPath.getGain()).isEqualTo(41);
        assertThat(bestPath.getPath()).onProperty("name").containsExactly("A", "B", "E");
    }

    @Test
    public void testPerf() throws IOException {
        testIteration();
    }

    private void testIteration() throws IOException {
        Random random = new Random();
        JSONArray array = new JSONArray();
        for (int number = 1; number < 10001; number *= 10) {
            for (int i = 0; i < number; i++) {
                int start = random.nextInt(23);
                int duration = 1 + random.nextInt(24 - start);
                JSONObject o = new JSONObject();
                o.put("VOL", String.valueOf(i + 1));
                o.put("DEPART", start);
                o.put("DUREE", duration);
                o.put("PRIX", random.nextInt(21) + 1);
                array.add(o);
            }

            HttpRequest request = new HttpRequest(HttpRequest.Method.POST,
                                                  "/jajascript/optimize",
                                                  HttpParameters.NO_PARAMETER,
                                                  HttpHeaders.PLAIN_ASCII_TEXT,
                                                  array.toJSONString().getBytes(StandardCharsets.US_ASCII));
            Action action1 = new JajascriptAction();

            Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();
            HttpResponse response = action1.execute(request);
            stopwatch.stop();
            System.out.println("For " + number + ", result = " + stopwatch.elapsed(TimeUnit.MICROSECONDS) + "µs.");
            System.out.println("Result = " + response.getBodyAsString(StandardCharsets.US_ASCII));
        }
    }
}
