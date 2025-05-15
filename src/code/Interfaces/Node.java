package code.Interfaces;

import code.Components.Station;

import java.util.List;

public interface Node<T>{
    String getName();

    Long getID();

    double getLongitude();

    double getLatitude();

    //public boolean isNearby(Node node);

    //public void addNearbyStation(Station station);

    //public List<T> getNearbyStations();

}
