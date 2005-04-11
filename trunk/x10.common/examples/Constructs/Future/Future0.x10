import x10.lang.*;
/**
 * Future test
 * Implied place: Future activity will run in (42).place 
 */
public class Future0 {
	public static boolean run() {
		return ((future { 47}).force()) == 47;
	}
	public static void main(String args[]) {
		boolean b= (new Future0()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
