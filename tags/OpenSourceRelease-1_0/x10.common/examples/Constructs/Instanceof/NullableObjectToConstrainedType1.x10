import harness.x10Test;

/**
 * Purpose: Check a nullable is not an instanceof a constrained type.
 * @author vcave
 **/
public class NullableObjectToConstrainedType1 extends x10Test {
	 
	public boolean run() {
		nullable<X10DepTypeClassOne> nullableVarNotNull =  new X10DepTypeClassOne(2);
		return !(nullableVarNotNull instanceof X10DepTypeClassOne(:p==1));
	}
	
	public static void main(String[] args) {
		new NullableObjectToConstrainedType1().execute();
	}
}
 