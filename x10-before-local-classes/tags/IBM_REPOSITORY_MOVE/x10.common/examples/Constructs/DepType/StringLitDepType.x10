import harness.x10Test;

/**
 * Check that a float literal can be cast to float.
 */
public class StringLitDepType extends x10Test {
	public boolean run() {
		String(:self=="abc") f = "abc";
		return true;
	}

	public static void main(String[] args) {
		new StringLitDepType().execute();
	}


}

