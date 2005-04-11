import x10.lang.*;
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
	public static void main(String args[]) {
		boolean b= (new Nullable2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

// to make optimizations difficult
class X {
    public static void use(String y) {}

    public static String chkForNull (nullable String x) {
           return (String)x;
    }
}

