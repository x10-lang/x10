/**
 * A simple 1-D stencil example in X10. Uses multiple asyncs in a single place.
 * @author vj 08/15/08
 * //done
 */

public class Stencil1D {
    const epsilon  = 1E-4D;
    val N: int, P: int;
    var iters: int;
    var delta: double = epsilon+1;

    def this(n: int, p: int) { this.N=n; this.P=p;}

    def step(A: Rail[Double], R: Region(1)) {
	   var diff: Double = 0;
	   for ((q) in R) {
	       val newVal = (A(q-1)+ A(q+1))/2.0 ; 
	       diff = Math.max(diff, Math.abs(newVal - A(q)));
	       A(q) = newVal;
	   }
	   return diff;
    }
    public def run() {
	   val A = Rail.makeVar[Double](N+2, (nat)=>0.0D); 
	   A(N+1) = N+1.0D;
	   val blocks = DistUtil.block(1..N, P);
	   for (; delta > epsilon; iters++) {
	      delta = 0;
	      finish foreach ((p) in 0..P-1) {
		     val myDelta  = step(A, blocks(p));
		     atomic  delta= Math.max(delta, myDelta);
	      }
	   }	
    }
    public static def main(args: Rail[String]) {
	   var n: int = args.length > 0 ? Int.parseInt(args(0)) : 1000;
	   var p: int = args.length > 1 ? Int.parseInt(args(1)) : 2;
	   x10.io.Console.ERR.println("Starting: N=" + n + " P=" + p);
	   var time: Long = -System.nanoTime();
	   val s = new Stencil1D(n, p); s.run();
	   time += System.nanoTime();
	   x10.io.Console.ERR.println("N=" + n + " P=" + p + " Iters=" + s.iters + " time=" 
			   + time/(1000*1000) + " ms");
    }
}
