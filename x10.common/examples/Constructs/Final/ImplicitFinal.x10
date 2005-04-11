/*
 * Simple array test #1
 */
import x10.lang.*;
public class ImplicitFinal {
	
	public boolean run() {
		point p = [1,2,3];
		region r = [10:10];
		point p1 = [1+1,2+2,3+3];
		return true;
	}
	
	public static void main(String args[]) {
		boolean b= (new ImplicitFinal()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
	
}
