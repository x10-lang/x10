import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastNullablePrimitiveToPrimitive extends x10Test {

	public boolean run() {
		try {
		nullable<int> k = null; // transformed to x10.compiler.BoxedInt
		int p = (int) k; // transformed to ((BoxedInt) k).intValue() 
		// --> fails because 'k' is null which throws a NullPointerException
		} catch (NullPointerException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new CastNullablePrimitiveToPrimitive().execute();
	}
}