import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Labb3 {

    public static int PageFaults = 0;
    public static int PageHits = 0;
    public static int PageReplacement = 0;
    public static int MemoryAcess = 0;

    public static void main(String[] args) throws Exception {
        System.out.print("\033c");
        Scanner scan = new Scanner(System.in);
        ArrayList <String> DataArray = new ArrayList<String>();
        
        String InputsVal = AlgorithmInput(scan);
        DataArray = TraceVal(scan);

        int FrameVal = FrameInput(scan);


        if(InputsVal.equals("FIFO") ){
            FirstInFirstOut(DataArray,FrameVal);
        }
        else if(InputsVal.equals("O")){
            Optimal(DataArray,FrameVal);
        }
        else if(InputsVal.equals("LRU")){
            LeastRecentlyUsed(DataArray,FrameVal);
        }

    }

    public static void FirstInFirstOut(ArrayList <String> InputArray,int FrameInput){
        int OldestFrame = 0;
        ArrayList <Integer> Frames = new ArrayList<Integer>();
        System.out.println("Running Simmulation for FIFO...\n");

        for(int i = 0; i < InputArray.size();i++){

            int k = Integer.parseInt(InputArray.get(i).substring(InputArray.get(i).length()-4),16) / 256;
            boolean Continue = true;

            if(Frames.size() == 0){
                System.out.println("Adress " + InputArray.get(i) + " is not in physical memory");
                System.out.println("Page " + k + " paged in");
                Frames.add(k);
                OldestFrame = k;
                MemoryAcess++;
                PageFaults++;
                continue;
            }
            for(int j = 0; j < Frames.size(); j++){
                if(k == Frames.get(j)){
                    System.out.println("Adress " + InputArray.get(i) + " is on page " + k + " wich is alredy in memory");
                    PageHits++;
                    MemoryAcess++;
                    Continue = false;
                }
            }
            if(Continue == true){
                if(Frames.size() < FrameInput){
                    System.out.println("Adress " + InputArray.get(i) + " is not in physical memory");
                    System.out.println("Page " + k + " paged in");
                    Frames.add(k);
                    MemoryAcess++;
                    PageFaults++;
                }
                else{
                    System.out.println("Adress " + InputArray.get(i) + "is not in physical memory");
                    System.out.println("Page " + Frames.get(OldestFrame) + " paged out");
                    System.out.println("Page " + k + " paged in");
                    MemoryAcess++;
                    PageFaults++;
                    PageReplacement++;
                    Frames.set(OldestFrame, k);
                    
                    if(OldestFrame == FrameInput-1){
                        OldestFrame = 0;
                    }
                    else{
                        OldestFrame++;
                    }
                }
            }
        }
        PrintFinal("First in first out",FrameInput);
    } 
    public static void Optimal(ArrayList <String> InputArray,int FrameInput){
        int NumberOfFramesCreated = 0;
        int Farthest = 0;
        ArrayList <Integer> Frames = new ArrayList<Integer>();
        for(int i = 0; i < 256; i++){
            Frames.add(0);
        }
        System.out.println("Running simulation for Optimal");

        for(int i = 0; i < InputArray.size();i++){

            int k = Integer.parseInt(InputArray.get(i).substring(InputArray.get(i).length()-4),16) / 256;
            boolean Continue = true;

            if(NumberOfFramesCreated == 0){
                    System.out.println("Adress " + InputArray.get(i) + " is not in physical memory");
                    System.out.println("Page " + k + " paged in");
                    Frames.set(NumberOfFramesCreated,k);
                    MemoryAcess++;
                    PageFaults++;
                    NumberOfFramesCreated++;
                    Farthest = k;
                    continue;
                }

            for(int j = 0; j < NumberOfFramesCreated; j++){
                if( k == Frames.get(j)){
                    System.out.println("Adress " + InputArray.get(i) + "is on page " + k + " wich is alredy in memory\n");
                    PageHits++;
                    MemoryAcess++;
                    Continue = false;
                }
            }
            if(Continue){
                if(NumberOfFramesCreated < FrameInput){
                        System.out.println("Adress " + InputArray.get(i) + " is not in physical memory");
                        System.out.println("Page " + k + " paged in");
                        Frames.set(NumberOfFramesCreated,k);
                        NumberOfFramesCreated++;
                        MemoryAcess++;
                        PageFaults++;
                    }
                    else{
                        int Highest = 0;
                        int tmp = 0;

                        for(int h = 0; h < NumberOfFramesCreated;h++){
                            int Counter = 0;

                            for(int g = h; g < FrameInput-1;g++){
                                int r = Integer.parseInt(InputArray.get(g).substring(InputArray.get(g).length()-4),16) / 256;
                                if(Frames.get(h) == r && Counter > Highest){
                                    Highest = Counter;
                                    tmp = h;
                                    break;
                                }
                                else if(h == FrameInput -1){
                                    tmp = h;
                                    break;
                                }
                                Counter++;
                            }
                        }
                        Farthest = tmp;
                        System.out.println("Adress " + InputArray.get(i) + "is not in physical memory");
                        System.out.println("Paged " + Frames.get(Farthest) + " paged out");
                        System.out.println("Page " + k + " paged in");
                        Frames.set(Farthest, k);
                        MemoryAcess++;
                        PageFaults++;
                        PageReplacement++;                        
                    }
                }
            }

            PrintFinal("Optimal", FrameInput);
        }
    
    public static void LeastRecentlyUsed(ArrayList <String>InputArray,int FrameInput ){
        int NumberOfFramesCreated = 0;
        ArrayList <Integer> Frames = new ArrayList<Integer>();
        for(int i = 0; i < 256; i++){
            Frames.add(0);
        }
        System.out.println("Running simulation for Least reacently used");

        for(int i = 0; i < InputArray.size();i++){
            int k = Integer.parseInt(InputArray.get(i).substring(InputArray.get(i).length()-4),16) / 256;
            boolean Continue = true;

            if(NumberOfFramesCreated == 0){
                    System.out.println("Adress " + InputArray.get(i) + " is not in physical memory");
                    System.out.println("Page " + k + " paged in");
                    Frames.add(k);
                    MemoryAcess++;
                    PageFaults++;
                    NumberOfFramesCreated++;
                    continue;
                }

                for(int j = 0; j < NumberOfFramesCreated; j++){

                    if(k == Frames.get(j)){
                        System.out.println("Adress " + InputArray.get(i) + "is on page " + k + " wich is alredy in memory\n");
                        
                        int tmp = Frames.get(j);

                        for(int h = j; h < NumberOfFramesCreated - 1; h++){
                            Frames.set(h,Frames.get(h+1));
                        }
                        Frames.set(NumberOfFramesCreated, tmp);

                        PageHits++;
                        MemoryAcess++;
                        Continue = false;
                    }   
                }
                if(Continue){
                    if(NumberOfFramesCreated < FrameInput){
                        System.out.println("Adress " + InputArray.get(i) + " is not in physical memory");
                        System.out.println("Page " + k + " paged in");
                        Frames.set(NumberOfFramesCreated,k);
                        NumberOfFramesCreated++;
                        MemoryAcess++;
                        PageFaults++;
                    }
                    else{
                        System.out.println("Adress " + InputArray.get(i) + "is not in physical memory");
                        System.out.println("Paged " + Frames.get(0) + " paged out");
                        System.out.println("Page " + k + " paged in");
                        MemoryAcess++;
                        PageFaults++;
                        PageReplacement++;

                        for(int h = 0; h < NumberOfFramesCreated; h++){
                            Frames.set(h, Frames.get(h+1));
                        }
                        Frames.set(NumberOfFramesCreated - 1, k);
                    }
                } 
            }
            PrintFinal("Last reasently used", FrameInput);
                
     }
/*     public static void LeastRecentlyUsed(ArrayList <String> InputArray,int FrameInput){
       ArrayList <Integer> Frames = new ArrayList<Integer>();
        System.out.println("Running simmulation for LRU...\n");
        
        for(int i = 0; i < InputArray.size(); i++){

            int k = Integer.parseInt(InputArray.get(i).substring(InputArray.get(i).length()-4),16) / 256;
            boolean Continue = true;

            if(Frames.size() == 0){
                System.out.println("Adress " + InputArray.get(i) + " is not in physical memory");
                System.out.println("Page " + k + " paged in");
                Frames.add(k);
                MemoryAcess++;
                PageFaults++;
                continue;
            }

             for(int j = 0; j < Frames.size(); j++){

                 if(k == Frames.get(j)){
                    System.out.println("Adress " + InputArray.get(i) + "is on page " + k + " wich is alredy in memory\n");
                    
                    int tmp = Frames.get(j);

                    for(int h = j; h < Frames.size()-1; h++){
                        Frames.set(h,Frames.get(h+1));
                    }
                    Frames.set(Frames.size()-1, tmp);

                    PageHits++;
                    MemoryAcess++;
                    Continue = false;
                 }   
            }
            if(Continue == true){
                if(Frames.size() < FrameInput){
                    System.out.println("Adress " + InputArray.get(i) + " is not in physical memory");
                    System.out.println("Page " + k + " paged in");
                    Frames.add(k);
                    MemoryAcess++;
                    PageFaults++;
                }
                else{
                    System.out.println("Adress " + InputArray.get(i) + "is not in physical memory");
                    System.out.println("Paged " + Frames.get(0) + " paged out");
                    System.out.println("Page " + k + " paged in");
                    MemoryAcess++;
                    PageFaults++;
                    PageReplacement++;

                    for(int h = 0; h < Frames.size()-1; h++){
                        Frames.set(h, Frames.get(h+1));
                    }
                    Frames.set(Frames.size()-1, k);
                }
            } 
        }
        PrintFinal("Last reasently used", FrameInput);
    }  */   
    public static void PrintFinal(String Algorithm,int FrameInput){
        System.out.println("\nSimulation done...\n");
        System.out.print("|-------------------------------------------|\n");
        System.out.println(" Algorithm: "+Algorithm);
        System.out.println(" Frames: " + FrameInput);
        System.out.println(" Memory Acesses: " + MemoryAcess);
        System.out.println(" Page hits: " + PageHits);
        System.out.println(" Page Faults: " + PageFaults);
        System.out.println(" Page Replacements: " + PageReplacement);
            System.out.print("|-------------------------------------------|\n");


    }
    public static String AlgorithmInput(Scanner scan){
        System.out.print("Vilken Algoritm vill du köra?\n------------------------------\nFirst In First Out: FIFO\nOptimal: O\nLeast Resently Used: LRU\n------------------------------\nInput: ");
        String AlgoritmVal = scan.nextLine();
        System.out.print("\033c");

    return AlgoritmVal;
    }
    public static int FrameInput(Scanner scan){

        int FrameVal = 0;
        while(FrameVal <= 0 || FrameVal > 256){
        System.out.print("Hur många Frames vill du ha?\nInput: ");
        FrameVal = scan.nextInt();
        System.out.print("\033c");
        }
        return FrameVal;
    }
    public static ArrayList <String> TraceVal(Scanner scan){
        ArrayList <String> tmpArray = new ArrayList<>();
        String line = "";
        System.out.println("Vill du välja bland standardiserade trace filer?\ny/n");
        System.out.print("Input:");
        String val = scan.nextLine();
        if(val.equals("y")){
        System.out.print("\033c");
        System.out.print("Vilken trace fil vill du använda?\n------------------------------\n");
        System.out.println("1. trace_1.dat\n2. trace_2.dat\n------------------------------\nFyll i namnet på den fil du vill välja ");
        System.out.print("Input:");
        line = scan.nextLine();
        }
        else if(val.equals("n")){
            System.out.print("\033c");
            System.out.print("Fyll i Sökvägen för din valda trace fil\n");
            System.out.print("Input:");
            line = scan.nextLine();
        }
        else{
            TraceVal(scan);
        }
        try (BufferedReader read = new BufferedReader(new FileReader(line))) {
            try{
                while((line = read.readLine()) != null){
                    tmpArray.add(line);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("\033c");

        return tmpArray;
    }
}