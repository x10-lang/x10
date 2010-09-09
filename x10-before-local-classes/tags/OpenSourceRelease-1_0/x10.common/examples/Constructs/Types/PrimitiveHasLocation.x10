import harness.x10Test;

/**
 * 3 should be an int, and ints are objects.
 *
 * @author vj, igor 09/06
 */
public class PrimitiveHasLocation extends x10Test {

	public boolean run() {
	int x = 3;
		return x.location==here;
	}

	public static void main(String[] args) {
		new PrimitiveHasLocation().execute();
	}
}

