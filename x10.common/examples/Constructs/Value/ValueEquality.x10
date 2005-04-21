/**
 * @author Christian Grothoff
 */
public class ValueEquality {
	public static boolean run() {
	    V v1 = new V(1);
	    V v2 = new V(1);
	    return v1 == v2;
	}
	public static void main(String args[]) {
		boolean b = (new ValueEquality()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	static value V extends x10.lang.Object {
	    int v;
	    V(int i) {
	       this.v = i;
	    }
	}
}
