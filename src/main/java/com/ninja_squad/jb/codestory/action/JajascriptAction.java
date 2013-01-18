package com.ninja_squad.jb.codestory.action;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.ContentTypes;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Computes the best planning
 * @author jbnizet.ext
 */
public class JajascriptAction implements Action {

    @Override
    public HttpResponse execute(HttpRequest request) {
        String body = request.getBodyAsString();
        try {
            Flight[] flights = unmarshal(body);
            Path result = findBestPath(flights);
            return new HttpResponse(HttpResponse.Status._201_CREATED,
                                    HttpHeaders.builder().setContentType(ContentTypes.APPLICATION_JSON,
                                                                         StandardCharsets.US_ASCII).build(),
                                    marshal(result).getBytes(StandardCharsets.US_ASCII));
        }
        catch (Exception e) {
            System.out.println("Exception in Jajascript: ");
            e.printStackTrace();
            System.out.println("Jajascript body:");
            System.out.println(body);
            return HttpResponse.badRequest("Invalid data: " + body);
        }
    }

    /**
     * Transforms a JSON-encoded array of flights into an array of Flight instances
     */
    private Flight[] unmarshal(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        List<Map<String, Object>> array = (List<Map<String, Object>>) parser.parse(s);
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
        Arrays.sort(flights, ByDescendingStartTimeComparator.INSTANCE);


        // create the graph
        createGraph(flights);

        // find the leaves
        Iterable<Flight> leaves = FluentIterable.from(Arrays.asList(flights)).filter(LeafPredicate.INSTANCE);

        // get the best flight out of the leaves
        Flight bestFlight = getBestFlight(leaves);

        // create a path from the best leaf
        return createPath(bestFlight);
    }

    private void createGraph(Flight[] flights) {
        NavigableSet<Flight> flightsSortedByEndAndDuration =
            new TreeSet<>(ByEndDateAndDurationComparator.INSTANCE);
        for (Flight f : flights) {
            flightsSortedByEndAndDuration.add(f);
        }

        Flight fake = new Flight("fake", 0, -1, 0);
        for (Flight flight: flights) {
            fake.setEndTime(flight.getStartTime());

            Set<Flight> parents = flightsSortedByEndAndDuration.tailSet(fake, false);
            int biggestStartTimeInParents = Integer.MIN_VALUE;
            for (Flight parent : parents) {
                if (parent.getEndTime() > biggestStartTimeInParents) {
                    flight.addParent(parent);
                    if (parent.getStartTime() > biggestStartTimeInParents) {
                        biggestStartTimeInParents = parent.getStartTime();
                    }
                }
                else {
                    break;
                }
            }
        }
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
        private final List<Flight> path;
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
        private static Function<Flight, String> TO_NAME = new Function<Flight, String>() {
            @Nullable
            @Override
            public String apply(Flight input) {
                return input.getName();
            }
        };

        private final String name;
        private final int startTime;
        private final int duration;
        private int endTime;
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
            this.endTime = startTime + duration;
            this.price = price;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public boolean isLeaf() {
            return leaf;
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
            return Objects.toStringHelper(this)
                          .add("name", name)
                          .add("startTime", startTime)
                          .add("duration", duration)
                          .add("endTime", endTime)
                          .add("price", price)
                          .add("leaf", leaf)
                          .add("bestGain", bestGain)
                          .add("bestParent", bestParent == null ? null : bestParent.getName())
                          .add("parents", Lists.newArrayList(Iterables.transform(parents, TO_NAME)))
                          .toString();
        }

        public boolean isBestGainComputed() {
            return bestGainComputed;
        }


    }

    protected static class ByDescendingStartTimeComparator extends Ordering<Flight> {
        public static final ByDescendingStartTimeComparator INSTANCE = new ByDescendingStartTimeComparator();

        @Override
        public int compare(Flight left, Flight right) {
            return Integer.compare(right.getStartTime(), left.getStartTime());
        }
    }

    private static class LeafPredicate implements Predicate<Flight> {
        public static final LeafPredicate INSTANCE = new LeafPredicate();

        @Override
        public boolean apply(Flight input) {
            return input.isLeaf();
        }
    }

    private static class ByEndDateAndDurationComparator implements Comparator<Flight> {
        public static final ByEndDateAndDurationComparator INSTANCE = new ByEndDateAndDurationComparator();

        @Override
        public int compare(Flight o1, Flight o2) {
            int r = Integer.compare(o2.getEndTime(), o1.getEndTime());
            if (r == 0) {
                r = Integer.compare(o1.getDuration(), o2.getDuration());
            }
            if (r == 0) {
                r = Integer.compare(System.identityHashCode(o1), System.identityHashCode(o2));
            }
            return r;
        }
    }
}
