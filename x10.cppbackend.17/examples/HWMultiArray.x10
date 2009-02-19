public class HWMultiArray {
	public static void main(String[] s) {
		final String[][][] data = { { { }, { "Hello," } }, { { "world!" }, { } } };
		System.out.println(data[0][1][0]+" "+data[1][0][0]);
	}
}
