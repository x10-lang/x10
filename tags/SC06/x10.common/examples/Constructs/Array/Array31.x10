import harness.x10Test;

/**
 * Simple array test #3. Tests declaration of arrays, storing in local
 * variables, accessing and updating for 1-d arrays.
 */
public class Array31 extends x10Test {

	public boolean run() {
		dist d = [1:10]->here;
		int[.] ia = new int[d];
		ia[1] = 42;
		return 42 == ia[1];
	}

	public static void main(String[] args) {
		new Array31().execute();
	}
}

