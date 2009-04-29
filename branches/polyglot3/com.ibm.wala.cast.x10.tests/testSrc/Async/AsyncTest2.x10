/**
 * Testing complex async bodies.
 *
 * @author Kemal Ebcioglu 4/2005
 */
public class AsyncTest2 {

	public def run(): boolean = {
		val NP: int = Place.MAX_PLACES;
		val A: Array[int] = Array.make[int](Dist.makeUnique());
		finish
			for (val (k): Point in 0..NP-1)
                                async (A.dist(k))
					ateach (val (i): Point{rank==A.rank} in A.dist)
                                                atomic A(i) += i;
		finish ateach (val (i): Point{rank==A.rank} in A.dist) { chk(A(i) == i*NP); }

		return true;
	}
	
	public static def chk(b: boolean, s: String): void = {
        if (!b) throw new Error(s);
    }

	public static def main(var args: Rail[String]): void = {
		new AsyncTest2().run();
	}
}
