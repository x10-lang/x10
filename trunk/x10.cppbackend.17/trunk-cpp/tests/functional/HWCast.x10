public class HWCast {
	public static class S {
		public S() { }
		public void id() {
			System.out.println("S");
		}
	}
	public static class C extends S {
		public C() { }
		public void id() {
			System.out.println("C");
		}
	}
	public static void foo(S y) {
		C x = (C) y;
		x.id();
	}
	public static void bar(C x) {
		S y = x;
		y = (S) x;
		y.id();
	}
	public static void main(String[] args) {
		foo(new C());
		bar(new C());
		System.out.println(new C() instanceof S);
		System.out.println(new S() instanceof C);
		C c = new C();
		System.out.println(c instanceof S);
		return;
	}
}


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110234
//@@X101X@@TCASE@@HWCast
//@@X101X@@VCODE@@FAIL_RUN
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWCast.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
