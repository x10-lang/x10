public class HWExtern {
	static {
		System.loadLibrary("HWExtern_externs");
	}
	private static final double[.] arr = new double[[0:1]] (point p[i]) { return (double)i; };
	public static extern void processArray(double[.] a, int i);
	public static void main(String[] s) {
		processArray(arr, 0);
		System.out.println(arr[0]);
	}
}

