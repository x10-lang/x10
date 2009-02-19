public class HWLoop {
	static class C {
		public int bar() { return 0; }
	}
	public static boolean foo(int a, boolean b) {
		class B {
			public int bar() { return 1; }
		}
		int c = 0;
		c += a;
		return b;
	}
	public static void main(String[] args) {
		System.out.println("Hello World");
		for (int i = 0; i < args.length; i++) {
			System.out.println("Arg "+i+": "+args[i]);
search:
			for (int j = 0; j < 10; ) {
				j++;
				break search;
			}
newwhile:
			while (i >= 0) {
				break newwhile;
			}
			foo(2 + 3, true || false);
			foo(2 << 3, true && false);
		}
		return;
	}
}

