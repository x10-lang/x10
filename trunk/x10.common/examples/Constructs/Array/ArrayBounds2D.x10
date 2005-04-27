import java.util.Random;
/**
 * Array bounds test - 2D
 *
 * Aandomly generate 2D arrays and indices.
 *
 * See if the array index out of bounds exception occurs 
 * in the right  conditions.
 *
 * @author kemal 1/2005
 *
 */

public class ArrayBounds2D {


public boolean run() {
	final int COUNT=100;
	final int L=10;
	final int K=3;
	for(int n=0;n<COUNT;n++) {
		int i=ranInt(-L-K,L+K);
		int j=ranInt(-L-K,L+K);
		int lb1=ranInt(-L,L);
		int lb2=ranInt(-L,L);
		int ub1=ranInt(lb1,L); 
		int ub2=ranInt(lb2,L);
		boolean withinBounds=arrayAccess(lb1,ub1,lb2,ub2,i,j);
		chk(iff(withinBounds,
			    i>=lb1 && i<=ub1 &&
			    j>=lb2 && j<=ub2));
	}		
	return true;
}

/**
 * create a[lb1..ub1,lb2..ub2] then access a[i,j], return true iff
 * no array bounds exception occurred
 */

private static boolean arrayAccess(int lb1, int ub1, int lb2, int ub2, int i, int j) {

//pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+i+" "+j);

int[.] a =new int[[lb1:ub1,lb2:ub2]->here];

boolean withinBounds=true;
try {
	a[i,j]=0xabcdef07;
	chk(a[i,j]==0xabcdef07);
} catch (ArrayIndexOutOfBoundsException e) {
	withinBounds=false;
}

//pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+i+" "+j+" "+withinBounds);

return withinBounds;
}

// utility methods after this point

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

public static void main(String args[]) {
	boolean b=false;
	try {
		b= (new ArrayBounds2D()).run();
	} catch (Throwable e) {
		e.printStackTrace();
		b= false;
	}
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
}

}
