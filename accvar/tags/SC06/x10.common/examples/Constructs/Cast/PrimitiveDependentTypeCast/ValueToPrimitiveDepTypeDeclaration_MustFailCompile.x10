import harness.x10Test;

/**
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * Issue: Constant to assign does not meet constraint requirement of target type.
 * @author vcave
 **/
public class ValueToPrimitiveDepTypeDeclaration_MustFailCompile extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 0) j = 1;
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new ValueToPrimitiveDepTypeDeclaration_MustFailCompile().execute();
	}

}
 