/*
 * Simple data race
 */
import x10.lang.*;

public class SimpleDataRace {

    public static void increment(final int[.] arr) {
	foreach (final point p: arr.distribution) { 
	    // oops forgot atomic here?
	    arr[p] = arr[p] + 1; 
	}
    }

    public boolean run() {
	final region r = region.factory.region(0,100);
	final distribution d = distribution.factory.constant(r, here);
	final int[d] table = new int[d];
	
	increment(table);
	// oops should have put finish?
	
	point p = point.factory.point(1);
	// oops forgot atomic here?
	table[p] = 123;

	// if the value is 1 or 124 - we observed a lost 
	// update due to the data race!
	return (table[p] == 123);
    }

    public static void main(String args[]) {
	boolean b= (new SimpleDataRace()).run();
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
    }

}
