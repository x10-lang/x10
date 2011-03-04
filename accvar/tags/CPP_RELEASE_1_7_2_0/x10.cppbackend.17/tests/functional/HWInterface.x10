public class HWInterface {
	static class HWIImp implements HWI {
		public String hw() { return "Hello, World!"; }
	}
	public static void main(String[] a) {
		System.out.println(new HWIImp().hw());
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110959
//@@X101X@@TCASE@@HWInterface
//@@X101X@@VCODE@@FAIL_COMPILE
//@@X101X@@TOUT@@0 60
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
