package ImportTestPackage1.SubPackage;

import ImportTestPackage2._T4;

/**
 * auxiliary class for ImportTest, also a test by itself.
 */
public class T3 {
	public static boolean m3(final int x) {
		return future(here){_T4.m4(x)}.force();
	}
	public boolean run() {
		return m3(49);
	}
	public static void main(String args[]) {
		boolean b=(new T3()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
