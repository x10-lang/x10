import x10.util.GrowableRail;
import x10.array.DenseIterationSpace_2;
import x10.compiler.Inline;

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
