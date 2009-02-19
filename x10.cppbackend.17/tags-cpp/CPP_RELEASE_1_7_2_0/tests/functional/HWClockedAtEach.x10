public class HWClockedAtEach {
	private final static dist(:unique) UNIQUE = dist.UNIQUE;
	public static int foo() { return 0; }
	public static void main(String[] args) {
                finish async {
                final clock clk=clock.factory.clock();
                        ateach (point p : UNIQUE) clocked(clk) {
                                System.out.println("Testing at each node: " + p[0]);
                        }
                }
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110324
//@@X101X@@TCASE@@HWClockedAtEach
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWClockedAtEach.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Testing at each node: 0
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
