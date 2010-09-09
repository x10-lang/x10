public class HWDoWhile {
	static final String HW = "Hello, World!";
	static final region R = [0:12];
	static final char[.] chars = new char[R] (point p[i]) { return HW.charAt(i); };
	public static void main(String[] a) {
                int i = 3;
		System.out.println(i);
		do{
                        System.out.println(i);
                        i--;
		} while (i >= 0);
		System.out.println(i);
		while (i < 3){
                        System.out.println(i);
                        i++;
		} ;
		System.out.println(i);

	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.110503
//@@X101X@@TCASE@@HWDoWhile
//@@X101X@@VCODE@@FAIL_BUILD
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@HWDoWhile.cc
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
