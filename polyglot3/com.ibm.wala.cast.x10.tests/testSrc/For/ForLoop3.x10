/**
 * Test for for loop with x10 for (point p: 1:N) syntax.
 *
 * @author kemal, 1/2005
 */
public class ForLoop3 {

	public const N: int = 100;

	public def run(): boolean = {
		//Ensure iterator works in lexicographic order
		var n: int = 0;
		var prev: int = -1;
		for (val p: Point in [0..N-1]->here) {
			n += p(0);
			if (prev+1 != p(0)) return false;
			prev = p(0);
		}
		if (n != N*(N-1)/2) return false;

		// now iterate over a region
		n = 0;
		prev = -1;
		for (val p: Point in [0..N-1]->here) {
			n += p(0);
			if (prev+1 != p(0)) return false;
			prev = p(0);
		}
		if (n != N*(N-1)/2) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ForLoop3().run();
	}
}
