/*
 *Simple data race, resulting from a forgotten finish.
 *
 *Added delay statements to make data race deterministically occur.
 *
 *@author cvp, kemal
 */

public class SimpleDataRace_MustFailRun {

    public static void increment(final int[.] arr) {
	// oops, should have written 'finish foreach'
	foreach (final point p: arr) { 
	    delay(3000); // to force data race 
	    atomic arr[p] +=1;
	}
    }

    public boolean run() {
	final int[.] table = new int[[0:10]->here];
	
	increment(table);
	// updates to table continuing here, because
	// increment did not finish
	atomic table[1] = 123;
	delay(5000); //to force data race

	// if the value is 1 or 124 - we observed a lost 
	// update due to the data race!
	int t;
	atomic t=table[1];
	System.out.println("table[1]="+t);
	return (t == 123);
    }

    static void delay(int millis) {
	try {
		java.lang.Thread.sleep(millis);
	} catch(InterruptedException e) {
	}
    }

    public static void main(String args[]) {
	boolean b= (new SimpleDataRace_MustFailRun()).run();
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
    }

}
