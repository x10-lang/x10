import harness.x10Test;

/**
 * Check that an int it generates the correct depclause for its type.
 */
public class IntLitDepType_MustFailCompile extends x10Test {
	public boolean run() {
		int(:self==1) f =  2;
		return true;
	}

	public static void main(String[] args) {
		new IntLitDepType_MustFailCompile().execute();
	}


}

