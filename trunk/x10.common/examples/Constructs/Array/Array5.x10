import x10.lang.*;
/* * This is a test, only a test...
 * 
 * Simple array test #5
 * Constructor called from main must have zero parameters. 
 */

public class Array5 {

	int[] ia;

	public Array5() {}

	public Array5(int [] ia) {
		this.ia = ia;
	}

	private boolean runtest() {
		ia[0] = 42;
		return 42 == ia[0];
		
	}

	public boolean run() {
		int[] temp = new int[1];
		temp[0] = 43;
		return (new Array5(temp)).runtest();
	}

	public static void main(String[] args) {
		boolean b=(new Array5()).run();	
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
