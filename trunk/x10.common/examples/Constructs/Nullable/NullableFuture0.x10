import x10.lang.*;
/**
 * nullable future test
 *
 * nullable future int x;
 *
 * x.force()==e if x is set to future{e}
 *
 * x.force() should throw an exception if x is null
 * and should work properly if x is not null.
 *
 * nullable T has the same methods as a non-nullable T, but
 * each method must  
 * throw a null ptr exception if "this" is null (as usual)
 */
public class NullableFuture0 {

	public boolean run() {
		nullable future <int>  x;
		if (X.t()) {
			x=future{42};
		} else {
			x=null;
		}
		if (x.force() !=42) return false;

		if (!X.t()) {} else {x=null;} 
		boolean gotNull=false;
		try {
		    if (x.force()==42) return false;
		} catch(NullPointerException e) {
			gotNull=true;
		}
		return gotNull;
	}

	public static void main(String args[]) {
		boolean b= (new NullableFuture0()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

class X {
	public static boolean t() { return true; }
}

