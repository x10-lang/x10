/**
 * @author Christian Grothoff
 */
public class ValueEquality {
	public static boolean run() {
	    V v1 = new V(1);
	    V v2 = new V(1);
	    return v1 == v2;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ValueEquality()).run();
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

	static value V extends x10.lang.Object {
	    int v;
	    V(int i) {
	       this.v = i;
	    }
	}
}
