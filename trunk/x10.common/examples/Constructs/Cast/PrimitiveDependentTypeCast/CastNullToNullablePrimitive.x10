import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastNullToNullablePrimitive extends x10Test {

	public boolean run() {
		nullable<int> i = (nullable<int>) null;
		return true;
	}

	public static void main(String[] args) {
		new CastNullToNullablePrimitive().execute();
	}
}