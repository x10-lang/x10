import harness.x10Test;

/**
 * Simple array test #1.
 */
public class ImplicitFinal extends x10Test {

	public boolean run() {
		point p = [1,2,3];
		region r = [10:10];
		point p1 = [1+1,2+2,3+3];
		return true;
	}

	public static void main(String[] args) {
		new ImplicitFinal().execute();
	}
}

