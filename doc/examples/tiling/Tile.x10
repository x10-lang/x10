import x10.compiler.Inline;
import x10.compiler.Foreach;
import x10.lang.Math;

public struct Tile {

    public static struct DiamondIterator(L:Long , U:Long, T:Long, tau:Long) {
    }
    /**
     * Diamond tiling following the implementation of "Parameterized
     * Diamond Tiling for Stencil Computations with Chapel parallel
     * iterators" ICS 2015.
     */
    public static class Diamond {
	public static @Inline operator for (iterator: DiamondIterator, body:(read:Long,write:Long,i:Long,j:Long)=>void) {
	    val L:Long = iterator.L;
	    val U:Long = iterator.U;
	    val T:Long = iterator.T;
	    val tau:Long = iterator.tau;
	    // Loop over tile wavefronts.
	    // for (kt in ceild(3,tau) .. floord(3*T,tau)) {   // XXXXXXXXXX Modif
	    // for (kt in (-2) .. floord(3*T,tau)) {           // XXXXXXXXXX Modif
	    for (kt in (ceild(3,tau)-3) .. floord(3*T,tau)) {  // XXXXXXXXXX Modif

		// The next two loops iterate within a tile wavefront.  Assumes a square iteration space.
		val k1_lb: Long = floord(3*L+2+(kt-2)*tau, tau*3);
		val k1_ub: Long = floord(3*U+(kt+2)*tau-2, tau*3);
		val k2_lb: Long = floord((2*kt-2)*tau-3*U+2, tau*3);
		val k2_ub: Long = floord((2+2*kt)*tau-3*L-2, tau*3);

		// Loops over tile coordinates within a parallel wavefront of tiles.
		Foreach.Basic.for (k1:Long in k1_lb .. k1_ub) {
		    for (x in k2_lb .. k2_ub) {
			val k2 = x-k1;

			// Loop over time within a tile.
			for (t in Math.max(1,floord(kt*tau,3)) .. Math.min(T,floord((3+kt)*tau-3,3))) {
			    val write = t & 1; // equivalent to t mod 2
			    val read = 1 - write;

			    // Loops over the spatial dimensions within each tile.
			    for (i in Math.max(L,Math.max((kt-k1-k2)*tau-t, 2*t-(2+k1+k2)*tau+2))
				     .. Math.min(U,Math.min((1+kt-k1-k2)*tau-t-1, 2*t-(k1+k2)*tau))) {
				for (j in Math.max(L,Math.max(tau*k1-t,t-i-(1+k2)*tau+1))
					 .. Math.min(U,Math.min((1+k1)*tau-t-1,t-i-k2*tau))) {
				    // atomic { Console.OUT.print(k1+": ");
				    body (read, write, i, j);
				    // }
				}
			    }
			}
		    }
		}
	    }
	}
    }


    /* Utility functions */
    // private static def floord(a:Long, b:Long) {
    // 	if (a % b == 0) {
    // 	    return a / b;
    // 	}
    // 	if (a * b > 0) {
    // 	    return a / b;
    // 	}
    // 	return a / b - 1;
    // }
    private static def floord( x: Long , y: Long ): Long {
	// assert( y > 0 && ((x % y >= 0) ? x % y : (x%y) +y) >= 0 && ((x%y>=0) ? x%y : (x%y) +y) <= y && x==(y*((x%y>=0) ? x/y : ((x/y) -1)) + ((x%y>=0) ? x%y : (x%y) +y)) );
	return (x%y>=0) ? x/y : (x/y)-1;
    }


    private static def ceild(a:Long, b:Long) {
	return 0 - floord(-a, b);
    }


    // =============================================
    /* Test */
    public static def main(Rail[String]) {
	Diamond.for (read:Long,write:Long,x:Long,y:Long in new DiamondIterator(0, 9, 9, 3)) {
	    // Console.OUT.println("read = "+read+", write = "+write+", i = "+i+", j = "+j);
	    Console.OUT.println("A["+write+", "+x+", "+y+"] = (A["+read+", "+(x-1)+", "+y+"] + A["+read+", "+x+", "+(y-1)+"] + A["+read+", "+x+", "+y+"] + A["+read+", "+x+", "+(y+1)+"] + A["+read+", "+(x+1)+", "+y+"]) / 5");
	}
    }

}
