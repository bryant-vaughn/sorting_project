
/**
 * Program: CS 2, fall 2017, project 8
 * Description: Implements and compares heap sort (non-recursive)
 * and flip sort, both implemented in an array.
 *
 * @author Bryant Vaughn
 * @version 12/2017
 */

import java.util.*;
import java.text.DecimalFormat;
import java.io.*;

public class FinalProject8 {

    private final int RUNS, N, 
    DATA_RANGE,          // random value range: zero to DATA_RANGE
    HEAP_TIME,FLIP_TIME, // timing array indices
    ROOT;                // index of heap tree root
    private Integer[] heapOriginalData,
    heapWorkingArray,
    heapSortedArray;
    private int[]     flipData;            // original, working, & sorted array
    private long[][]  timingArray;         // # of runs cross # of sorts      
    private Random    rand;                // data generator
    private static                         // commas
    DecimalFormat pattern;

    public FinalProject8 () {
        RUNS = 5;
        N = 100000;
        DATA_RANGE = 1_000_000_000;         // minimize duplicates w/o needing checking
        HEAP_TIME = 0;    
        FLIP_TIME = 1;  
        ROOT = 1;  
        rand = new Random();    
        heapOriginalData = new Integer[N+1];
        heapWorkingArray = new Integer[N+1];
        heapSortedArray  = new Integer[N+1];
        flipData         = new int[N];       // in-place sort
        timingArray      = new long[RUNS][2];
        pattern          = new DecimalFormat("#,##0"); 
    }

    // createData method
    // fill two arrays with identical data for the two sorting algorithms
    public void createData () {
        int temp;

        for (int i = 0; i < N; i++) {
            temp = rand.nextInt(DATA_RANGE);     
            heapOriginalData[i+1] = temp;     // one-based data array
            flipData[i] = temp;
        }
    }

    // -------------------------------------------------------------------

    // heap sort, non-recursive
    // array index position 0 unused in all heap arrays
    public void heapSort() {

        int heapCount,
        currentPosition;    // index of tree node during reheapify
        boolean done;

        // heap sort phase 1: build min heap

        for (heapCount = 1; heapCount <= N; heapCount++) {
            // insert next element at end of heap
            heapWorkingArray[heapCount] = heapOriginalData[heapCount];

            // fix heap: reheapify up
            currentPosition = heapCount;
            // reheapify up halts when the new element is the (new) root or ...
            // ... when it is correct in the current parent position 
            // ... by the min heap rule 
            done = false;
            while (!done) {
                if (isRoot(currentPosition)) {
                    done = true;
                }
                else if (isCorrectChild(currentPosition)) {
                    done = true; 
                }
                else {
                    // child < parent, fix by min heap rule
                    heapSwap(currentPosition, currentPosition/2);
                    // move up tree
                    currentPosition = currentPosition / 2;       
                }     
            }  // reheapify up 
        }  // insertion of all data into min heap

        // heap sort phase 2: extract sorted data from min heap  
        for(int i = 1; i <= N; i++) {
            heapSortedArray[i] = heapWorkingArray[ROOT]; // move root to sorted array
            heapWorkingArray[ROOT] = heapWorkingArray[heapCount-1]; // move last element to root
            heapWorkingArray[heapCount-1] = null; // clear out last element
            heapCount--; // decrease count of elements in heapWorkingArray

            currentPosition = ROOT;
            done = false;
            while(!done) {
                // if currentPosition is a leaf
                if(currentPosition*2 > N) {
                    done = true;
                }
                else if (heapWorkingArray[currentPosition*2] == null) {
                    done = true;
                }
                else if(isCorrectParent(currentPosition)) {
                    done = true;  
                }
                else {
                    if(heapWorkingArray[currentPosition*2+1] != null) {
                        boolean smallChild = smallestChild(currentPosition*2, currentPosition*2+1);
                        if(smallChild) {
                            // left child < parent, fix by min heap rule
                            heapSwap(currentPosition, currentPosition*2);
                            // movedown tree
                            currentPosition = currentPosition * 2;
                        }
                        else {
                            // right child < parent, fix by min heap rule
                            heapSwap(currentPosition, currentPosition*2+1);
                            // move down tree
                            currentPosition = currentPosition * 2 + 1;
                        }
                    }
                    else {
                        // left child < parent, fix by min heap rule
                        heapSwap(currentPosition, currentPosition*2);
                        // move down tree
                        currentPosition = currentPosition * 2;
                    }
                }
            } // while loop
        } // for loop
        // to be written ...
    }  // heapSort
    // smallestChild method
    // used to see if right or left child is smaller
    private boolean smallestChild(int childIndex1, int childIndex2) {
        Integer left = heapWorkingArray[childIndex1];
        Integer right = heapWorkingArray[childIndex2];
        if(left <= right) {
            return true;
        }
        return false;
    }
    // isCorrectParent method
    // used for reheapify down
    private boolean isCorrectParent(int parentIndex) {
        // child index out of bounds?
        if(parentIndex*2 > N) {
            return true;
        }

        // leaf?
        if(heapWorkingArray[parentIndex*2] == null) {
            return true;
        }

        // not a leaf, check children
        if(heapWorkingArray[parentIndex*2+1] != null) {
            boolean smallest = smallestChild(parentIndex*2, parentIndex*2+1);
            if(smallest) {
                if(heapWorkingArray[parentIndex] <= heapWorkingArray[parentIndex*2]) {
                    return true;
                }
            }
            else {
                if(heapWorkingArray[parentIndex] <= heapWorkingArray[parentIndex*2+1]) {
                    return true;
                }
            }
        }
        else {
            if(heapWorkingArray[parentIndex] <= heapWorkingArray[parentIndex*2]) {
                return true;
            }
        }
        return false;
    }
    // isCorrectChild method
    // used for reheapify up
    private boolean isCorrectChild (int childIndex) {

        // root? trivially correct for min heap rule
        if (isRoot(childIndex)) {
            return true;
        }

        // not at root, check parent
        if (heapWorkingArray[childIndex/2] <= heapWorkingArray[childIndex]) {
            return true;
        }

        // parent > child
        return false;
    }

    private boolean isRoot (int index) {
        return index == ROOT;
    }

    // heapSwap method
    // used to restore min heap rule between parents and children
    private void heapSwap (int position1, int position2) {
        Integer tempData = heapWorkingArray[position1];
        heapWorkingArray[position1] = heapWorkingArray[position2];
        heapWorkingArray[position2] = tempData;
    }

    // checkHeapSort method
    // verify that all data is in non-descending order        
    private boolean checkHeapSort () {
        for (int i = 1; i < heapSortedArray.length-1; i++)
            if (heapSortedArray[i] > heapSortedArray[i+1])
                return false;
        return true;
    }  // checkSort

    // printHeapData method
    // to be used for very small n only 
    public void printHeapData () {
        System.out.println("Final heap sort data");
        for (int i = 1; i <= N; i++) {
            System.out.format("%12d   ", pattern.format(heapSortedArray[i]));
            if (i % 5 == 0)
                System.out.println();
        }   
    }

    // -------------------------------------------------------------------

    public void flipSort() {

        // to be written ...
        boolean notDone = true; // used to see if sorted
        int low = 0; // used to move the starting position
        int high = N - 1; // used to move the ending position
        
        while(low < high) {
            
            // forward run through array
            for(int i = low; i < high; i++) {
                // compare two elements to see if flip is needed
                if (flipData[i] > flipData[i + 1]) {
                    int temp = flipData[i];
                    flipData[i] = flipData[i + 1];
                    flipData[i + 1] = temp;
                }
            }

            high--; // last item is in order
            
            // backwards run through array
            for(int i = high; i > low; i--) {
                // compare two elements to see if flip is needed
                if(flipData[i] < flipData[i-1]) {
                    int temp = flipData[i];
                    flipData[i] = flipData[i-1];
                    flipData[i-1] = temp;
                }
            }
            
            // increase starting point because
            // ...another element is in order
            low++;
        }
    }  // flipSort
    // checkFlipSort method
    // verify that all data is in non-descending order
    private boolean checkFlipSort() {
        for(int i = 0; i < flipData.length-1; i++) {
            if(flipData[i] > flipData[i+1]) {
                return false;
            }
        }
        return true;
    }
    // -------------------------------------------------------------------

    public void reportResults () {
        System.out.println("\n-----\n\nTiming Report");
        for (int k = 0; k < RUNS; k++) {
            System.out.println("\nRun #" + (k+1) + ": ");
            System.out.print("   heap sort: ");
            System.out.format("%6s", pattern.format(timingArray[k][HEAP_TIME]));
            System.out.println(" milliseconds");
            System.out.print("   flip sort: ");
            System.out.format("%6s", pattern.format(timingArray[k][FLIP_TIME]));
            System.out.println(" milliseconds");
        }  // print all timing data
    }
    
    public void printResultsToFile() {
        try {
            int heapAverage = 0;
            int flipAverage = 0;
            String fastestSort = "";
            BufferedWriter writer = new BufferedWriter(new FileWriter("OutputFile.txt"));
            String output = "Bryant Vaughn\r\n";
            output += "CS 2, Project 8\r\n\r\n";
            output += "N = " + pattern.format(N) + "\r\n\r\n";
            output += "Timing Report\r\n\r\n";
            for(int k = 0; k < RUNS; k++) {
                output += "Run #" + (k+1) + "\r\n";
                output += "   heap sort: ";
                output += pattern.format(timingArray[k][HEAP_TIME]);
                output += " milliseconds\r\n";
                output += "   flip sort: ";
                output += pattern.format(timingArray[k][FLIP_TIME]);
                output += " milliseconds\r\n";
                if(timingArray[k][HEAP_TIME] < timingArray[k][FLIP_TIME]) {
                    fastestSort = "   heap ";
                }
                  else if(timingArray[k][HEAP_TIME] > timingArray[k][FLIP_TIME]) {
                    fastestSort = "   flip ";
                }
                  else {
                    fastestSort = "   neither ";
                }
                output += fastestSort + "sort was faster.\r\n\r\n";
                heapAverage += timingArray[k][HEAP_TIME];
                flipAverage += timingArray[k][FLIP_TIME];
            }  // print all timing data to file
            output += "Average run times:\r\n";
            output += "   heap sort: " + pattern.format(heapAverage/RUNS) + " milliseconds\r\n";
            output += "   flip sort: " + pattern.format(flipAverage/RUNS) + " milliseconds\r\n";
            writer.write(output);
            writer.close();
        }
          catch(Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------

    public static void main (String args[]) {

        FinalProject8 sorts = new FinalProject8(); 
        long startTime, endTime;

        System.out.println("Project 8, Sorting, J. Gurka & Bryant Vaughn");

        System.out.println("N = " + pattern.format(sorts.N));

        // do RUNS number of tests
        for (int run = 1; run <= sorts.RUNS; run++) {

            System.out.println ("\nRun #" + run);
            // fill 2 arrays with identical data for two sorts
            sorts.createData();

            // heap sort
            startTime = System.currentTimeMillis(); 
            sorts.heapSort();
            endTime = System.currentTimeMillis();
            // calculate & store total heap sort time
            sorts.timingArray[run-1][sorts.HEAP_TIME] = endTime - startTime;

            System.out.println(sorts.checkHeapSort() ? "   heap data is sorted" : "   heap sort failed");
            // sorts.printHeapData();
                    
            // flip sort
            startTime = System.currentTimeMillis(); 
            sorts.flipSort();
            endTime = System.currentTimeMillis();
            // calculate & store total flip sort time
            sorts.timingArray[run-1][sorts.FLIP_TIME] = endTime - startTime;

            System.out.println(sorts.checkFlipSort() ? "   flip data is sorted" : "   flip sort failed");
            // sorts.printFlipData();
            
        }
        
        sorts.reportResults();
        sorts.printResultsToFile();
    }  // main
}  // project 8