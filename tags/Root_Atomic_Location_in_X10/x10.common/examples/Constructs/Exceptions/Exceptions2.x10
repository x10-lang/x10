/**
 *@author kemal 4/2005
 *
 *Test multiple exceptions with multiple children running out of memory.
 *
 *Parent must wait until all children are finished even if 
 *any child threw an out-of-memory exception.
 *
 *Note that this test case times-out if memoryHog()
 *shows more extensive memory-consuming behavior than here.
 *
 */

class Node2 {
	int data;
	nullable Node2 next;
}

class X {
	static void use (Node2 x) {}
	static void use (byte[] x) {}
}


	

public class Exceptions2 {
	const int MAXINT= 2147483647;
	const int N=1000000;
        final java.util.Set a=new java.util.HashSet();	
	void memoryHog() {byte[] a=new byte[MAXINT]; X.use(a);}
	public boolean run() {
	     final long N=1000000L;
	     final int M=4;
	     final int MIN_MSG_SIZE=100;
	     try {
		finish {
		 ateach(point [i]:dist.factory.unique()) {
			foreach(point [j]: [1:M])
				memoryHog();
		 }
		 async(here) {for(long i=0;i<N;i++) {Node2 x=new Node2();X.use(x);}}
	        }
		return false;
             } catch (x10.lang.MultipleExceptions me) {
		// Ensure that message is informative
		if ((me.toString()).length()<=MIN_MSG_SIZE) return false;
		//me.printStackTrace();
		return true;
             }
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Exceptions2()).run();
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
