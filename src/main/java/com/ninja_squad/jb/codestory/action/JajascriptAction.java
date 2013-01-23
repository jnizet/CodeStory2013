package com.ninja_squad.jb.codestory.action;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.ContentTypes;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;
import com.ninja_squad.jb.codestory.HttpStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;

/**
 * Computes the best planning
 * @author jbnizet.ext
 */
public class JajascriptAction implements Action {

    private Flight fake = new Flight("fake", 0, -1, 0);
    private NavigableSet<Flight> sortedFlights = Sets.newTreeSet(ByEndDateAndDurationComparator.INSTANCE);

    public JajascriptAction() {
    }

    JajascriptAction(Collection<Flight> flights) {
        sortedFlights.addAll(flights);
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        String body = request.getBodyAsString();
        try {
            unmarshal(body);
            Path result = findBestPath();
            return HttpResponse.builder()
                               .status(HttpStatus._201_CREATED)
                               .contentType(ContentTypes.APPLICATION_JSON, request.getContentCharset())
                               .body(marshal(result))
                               .build();
        }
        catch (Exception e) {
            System.out.println("Exception in Jajascript: ");
            e.printStackTrace();
            System.out.println("Jajascript body:");
            System.out.println(body);
            return StandardResponses.badRequest("Invalid data: " + body);
        }
    }

    /**
     * Transforms a JSON-encoded array of flights into an array of Flight instances
     */
    @SuppressWarnings("unchecked")
    private void unmarshal(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        List<Map<String, Object>> array = (List<Map<String, Object>>) parser.parse(s);
        for (Map<String, Object> o : array) {
            Flight flight = new Flight((String) o.get("VOL"),
                                       toInt(o.get("DEPART")),
                                       toInt(o.get("DUREE")),
                                       toInt(o.get("PRIX")));
            sortedFlights.add(flight);
        }
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
    @SuppressWarnings("unchecked")
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

    public Path findBestPath() {
        // find the best flight
        Flight bestFlight = findBestFlight();

        // create a path from the best leaf
        return createPath(bestFlight);
    }

    private Flight findBestFlight() {
        return findBestFlight(sortedFlights);
    }

    /**
     * Gets the list of direct parents of the given flight, i.e. the flights that can starte before the current flight
     * without overlapping, and which don't have any other non-overlapping flight between them and the given flight
     * @param flight
     * @return
     */
    private List<Flight> getParents(Flight flight) {
        fake.setEndTime(flight.getStartTime());
        List<Flight> result = Lists.newArrayList();
        Set<Flight> parents = sortedFlights.tailSet(fake, false);
        int biggestStartTimeInParents = Integer.MIN_VALUE;
        for (Flight parent : parents) {
            if (parent.getEndTime() > biggestStartTimeInParents) {
                result.add(parent);
                if (parent.getStartTime() > biggestStartTimeInParents) {
                    biggestStartTimeInParents = parent.getStartTime();
                }
            }
            else {
                break;
            }
        }
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

    /**
     * Finds the best flight among the given flights
     * @return the flight which has the best gain among the given flights. This method modifies the flights by
     * storing their best gain and best parent, in order to avoid recomputing them every time.
     */
    private Flight findBestFlight(Iterable<Flight> flights) {
        Flight bestFlight = null;
        for (Flight flight : flights) {
            computeBestGain(flight);
            if (bestFlight == null || flight.getBestGain() > bestFlight.getBestGain()) {
                bestFlight = flight;
            }
        }
        return bestFlight;
    }

    /**
     * If the given flight has its best gain already computed, returns immediately. Else,
     * gets the direct parents of the given flight, gets the best flight among them,
     * computes the best gain, and stores the best gain and best parent in the flight.
     */
    private void computeBestGain(Flight flight) {
        if (flight.isBestGainComputed()) {
            return; // already computed
        }

        List<Flight> parents = getParents(flight);
        if (parents.isEmpty()) {
            flight.setBestGainComptationResult(flight.getPrice(), null);
        }
        else {
            Flight bestParentFlight = findBestFlight(parents);
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
        private final String name;
        private final int startTime;
        private final int duration;
        private int endTime;
        private final int price;

        private boolean bestGainComputed;
        private int bestGain;
        private Flight bestParent;

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
                          .add("bestGain", bestGain)
                          .add("bestParent", bestParent == null ? null : bestParent.getName())
                          .toString();
        }

        public boolean isBestGainComputed() {
            return bestGainComputed;
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
