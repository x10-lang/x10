/**
 * @author Christoph von Praun, Christian Grothoff
 */
public class RemoteAccessCheck {
	public static boolean run() {
		dist d = dist.factory.unique(x10.lang.place.places);
		if (d.region.size() < 2) {
			System.out.println("RemoteAccessCheck requires 2 or more places.");
			return false;
		}
		final A a = future(d[0]) { new A() }.force();
	    int error = future(d[1]) { checkField(a) }.force();
	    if (error != 0)
	        System.out.println(error);
	    int error2 = future(d[1]) { checkMethod(a) }.force();
	    if (error2 != 0)
	        System.out.println(error2);
	    return error == 0 && error2 == 0;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new RemoteAccessCheck()).run();
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

	static int checkField(A a) {
		try {
		    a.f.a = a;
		    return 2;
		} catch (BadPlaceException bpe) {
		}
		return 0;
		
	}
        static int checkMethod(A a) {
		try {
		    a.f.m();
		    return 3;
		} catch (BadPlaceException bpe) {
		}
		
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
