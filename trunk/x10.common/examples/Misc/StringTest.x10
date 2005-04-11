import x10.lang.*;
/*
 * Testing quotes in strings
 */
public class StringTest {
	public int val;
	public StringTest() {
		val=10;
	}
	public boolean run() { 
		String foo="the number is "+val;
		if(!( val==10 && foo.equals("the number is "+"10"))) return false;
		if(foo.charAt(2) !='e') return false;
		return true;
		
	}
	public static void main(String args[]) {
		boolean b=(new StringTest()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
