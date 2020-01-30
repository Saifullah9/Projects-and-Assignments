package assignment_1;
import java.io.*;
import java.util.*;
import assignment_1.Open_Addressing.*;
import assignment_1.Chaining.*;


public class main {     

	
    public static void main(String[] args) {
    //TODO:build the hash table and insert keys using the insertKeyArray function.
    	    
         /* according to the EXCEl sheet provided,
         these are the lists X and Y with their corresponding A values */
         int []listX = {12, 14, 77, 74, 63, 21, 69, 13, 84, 93, 35, 89, 45, 60, 15, 57, 51, 18, 42, 62};
         int A_x = 683;
         
         int []listY = {79, 13, 45, 64, 32, 95, 67, 27, 78, 18, 41, 69, 15, 29, 72, 57, 81, 50, 60, 14};
         int A_y = 590;
         
         int w = 10; // the value of w for both lists which satisfies the condition 2^(w - 1) < A < 2^(w)
        
         // two hash tables for each list (X and Y)
         Chaining ChainX = new Chaining(w , 1 , A_x);
         Chaining ChainY = new Chaining(w , 1 , A_y);
         
         Open_Addressing ProbeX = new Open_Addressing(w , 1, A_x);
         Open_Addressing ProbeY = new Open_Addressing(w , 1, A_y);
         
         // number of chain collisions for lists X and Y
         int chainCollX = ChainX.insertKeyArray(listX);
         int chainCollY = ChainY.insertKeyArray(listY);
         
         // number of probe collisions for lists X and Y
         int probeCollX = ProbeX.insertKeyArray(listX);        
         int probeCollY = ProbeY.insertKeyArray(listY);
         
         /**
          * The part below is not needed for this situation since both lists have the same number of elements
          * However, just in case the tester varies the lists X an Y
          */
         double chainCollAverageX = (double)chainCollX / ((double)listX.length);
         double chainCollAverageY = (double)chainCollY / ((double)listY.length);
         
         double probeCollAverageX = (double)probeCollX / ((double)listX.length);
         double probeCollAverageY = (double)probeCollY / ((double)listY.length);
         
         System.out.println("Chain collision average for X is " + chainCollAverageX);
         System.out.println("Chain collision average for Y is " + chainCollAverageY);
         System.out.println("Probe collision average for X is " + probeCollAverageX);
         System.out.println("Probe collision average for Y is " + probeCollAverageY);
    }
        
}