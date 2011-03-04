import harness.x10Test;

/**
 * Purpose: Cast check code should detect constraint boxed in nullable type is not meet.
 * @author vcave
 **/
public class CastConstrainedToConstrainedNullable_MustFailCompile extends x10Test {

	public boolean run() {
		// null can be cast to any nullable 
		nullable<X10DepTypeClassOne(:p==1)> nullableVarCasted2 = 
			(nullable<X10DepTypeClassOne(:p==1)>) new X10DepTypeClassOne(2);

		return false;
	}

	public static void main(String[] args) {
		new CastConstrainedToConstrainedNullable_MustFailCompile().execute();
	}

}
 