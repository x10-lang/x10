import harness.x10Test;

/**
 * Ensures boolean arrays are implemented.
 */
public class Array3Boolean extends x10Test {

	public boolean run() {
		boolean[.] ia = new boolean[[1:10,1:10]->here];
		ia[1,1] = true;
		return (true == ia[1,1]);
	}

	public static void main(String[] args) {
		new Array3Boolean().execute();
	}
}

