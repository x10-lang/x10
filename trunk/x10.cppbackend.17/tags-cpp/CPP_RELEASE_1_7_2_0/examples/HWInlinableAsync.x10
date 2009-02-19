public class HWInlinableAsync {
	//static final int i = 10;

	public static void foo(/*final*/ int k) {
		final int j = 5;
		System.out.println(" foo " + k);
//		/*async (here)*/ { System.out.println(" foo " + k); }
	}

	public static void main(String [] args) {
		final int j = 100;
		final dist(:unique) ALLPLACES= dist.UNIQUE;
		finish /* async */{
			clock clk = clock.factory.clock();
			ateach (point [PID]: ALLPLACES) clocked(clk) {
				if (true) {
					final int beta = 3;
					foo (5);
					async (here) clocked(clk) {
						int alpha = 5;
						//throw new x10.lang.Exception();
						alpha = j + j + alpha + beta;
					}
				}
			}
		}
	}
}

