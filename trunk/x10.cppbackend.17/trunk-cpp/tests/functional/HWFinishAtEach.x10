public class HWFinishAtEach {
	private final static dist(:unique) UNIQUE = dist.UNIQUE;
	public static int foo() { return 0; }
	public static void main(String[] args) {
		int i = 0;
		i = 2 + 3;
		System.out.println("Only at place 0");
		finish ateach (point p : UNIQUE) {
			System.out.println("Testing at each node: " + p[0]);
		}
		if (true) {
			System.out.println("Only at place 0");
			i = i + 1;
		}
		if (false) {
			final String[] s = args;
			i = i + 1;
			finish ateach (point p : UNIQUE) {
				System.out.println("Testing at each node.");
				for (int q = 0; q < s.length; ++q) {
					System.out.println(s[q]);
				}
				async (UNIQUE[p]) {
					int j = 0;
				}
			}
		}
		finish for (i = 0; i < 10; ++i) i++;
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110705
//@@X101X@@TCASE@@HWFinishAtEach
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWFinishAtEach.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Only at place 0
//@@X101X@@DATA@@Testing at each node: 0
//@@X101X@@DATA@@Only at place 0
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
