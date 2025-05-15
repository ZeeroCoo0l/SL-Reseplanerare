package code.Components;

import code.Interfaces.Node;

public class Station implements Node, Comparable<Station> {
    private final String name;
    private final Long id;
    private final double lat;
    private final double lon;
    private final String location_type;

    public Station(String name, String id, double lat, double lon, String locationType) {
        this.name = name;
        this.id = Long.parseLong(id);
        this.lat = lat;
        this.lon = lon;
        location_type = locationType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public double getLongitude() {
        return lon;
    }

    @Override
    public double getLatitude() {
        return lat;
    }


    @Override
    public String toString() {
        return "[" + name + " : " + id + " | " + lat + " : " + lon + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;

        return this.id.equals(((Station) obj).id);
    }

    @Override
    public int hashCode() {
        int i = name.hashCode();
        i += id.hashCode() * 37;
        i += lat * 37;
        i += lon * 37;
        return i;
    }

    @Override
    public int compareTo(Station o) {
        return this.id.compareTo(o.id);
    }
}
