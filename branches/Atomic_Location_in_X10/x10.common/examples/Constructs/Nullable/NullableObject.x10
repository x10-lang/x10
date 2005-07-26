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
		} catch (ClassCastException e) {
		        gotNull=true;	
		}
                return gotNull;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new NullableObject()).run();
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
    public static nullable x10.lang.Object mynull() {return null; }
    public static void use(x10.lang.Object y) { }
}

