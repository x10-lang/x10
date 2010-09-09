public class HWForeach {
	static final String HW = "Hello, World!";
	static final region R = [0:12];
	static final char[.] chars = new char[R] (point p[i]) { return HW.charAt(i); };
	public static void main(String[] a) {
		finish foreach (point p : chars) {
			// will scramble the string.
			System.out.println("" + chars[p]);
		}
		System.out.println();
	}
}
