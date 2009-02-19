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
