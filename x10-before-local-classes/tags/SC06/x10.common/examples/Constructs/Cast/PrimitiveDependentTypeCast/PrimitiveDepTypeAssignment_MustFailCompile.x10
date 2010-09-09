import harness.x10Test;

/**
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * Issue: Value to cast does not meet constraint requirement of target type.
 * @author vcave
 **/
public class PrimitiveDepTypeAssignment_MustFailCompile extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 1) i = 1
			int (: self == 0) j = i;
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new PrimitiveDepTypeAssignment_MustFailCompile().execute();
	}

}
 