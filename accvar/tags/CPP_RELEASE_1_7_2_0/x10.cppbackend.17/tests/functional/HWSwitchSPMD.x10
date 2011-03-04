public class HWSwitchSPMD {
	private final static dist(:unique) UNIQUE = dist.UNIQUE;
	public static void main(String[] args) {
		switch (args.length) {
			case 2:
				System.out.println("Two arguments");
				break;
			case 1:
				System.out.println("One argument");
				finish ateach (point p : UNIQUE) {
				}
				break;
			default:
				System.out.println("Stop flooding, man!");
				break;
		}
		System.out.println((String)"Hello World");
	}
}


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111524
//@@X101X@@TCASE@@HWSwitchSPMD
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWSwitchSPMD.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Stop flooding, man!
//@@X101X@@DATA@@Hello World
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
