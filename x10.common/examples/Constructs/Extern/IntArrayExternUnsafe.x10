import harness.x10Test;

/**
 * Test to check that unsafe is being parsed correctly.
 */
public class IntArrayExternUnsafe extends x10Test {

	public static extern void howdy(int[.] yy);
	static { System.loadLibrary("IntArrayExternUnsafe"); }

	public boolean run() {
		int high = 10;
		boolean verified = false;
		dist d = [0:high]->here;
		int[.] y = new int unsafe[d];

		for (int j = 0; j < 10; ++j) {
			y[j] = j;
		}

		howdy(y);

		for (int j = 0; j < 10; ++j) {
			int expected = j + 100;
			if (y[j] != expected) {
				System.out.println("y["+j+"] = "+y[j]+" != "+expected);
				return false;
			}
			//System.out.println("y["+j+"]:"+(y[j]));
		}
		return true;
	}

	public static void main(String[] args) {
		new IntArrayExternUnsafe().execute();
	}
}

