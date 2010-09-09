import harness.x10Test;

/**
 * Check that an int lit generates the correct dep clause.
 */
public class LongLitDepType extends x10Test {
	public boolean run() {
		long(:self==100L) f =  100L;
		return true;
	}

	public static void main(String[] args) {
		new LongLitDepType().execute();
	}


}

