import x10.lang.*;
public class Future1Boxed {
	public boolean run() {
		future<x10.compilergenerated.BoxedInteger> x = future (here) {new x10.compilergenerated.BoxedInteger(42)};
		return (x.force()).intValue()==42;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Future1Boxed()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }

}
