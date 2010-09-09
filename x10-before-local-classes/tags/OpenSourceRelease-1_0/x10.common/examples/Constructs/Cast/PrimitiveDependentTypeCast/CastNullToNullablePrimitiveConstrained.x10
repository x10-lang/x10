import harness.x10Test;

/**
 * Purpose: A nullable type can always cast null.
 * @author vcave
 **/
 public class CastNullToNullablePrimitiveConstrained extends x10Test {

	public boolean run() {
		nullable<int(:self==3)> i = (nullable<int(:self==3)>) null;
		return true;
	}

	public static void main(String[] args) {
		new CastNullToNullablePrimitiveConstrained().execute();
	}
}