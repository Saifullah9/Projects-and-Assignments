import java.util.*;
import java.io.*;

public class Multiply{

    private static int randomInt(int size) {
        Random rand = new Random();
        int maxval = (1 << size) - 1;
        return rand.nextInt(maxval + 1);
    }
    
    public static int[] naive(int size, int x, int y) {

        // YOUR CODE GOES HERE  (Note: Change return statement)
    	int[] res = new int[] {0, 0};
    	
    	//base case cost will be 1
    	if(size == 1) { 
    		res[0] = (x%2) * (y%2);
    		res[1] = 1;
    		return res;
    	}
    	
    	int m_ = (int) Math.ceil(size/2.0);
    	int a_ = (int) Math.floor(x >> m_);
    	int b_ = x % (1 << m_);
    	int c_ = (int) Math.floor(y >> m_);
    	int d_ = y % (1 << m_);
    	
    	int[] a_c = naive(m_, a_, c_);
    	int[] b_d = naive(m_, b_, d_);
    	int[] b_c = naive(m_, b_, c_);
    	int[] a_d = naive(m_, a_, d_);
    	res[0] = (a_c[0] << (2 * m_)) + ((b_c[0] + a_d[0]) << m_) + b_d[0];
    	
    	res[1] = a_c[1] + b_d[1] + b_c[1] + a_d[1] + 3 * m_;
        
        return res;
    }

    public static int[] karatsuba(int size, int x, int y) {
        
        // YOUR CODE GOES HERE  (Note: Change return statement) 
    	
    	int[] res = new int[2];
    	int[] e_ = new int[2];
    	int[] f_ = new int[2];
    	int[] g_ = new int[2];
    	if (size == 1) {
    		
    	  res[0] = (x%2) * (y%2);
    	  res[1] = 1;
    	  return res;
    	}
    	else {
    	  int m_ = (int)Math.ceil(size / 2.0);
    	  int a_ = (int)(x/Math.pow(2,m_));
    	  int b_ = (int)(x % Math.pow(2,m_));
    	  int c_ = (int)(y/Math.pow(2,m_));
    	  int d_ = (int)(y % Math.pow(2,m_));
    	  e_ = karatsuba(m_,a_,c_);
    	  f_ = karatsuba(m_,b_,d_);
    	  g_ = karatsuba(m_,a_-b_,c_-d_);
    	  res[0] = ((int)(Math.pow(2,(2*m_)))) * e_[0] + ((int)(Math.pow(2,m_))) * (e_[0]+f_[0]-g_[0]) + f_[0];
    	  res[1] = (e_[1]+f_[1]+g_[1])+6*m_;
    	  return res;
    	}
    	  
      
    }
    
    public static void main(String[] args){

        try{
            int maxRound = 20;
            int maxIntBitSize = 16;
            for (int size=1; size<=maxIntBitSize; size++) {
                int sumOpNaive = 0;
                int sumOpKaratsuba = 0;
                for (int round=0; round<maxRound; round++) {
                    int x = randomInt(size);
                    int y = randomInt(size);
                    int[] resNaive = naive(size,x,y);
                    int[] resKaratsuba = karatsuba(size,x,y);
            
                    if (resNaive[0] != resKaratsuba[0]) {
                        throw new Exception("Return values do not match! (x=" + x + "; y=" + y + "; Naive=" + resNaive[0] + "; Karatsuba=" + resKaratsuba[0] + ")");
                    }
                    
                    if (resNaive[0] != (x*y)) {
                        int myproduct = x*y;
                        throw new Exception("Evaluation is wrong! (x=" + x + "; y=" + y + "; Your result=" + resNaive[0] + "; True value=" + myproduct + ")");
                    }
                    
                    sumOpNaive += resNaive[1];
                    sumOpKaratsuba += resKaratsuba[1];
                }
                int avgOpNaive = sumOpNaive / maxRound;
                int avgOpKaratsuba = sumOpKaratsuba / maxRound;
                System.out.println(size + "," + avgOpNaive + "," + avgOpKaratsuba);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }

   } 
}
