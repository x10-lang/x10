public class HWSwitch {
	public static void main(String[] args) {
                switch (args.length) {

                        case 1: System.out.println ("One argument");
                                break;
                        case 2: System.out.println ("Two arguments");
                                break;
                        default: System.out.println ("Stop flooding man!");
                                break;
                }
		System.out.println((String)"Hello World");
	}
}


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111500
//@@X101X@@TCASE@@HWSwitch
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWSwitch.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Stop flooding man!
//@@X101X@@DATA@@Hello World
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
