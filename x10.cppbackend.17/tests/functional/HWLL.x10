public class HWLib {
        public static extern void test();
	public static void main(String[] args) {
                        System.loadLibrary ("test.o");
		System.out.println("Hello World");
                test();
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111040
//@@X101X@@TCASE@@HWLL
//@@X101X@@VCODE@@FAIL_BUILD
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWLib.cc
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
