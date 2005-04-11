import x10.lang.*;
public class Future3Boxed {
	public boolean run() {
		future<x10.compilergenerated.BoxedInteger> x = future { new x10.compilergenerated.BoxedInteger(42)};
		return (x.force()).intValue()==42;
	}
	public static void main(String args[]) {
		boolean b= (new Future3Boxed()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

