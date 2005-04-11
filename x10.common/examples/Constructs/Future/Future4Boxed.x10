import x10.lang.*;
public class Future4Boxed {
	public boolean run() {
		future<x10.compilergenerated.BoxedInteger> x = future{ new x10.compilergenerated.BoxedInteger(41)};
		return (x.force().intValue() +1)==42;
	}
	public static void main(String args[]) {
		boolean b= (new Future4Boxed()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}

