/**
 * Test for foreach.
 *
 * @author kemal, 12/2004
 */
public class Foreach2 {

	public const N: int = 100;
	var nActivities: int = 0;

	public def run(): boolean = {
		val P0: Place = here; // save current place
		val r: Region = [0..N-1];
		val d: Dist = r->P0;

		finish
			foreach (val p: Point in d.region) {
				// Ensure each activity spawned by foreach
				// runs at P0
				// and that the hasbug array was
				// all false initially
				if (P0 != d(p) || P0 != here)
					throw new Error("Test failed.");
				atomic { nActivities++; }
			}
		return nActivities == N;
	}

	public static def main(var args: Rail[String]): void = {
		new Foreach2().run();
	}
}
