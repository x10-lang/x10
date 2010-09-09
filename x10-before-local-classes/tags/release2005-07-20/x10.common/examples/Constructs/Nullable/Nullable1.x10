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
		} catch (ClassCastException e) {
			gotNull=true;
		}
                return gotNull;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Nullable1()).run();
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
    public static void use(String y) {}
}

