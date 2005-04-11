import x10.lang.*;

/**
 * Minimal test for await.
 * This may need memory fences with weak consistency model
 */

public class AwaitTest2  {
	
	int val=42;
	
	public boolean run() {
		await(val == 42);
		return true;
	}
	public static void main(String args[]) {
		boolean b= (new AwaitTest2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
