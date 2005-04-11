import x10.lang.*; import java.util.Random;
/**
 * Array bounds test - 3D
 *
 * randomly generate 3D arrays and indices,
 *
 * see if the array index out of bounds exception occurs 
 * in the right  conditions
 *
 */

public class ArrayBounds3D {


public boolean run() {
	final int COUNT=100;
	final int L=3;
	final int K=1;
	for(int n=0;n<COUNT;n++) {
		int i=ranInt(-L-K,L+K);
		int j=ranInt(-L-K,L+K);
		int k=ranInt(-L-K,L+K);
		int lb1=ranInt(-L,L);
		int lb2=ranInt(-L,L);
		int lb3=ranInt(-L,L);
		int ub1=ranInt(lb1,L); 
		int ub2=ranInt(lb2,L);
		int ub3=ranInt(lb3,L);
		boolean withinBounds=arrayAccess(lb1,ub1,lb2,ub2,lb3,ub3,i,j,k);
		chk(iff(withinBounds,
			    i>=lb1 && i<=ub1 &&
			    j>=lb2 && j<=ub2 &&
			    k>=lb3 && k<=ub3));
	}		
	return true;
}

/**
 * create a[lb1..ub1,lb2..ub2,lb3..ub3] then access a[i,j,k], 
 * return true iff
 * no array bounds exception occurred
 */

private static boolean arrayAccess(int lb1, int ub1, 
      int lb2, int ub2, int lb3,int ub3, int i, int j, int k) {

//pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+lb3+" "+ub3+" "+i+" "+j+" "+k);

int[.] a =new int[distHere(rg(rg(lb1,ub1),rg(lb2,ub2),rg(lb3,ub3)))];

boolean withinBounds=true;
try {
	a[i,j,k]=0xabcdef07;
	chk(a[i,j,k]==0xabcdef07);
} catch (ArrayIndexOutOfBoundsException e) {
	withinBounds=false;
}

//pr(lb1+" "+ub1+" "+lb2+" "+ub2+" "+lb3+" "+ub3+" "+i+" "+j+" "+k+" "+ withinBounds); 
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


/**
 *  Create the region lb..ub
 */
private static region rg(int lb, int ub) {
	return region.factory.region(lb,ub);
}

/**
 * Create the region (r1 times r2)
 */

private static region rg(region r1, region r2) {
	return region.factory.region(new region[] {r1,r2});
}

/**
 * Create the region (r1 times r2 times r3)
 */

private static region rg(region r1, region r2, region r3) {
	return region.factory.region(new region[] {r1,r2,r3});
}

/**
 * create the constant-here distribution  for region r
 */
private static distribution distHere(region r) {
	return distribution.factory.constant(r, here);
}

private Random myRand=new Random(1L);

/**
 * return a random integer between lb and ub (inclusive)
 */
private int ranInt(int lb,int ub) {
	return lb+myRand.nextInt(ub-lb+1);
}

public static void main(String args[]) {
	boolean b=true;
	try {
		b= (new ArrayBounds3D()).run();
	} catch (Error e) {
		e.printStackTrace();
		b= false;
	}
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
}

}
