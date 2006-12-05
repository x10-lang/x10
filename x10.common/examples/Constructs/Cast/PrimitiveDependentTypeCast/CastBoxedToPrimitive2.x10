import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastBoxedToPrimitive2 extends x10Test {

	public boolean run() {
		try {
			x10.lang.Object obj = new CastBoxedToPrimitive2();
			int i = (int) obj; 
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new CastBoxedToPrimitive2().execute();
	}
}