public class HWAsync1 {
	static final int i = 10;

	public static void foo(final int k) {
		async (here.next()) { System.out.println(" foo " + k); }
		int b = i + k;
	}


	public static void main(String [] args) {
		final int j = 100;
		final int m = 6;
		final int n = 11;
		final int p = 12;
		async (here) {
			int MARK1 = 0;
			//int alpha = j + m;

			if (true) {
				final int beta = 3;
				foo (5);
				async (here) {
					int alpha = 5;
					System.out.println ("Hello X10 World! " + i + j + j + args + alpha + beta);
				}
			}
			else {
				int MARK2 = 0;
				async (here) { int q = m; }
			}
			async (here) { int q = n; }
			async (here) { int q = p; }
		}

	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110134
//@@X101X@@TCASE@@HWAsync1
//@@X101X@@VCODE@@FAIL_COMPILE
//@@X101X@@TOUT@@0 60
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
