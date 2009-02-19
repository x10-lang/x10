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
