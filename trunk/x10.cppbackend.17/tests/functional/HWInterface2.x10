public class HWInterface2 {
	public static void main(String[] a) {
		System.out.println(new HWI() {
			public String hw() {
				return "Hello, World!";
			}
		}.hw());
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.111019
//@@X101X@@TCASE@@HWInterface2
//@@X101X@@VCODE@@FAIL_COMPILE
//@@X101X@@TOUT@@0 60
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
