public class HWClockedAtEach {
	private final static dist(:unique) UNIQUE = dist.UNIQUE;
	public static int foo() { return 0; }
	public static void main(String[] args) {
                finish async {
                final clock clk=clock.factory.clock();
                        ateach (point p : UNIQUE) clocked(clk) {
                                System.out.println("Testing at each node: " + p[0]);
                        }
                }
	}
}
