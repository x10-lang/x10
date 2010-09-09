import harness.x10Test;

/**
 * Accessing p[3] in a 2D point should cause an array
 * index out of  bounds exception.
 *
 * @author kemal, 6/2005
 */
public class PointIndex extends x10Test {

	public boolean run() {
		int sum = 0;
		boolean gotException;
		point p = [1,2];

		gotException = false;
		try {
			sum += p[-1];
		} catch (ArrayIndexOutOfBoundsException e) {
			gotException = true;
		}
		System.out.println("1: sum = "+sum+" gotException = "+gotException);
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		try {
			sum += p[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			gotException = true;
		}
		System.out.println("2: sum = "+sum+" gotException = "+gotException);
		return sum == 0 && gotException;
	}

	public static void main(String[] args) {
		new PointIndex().execute();
	}
}

