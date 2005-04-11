import x10.lang.*;
public class Future3 {
	public boolean run() {
		future<int> x = future{47};
		return (x.force())==47;
	}
	public static void main(String args[]) {
		boolean b= (new Future3()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}

