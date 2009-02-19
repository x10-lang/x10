public class UnaryNeg {
	static final String HW = "Hello, World!";
	static final region R = [0:12];
	static final char[.] chars = new char[R] (point p[i]) { return HW.charAt(i); };
	public static void main(String[] a) {
		for (point p : chars) {
			System.out.println("" + chars[+(- (-p))]);
		}
		System.out.println();
	}
}
