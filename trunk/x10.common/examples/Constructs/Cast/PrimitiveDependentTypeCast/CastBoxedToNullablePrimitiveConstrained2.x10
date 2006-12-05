import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastBoxedToNullablePrimitiveConstrained2 extends x10Test {

	public boolean run() {
		try {
		nullable<int(:self==3)> i = (nullable<int(:self==3)>) mth();
		} catch(ClassCastException e) {
			return true;
		}
		return false;
	}
	
	public x10.lang.Object mth() {
		return 5;
	}
	public static void main(String[] args) {
		new CastBoxedToNullablePrimitiveConstrained2().execute();
	}
}