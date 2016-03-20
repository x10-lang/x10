import x10.util.GrowableRail;
import x10.array.DenseIterationSpace_2;
import x10.compiler.Inline;
import x10.util.foreach.*;

class Chapel {

    /** for syntax */

    public static @Inline operator for(iterator:((Long)=>void)=>void, body: (Long)=>void) {
    	iterator(body);
    }

    public static @Inline operator for[T](iterator:((T)=>void)=>void, body: (T)=>void) {
	iterator(body);
    }

    public static @Inline operator for[T1,T2](iterator:((T1,T2)=>void)=>void, body: (T1,T2)=>void) {
	iterator(body);
    }

    public static @Inline operator for[T1,T2,T3](iterator:((T1,T2,T3)=>void)=>void, body: (T1,T2,T3)=>void) {
        iterator(body);
    }

    public static @Inline operator for[T1,T2,T3,T4](iterator:((T1,T2,T3,T4)=>void)=>void, body: (T1,T2,T3,T4)=>void) {
        iterator(body);
    }

    /** Iterators */
    public static class Iter {
	public static @Inline def seq(range:LongRange){
	    return (yield:(Long)=>void) => {
		for (i in range) {
		    yield(i);
		}
	    };
	}

	public static @Inline def fib(n:Long) {
	    return (yield:(Long)=>void) => {
		var current:Long = 0;
		var next:Long = 1;
		for (i in 1..n) {
		    yield(current);
		    current += next;
		    val tmp = next;
		    next = current;
		    current = tmp;
		}
	    };
	}

	public static @Inline def triangle(n:Long) {
	    return (yield:(Long,Long)=>void) => {
		for (i in 1 .. n) {
		    for (j in (i+1) .. n) {
			yield(i,j);
		    }
		}
	    };
	}

	public static @Inline def nest[Elem1,Elem2](iter1:((Elem1)=>void)=>void,
						iter2:((Elem2)=>void)=>void) {
	    return (yield:(Elem1,Elem2)=>void) => {
		iter1((x:Elem1)=> { iter2((y:Elem2) => { yield(x,y); }); });
	    };
	}


	/**
	 * Tiled Row-Major Order:
	 * iterate over domain D using tilesize x tilesize tiles in row-major order.
	 * Limitation: tilesize must be a multiple of the dimensions.
	 */
	public static @Inline def tiledRMO(D: DenseIterationSpace_2, tilesize: Long) {
	    return (yield:(Long,Long)=>void) => {
		for (var base0: Long = D.min0; base0+tilesize-1 <= D.max0; base0 = base0+tilesize) {
		    for (var base1: Long = D.min1; base1+tilesize-1 <= D.max1; base1 = base1+tilesize) {
			for (i in base0 .. (base0+tilesize-1)) {
			    for (j in base1 .. (base1+tilesize-1)) {
				yield(i,j);
			    }
			}
		    }
		}
	    };
	}

	// XXX To improve
	// => use parallel execution of iter1 and iter2 in lock step
	// the current version do not support iteration domain of different size
	public static @Inline def zip[Elem1,Elem2](iter1:((Elem1)=>void)=>void,
					       iter2:((Elem2)=>void)=>void) {
	    return (yield:(Elem1,Elem2)=>void) => {
		val save1 = new GrowableRail[Elem1]();
		iter1((x:Elem1) => { save1.add(x); });
		val i = new Cell[Long](0);
		iter2((y:Elem2) => {
			yield(save1(i()), y);
			i() = i() + 1;
		    });
	    };
	}

        /**
         * Diamond tiling following the implementation of "Parameterized
         * Diamond Tiling for Stencil Computations with Chapel parallel
         * iterators" ICS 2015.
         */
        public static @Inline def diamond (L:Long , U:Long, T:Long, tau:Long) {
            return (yield:(read:Long,write:Long,i:Long,j:Long)=>void) => {
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
                    Basic.for (k1:Long in k1_lb .. k1_ub) {
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
                                        yield (read, write, i, j);
                                    }
                                }
                            }
                        }
                    }
                }
            };
        }
        private static def floord( x: Long , y: Long ): Long {
            return (x%y>=0) ? x/y : (x/y)-1;
        }
        private static def ceild(a:Long, b:Long) {
            return 0 - floord(-a, b);
        }

    }

    /* Test */
    public static def main(Rail[String]) {
	Console.OUT.println("*** Iter.seq ***");
	Chapel.for(i:Long in Iter.seq(1..10)) {
	    Console.OUT.println(i);
	}

	Console.OUT.println("*** Iter.fib ***");
	Chapel.for(i:Long in Iter.fib(10)) {
	    Console.OUT.println(i);
	}

	Console.OUT.println("*** Iter.triangle ***");
	Chapel.for(i:Long, j:Long in Iter.triangle(5)) {
	    Console.OUT.println("("+i+", "+j+")");
	}

	Console.OUT.println("*** Iter.nest ***");
	Chapel.for(i:Long, j:Long in Iter.nest(Iter.seq(1..3), Iter.seq(1..2))) {
	    Console.OUT.println("("+i+", "+j+")");
	}

	Console.OUT.println("*** Iter.tiledRMO ***");
	Chapel.for(i:Long, j:Long in Iter.tiledRMO(1..8 * 1..6, 2)) {
	    Console.OUT.println("("+i+", "+j+")");
	}

	Console.OUT.println("*** Iter.zip ***");
	Chapel.for(f:Long, i:Long in Iter.zip(Iter.fib(10), Iter.seq(1..10))) {
	    Console.OUT.println("fib("+i+") = "+f);
	}

    }

}
