import harness.x10Test;

/**
 * 3 should be an int, and ints are objects.
 *
 * @author vj, igor 09/06
 */
public class PrimitiveLiteralIsAnObject extends x10Test {

	public boolean run() {
		return 3 instanceof x10.lang.Object;
	}

	public static void main(String[] args) {
		new PrimitiveLiteralIsAnObject().execute();
	}
}

