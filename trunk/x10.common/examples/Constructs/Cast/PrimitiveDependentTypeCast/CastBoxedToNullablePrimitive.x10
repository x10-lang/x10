import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastBoxedToNullablePrimitive extends x10Test {

	public boolean run() {
		nullable<int> i = (nullable<int>) mth();
		return true;
	}
	
	public x10.lang.Object mth() {
		return 3;
	}
	public static void main(String[] args) {
		new CastBoxedToNullablePrimitive().execute();
	}
}