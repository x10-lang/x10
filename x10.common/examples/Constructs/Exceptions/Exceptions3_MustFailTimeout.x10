/**
 *@author kemal 5/2005
 *
 *Test multiple exceptions.
 *
 *
 *Behavior of conjunctive finish:
 *
 *If any child activity does not terminate, 
 *parent will not terminate, even if other children threw exceptions.
 *
 *Desired behavior of test: must time out.
 *
 *
 */

class X {
	static void use (byte[] x) {}
	static boolean t() {return true;}
}


	

public class Exceptions3_MustFailTimeout {
	const int MAXINT= 2147483647;
	void memoryHog() {byte[] a=new byte[MAXINT]; X.use(a);}
	public boolean run() {
	     final int M=4;
	     try {
		finish {
		 ateach(point [i]:dist.factory.unique()) {
			foreach(point [j]:1:M)
				memoryHog();
		 }
		 async(here) await(!X.t());
	        }
             } catch (x10.lang.MultipleExceptions me) {
             }
	     return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Exceptions3_MustFailTimeout()).run();
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
