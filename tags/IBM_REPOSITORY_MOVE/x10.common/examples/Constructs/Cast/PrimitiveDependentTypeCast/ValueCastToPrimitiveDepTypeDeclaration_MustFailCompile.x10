import harness.x10Test;

/**
 * Purpose: Checks dependent type constraint information are propagated along with the variable.
 * Issue: Constant to promotedoes not meet constraint of targeted type.
 * @author vcave
 **/
public class ValueCastToPrimitiveDepTypeDeclaration_MustFailCompile extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 0) j = ((int (: self == 0)) 1);
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new ValueCastToPrimitiveDepTypeDeclaration_MustFailCompile().execute();
	}

}
 