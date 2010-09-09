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
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Nullable0Ref()).run();
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
    public static nullable Nullable0Ref mynull() {return null; }
    public static void use(Nullable0Ref y) { }
}

