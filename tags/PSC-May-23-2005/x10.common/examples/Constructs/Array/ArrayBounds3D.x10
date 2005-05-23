import java.util.Random;
/**
 * Array bounds test - 3D
 *
 * Randomly generate 3D arrays and indices.
 *
 * See if the array index out of bounds exception occurs 
 * in the right  conditions.
 *
 * @author kemal 1/2005
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

int[.] a =new int[[lb1:ub1,lb2:ub2,lb3:ub3]->here];

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
                finish b.val=(new ArrayBounds3D()).run();
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
