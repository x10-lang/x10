import java.util.Random;
/**
 * Array bounds test - 1D
 *
 * Randomly generate 1D arrays and indices.
 *
 * See if the array index out of bounds exception occurs 
 * in the right  conditions.
 *
 * @author kemal 1/2005
 *
 */

public class ArrayBounds1D {


public boolean run() {
	final int COUNT=100;
	final int L=10;
	final int K=3;
	for(int n=0;n<COUNT;n++) {
		int i=ranInt(-L-K,L+K);
		int lb1=ranInt(-L,L);
		int ub1=ranInt(lb1-1,L); // include empty reg.
		boolean withinBounds=arrayAccess(lb1,ub1,i);
		chk(iff(withinBounds,
			    i>=lb1 && i<=ub1));
	}		
	return true;
}


/**
 * create a[lb1..ub1] then access a[i], return true iff
 * no array bounds exception occurred
 */
private static boolean arrayAccess(int lb1, int ub1, int i) {


//pr(lb1+" "+ub1+" "+i);

int[.] a =new int[(lb1:ub1)->here];

boolean withinBounds=true;
try {
	a[i]=0xabcdef07;
	chk(a[i]==0xabcdef07);
} catch (ArrayIndexOutOfBoundsException e) {
	withinBounds=false;
}

//pr(lb1+" "+ub1+" "+i+" "+withinBounds);

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
                finish async b.val=(new ArrayBounds1D()).run();
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
