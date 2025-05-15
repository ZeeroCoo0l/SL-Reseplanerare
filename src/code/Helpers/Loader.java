package code.Helpers;

import code.Components.ListGraph;
import code.Components.Station;
import code.Interfaces.Node;

import java.io.*;


public class Loader {
    static BufferedReader reader;
    static final String FILENAME_SAVED_STATIONS = "src/sl_gtfs_data/sl_stops.txt";
    static final String FILENAME_SAVE_DEPARTURES = "src/sl_gtfs_data/sl_stop_times.txt";
    static int count = 0;


    public static <T extends Node> ListGraph<T> createGraph(){
        return loadDepartures();
    }

    protected static <T extends Node> void loadStations(String fileName, ListGraph<T> graph) {
        try {
            reader = new BufferedReader(new FileReader(fileName));

            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                parseStations(line, graph);
            }

            reader.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends Node> void parseStations(String line, ListGraph<T> graph) {
        String[] parts = line.split(",");
        String stop_id = parts[0].trim();
        String name = parts[1].trim();
        double lat = Double.parseDouble(parts[2].trim());
        double lon = Double.parseDouble(parts[3].trim());

        Node station = new Station(name, stop_id, lat, lon, null);

        graph.add((T) station);
    }

    public static <T extends Node> ListGraph<T> loadDepartures() {
        int countEdges = 0;
        ListGraph<T> listGraph = new ListGraph<>();

        // Load graph with keys (stations)
        loadStations(FILENAME_SAVED_STATIONS, listGraph);

        try {
            reader = new BufferedReader(new FileReader(FILENAME_SAVE_DEPARTURES));

            String line = reader.readLine();
            Temp last = null;
            String currentTrip = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String trip_id = parts[0].trim();

                String arr_time = parts[1].trim();
                String dep_time = parts[2].trim();

                Long stop_id = Long.parseLong(parts[3].trim());
                int stop_sequence = Integer.parseInt(parts[4].trim());
                int pickup_type = Integer.parseInt(parts[5].trim());
                int drop_off_type = Integer.parseInt(parts[6].trim());

                if(currentTrip == null) currentTrip = trip_id;

                Temp current = new Temp(trip_id, arr_time, dep_time, stop_id, stop_sequence, pickup_type, drop_off_type);

                count++;
                if(drop_off_type == 1){
                    currentTrip = trip_id;
                    last = new Temp(trip_id, arr_time, dep_time, stop_id, stop_sequence, pickup_type, drop_off_type);
                    continue;
                }

                if(last != null){
                    createEdge(current, last, listGraph);
                }
                last = current;
            }

            System.out.println("COUNT: " + countEdges);
            reader.close();
            return listGraph;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends Node> void createEdge(Temp current, Temp last, ListGraph<T> listGraph) {
        T from = null;
        T destination = null;
        for (T node : listGraph.getNodes()) {
            if(node.getID().equals(current.stop_id))
                destination = node;

            if(node.getID().equals(last.stop_id))
                from = node;
        }

        //System.out.println(current.arr_time + " - " + last.dep_time);
        int arrMinutes = timeToMinutes(current.arr_time);
        int depMinutes = timeToMinutes(last.dep_time);
        //int weight = depMinutes - arrMinutes;


        listGraph.connect(from, destination, current.trip_id, arrMinutes, depMinutes);
    }

    private static int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int t = 0;
        t += 60 * Integer.parseInt(parts[0]);
        t += Integer.parseInt(parts[1]);
        return t;
    }

    private static class Temp{
        private final String trip_id;
        private final String arr_time;
        private final String dep_time;
        private final Long stop_id;
        private final int stop_sequence;
        private final int pickup_type;
        private final int drop_off_type;

        Temp(String trip_id, String arr_time, String dep_time, Long stop_id, int stop_sequence, int pickup_type, int drop_off_type){
            this.trip_id = trip_id;
            this.arr_time = arr_time;
            this.dep_time = dep_time;
            this.stop_id = stop_id;
            this.stop_sequence = stop_sequence;
            this.pickup_type = pickup_type;
            this.drop_off_type = drop_off_type;
        }
    }



}
