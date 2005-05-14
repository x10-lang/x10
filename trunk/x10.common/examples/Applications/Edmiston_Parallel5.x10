/** 
 * Parallel version of Edmiston's algorithm for Sequence Alignment.
 * This code is an X10 port of the Edmiston_Sequential.c program written by 
 * Sirisha Muppavarapu (sirisham@cs.unm.edu), U. New Mexico.
 *
 * @author Vivek Sarkar   (vsarkar@us.ibm.com)
 * @author Kemal Ebcioglu (kemal@us.ibm.com)
 * 
 * This version performs SPMD computations. 
 */


import java.util.Random;

public class Edmiston_Parallel5 {
    const int N = 10;
    const int M = 10;

    /**
     * main run method
     */
    public boolean run() {
        charStr c1= new charStr(N,0);
        charStr c2= new charStr(M,N);
        editDistMatrix m=new editDistMatrix(c1,c2);
        m.pr("Edit distance matrix:");
        m.verify();
        return true;
    }

   /**
    * main method
    */
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Edmiston_Parallel5()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }
}

/**
 * Operations and distributed data structures related to
 * an edit distance matrix
 */
class editDistMatrix {
   const int gapPen = 2;
   const int match = 0;
   const int misMatch= -1;
   const int EXPECTED_RESULT= 549;
   const dist emptyDist=[0:-1,0:-1]->here;

   final int[.] e;
   final charStr c1;
   final charStr c2;
   final int N;
   final int M;
   final dist P;
   final int blockSize;
   final int blocksPerPlace;
   final dist D;
   final dist Dinner;
   final dist Dboundary;
   /**
    * Create edit distance matrix with Edmiston's algorithm
    */
   public editDistMatrix(charStr cSeq1, charStr cSeq2) {
        c1=cSeq1;
        c2=cSeq2;
        N=c1.s.region.high();
        M=c2.s.region.high();
	P=dist.factory.unique();
        //blockSize=number of rows per place in (block,*) distribution. 
        //Last place may have less rows than others
        blockSize=(N+place.MAX_PLACES)/place.MAX_PLACES;
        //A block is a blockSize**2 submatrix.
        //Number of blocks in each place, last block may be partial
	blocksPerPlace=(M+blockSize)/blockSize;
	// Emulate the (block,*) distribution
	dist d1=emptyDist;
	for(point [i]:P) d1=(d1||([(blockSize*i):min(blockSize*i+blockSize-1,N),0:M]->P[i]));
	D=d1;
        // Distribution of matrix not including row or column 0.
        Dinner=D|[1:N,1:M];
        // Distribution of row and column 0.
        Dboundary=D-Dinner;
        
	
        e=new int[D];

        // Now compute the edit distance matrix.
        // This is done with a wavefront computation, where in each step
        // each place computes a blockSize**2 submatrix.
        // After computation of the block for this wave, 
        // lockstep synchronization
        // occurs using a clock, so that the next wave can
        // correctly consume the results of this wave.
        finish {
            final clock c=clock.factory.clock();
	    //SPMD computation
            ateach(point [i]:P) clocked(c) {
               // initialize boundary for this place
	       for(point [x,y]:getBoundary(i)) e[x,y]=(x+y)*gapPen;
               // wait for my wave by executing i next's
               for(point [k]: [1:i]) next;
	       // Now do the real Edmiston computation for each
               // block in this set of rows from left to right, where
               // each block is computed using the blocks on its west, north
               // and northwest.
	       for(point [j]: 0:blocksPerPlace-1) {
	           for(point [x,y]: getBlockDist(i,j))
                       e[x,y]=min(rd(x-1,y)+gapPen,
                                  rd(x,y-1)+gapPen,
                                  rd(x-1,y-1)
                                   +(c1.s[x]==c2.s[y]?match:misMatch));
	           next;
	       }
               c.drop();//immediately de-register with clock, so future
                        //waves will not need to wait for me
            }
        }
   }

    /**
     * return the distribution/region for block numbered i,j.
     */
    dist getBlockDist(int i, int j) {
	return (Dinner|[0:N,(blockSize*j):min(blockSize*j+blockSize-1,M)])|P[i];
    }

    /**
     *Return the boundary nodes in the entire place
     */
    dist getBoundary(int i) {
	return Dboundary|P[i];
    }
	  
	

    /**
     * Find the sum of the elements of the edit distance matrix
     */
    int checkSum() {
        int sum=0;
        for(point [i,j]:e) sum+=rd(i,j);
        return sum;
    }
    /**
     * Verify that the edit distance matrix has the expected
     * checksum.
     */
    public void verify() {
        if(checkSum()!=EXPECTED_RESULT) throw new Error();
    }

    /* 
     * Print the Edit Distance Matrix 
     */
    public void pr(final String s)
    {
        final int K=4; // padding amount

        System.out.println(s);

        System.out.print(" "+pad(' ',K));
        for(point [j]:0:M) System.out.print(" "+pad(c2.s[j],K));
        System.out.println();

        for(point [i]:0:N){
            System.out.print(" "+pad(c1.s[i],K));
            for(point [j]:0:M) System.out.print(" "+pad(rd(i,j),K));
            System.out.println();
        }
    }

   /*
    * utility functions
    */

    /**
     * possibly remote read of e[i,j]
     */
    int rd(final int i, final int j) {
        return future(e.distribution[i,j]){e[i,j]}.force();
    }
    /**
     * returns the minimum of x y and z.
     */
    static int min(int x, int y, int z) {
        int t=(x<y)?x:y;
        return (t<z)?t:z;
    }
    /**
     * returns the minimum of x and y.
     */
    static int min(int x, int y) {
        return (x<y)?x:y;
    }
    /**
     * right justify an integer in a field of n blanks
     */
    static String pad(int x, int n) {
        String s=""+x;
        while (s.length()<n) s=" "+s;
        return s;
    }
    /**
     * right justify a character in a field of n blanks
     */
    static String pad(char x, int n) {
        String s=""+x;
        while (s.length()<n) s=" "+s;
        return s;
    }

}

/**
 * A random character array consisting of the letters ACTG 
 * and beginning with -
 */
value class charStr { 
    final char value[.] s;
    const char[] aminoAcids={'A','C','G','T'};
    public charStr(final int siz, final int randomStart) {
        s= new char value[[0:siz]->here] 
         (point [i]) { return i==0?'-':randomChar(randomStart+i);};
    }
          
    /** 
     * Function to generate the i'th random character
     */
   
    private static char randomChar(int i) {
        // Randomly select one of 'A', 'C', 'G', 'T' 
        int n=0;
        final Random  rand=new Random(1L);
        // find i'th random number.
        // TODO: need to pre-compute random numbers and re-use
        for(point [k]: 1:i) n = nextChoice(rand);
        return aminoAcids[n];
    }

    private static int nextChoice(Random rand) {
        int k1=rand.nextBoolean()?0:1;
        int k2=rand.nextBoolean()?0:1;
        return k1*2+k2;
    }
}
