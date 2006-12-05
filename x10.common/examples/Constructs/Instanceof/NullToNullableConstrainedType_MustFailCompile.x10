import harness.x10Test;

/**
 * Purpose: Checks a null value is not an instance of a constrained nullable type
 * Issue: null is not an instance of a constrained nullable type.
 * @author vcave
 **/
public class NullToNullableConstrainedType_MustFailCompile extends x10Test {
	 
	public boolean run() {
		return !(null instanceof nullable<X10DepTypeClassOne(:p==1)>);
	}
	
	public static void main(String[] args) {
		new NullToNullableConstrainedType_MustFailCompile().execute();
	}
}
 