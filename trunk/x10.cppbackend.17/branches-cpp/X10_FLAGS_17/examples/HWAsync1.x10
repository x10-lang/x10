public class HWAsync1 {
	static final int i = 10;

	public static void foo(final int k) {
		async (here.next()) { System.out.println(" foo " + k); }
		int b = i + k;
	}


	public static void main(String [] args) {
		final int j = 100;
		final int m = 6;
		final int n = 11;
		final int p = 12;
		async (here) {
			int MARK1 = 0;
			//int alpha = j + m;

			if (true) {
				final int beta = 3;
				foo (5);
				async (here) {
					int alpha = 5;
					System.out.println ("Hello X10 World! " + i + j + j + args + alpha + beta);
				}
			}
			else {
				int MARK2 = 0;
				async (here) { int q = m; }
			}
			async (here) { int q = n; }
			async (here) { int q = p; }
		}

	}
}
