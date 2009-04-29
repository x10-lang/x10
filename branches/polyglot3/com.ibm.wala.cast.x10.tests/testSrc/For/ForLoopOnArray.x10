/**
 * Test for for loop on an array.
 *
 * @author vj
 */
public class ForLoopOnArray {

	public const N: int = 3;

	public def run(): boolean = {
		var a: Array[double] = Array.make[double]([0..10], ((i): Point): double => i as double);

		for (val (i): Point in a) {
			if (a(i) != i) return false;
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ForLoopOnArray().run();
	}
}
