package com.ninja_squad.jb.codestory.action;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Computes the best planning
 * @author jbnizet.ext
 */
public class JajascriptAction implements Action {

    @Override
    public HttpResponse execute(HttpRequest request) {
        byte[] body = request.getBody();
        try {
            Flight[] flights = unmarshal(request);
            Path result = findBestPath(flights);
            HttpResponse response = new HttpResponse(HttpResponse.Status._201_CREATED,
                                    HttpHeaders.builder().setContentType("application/json", StandardCharsets.US_ASCII).build(),
                                    marshal(result).getBytes(StandardCharsets.US_ASCII));
            return response;
        }
        catch (Exception e) {
            return HttpResponse.badRequest("Invalid data: " + body);
        }
    }

    /**
     * Transforms a JSON-encoded array of flights into an array of Flight instances
     */
    private Flight[] unmarshal(HttpRequest request) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        List<Map<String, Object>> array =
            (List<Map<String, Object>>) parser.parse(new InputStreamReader(new ByteArrayInputStream(request.getBody()), request.getContentCharset()));
        Flight[] flights = new Flight[array.size()];
        int i = 0;
        for (Map<String, Object> o : array) {
            Flight flight = new Flight((String) o.get("VOL"),
                                       toInt(o.get("DEPART")),
                                       toInt(o.get("DUREE")),
                                       toInt(o.get("PRIX")));
            flights[i] = flight;
            i++;
        }
        return flights;
    }

    /**
     * Transforms a integer, encoded as a number or as a String, into an int
     */
    private int toInt(Object o) {
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        else {
            return Integer.parseInt((String) o);
        }
    }

    /**
     * Transforms a path into a JSON string
     */
    private String marshal(Path path) {
        JSONObject o = new JSONObject();
        o.put("gain", path.getGain());
        JSONArray pathArray = new JSONArray();
        for (Flight flight : path.getPath()) {
            pathArray.add(flight.getName());
        }
        o.put("path", pathArray);
        return o.toJSONString();
    }

    /**
     * Finds the best path among an array of flights. It works this way:
     * <ol>
     *    <li>Sort the flights by start date, in descending order (latest starting first)</li>
     *    <li>In each flight, store its direct parents, i.e. the flights that can happen before
     *        without overlapping. This creates a graph of flights.</li>
     *    <li>For each leaf flight, compute and store the best parent and cumulated best gain,
     *    recursively. Find the leaf with the best gain, and create a path from this leaf.</li>
     * </ol>
     * @param flights the flights to plan
     * @return the best path
     */
    public Path findBestPath(Flight[] flights) {
        // sort
        Arrays.sort(flights, new ByDescendingStartTimeComparator());

        // create the graph
        for (int i = 0; i < flights.length; i++) {
            Flight flight = flights[i];
            for (int j = i + 1; j < flights.length; j++) {
                Flight parent = flights[j];
                if (flight.acceptAsParent(parent)) {
                    flight.addParent(parent);
                }
            }
        }

        // find the leaves
        Iterable<Flight> leaves = FluentIterable.from(Arrays.asList(flights)).filter(new Predicate<Flight>() {
            @Override
            public boolean apply(Flight input) {
                return input.isLeaf();
            }
        });

        // get the best flight out of the leaves
        Flight bestFlight = getBestFlight(leaves);

        // create a path from the best leaf
        Path result = createPath(bestFlight);

        return result;
    }

    private Path createPath(Flight flight) {
        LinkedList<Flight> path = Lists.newLinkedList();
        int gain = flight == null ? 0 : flight.getBestGain();
        while (flight != null) {
            path.addFirst(flight);
            flight = flight.getBestParent();
        }
        return new Path(path, gain);
    }

    private Flight getBestFlight(Iterable<Flight> flights) {
        Flight bestFlight = null;
        for (Flight flight : flights) {
            computeBestPath(flight);
            if (bestFlight == null || flight.getBestGain() > bestFlight.getBestGain()) {
                bestFlight = flight;
            }
        }
        return bestFlight;
    }

    private void computeBestPath(Flight flight) {
        if (flight.isBestGainComputed()) {
            return; // already computed
        }

        if (flight.getParents().isEmpty()) {
            flight.setBestGainComptationResult(flight.getPrice(), null);
        }
        else {
            Flight bestParentFlight = getBestFlight(flight.getParents());
            flight.setBestGainComptationResult(flight.getPrice() + bestParentFlight.getBestGain(),
                                               bestParentFlight);
        }
    }

    protected static class Path {
        private List<Flight> path = Lists.newArrayList();
        private final int gain;

        public Path(List<Flight> path, int gain) {
            this.path = path;
            this.gain = gain;
        }

        public List<Flight> getPath() {
            return path;
        }

        public int getGain() {
            return gain;
        }

        @Override
        public String toString() {
            return path.toString() + "-" + gain;
        }
    }

    protected static class Flight {
        private final String name;
        private final int startTime;
        private final int duration;
        private final int price;
        private boolean leaf = true;

        private boolean bestGainComputed;
        private int bestGain;
        private Flight bestParent;

        // direct parents, sorted by descending start time
        private final List<Flight> parents = Lists.newArrayList();

        public Flight(String name, int startTime, int duration, int price) {
            this.name = name;
            this.startTime = startTime;
            this.duration = duration;
            this.price = price;
        }

        public int getEndTime() {
            return startTime + duration;
        }

        public boolean isLeaf() {
            return leaf;
        }

        public boolean acceptAsParent(Flight flight) {
            return flight.getEndTime() <= this.startTime
                && (parents.isEmpty() || parents.get(0).startTime < flight.getEndTime());
        }

        public void addParent(Flight flight) {
            parents.add(flight);
            flight.leaf = false;
        }

        public String getName() {
            return name;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getDuration() {
            return duration;
        }

        public int getPrice() {
            return price;
        }

        public List<Flight> getParents() {
            return parents;
        }

        public int getBestGain() {
            return bestGain;
        }

        public void setBestGainComptationResult(int bestGain, Flight bestParent) {
            this.bestGain = bestGain;
            this.bestParent = bestParent;
            this.bestGainComputed = true;
        }

        public Flight getBestParent() {
            return bestParent;
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean isBestGainComputed() {
            return bestGainComputed;
        }
    }

    private static class ByDescendingStartTimeComparator extends Ordering<Flight> {
        @Override
        public int compare(Flight left, Flight right) {
            return -Integer.compare(left.getStartTime(), right.getStartTime());
        }
    }
}
