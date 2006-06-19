import harness.x10Test;

/**
 * Array Initializer test.
 */
public class ArrayInitializer extends x10Test {

	public boolean run() {
		region e = [0:9];
		region r = [e, e, e];
		//TODO: next line causes runtime error
		//dist d=r->here;
		dist d = [0:9,0:9,0:9]->here;

		final int value [d] ia =
			new int value [d]
			new intArray.pointwiseOp() {
				public int apply(point [i,j,k]) {
					return i;
				}
			};

		for (point [i,j,k]: d) chk(ia[i,j,k] == i);

		return true;
	}

	public static void main(String[] args) {
		new ArrayInitializer().execute();
	}
}

