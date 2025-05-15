package code;

import code.Components.Edge;
import code.Components.ListGraph;
import code.Components.Station;
import code.Helpers.Loader;
import code.Interfaces.Node;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

    public static <T extends Node> void main(String[] args) {
        ListGraph<T> graph = Loader.createGraph();

        if(true){
            testGraphReturnNull(graph);
            return;
        }

        Random rnd = new Random();
        int sum = 0;
        T n1 = null;
        T n2 = null;
        int i1 = rnd.nextInt(graph.getNodes().size());
        int i2 = i1;
        while (i1 == i2){
            i2 = rnd.nextInt(graph.getNodes().size());
        }

        n1 = (T) graph.getNodes().toArray()[i1];
        n2 = (T) graph.getNodes().toArray()[i2];

        String time = "12:00";
        int minutes = timeToMinutes(time);
        System.out.println("Time:" + time);
        Map<String, List<Edge<T>>> path = graph.getFastestPath(n1, n2, minutes);
    }

    public static void testGraphReturnNull(ListGraph graph){
        Random rnd = new Random();

        int sum = 0;
        Station n1 = null;
        Station n2 = null;
        int isNUll = 0;
        int tot = 1000;

        for (int i = 0; i < tot; i++) {
            int i1 = rnd.nextInt(graph.getNodes().size()-1);
            int i2 = i1;
            while (i1 == i2){
                i2 = rnd.nextInt(graph.getNodes().size()-1);
            }
            int hour = rnd.nextInt(18);
            int minute = rnd.nextInt(55);
            String timeString = hour + ":" + minute;

            n1 = (Station) graph.getNodes().toArray()[i1];
            n2 = (Station) graph.getNodes().toArray()[i2];
            int minutes = timeToMinutes(timeString);
            Map path = graph.getFastestPath(n1, n2, rnd.nextInt(1440));
            if(path == null){
                System.out.println("FAILED AT: " + timeString);
                //System.out.println("Path exists: " + graph.pathExists(n1, n2) + "\n");
                isNUll++;
            }


        }
        System.out.println("FAILED: " + isNUll + "/" + tot);
    }

    public static String convertMinutesToTime(double minutes) {
        double hours = minutes / 60; // Get hours
        double mins = minutes % 60;  // Get remaining minutes

        return String.format("%02d:%02d", hours, mins); // Format as HH:MM
    }

    private static int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int t = 0;
        t += 60 * Integer.parseInt(parts[0]);
        t += Integer.parseInt(parts[1]);
        return t;
    }

    private void writeOutPath(Map<String, List<Edge<Station>>> path, String startName) {
        System.out.println("\nPATH: ");

        Station lastTripsStation = null;
        double totalTime = 0;
        double startTime = -1;
        double endTime = -1;
        for (int i = path.size()-1; i >= 0 ; i--) {
            // Hämta tripID
            String o = (String) path.keySet().toArray()[i];

            // Namnet på nuvarande station
            String stationName = lastTripsStation == null ? startName : lastTripsStation.getName();

            // Hämta och skriv ut trip.
            List<Edge<Station>> edges = path.get(o);
            double minutes = (edges.getLast().getDepartureTime() + edges.getLast().getWeight()) - edges.getFirst().getDepartureTime() * 1.00;
            //totalTime += minutes;

            if(i == path.size()-1){
                startTime = edges.getFirst().getDepartureTime();
            }
            if(i == 0){
                endTime = edges.getLast().getDepartureTime() + edges.getLast().getWeight();
            }

            String tripStartTime = convertMinutesToTime(edges.getFirst().getDepartureTime());
            String tripEndTime = convertMinutesToTime(edges.getLast().getDepartureTime() + edges.getLast().getWeight());
            System.out.println(stationName + " [" + tripStartTime + "]" + " --> " + edges.getLast().getDestination().getName() + " ["+ tripEndTime + "]" + " | " + minutes + " min");


            // Lägg till nästa destination som lastTripsStation.
            lastTripsStation = edges.getLast().getDestination();
            //totalTime++;
        }
        totalTime = endTime - startTime;
        System.out.println("TOTAL TIME: " + totalTime + " min");
    }

}
