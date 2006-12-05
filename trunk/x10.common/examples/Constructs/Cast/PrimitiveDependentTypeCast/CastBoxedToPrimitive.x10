import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastBoxedToPrimitive extends x10Test {

	public boolean run() {
		x10.lang.Object obj = 3;
		int i = (int) obj;
		return true;
	}

	public static void main(String[] args) {
		new CastBoxedToPrimitive().execute();
	}
}