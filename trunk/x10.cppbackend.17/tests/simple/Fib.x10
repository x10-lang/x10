public class Fib {

	int x;

	public void run(final int n) {
		if (n < 2) {
			x++;
			return;
		}
		async fib(n-1);
		async fib(n-2);
	}
	public int fib(int n) {
		finish run(n);
		return x;
	}

	public static void main(String[] a) {
		int n = java.lang.Integer.parseInt(a[0]);
		long s = java.lang.System.currentTimeMillis();
		int nn =new Fib().fib(n);
		long t = java.lang.System.currentTimeMillis();
		System.out.println("fib(" + n + ")=" + nn + " (" + (t-s) + " msec).");
	}
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.105704
//@@X101X@@TCASE@@Fib
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@Fib.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@fib(0)=1 (0 msec).
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
