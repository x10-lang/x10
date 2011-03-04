import harness.x10Test;

/**
 * Purpose: Checks nullable cast is working for references type.
 * @author vcave
 **/
 public class CastNullToNullableReference extends x10Test {

	public boolean run() {
		nullable<x10.lang.Object> obj = (nullable<x10.lang.Object>) null;
		return true;
	}

	public static void main(String[] args) {
		new CastNullToNullableReference().execute();
	}
}