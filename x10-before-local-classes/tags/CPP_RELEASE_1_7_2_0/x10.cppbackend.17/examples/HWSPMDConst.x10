public class HWSPMDConst {
	private final static dist(:unique) UNIQUE = dist.UNIQUE;
	public static int foo() { return 0; }
	public static void main(String[] args) {
		int i = 0;
		i = 2 + 3;
		System.out.println("Only at place 0");
		finish ateach (point p : UNIQUE) {
			System.out.println("Testing at each node: " + p[0]);
		}
		if (true) {
			System.out.println("Only at place 0");
			i = i + 1;
		}
		if (false) {
			final String[] s = args;
			i = i + 1;
			finish ateach (point p : UNIQUE) {
				System.out.println("Testing at each node.");
				for (int q = 0; q < s.length; ++q) {
					System.out.println(s[q]);
				}
				async (UNIQUE[p]) {
					int j = 0;
				}
			}
		}
		finish for (i = 0; i < 10; ++i) i++;
	}
}
