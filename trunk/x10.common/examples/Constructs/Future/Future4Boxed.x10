public class Future4Boxed {
	public boolean run() {
		future<x10.compilergenerated.BoxedInteger> x = future{ new x10.compilergenerated.BoxedInteger(41)};
		return (x.force().intValue() +1)==42;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Future4Boxed()).run();
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

