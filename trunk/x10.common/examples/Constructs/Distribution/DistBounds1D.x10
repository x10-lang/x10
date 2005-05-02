import x10.lang.*;
import java.util.Random;
/**
 * Array bounds test - 1D
 *
 * randomly generate 1D arrays and indices
 *
 * This version also generates random distributions for the
 * arrays
 *
 * see if the array index out of bounds exception occurs 
 * in the right  conditions
 *
 */

public class DistBounds1D {


public boolean run() {
	final int COUNT=200;
	final int L=10;
	final int K=3;
	for(int n=0;n<COUNT;n++) {
		int i=ranInt(-L-K,L+K);
		int lb1=ranInt(-L,L);
		int ub1=ranInt(lb1-1,L); // include empty reg.
		int d=ranInt(0,dist.N_DIST_TYPES-1);
		boolean withinBounds=arrayAccess(lb1,ub1,i,d);
		chk(iff(withinBounds,
			    i>=lb1 && i<=ub1));
	}		
	return true;
}


/**
 * create a[lb1..ub1] then access a[i], return true iff
 * no array bounds exception occurred
 */
private static boolean arrayAccess(int lb1, int ub1, final int i, int distType) {


//pr(lb1+" "+ub1+" "+i+" "+distType);

final int[.] a =new int[dist.getDist(distType,(lb1:ub1))];

boolean withinBounds=true;
try {
	chk(a.distribution[i].id<x10.lang.place.MAX_PLACES &&
            a.distribution[i].id>=0);
	finish async(a.distribution[i]) {
		a[i]=0xabcdef07;
		chk(a[i]==0xabcdef07); 
	}
} catch (ArrayIndexOutOfBoundsException e) {
	withinBounds=false;
}

//pr(lb1+" "+ub1+" "+i+" "+distType+" "+withinBounds);

return withinBounds;
}

// utility functions after this point

/**
 * print a string
 */
private static void pr(String s) {
	System.out.println(s);
}

/**
 * assert function
 */
private static void chk(boolean b) {
	if (!b) throw new Error();
}

/**
 * true iff (x if and only if y)
 */
private static boolean iff(boolean x, boolean y) {
	return x==y;
}

private Random myRand=new Random(1L);

/**
 * return a random integer between lb and ub (inclusive)
 */
private int ranInt(int lb,int ub) {
	return lb+myRand.nextInt(ub-lb+1);
}


    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new DistBounds1D()).run();
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
 * utility for creating a distribution from a
 * a distribution type int value
 */
class dist {
   // Java has poor support for enum
   public final static int BLOCK=0;
   public final static int CYCLIC=1;
   public final static int CONSTANT=2;
   public final static int RANDOM=3;
   public final static int ARBITRARY=4;
   public final static int N_DIST_TYPES=5;

   /**
    * Return a distribution with region r, of type disttype
    *
    */

   public static distribution getDist(int distType, region r) {
      switch(distType) {
         case BLOCK: return distribution.factory.block(r);
         case CYCLIC: return distribution.factory.cyclic(r);
         case CONSTANT: return distribution.factory.constant(r, here);
         case RANDOM: return distribution.factory.random(r);
         case ARBITRARY: return distribution.factory.arbitrary(r);
         default: throw new Error("TODO");
      }
     
   } 
}
