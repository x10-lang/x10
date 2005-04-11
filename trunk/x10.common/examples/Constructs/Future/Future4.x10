import x10.lang.*;
public class Future4 {
	public boolean run() {
		future<int> x = future{46};
		return (x.force()+1)==47;
	}
	public static void main(String args[]) {
		boolean b= (new Future4()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

