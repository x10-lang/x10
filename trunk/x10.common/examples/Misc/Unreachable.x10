/**
 *@author Mandana Vaziri, kemal 4/2005
 *
 * Test resulted in unreachable statement message
 * as of 4/20/2005
 *
 */

public class Unreachable {
 	int N = 10;
	region R = [0:N];
	distribution D = distribution.factory.arbitrary(R);
	
	void test() {
		async(D[0]) {	
			return ; 
		}
	}

	public boolean run() {
		finish test();
		return true;
	}

	public static void main(String args[]) {
		boolean b= (new Unreachable()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
		
}
