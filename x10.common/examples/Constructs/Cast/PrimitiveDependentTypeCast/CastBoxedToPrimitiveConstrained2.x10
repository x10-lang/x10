import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastBoxedToPrimitiveConstrained2 extends x10Test {

	public boolean run() {
		try {
			x10.lang.Object obj = 2;
			int i = (int(:self==3)) obj; 
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new CastBoxedToPrimitiveConstrained2().execute();
	}
}