/**
 *@author kemal, 5/2005
 *
 * Test resulted in unreachable statement message
 * as of 5/12/2005
 *
 */
class X {
	static boolean t() { return true;}
}

public class Unreachable2 {
 	final int N = 10;
	final region R = [0:N];
	final dist D = dist.factory.arbitrary(R);
	boolean flag;
	
	int test() {
		when(X.t()) {
			return 1; 
		}
	}

	public boolean run() {
		int x=test();
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Unreachable2()).run();
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
