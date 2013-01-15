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

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Computes the best planning
 * @author jbnizet.ext
 */
public class JajascriptAction implements Action {

    @Override
    public HttpResponse execute(HttpRequest request) {
        String body = request.getBodyAsString();
        try {
            List<Flight> flights = unmarshal(body);
            Path result = findBestPath(flights);
            return new HttpResponse(HttpResponse.Status._200_OK,
                                    HttpHeaders.builder().setContentType("application/json", StandardCharsets.US_ASCII).build(),
                                    marshal(result).getBytes(StandardCharsets.US_ASCII));
        }
        catch (Exception e) {
            return HttpResponse.badRequest("Invalid data: " + body);
        }
    }

    private List<Flight> unmarshal(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        List<Map<String, Object>> array = (List<Map<String, Object>>) parser.parse(s);
        List<Flight> flights = Lists.newArrayListWithExpectedSize(array.size());
        for (Map<String, Object> o : array) {
            Flight flight = new Flight((String) o.get("VOL"),
                                       toInt(o.get("DEPART")),
                                       toInt(o.get("DUREE")),
                                       toInt(o.get("PRIX")));
            flights.add(flight);
        }
        return flights;
    }

    private int toInt(Object o) {
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        else {
            return Integer.parseInt((String) o);
        }
    }

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

    public Path findBestPath(List<Flight> flights) {
        Collections.sort(flights, Collections.reverseOrder(new ByStartTimeComparator()));
        for (int i = 0; i < flights.size(); i++) {
            Flight flight = flights.get(i);
            for (int j = i + 1; j < flights.size(); j++) {
                Flight parent = flights.get(j);
                if (flight.acceptAsParent(parent)) {
                    flight.addParent(parent);
                }
            }
        }

        Iterable<Flight> leaves = FluentIterable.from(flights).filter(new Predicate<Flight>() {
            @Override
            public boolean apply(Flight input) {
                return input.isLeaf();
            }
        });

        Path bestPath = getBestPath(leaves);

        return bestPath;
    }

    private Path getBestPath(Iterable<Flight> leaves) {
        Path bestPath = new Path();
        for (Flight flight : leaves) {
            Path path = computeBestPath(flight);
            if (path.getGain() > bestPath.getGain()) {
                bestPath = path;
            }
        }
        return bestPath;
    }

    private Path computeBestPath(Flight flight) {
        if (flight.getParents().isEmpty()) {
            return new Path(flight);
        }
        else {
            Path bestPath = getBestPath(flight.getParents());
            bestPath.addFlight(flight);
            return bestPath;
        }
    }

    protected static class Path {
        private List<Flight> path = Lists.newArrayList();
        private int gain;

        public Path() {
        }

        public Path(Flight flight) {
            addFlight(flight);
        }

        public void addFlight(Flight flight) {
            path.add(flight);
            gain += flight.getPrice();
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
        private String name;
        private int startTime;
        private int duration;
        private int price;
        private boolean leaf = true;

        // direct parents, sorted by descending start time
        private List<Flight> parents = Lists.newArrayList();

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

        @Override
        public String toString() {
            return name;
        }
    }

    private static class ByStartTimeComparator extends Ordering<Flight> {
        @Override
        public int compare(Flight left, Flight right) {
            return Integer.compare(left.getStartTime(), right.getStartTime());
        }
    }
}
