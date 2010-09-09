package FT;

import x10.compiler.Native;
import x10.util.Random;

public final class HPL_Dist[T] {

	public final static class Block[T] implements (Int,Int)=>T {

		val min_x:Int;
		val min_y:Int;
		val max_x:Int;
		val max_y:Int;
		private val delta:Int;
		private val offset:Int;
		val raw:Rail[T]!;
	
		public def this(I:Int, J:Int, bx:Int, by:Int, rand:Random!) {
			min_x = I * bx;
			min_y = J * by;
			max_x = (I + 1) * bx - 1;
			max_y = (J + 1) * by - 1;
			delta = by;
			offset = (I * bx + J) * by;
			val raw = Rail.make[T](bx * by);
			//for(var i:Int=0; i<bx * by; i++) raw(i) = rand.nextDouble()*10;
			this.raw = raw;
		}
	
		@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset) + (#2) * (#0)->FMGL(delta) + (#3)] = (#1)")
		public def set(val:T, i:Int, j:Int) {
			raw(i * delta + j - offset) = val;
		}
	
		@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset) + (#1) * (#0)->FMGL(delta) + (#2)]")
		public def apply(i:Int, j:Int) = raw(i * delta + j - offset);
	}

    public final static class LocalArray[T] {

        private val min_x:Int;
        private val min_y:Int;
        private val ny:Int;
        private val bx:Int;
        private val by:Int;
        private val px:Int;
        private val py:Int;
        val data:ValRail[Block[T]!];

        public def this(M:Int, N:Int, bx:Int, by:Int, px:Int, py:Int) {
            val min_x = here.id / py;
            val min_y = here.id % py;
            val nx = M / (bx * px); /* do the below for nx also.... not required for HPL, but in general */
            val ny = N  / (by * py);
            this.min_x = min_x;
            this.min_y = min_y;
            this.ny = ny;
            this.px = px;
            this.py = py;
            this.bx = bx;
            this.by = by;
            val rand = new Random(here.id*1000);
            data = ValRail.make[Block[T]!](nx * ny, (k:Int)=>new Block[T](k / ny * px + min_x, k % ny * py + min_y, bx, by, rand));
        }

        def block(I:Int, J:Int) = data((I - min_x) / px * ny + (J - min_y) / py);
        def blockOf(i:Int, j:Int) = block(i / bx, j / by);
        public def apply(i:Int, j:Int) = blockOf(i, j)(i, j);
        public def set(val:T, i:Int, j:Int) = blockOf(i, j)(i, j) = val;
    }

    public static struct ArrayView[T] implements (Int,Int)=>T {

        val min_x:Int;
        val min_y:Int;
        val max_x:Int;
        val max_y:Int;
        private val array:LocalArray[T];

	    public def this(min_x:Int, min_y:Int, max_x:Int, max_y:Int, array:LocalArray[T]) {
        	this.min_x = min_x;
        	this.min_y = min_y;
        	this.max_x = max_x;
        	this.max_y = max_y;
			this.array = array;
        }

        // FIXME: [DC] do we need the 'at' constructs here?
        def block(I:Int, J:Int) = at (array) array.block(I, J);
        def blockOf(i:Int, j:Int) = at (array) array.blockOf(i, j);
        public def apply(i:Int, j:Int) = at (array) array(i, j);
        def empty() = array == null;

        def flat_region_min_x() = at(array) array.block(min_x, min_y).min_x;
        def flat_region_min_y() = at(array) array.block(min_x, min_y).min_y;
        def flat_region_max_x() = at(array) array.block(max_x, max_y).max_x;
        def flat_region_max_y() = at(array) array.block(max_x, max_y).max_y;   
    }

    private global val bx:Int;
    private global val by:Int;
    private global val px:Int;
    private global val py:Int; 
    private global val A:PlaceLocalHandle[LocalArray[T]];

    def this(M:Int, N:Int, bx:Int, by:Int, px:Int, py:Int, unique:Dist) {
        this.bx = bx;
        this.by = by;
        this.px = px;
        this.py = py;
        A = PlaceLocalHandle.make[LocalArray[T]](unique, ()=>new LocalArray[T](M, N, bx, by, px ,py));
    }

    def placeOf(i:Int, j:Int) = placeOfBlock(i / bx, j / by);
    def hasBlock(I:Int, J:Int) = placeOfBlock(I, J) == here.id;
    def hasCol(J:Int) = (J - here.id) % py == 0;
    def hasRow(I:Int) = here.id / py == I % px;
    def placeOfBlock(I:Int, J:Int) = I % px * py + J % py;

    //a.block(.., ..) | here, where the region is full region
    global def blockFullHere() = A();

    // a.block(.., ..) | here
    /* def block_restrict_here(min_x:Int, max_x:Int, min_y:Int, max_y:Int) {
    	val a = A();
        val view_min_x = min_x + (px + a.min_x - (min_x % px)) % px;
        val view_min_y = min_y + (py + a.min_y - (min_y % py)) % py;
        val view_max_x = max_x - (px + (max_x % px) - a.min_x) % px;
        val view_max_y = max_y - (py + (max_y % py) - a.min_y) % py;
        val b = (view_max_x < view_min_x || view_max_y < view_min_y) ? null : a;
        return ArrayView[T](view_min_x, view_min_y, view_max_x, view_max_y, b);
    }a*/

    public static safe def max(a:Int, b:Int) = a<b ? b : a;
	public static safe def min(a:Int, b:Int) = a<b ? a : b;

    // a(.., ..) | here
    /* def array_restrict_here(row:Int, min_y:Int, max_y:Int, values:Rail[T]!) {
    	val brow = row / bx;
        val view = block_restrict_here(brow, brow, min_y / by, max_y / by);
        var n:Int = 0;
        for (var J:Int = view.min_y; J<=view.max_y; J+=py) {
            val b = view.block(view.min_x, J);
            for (var j:Int = max(min_y, b.min_y); j<=min(max_y, b.max_y); j++) {
                values(n++) = b(row, j);
            }
        }
        return n;
    }

    def array_restrict_here_update(row:Int, min_y:Int, max_y:Int, values:Rail[T]!) {
    	val brow = row / bx;
        val view = block_restrict_here(brow, brow, min_y / by, max_y / by);
        var n:Int = 0;
        for (var J:Int = view.min_y; J<=view.max_y; J+=py) {
            val b = view.block(view.min_x, J);
            for (var j:Int = max(min_y, b.min_y); j<=min(max_y, b.max_y); j++) {
                b(row, j) = values(n++);
            }
        }
    }

    def array_restrict_here_swap(row:Int, min_y:Int, max_y:Int, values:Rail[T]!) {
    	val brow = row / bx;
        val view = block_restrict_here(brow, brow, min_y / by, max_y / by);
        var n:Int = 0;
        for (var J:Int = view.min_y; J<=view.max_y; J+=py) {
            val b = view.block(view.min_x, J);
            for (var j:Int = max(min_y, b.min_y); j<=min(max_y, b.max_y); j++) {
                val v = b(row, j);
                b(row, j) = values(n);
                values(n++) = v;
            }
        }
        return n;
    } */
}
