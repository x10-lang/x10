/**
 *@author Mandana Vaziri, kemal 4/2005
 *
 * Test resulted in unreachable statement message
 * as of 4/20/2005
 *
 */

public class Unreachable {
 	final int N = 10;
	final region R = [0:N];
	final dist D = dist.factory.arbitrary(R);
	
	void test() {
		async(D[0]) {	
			return ; 
		}
	}

	public boolean run() {
		finish test();
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Unreachable()).run();
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
