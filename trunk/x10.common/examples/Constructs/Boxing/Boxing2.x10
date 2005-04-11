
import x10.lang.*;

/**
 * tests interaction of parentheses and boxing
 * 
 */
public class Boxing2 {
	public boolean run() {
		String x="The number is "+(X.five()*2);
		if (!x.equals("The number is 10")) return false;
		String y="The number is "+(200+X.five()*2);
		if (!y.equals("The number is 210")) return false;
		return true;
	}
	public static void main(String args[]) {
		boolean b= (new Boxing2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

class X {
	public static int five() { return 5;}
}
