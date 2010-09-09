public class HWAsync {
	static final int i = 10;

	public static void foo(/*final*/ int k) {
		final int j = 5;
		System.out.println(" foo " + k);
//		/*async (here)*/ { System.out.println(" foo " + k); }
	}

	public static void main(String [] args) {
		final int j = 100;
		if (true) { 
			final int beta = 3;
			foo (5);
			/*async (here)*/ {int alpha = 5;
				System.out.println ("Hello X10 World! " + i + j + args + alpha + beta);}
		}
	}
}


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110100
//@@X101X@@TCASE@@HWAsync
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWAsync.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@ foo 5
//@@X101X@@DATA@@Hello X10 World! 1010053
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
