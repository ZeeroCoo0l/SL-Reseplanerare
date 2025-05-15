package code.Components;

import code.Interfaces.Node;

import java.io.Serializable;
import java.util.Objects;


public class Edge<T extends Node> implements Serializable {
    private final T destination;
    private final T source;
    private final String trip_id;
    private int weight;
    private final int arrivalTime;
    private final int departureTime;


    public Edge(T destination, T source, String trip_id, int arrivalTime, int departureTime) {
        this.destination = destination;
        this.source = source;
        this.trip_id = trip_id;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;

        // Beräknar restiden till stationen.
        this.weight = arrivalTime - departureTime;
    }

    public T getDestination() {
        return destination;
    }

    public T getSource() {
        return source;
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("The weight is negative");
        }
        this.weight = weight;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getDepartureTime() {
        return departureTime;
    }

    public String getTrip_id() {
        return trip_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge edge) {
            return destination.equals(edge.destination) && trip_id.equals(edge.trip_id) && weight == edge.getWeight() && departureTime == edge.getDepartureTime() && arrivalTime == edge.getArrivalTime();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, trip_id, weight, arrivalTime, departureTime);
    }

    @Override
    public String toString() {
        return "till " + destination + " med id " + trip_id + " och tar " + weight + " minuter";
    }

}
