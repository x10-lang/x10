import harness.x10Test;

/**
 * Purpose: Cast of the null value should fail at compile time. 
 * Issue: 'null' value can't be cast to some Reference type.
 * Note: Array access are not inlined.
 * @author vcave
 **/
public class NullObjectToNonNullable extends x10Test {

	public boolean run() {
		try { 
		// little hack to get a non nullable object referencing null.
		X10DepTypeClassTwo [] array = new X10DepTypeClassTwo [1];

		// array[0] is null. The cast must throw a ClassCastException
		X10DepTypeClassTwo(:p==0&&q==1) nonNull2 = 
			(X10DepTypeClassTwo(:p==0&&q==1)) array[0];
		} catch(ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new NullObjectToNonNullable().execute();
	}

}
 