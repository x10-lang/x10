/**
 * A representation of the UTS Binomial tree benchmark.
 */
import x10.util.Stack;
public final class Binomial(b0:Int, q:Double, m:Int) implements TaskFrame[UTS.SHA1Rand]{
	static type SHA1Rand=UTS.SHA1Rand;
	public static def usageLine(b0:Int, r:Int, mf:Int, seq:Int, w:Int, nu:Int, q:Double, l:Int, z:Int) {
		Console.OUT.println("b0=" + b0 +
				"   r=" + r +
				"   m=" + mf +
				"   s=" + seq +
				"   w=" + w +
				"   n=" + nu +
				"   q=" + q +
                "   l=" + l + 
                "   z=" + z +
                (l==3 ?" base=" + NetworkGenerator.findW(Place.MAX_PLACES, z) : ""));
	}
	public def runTask(s:SHA1Rand, stack:Stack[SHA1Rand]!) {
		pushN(s, s() < q ? m : 0, stack);
	}
	public def runRootTask(s:SHA1Rand, stack:Stack[SHA1Rand]!) {
		pushN(s, b0, stack);
	}
	private def pushN(s:SHA1Rand, N:Int, stack:Stack[SHA1Rand]!) {
		for (var i:Int=0; i<N; ++i) 
			stack.push(SHA1Rand(s, i));
	}
	
}
