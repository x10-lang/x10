import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastBoxedToPrimitiveConstrained1 extends x10Test {

	public boolean run() {
		x10.lang.Object obj = 3;
		int i = (int(:self==3)) obj;
		return true;
	}

	public static void main(String[] args) {
		new CastBoxedToPrimitiveConstrained1().execute();
	}
}