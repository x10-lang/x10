import harness.x10Test;

/**
 * Checks comparison between a primitive and a boxed one
 *
 * @author vcave
 */
public class ObjectEqualsPrimitive extends x10Test {

	public boolean run() {
		x10.lang.Object x = 2+1;
		boolean res = 3==x;
		res &= x==3;
		return res;
	}

	public static void main(String[] args) {
		new ObjectEqualsPrimitive().execute();
	}
}

