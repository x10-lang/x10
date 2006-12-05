import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastBoxedToNullablePrimitiveConstrained extends x10Test {

	public boolean run() {
		nullable<int(:self==3)> i = (nullable<int(:self==3)>) mth();
		return true;
	}
	
	public x10.lang.Object mth() {
		return 3;
	}
	public static void main(String[] args) {
		new CastBoxedToNullablePrimitiveConstrained().execute();
	}
}