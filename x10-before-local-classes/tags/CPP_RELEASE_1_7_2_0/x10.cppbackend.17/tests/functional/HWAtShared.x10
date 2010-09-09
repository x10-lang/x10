public class HWAtShared {
        @shared int three ;
        @shared final static int four = 3;
	public static void main(String[] args) {
                System.out.println("Hello Shared");
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110156
//@@X101X@@TCASE@@HWAtShared
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWAtShared.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Hello Shared
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
