package com.ninja_squad.jb.codestory.action;

import com.google.common.collect.Lists;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpParameters;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.ninja_squad.jb.codestory.action.JajascriptAction.*;
import static org.fest.assertions.Assertions.assertThat;

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
        assertThat(response.getHeaders().getContentType().get().getName()).isEqualTo("application/json");
        new JSONParser().parse(response.getBodyAsString(StandardCharsets.US_ASCII));
    }

    @Test
    public void shouldReturnBestPath() {
        List<JajascriptAction.Flight> flights = Lists.newArrayList(new JajascriptAction.Flight("A", 0, 5, 10),
                                                                   new JajascriptAction.Flight("B", 6, 4, 21),
                                                                   new JajascriptAction.Flight("C", 8, 1, 10),
                                                                   new JajascriptAction.Flight("D", 9, 2, 10),
                                                                   new JajascriptAction.Flight("E", 12, 1, 10),
                                                                   new JajascriptAction.Flight("F", 4, 9, 40));
        Path bestPath = new JajascriptAction().findBestPath(flights);
        assertThat(bestPath.getGain()).isEqualTo(41);
        assertThat(bestPath.getPath()).onProperty("name").containsExactly("A", "B", "E");
    }
}
