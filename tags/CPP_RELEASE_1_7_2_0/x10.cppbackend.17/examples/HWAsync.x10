public class HWAsync {
	static final int i = 10;

	public static void foo(/*final*/ int k) {
		final int j = 5;
		System.out.println(" foo " + k);
//		/*async (here)*/ { System.out.println(" foo " + k); }
	}

	public static void main(String [] args) {
		final int j = 100;
		if (true) { 
			final int beta = 3;
			foo (5);
			/*async (here)*/ {int alpha = 5;
				System.out.println ("Hello X10 World! " + i + j + args + alpha + beta);}
		}
	}
}

