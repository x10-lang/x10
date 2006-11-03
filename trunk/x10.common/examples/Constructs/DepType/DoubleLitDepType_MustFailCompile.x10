import harness.x10Test;

/**
 * Check that a double lit generates a dep clause.
 */
public class DoubleLitDepType_MustFailCompile extends x10Test {
	public boolean run() {
		double(:self==1.2D) f =  1.3D;
		return true;
	}

	public static void main(String[] args) {
		new DoubleLitDepType_MustFailCompile().execute();
	}


}

