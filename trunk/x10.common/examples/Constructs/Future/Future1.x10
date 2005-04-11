import x10.lang.*;
/**
 * Future test;
 * Future activity will run in (41).place
 */
public class Future1 {
	public boolean run() {
		future<int> x = future{41};
		return x.force()+1==42;
	}
	public static void main(String args[]) {
		boolean b= (new Future1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
