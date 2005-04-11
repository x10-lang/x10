/**
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * Minimal test for async.
 * Uses busy-wait to check for execution of async.
 * run() method returns true if successful, false otherwise.
 */
public class AsyncTest1  {

	boolean flag;

	public boolean run() {
		async (here) { atomic{flag = true;} }
                boolean b;
                do {
                   atomic{b=flag;}
		} while(!b);
	  	return flag;
	}
	public static void main(String args[]) {
		boolean b= (new AsyncTest1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
