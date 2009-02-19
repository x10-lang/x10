public class HWException {
	public static void main(String[] args) {
		try {
			throw new RuntimeException("Hello World");
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
		}
	}
}


//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110523
//@@X101X@@TCASE@@HWException
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWException.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Hello World
//@@X101X@@DATA@@x10::lang::RuntimeException
//@@X101X@@DATA@@	Hello World
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
