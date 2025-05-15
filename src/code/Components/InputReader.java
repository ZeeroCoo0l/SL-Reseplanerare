package code.Components;

import java.util.Scanner;
import java.io.InputStream;
import java.util.ArrayList;

public class InputReader {
    
    //Klassvariabel
    private static final ArrayList<InputStream> savedStreams = new ArrayList<>();

    //Instansvariabel
    private final Scanner input;
    private final InputStream inputStream;

    // Konstruktorer
    public InputReader(){
        this(System.in);
    }

    public InputReader(InputStream in) {
        if(savedStreams.contains(in)){ 
            throw new IllegalStateException("InputStream already in use");
        }
        else{
            input = new Scanner(in);
            savedStreams.add(in);
            inputStream = in;
        }
    }

    // Metoder
    public void close(){
        input.close();
        savedStreams.remove(inputStream);
    }
    

    public int readInt(String prompt){
        System.out.print(prompt + "?>");
        int answer = input.nextInt();
        input.nextLine(); // Töm inmatningsbufferten
        return answer;
    }

    public double readDouble(String prompt){
        System.out.print(prompt  + "?>");
        double answer = input.nextDouble();
        input.nextLine(); // Töm inmatningsbufferten
        return answer;
    }

    public String readString( String prompt){
        System.out.print(prompt  + "?>");
        String answer = input.nextLine().trim();
        
        return answer;


    }

    /* Metoderna för inläsning av tal måste också 
    tömma inmatningsbufferten (HR4.2)
    efter inläsning för att undvika problem med buffring.
    */
}
