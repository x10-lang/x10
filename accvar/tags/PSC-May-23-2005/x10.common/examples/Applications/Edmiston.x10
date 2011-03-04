import java.lang.Integer;
/** 
 * Sequential reference version of Edmiston's algorithm 
 * for sequence alignment, written in x10.
 *
 * This code is an X10 port of the Edmiston_Sequential.c program written by 
 * Sirisha Muppavarapu (sirisham@cs.unm.edu), U. New Mexico.
 *
 * @author Vivek Sarkar   (vsarkar@us.ibm.com)
 * @author Kemal Ebcioglu (kemal@us.ibm.com)
 * 
 */

public class Edmiston {
    /**
     * main method
     */
    public static void main(String[] args) {
        if (args.length<3) usage();
        final int N=Integer.parseInt(args[0]); //rows
        final int M=Integer.parseInt(args[1]); //columns
        final int NITER=Integer.parseInt(args[2]); //iterations
        for(point [i]:[0:NITER-1]) {
            // generate two random input strings c1 and c2
            Random r=new Random(i);
            RandCharStr c1= new RandCharStr(r,N);
            RandCharStr c2= new RandCharStr(r,M);
            // run the user's parallel code with inputs c1 and c2
            EditDistMatrixParallel m=new EditDistMatrixParallel(c1,c2);
            // print result
            m.pr();
            // verify user's result against the reference algorithm
            m.verify(new EditDistMatrix(c1,c2));
        }
    }

    /**
     * Print usage message and exit
     */
    static void usage() {
        System.out.println("Need arguments: nRows nCols nIter"); 
        System.exit(1); 
    }
}

/**
 * Operations and data structures related to
 * an edit distance matrix.
 */
class EditDistMatrix {
    const int gapPen = 2;
    const int match = 0;
    const int misMatch= 1;
 
    public final int[.] e;// the edit distance matrix
    final RandCharStr c1; // input string 1
    final RandCharStr c2; // input string 2
    final int N; // matrix dimensions 
    final int M;  
 
   /**
    * Create the edit distance matrix using Edmiston's algorithm,
    * from the input strings cSeq1 and cSeq2.
    *
    * This is the sequential 'reference' version that 
    * runs on a single place.
    */
    public EditDistMatrix(RandCharStr cSeq1, RandCharStr cSeq2) {
        c1=cSeq1;
        c2=cSeq2;
        N=c1.s.length-1;
        M=c2.s.length-1;

        // All elements of the matrix are mapped to place 'here'.
        final dist D=[0:N,0:M]->here;

        // Dinner is the distribution for the 
        // inner part of the matrix, 
        // with row 0 and column 0 missing.
        final dist Dinner=D|[1:N,1:M];

        // Dboundary applies to just row and column 0. 
        final dist Dboundary=D-Dinner;

        e=new int[D];

        //  Boundary of e is initialized to:
        //  0     1*gapPen     2*gapPen     3*gapPen ...
        //  1*gapPen ...
        //  2*gapPen ...
        //  3*gapPen ...
        //  ...
        for(point [i,j]:Dboundary) e[i,j]=gapPen*(i+j);
        
        // Now compute the inner part of the edit distance matrix
        for(point [i,j]:Dinner)
           e[i,j]=min(e[i-1,j]+gapPen,
                      e[i,j-1]+gapPen,
                      e[i-1,j-1]+
                        (c1.s[i]==c2.s[j]?match:misMatch));
        
    }

    /**
     * Return element [i,j] of matrix 
     */
    public int rd(final int i, final int j) {
        return e[i,j];
    }

    /**
     * Throw an error iff this matrix does not match the other matrix.
     */
    public void verify(final EditDistMatrix other) {
        for(point [i,j]: other.e.distribution) 
           chk(rd(i,j)==other.rd(i,j));
    }

    /* 
     * Print the Edit Distance Matrix. 
     */
    public void pr()
    {
        System.out.println("Minimum Matrix EditDistance is: "+rd(N,M));
        System.out.println("Matrix EditDistance is:");

        System.out.print(pad(' '));
        for(point [j]:0:M) System.out.print(pad(c2.s[j]));
        System.out.println();

        for(point [i]:0:N){
            System.out.print(pad(c1.s[i]));
            for(point [j]:0:M) System.out.print(pad(rd(i,j)));
            System.out.println();
        }
    }

    /*
     * Utility methods.
     */

    /**
     * returns the minimum of x y and z.
     */
    static int min(int x, int y, int z) {
        int t=(x<y)?x:y;
        return (t<z)?t:z;
    }

    /**
     * Throws an error iff b is false.
     */
    static void chk(boolean b) {if(!b) throw new Error();}


    /**
     * Pad a string s on the left with blanks,
     * to create a string of length at least n.
     * Then add two blanks to the end and beginning of the string.
     */
    static String pad0(String s) {
        final int n=3;
        while (s.length()<n) s=" "+s;
        return " "+s+" ";
    }

    static String pad(int x)  { return pad0(""+x);}

    static String pad(char x) { return pad0(""+x);}

}

    
/**
 * Common random number generator class. 
 */
class Random {

    int randomSeed;

    /** 
     * Create a new random number generator with seed x
     */
    public Random(int x) {
        randomSeed=x;
    }

    /** 
     * Returns the next random number between 0 and 128, 
     * according to this random number generator's sequence.
     */
    public int nextAsciiNumber(){
         randomSeed = (randomSeed * 1103515245 +12345);
         return (int)(unsigned(randomSeed / 65536) % 128L); 
    }

    /**
     * Convert an int to an unsigned int (C-style).
     */
    static long unsigned(int x) {
        return ((long)x & 0x00000000ffffffffL);
    }

}


/**
 * A class pertaining to random character arrays (strings).
 */

value class RandCharStr { 
    public final char [] s; // the string (character array).

    /**
     * Create a random character array of 
     * length len from the alphabet A,C,G,T, 
     * using the random number generator r.
     * The array begins with an extra '-', 
     * thus it will have len+1 characters.
     */
    public RandCharStr (Random r, int len) {
        s=new char[len+1];
        s[0]= '-';
        int i=1;
        while(i<=len) {
            int x= r.nextAsciiNumber();
            switch(x) {
                case 65: s[i++]='A';  break;
                case 67: s[i++]='C';  break;
                case 71: s[i++]='G';  break;
                case 84: s[i++]='T';  break;
                default:
            }
        }
    }
}


/**
 * User's extensions to EditDistanceMatrix.
 * To implement the parallel version.
 */

class EditDistMatrixParallel extends EditDistMatrix {
   /**
    * Create the edit distance matrix using Edmiston's algorithm,
    * from the input strings cSeq1 and cSeq2.
    *
    * You can modify this method and override other
    * methods of EditDistMatrix as needed, to create the 
    * parallel version of the algorithm.
    */

   public EditDistMatrixParallel(RandCharStr cSeq1, RandCharStr cSeq2) {
      // Initially just invokes the sequential reference version
      super(cSeq1,cSeq2);
   }

}
