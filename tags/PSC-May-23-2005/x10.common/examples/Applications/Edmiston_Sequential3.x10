/** 
 * Sequential x10 version of Edmiston's algorithm for Sequence Alignment.
 * This code is an X10 port of the Edmiston_Sequential.c program written by 
 * Sirisha Muppavarapu (sirisham@cs.unm.edu), U. New Mexico.
 *
 * @author Vivek Sarkar   (vsarkar@us.ibm.com)
 * @author Kemal Ebcioglu (kemal@us.ibm.com)
 * 
 */



public class Edmiston_Sequential3 {
    /**
     * main method
     */
    public static void main(String[] args) {
	if (args.length<3) throw new Error("missing arguments");
	final int N=java.lang.Integer.parseInt(args[0]); //rows
	final int M=java.lang.Integer.parseInt(args[1]);//columns
	final int NITER=java.lang.Integer.parseInt(args[2]);//iterations
	for(point [i]:[0:NITER-1]) {
          // generate two random input strings c1 and c2
	  Random r=new Random(i);
	  randCharStr c1= new randCharStr(r,N);
          randCharStr c2= new randCharStr(r,M);
          // run the user's parallel code with inputs c1 and c2
	  editDistMatrixParallel m=new editDistMatrixParallel(c1,c2);
          // print result
	  m.pr();
	}
    }

	

}

/**
 * Operations and distributed data structures related to
 * an edit distance matrix
 */
class editDistMatrix {
   const int gapPen = 2;
   const int match = 0;
   const int misMatch= 1;

   public final int[.] e;// the edit distance matrix
   final randCharStr c1;
   final randCharStr c2;
   final int N;
   final int M;

   /**
    * Create edit distance matrix with Edmiston's algorithm.
    * Sequential 'reference' version that runs on a single place.
    */
   public editDistMatrix(randCharStr cSeq1, randCharStr cSeq2) {
	c1=cSeq1;
	c2=cSeq2;
	N=c1.s.length-1;
	M=c2.s.length-1;
        final dist D=[0:N,0:M]->here;
        final dist Dinner=D|[1:N,1:M];
        final dist Dboundary=D-Dinner;
        e=new int[D];
        //  Boundary of e is initialized to:
        //  0     1*gapPen     2*gapPen     3*gapPen ...
        //  1*gapPen ...
        //  2*gapPen ...
        //  3*gapPen ...
        //  ...
        for(point [i,j]:Dboundary) e[i,j]=gapPen*(i+j);
	
        // now compute the edit distance matrix proper
        for(point [i,j]:Dinner)
           e[i,j]=min(e[i-1,j]+gapPen,
                      e[i,j-1]+gapPen,
                      e[i-1,j-1]+
                        +(c1.s[i]==c2.s[j]?match:misMatch));
	
   }

   /**
    * return element e[i,j] 
    *
    * In the parallel version this could be a remote read.
    */
   int rd(final int i, final int j) {
	return e[i,j];
   }


    /**
     * throws an error iff b is false.
     */
    static void chk(boolean b) {if(!b) throw new Error();}

    /* 
     * Print the Edit Distance Matrix 
     */
    public void pr()
    {
        final int K1=3; // first column padding amount
        final int K=3; // column padding amount

        System.out.println("Minimum Matrix EditDistance is: "+rd(N,M));
        System.out.println("Matrix EditDistance is:");

        System.out.print(pad(""+' '));
        for(point [j]:0:M) System.out.print(pad(""+c2.s[j]));
        System.out.println();

        for(point [i]:0:N){
            System.out.print(pad(""+c1.s[i]));
            for(point [j]:0:M) System.out.print(pad(""+rd(i,j)));
            System.out.println();
        }
    }

   /*
    * utility functions
    */
    /**
     * returns the minimum of x y and z.
     */
    static int min(int x, int y, int z) {
        int t=(x<y)?x:y;
        return (t<z)?t:z;
    }

    /**
     * Pad a string s on the left with blanks,
     * to create a string of length at least n.
     */
    static String pad(String s) {
	final int n=3;
        while (s.length()<n) s=" "+s;
        return " "+s+" ";
    }


}

    
class Random {

    int random_seed;
    Random(int x) {
        random_seed=x;
    }

    static long unsigned(int x) {
        return ((long)x & 0x00000000ffffffffL);
    }

    /* Function to generate random numbers between 0 and 128 */
    int nextRandom(){
         random_seed = (random_seed * 1103515245 +12345);
         return (int)(unsigned(random_seed / 65536) % 128L); 
    }
}


/**
 * A random character array consisting of the letters ACTG 
 * and beginning with -
 */
value class  randCharStr { 
    final char [] s;
    final Random rand;

    public randCharStr (Random r, int len) {
	rand=r;
	s=new char[len+1];
	s[0]= '-';
	int i=1;
	while(i<=len) {
            int iVal = rand.nextRandom();
	    switch(iVal) {
	        case 65: s[i++]='A';  break;
		case 67: s[i++]='C';  break;
		case 71: s[i++]='G';  break;
		case 84: s[i++]='T';  break;
		default:
             }
	}
    }
}


class editDistMatrixParallel extends editDistMatrix {
   /**
    * Create edit distance matrix with Edmiston's algorithm
    * Sequential version.
    */
   public editDistMatrixParallel(randCharStr cSeq1, randCharStr cSeq2) {
      // ===> This is the method to be parallelized as the exercise.
      // Initially just invoke the sequential reference version
      super(cSeq1,cSeq2);
   }
}
