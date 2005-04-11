import x10.lang.*;
/**
 * Casting nullable String to String when value is null
 * should cause a null pointer exception
 */
public class Nullable1 {
	public boolean run() {
                boolean gotNull=false;
		try {
			nullable String x = null;
			String y = (String)x;
		        X.use(y);	
		} catch (NullPointerException e) {
			gotNull=true;
		}
                return gotNull;
	}
	public static void main(String args[]) {
		boolean b= (new Nullable1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

class X {
    public static void use(String y) {}
}

