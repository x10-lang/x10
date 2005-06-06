/**
 * Casting nullable String to String should not cause
 * exception when the value is not null
 */
public class Nullable2 {
	boolean gotNull;
	public  boolean run() {
                gotNull=false;
		try {
			nullable String x = "May the force be with you!";
			String y = X.chkForNull(x);
			X.use(y);
		} catch (NullPointerException e) {
			gotNull=true;
		}
                return !gotNull;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Nullable2()).run();
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

// to make optimizations difficult
class X {
    public static void use(String y) {}

    public static String chkForNull (nullable String x) {
           return (String)x;
    }
}

