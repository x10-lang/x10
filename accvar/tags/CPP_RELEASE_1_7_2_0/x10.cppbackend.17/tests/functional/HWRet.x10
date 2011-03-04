public class HWRet {
	boolean x = false;
	final void foo() {
		HWRet o = new HWRet();
		if (o.x) {
			System.out.println("x was true");
			return;
		}
		System.out.println("x was false");
		o.x = true;
		return;
	}
	final void bar() {
		new HWRet().foo();
	}
	final void fubar() { return; }
	public static void main(String[] args) {
		new HWRet().bar();
		new HWRet().fubar();
		System.out.println("The end.");
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111225
//@@X101X@@TCASE@@HWRet
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWRet.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@x was false
//@@X101X@@DATA@@The end.
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
