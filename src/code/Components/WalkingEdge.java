package code.Components;

import code.Interfaces.Node;

public class WalkingEdge<T extends Node> extends Edge<T> {

    /**
     * @param destination   Node where the edge is pointing to.
     * @param source        Node that the edge comes from
     * @param trip_id       ID of trip. This will be used to track time for trip.
     * @param arrivalTime   time of arrival to destination in minutes of the day.
     * @param departureTime time of arrival from station in minutes of the day.
     *
     */
    public WalkingEdge(T destination, T source, String trip_id, int arrivalTime, int departureTime) {
        super(destination, source, trip_id, arrivalTime, departureTime);
    }


}
