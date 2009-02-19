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


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110600
//@@X101X@@TCASE@@HWExtern
//@@X101X@@VCODE@@FAIL_BUILD
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWExtern.cc
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
