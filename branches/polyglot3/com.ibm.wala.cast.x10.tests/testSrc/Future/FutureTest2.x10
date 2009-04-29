/**
 * Minimal test for future.
 */
public class FutureTest2 {

	public def run(): boolean = {
		val ret = future (here) { this.m() };
		return ret();
	}

	def m() = true;

	public static def main(var args: Rail[String]): void = {
		new FutureTest2().run();
	}
}
