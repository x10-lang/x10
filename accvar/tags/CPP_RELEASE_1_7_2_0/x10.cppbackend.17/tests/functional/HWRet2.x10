public class HWRet2 {
	private final static dist(:unique) UNIQUE=dist.UNIQUE;
	boolean x = false;
	static void fubar() { return; }
	static void foo() {
		final HWRet2[.] a = new HWRet2[UNIQUE] (point [p]) { return new HWRet2(); };
		if (a[here.id].x) {
			System.out.println("x was true");
			return;
		}
		System.out.println("x was false");
		a[here.id].x = true;
		return;
	}
	static void bar() {
		foo();
		return;
	}
	public static void main(String[] args) {
		bar();
		System.out.println("The end.");
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111254
//@@X101X@@TCASE@@HWRet2
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWRet2.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@x was false
//@@X101X@@DATA@@The end.
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
