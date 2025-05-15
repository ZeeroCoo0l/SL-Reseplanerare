package code;

import code.Components.Edge;
import code.Components.InputReader;
import code.Components.ListGraph;
import code.Helpers.Loader;
import code.Interfaces.Node;

import java.util.*;

public class Program<T extends Node> {

    private final InputReader input = new InputReader();
    private final Map<Integer, String> commands = new HashMap<>();
    ListGraph<T> graph;

    public Program(){
        // Get menu
        loadCommands();
        graph = Loader.createGraph();
    }

    public void runProgram(){
        // Show menu
        showMenu();
        showNavigator();
    }

    private void showMenu() {
        System.out.println("\n---------------------------------------------------");
        System.out.println(" S L   R E S E P L A N E R A R E ");
        for (Map.Entry<Integer, String> entry : commands.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
    }

    private int showNavigator() {
        int i = 0;
        while ( i == 0){
            String answer = input.readString("Enter number for action:");
            try {
                i = Integer.parseInt(answer);
            } catch (NumberFormatException e) {
                i = 0;
            }
        }
        switch (i){
            case(1):
                searchTravel();
                break;
            case(2):
                input.close();
                break; // Ends program
            default:
                System.out.println("Try again! Number between 1-" + commands.size());
                return showNavigator();
        }
        return i;
    }

    private void searchTravel() {
        int programNav = -1;
        String exitCommand = "exit";

        while (programNav != 1){
            String from = input.readString("From: ");
            if(from.trim().toLowerCase().equals(exitCommand)){
                System.out.println("Closing program.");
                break;
            }

            String to = input.readString("To: ");
            if(to.trim().toLowerCase().equals(exitCommand)){
                System.out.println("Closing program.");
                break;
            }
            String timeString = input.readString("Time: ");
            if(timeString.trim().toLowerCase().equals(exitCommand)){
                System.out.println("Closing program.");
                break;
            }

            if(from.isBlank() || to.isBlank() || timeString.isBlank()){
                System.out.println("\nOBS! Try again!");
                continue;
            }


            int currentTime = timeToMinutes(timeString);
            Map<String, List<Edge<T>>> path = graph.getFastestPath(from, to, currentTime);
            if(path == null){
                System.out.println("OBS! Could not find path between: ");
                System.out.println("    - " + from);
                System.out.println("    - " + to);
            }
            else{
                writeOutPath(path, from);
            }
            showMenu();
            programNav = showNavigator();


        }

    }

    private void writeOutPath(Map<String, List<Edge<T>>> path, String startName) {
        System.out.println("\nPATH:");
        System.out.println("---------------------------------------------------");

        T lastTripsStation = null;
        double totalTime = 0;
        double startTime = -1;
        double endTime = -1;
        for (int i = path.size()-1; i >= 0 ; i--) {
            // Hämta tripID
            String o = (String) path.keySet().toArray()[i];

            // Namnet på nuvarande station
            String stationName = lastTripsStation == null ? startName : lastTripsStation.getName();

            // Hämta och skriv ut trip.
            List<Edge<T>> edges = path.get(o);
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
            System.out.println(stationName + " [" + tripStartTime + "]" + " --> " + edges.getLast().getDestination().getName() + " ["+ tripEndTime + "], " + edges.size() + " stationer"+ " | " + minutes + " min");


            // Lägg till nästa destination som lastTripsStation.
            lastTripsStation = edges.getLast().getDestination();
            //totalTime++;
        }
        totalTime = endTime - startTime;
        System.out.println("TOTAL TIME: " + totalTime + " min");
        System.out.println("---------------------------------------------------\n");
    }

    private void loadCommands() {
        commands.put(1, "Search travel");
        commands.put(2, "Exit");
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int t = 0;
        try {
            t += 60 * Integer.parseInt(parts[0]);
            t += Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            t = -1;
        }
        return t;
    }

    public static String convertMinutesToTime(double min) {
        int minutes = (int) min;
        int hours = minutes / 60; // Get hours
        int mins = minutes % 60;  // Get remaining minutes
        return String.format("%02d:%02d", hours, mins); // Format as HH:MM
    }

    public static void main(String[] args) {
        Program program = new Program();
        program.runProgram();

    }
}
