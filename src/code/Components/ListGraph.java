package code.Components;

import code.Interfaces.Graph;
import code.Interfaces.Node;

import java.io.Serializable;
import java.util.*;

public class ListGraph<T extends Node> implements Graph<T>, Serializable {
    private final Map<T, Set<Edge<T>>> locations = new HashMap<>();
    //private final static int ACCEPTABLE_TIME_TO_WAIT = 15;
    //private final static double AVG_SPEED_MPS = 1.5;

    @Override
    public void add(T node) {
        locations.putIfAbsent(node, new HashSet<>());
    }

    @Override
    public void addAll(List<T> nodes) {
        for (T node : nodes) {
            add(node);
        }
    }

    @Override
    public void connect(T node1, T node2, String trip_id, int arrivalTime, int departureTime) {
        if (!locations.containsKey(node1) || !locations.containsKey(node2)) {
            throw new NoSuchElementException("The node is not found");
        }
        if (departureTime < 0 || arrivalTime < 0) {
            throw new IllegalArgumentException("The arrival or departure-time is negative");
        }

        Edge<T> edge1 = new Edge(node2, node1, trip_id, arrivalTime, departureTime);

        Set<Edge<T>> fromDestinations = locations.get(node1);

        if (fromDestinations.contains(edge1)) {
            throw new IllegalStateException("The edge already exists");
        }
        fromDestinations.add(edge1);
    }

    @Override
    public void disconnect(T node1, T node2) {
        if (!locations.containsKey(node1) || !locations.containsKey(node2)) {
            throw new NoSuchElementException("The node is not found");
        }

        Edge<T> edgeToFrom = getEdgeBetween(node1, node2);

        if (edgeToFrom == null/* || edgeFromTo == null*/) {
            throw new IllegalStateException();
        }

        locations.get(node1).remove(edgeToFrom);
    }

    @Override
    public void remove(T node) {
        if (!locations.containsKey(node)) {
            throw new NoSuchElementException("The node is not found");
        }
        locations.remove(node);
        for (Set<Edge<T>> edges : locations.values()) {
            edges.removeIf(edge -> edge.getDestination().equals(node));
        }
    }

    @Override
    public void setConnectionWeight(T node1, T node2, int weight) {
        if (!locations.containsKey(node1) || !locations.containsKey(node2)) {
            throw new NoSuchElementException("The node is not found");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("The weight is negative");
        }

        Edge<T> edge1 = getEdgeBetween(node1, node2);
        Edge<T> edge2 = getEdgeBetween(node2, node1);

        if (edge1 == null || edge2 == null) {
            throw new NullPointerException();
        }

        edge1.setWeight(weight);
        edge2.setWeight(weight);
    }

    @Override
    public Set<T> getNodes() {
        return Collections.unmodifiableSet(locations.keySet());
    }

    @Override
    public Collection<Edge<T>> getEdgesFrom(T node) {
        if (!locations.containsKey(node)) {
            throw new NoSuchElementException("The node is not found");
        }
        Set<Edge<T>> edgeList = locations.get(node);
        return Set.copyOf(edgeList);
    }

    @Override
    public Edge<T> getEdgeBetween(T node1, T node2) {
        if (!locations.containsKey(node1) || !locations.containsKey(node2)) {
            throw new NoSuchElementException("The node is not found");
        }

        Set<Edge<T>> edges = locations.get(node1);
        for (Edge<T> e : edges) {
            if (e.getDestination().equals(node2)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public boolean pathExists(T from, T to) {
        Set<T> visited = new HashSet<>();

        return recursiveVisitAll(from, to, visited);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<T, Set<Edge<T>>> l : locations.entrySet()) {
            builder.append(l.getKey());
            for (Edge<T> edge : l.getValue()) {
                builder.append(edge.toString());
            }
        }
        return builder.toString();
    }

    public Map<String, List<Edge<T>>> getFastestPath(String startName, String goalName, int timeToBegin) {
        T start = getNodeByName(startName);
        T goal = getNodeByName(goalName);

        if (goal == null) {
            System.out.println("Could not find station with name: " + goalName);
            return null;
        }

        if (start == null) {
            System.out.println("Could not find station with name: " + startName);
            return null;
        }
        return getFastestPath(start, goal, timeToBegin);

    }

    public Map<String, List<Edge<T>>> getFastestPath(T start, T goal, int timeToBegin) {
        int time = timeToBegin;

        // Travel Scores
        Map<T, Integer> gScores = new HashMap<>();
        Map<T, Double> fScores = new HashMap<>();

        // INIT lists
        Queue<T> openQueue = new PriorityQueue<>(getComparatorForOpenList(fScores));
        Set<T> alreadyProcessed = new HashSet<>();
        Map<T, Edge<T>> cameFrom = new HashMap<>();


        int startG = 0;
        double startH = calculateEuclidean(start, goal);
        double startF = startG + startH;
        cameFrom.put(start, null);
        gScores.put(start, startG);
        fScores.put(start, startF);
        openQueue.add(start);

        T current = null;
        while (!openQueue.isEmpty()) {
            current = openQueue.remove();

            // Update time
            if (!current.equals(start)) {
                time = gScores.get(current);
            }

            // FOUND GOAL!
            if (current.equals(goal)) {
                return reconstruct_path(current, cameFrom);
            }

            for (Edge<T> edge : this.getEdgesFrom(current)) {
                T nextDestination = edge.getDestination();

                if (alreadyProcessed.contains(nextDestination) || !isEdgeInReasonableTime(time, edge.getDepartureTime()))
                    continue;


                if (!gScores.containsKey(nextDestination))
                    gScores.put(nextDestination, Integer.MAX_VALUE);
                if (!fScores.containsKey(nextDestination)) {
                    fScores.put(nextDestination, Double.POSITIVE_INFINITY);
                    updateOpenList(nextDestination, openQueue);
                }

                int arrivalTimeAtCurrent = time; //gScores.get(current);
                /*if (edge.getDepartureTime() < arrivalTimeAtCurrent) {
                    continue;  // Skip this edge - train already left
                }*/

                // Calculate waiting time + travel time
                int waitingTime = edge.getDepartureTime() - arrivalTimeAtCurrent;
                int tentative_gScore = arrivalTimeAtCurrent + waitingTime + edge.getWeight();

                // Control and update scores
                if (tentative_gScore < gScores.get(nextDestination)) {
                    updateScores(goal, edge, cameFrom, nextDestination, gScores, tentative_gScore, fScores, openQueue);
                }
            }

            alreadyProcessed.add(current);
        }
        return null;
    }


    // Hjälpmetod - getFastestPath()
    private void updateScores(T goal, Edge<T> edge, Map<T, Edge<T>> cameFrom, T nextDestination, Map<T, Integer> gScores, int tentative_gScore, Map<T, Double> fScores, Queue<T> openList) {
        cameFrom.put(nextDestination, edge);
        gScores.put(nextDestination, tentative_gScore);
        double fScore = tentative_gScore + calculateEuclidean(nextDestination, goal);
        fScores.put(nextDestination, fScore);

        updateOpenList(nextDestination, openList);
    }

    private void updateOpenList(T nextDestination, Queue<T> openList) {
        openList.remove(nextDestination);
        openList.add(nextDestination);
    }

    private Comparator<T> getComparatorForOpenList(Map<T, Double> fScores) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                double f1 = fScores.getOrDefault(o1, Double.POSITIVE_INFINITY);
                double f2 = fScores.getOrDefault(o2, Double.POSITIVE_INFINITY);

                if (f1 < f2) {
                    return -1;
                } else if (f1 > f2) {
                    return 1;
                }
                return 0;
            }
        };
    }

    private double calculateEuclidean(T start, T other) {
        int meters = 111000;
        final double metersPerMinute = 80 * 1000.0 / 60;

        //Latitude to meters
        double lat = (start.getLatitude() - other.getLatitude());
        double calLat = lat * meters;

        //Longitude to meters
        double lon = (start.getLongitude() - other.getLongitude());
        double calLon = lon * meters * Math.cos(lat);

        double i = Math.pow(calLat, 2);
        i += Math.pow(calLon, 2);
        i = Math.sqrt(i);
        return i / metersPerMinute;
    }

    private boolean isEdgeInReasonableTime(int currentTimeOnTrip, int departureTime) {
        return (departureTime >= currentTimeOnTrip);
    }

    private boolean recursiveVisitAll(T node1, T node2, Set<T> visited) {
        if (!locations.containsKey(node1) || !locations.containsKey(node2)) {
            return false;
        }

        visited.add(node1);
        if (node1.equals(node2)) {
            return true;
        }

        for (Edge<T> e : locations.get(node1)) {
            if (!visited.contains(e.getDestination())) {
                if (recursiveVisitAll(e.getDestination(), node2, visited)) {
                    return true;
                }
            }
        }
        return false;
    }


    private Map<String, List<Edge<T>>> reconstruct_path(T current, Map<T, Edge<T>> cameFrom) {
        Map<String, List<Edge<T>>> edgesByTrip = new LinkedHashMap<>();
        T station = current;
        while (cameFrom.containsKey(station)) {
            Edge<T> edge = cameFrom.get(station);
            if (edge == null) {
                break;
            }
            if (!edgesByTrip.containsKey(edge.getTrip_id())) {
                edgesByTrip.put(edge.getTrip_id(), new LinkedList<>());
            }
            station = edge.getSource();
            edgesByTrip.get(edge.getTrip_id()).addFirst(edge);
        }
        return edgesByTrip;
    }


    private T getNodeByName(String name) {
        for (T t : locations.keySet()) {
            if (name.trim().equalsIgnoreCase(t.getName())) {
                return t;
            }
        }
        return null;
    }

}