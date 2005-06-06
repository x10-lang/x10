/**
 * converting nullable future int to future int causes
 * exception when value is null
 */
public class NullableFuture2 {
	public boolean run() {
                boolean gotNull=false;
		nullable future<int> x;
		if (!X.t()) {
			x=future{42};
		} else {
			x=null;
		}
		try {
			X.use(((future<int>)x).force());
		} catch (NullPointerException e) {
			gotNull=true;
		}
                return gotNull;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new NullableFuture2()).run();
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

class X {
   public static boolean t() {return true;}
   public static void use(int x) {}
}
   

