public class HWMultiArray {
	public static void main(String[] s) {
		final String[][][] data = { { { }, { "Hello," } }, { { "world!" }, { } } };
		System.out.println(data[0][1][0]+" "+data[1][0][0]);
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111153
//@@X101X@@TCASE@@HWMultiArray
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWMultiArray.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Hello, world!
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
