import x10.lang.*;
/**
 * @author Christian Grothoff
 */
public class RemoteAccessCheck {
	public static boolean run() {
		distribution d = distribution.factory.unique(x10.lang.place.places);
		if (d.region.size() < 2) {
			System.out.println("RemoteAccessCheck requires 2 or more places.");
			return false;
		}
		final A a = future(d[0]) { new A() }.force();
	    int error = future(d[1]) { check(a) }.force();
	    if (error != 0)
	        System.out.println(error);
	    return error == 0;
	}
	public static void main(String args[]) {
		boolean b= (new RemoteAccessCheck()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	static int check(A a) {
		try {
			a.a[1] = 42;
			return 1;
		} catch (java.lang.AssertionError ae) {
		}
		/*
		try {
		    a.f.a = a;
		    return 2;
		} catch (BadPlaceException bpe) {
		}
		try {
		    a.f.m();
		    return 3;
		} catch (BadPlaceException bpe) {
		}
		*/
		return 0;
		
	}
	static value A extends x10.lang.Object {
	    F f;
	    int[.] a;
	    A() {
	       f = new F();
	       a = new int[[0:4]->here](point i) { return 3;} ;
	    }
	}
	static class F {
	    A a;
	    void m() { }
	}
}
