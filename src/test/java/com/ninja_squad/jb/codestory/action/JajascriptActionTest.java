package com.ninja_squad.jb.codestory.action;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.ContentTypes;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import com.ninja_squad.jb.codestory.HttpStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
    public void shouldReturnBadData() {
        String json = "[{'VOL': 'A'";

        HttpRequest request = HttpRequest.postBuilder("/jajascript/optimize")
                                         .contentType(ContentTypes.APPLICATION_JSON, StandardCharsets.US_ASCII)
                                         .body(json)
                                         .build();
        HttpResponse response = new JajascriptAction().execute(request);
        assertThat(response.getStatus()).isEqualTo(HttpStatus._400_BAD_REQUEST);
    }


    @Test
    public void shouldReturnEmptyPathInJSON() throws ParseException {
        String json = "[]";
        HttpRequest request = HttpRequest.postBuilder("/jajascript/optimize")
                                         .contentType(ContentTypes.APPLICATION_JSON, StandardCharsets.US_ASCII)
                                         .body(json)
                                         .build();
        HttpResponse response = new JajascriptAction().execute(request);
        assertThat(response.getStatus()).isEqualTo(HttpStatus._201_CREATED);
        assertThat(response.getHeaders().getContentType().get().getName()).isEqualTo(ContentTypes.APPLICATION_JSON);
        new JSONParser().parse(response.getBodyAsString(StandardCharsets.US_ASCII));
        assertThat(response.getBodyAsString(StandardCharsets.US_ASCII)).isEqualTo("{\"path\":[],\"gain\":0}");
    }

    @Test
    public void shouldReturnBestPathInJSON() throws ParseException {
        String json = ("[{'VOL': 'A', 'DEPART': '0', 'DUREE': 5, 'PRIX': 10}"
                       + ", {'VOL': 'B', 'DEPART': 6, 'DUREE': 4, 'PRIX': 21}"
                       + ", {'VOL': 'C', 'DEPART': 8, 'DUREE': '1', 'PRIX': 10}"
                       + ", {'VOL': 'D', 'DEPART': 9, 'DUREE': 2, 'PRIX': 10}"
                       + ", {'VOL': 'E', 'DEPART': 12, 'DUREE': 1, 'PRIX': 10}"
                       + ", {'VOL': 'F', 'DEPART': 4, 'DUREE': 9, 'PRIX': 40}]").replace('\'', '"');


        HttpRequest request = HttpRequest.postBuilder("/jajascript/optimize")
                                         .contentType(ContentTypes.APPLICATION_JSON, StandardCharsets.US_ASCII)
                                         .body(json)
                                         .build();
        HttpResponse response = new JajascriptAction().execute(request);
        assertThat(response.getStatus()).isEqualTo(HttpStatus._201_CREATED);
        assertThat(response.getHeaders().getContentType().get().getName()).isEqualTo(ContentTypes.APPLICATION_JSON);
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

        flights = Lists.newArrayList(new Flight("A", 0, 5, 10),
                                     new Flight("B", 5, 5, 10),
                                     new Flight("C", 5, 5, 30),
                                     new Flight("D", 5, 5, 20),
                                     new Flight("E", 10, 3, 10),
                                     new Flight("F", 13, 9, 40));
        bestPath = new JajascriptAction().findBestPath(flights.toArray(new Flight[flights.size()]));
        assertThat(bestPath.getGain()).isEqualTo(90);
        assertThat(bestPath.getPath()).onProperty("name").containsExactly("A", "C", "E", "F");
    }

    @Test
    public void shouldCorrectPath() {
        List<JajascriptAction.Flight> flights = Lists.newArrayList();
        Map<String, Flight> flightsByName = Maps.newHashMap();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int start = random.nextInt(23);
            int duration = 1 + random.nextInt(24 - start);
            Flight f = new Flight(String.valueOf(i + 1), start, duration, random.nextInt(21) + 1);
            flights.add(f);
            flightsByName.put(f.getName(), f);
        }

        Path bestPath = new JajascriptAction().findBestPath(flights.toArray(new Flight[flights.size()]));

        int gain = bestPath.getPath().get(0).getPrice();
        for (int i = 1; i < bestPath.getPath().size(); i++) {
            Flight f = bestPath.getPath().get(i);
            Flight before = bestPath.getPath().get(i - 1);
            assertThat(before.getStartTime() + before.getDuration() <= f.getStartTime());
            gain += f.getPrice();
        }
        assertThat(bestPath.getGain() == gain);

        /*
        Collections.sort(flights, ByDescendingStartTimeComparator.INSTANCE);
        for (Flight f : flights) {
            System.out.println(f);
        }
        System.out.println("bestPath = " + bestPath);
        */
    }

    @Test
    public void testPerf() throws IOException {
        for (int i = 0; i< 10; i++) {
            testIteration();
        }
    }

    private void testIteration() throws IOException {
        for (int number = 1; number < 10001; number *= 10) {
            String jsonString = generateJajascriptJSON(number);
            HttpRequest request = HttpRequest.postBuilder("/jajascript/optimize")
                                             .contentType(ContentTypes.APPLICATION_JSON, StandardCharsets.US_ASCII)
                                             .body(jsonString)
                                             .build();
            Action action1 = new JajascriptAction();

            Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();
            HttpResponse response = action1.execute(request);
            stopwatch.stop();
            System.out.println("For " + number + ", result = " + stopwatch.elapsed(TimeUnit.MICROSECONDS) + "µs.");
            System.out.println("Result = " + response.getBodyAsString(StandardCharsets.US_ASCII));
        }
    }

    public static String generateJajascriptJSON(int number) {
        Random random = new Random();
        JSONArray array = new JSONArray();
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

        return array.toJSONString();
    }
}
