public class Fib2 {
	public static int fib(final int n) {
		if (n < 2) {
			return 1;
		}
		future<int> f=future { fib(n-1)}, g=future { fib(n-2)};
		return f.force()+g.force();
	}
	public static void main(String[] a) {
		int n = java.lang.Integer.parseInt(a[0]);
		long s = java.lang.System.currentTimeMillis();
		int nn =fib(n);
		long t = java.lang.System.currentTimeMillis();
		System.out.println("fib(" + n + ")=" + nn + " (" + (t-s) + " msec).");
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.105745
//@@X101X@@TCASE@@Fib2
//@@X101X@@VCODE@@FAIL_COMPILE
//@@X101X@@TOUT@@0 60
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
