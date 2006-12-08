import harness.x10Test;

/**
 * Check that a float literal can be cast to float.
 */
public class DoubleLitDepType extends x10Test {
	public boolean run() {
		double(:self==1.2D) f =  1.2D;
		return true;
	}

	public static void main(String[] args) {
		new DoubleLitDepType().execute();
	}


}

