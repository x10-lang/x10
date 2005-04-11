import x10.lang.*;
/**
 * Casting nullable T to T when the value is null
 * should cause a null pointer exception.
 * (unless T is itself nullable S)
 */
public class Nullable0Ref {
	public  boolean run() {
                boolean gotNull=false;
		try {
			nullable Nullable0Ref x = X.mynull();
			Nullable0Ref y = (Nullable0Ref) x;
                        X.use(y);
		} catch (NullPointerException e) {
		        gotNull=true;	
		}
                return gotNull;
	}
	public static void main(String args[]) {
		boolean b= (new Nullable0Ref()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
class X {
    public static nullable Nullable0Ref mynull() {return null; }
    public static void use(Nullable0Ref y) { }
}

