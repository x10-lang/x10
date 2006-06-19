import harness.x10Test;

/**
 * Cannot bind point components in array declaration.
 *
 * @author igor, 1/2006
 */
public class ArrayPointBinding_MustFailCompile extends x10Test {

	public boolean run() {
		point p [i,j] [] = new point[1];
		p[0] = [1,2];

		return (i == 1 && j == 2);
	}

	public static void main(String[] args) {
		new ArrayPointBinding_MustFailCompile().execute();
	}
}

