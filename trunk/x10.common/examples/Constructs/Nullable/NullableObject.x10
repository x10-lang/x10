/**
 * Casting nullable T to T when the value is null
 * should cause a null pointer exception.
 * (unless T is itself nullable S)
 * This is also true for T==Object
 * 
 */
public class NullableObject {
	public  boolean run() {
                boolean gotNull=false;
		try {
			nullable x10.lang.Object x = X.mynull();
			x10.lang.Object y = (x10.lang.Object) x;
                        X.use(y);
		} catch (NullPointerException e) {
		        gotNull=true;	
		}
                return gotNull;
	}
	public static void main(String args[]) {
		boolean b= (new NullableObject()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
class X {
    public static nullable x10.lang.Object mynull() {return null; }
    public static void use(x10.lang.Object y) { }
}

