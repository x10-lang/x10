import harness.x10Test;

/**
 * Purpose: 
 * Issue: Contraint value is stored as an integer
 * @author vcave
 **/
public class Short_IntegerAssignment_MustFailCompile extends x10Test {

	public boolean run() {
		final short constraint = 0;
		short (:self == constraint) i = 0;
		return false;
	}

	public static void main(String[] args) {
		new Short_IntegerAssignment_MustFailCompile().execute();
	}

}
 