import x10.lang.*;

/**
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * Minimal test for clock.
 * run() method returns true if successful, false otherwise.
 */
public class ClockTest1  {

	boolean flag;
	
	public boolean run() {
		clock c = clock.factory.clock();
  	    now (c) { atomic {flag = true;} }
		next ;		
		boolean b;
		atomic{b=flag;}
		return b;
	}
	public static void main(String args[]) {
		boolean b= (new ClockTest1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
