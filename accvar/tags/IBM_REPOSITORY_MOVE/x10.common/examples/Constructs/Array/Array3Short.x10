import harness.x10Test;

/**
 * Ensures short arrays are implemented.
 */
public class Array3Short extends x10Test {

	public boolean run() {
		short[.] ia = new short[[1:10,1:10]->here];
		ia[1,1] = (short) 42;
		return (42 == ia[1,1]);
	}

	public static void main(String[] args) {
		new Array3Short().execute();
	}
}

