package rc7;

import x10.compiler.Native;
import x10.runtime.PlaceLocalHandle;
import x10.runtime.PlaceLocalStorage;
import x10.util.Random;

public final class HPL_Dist implements (Int,Int)=>Double {

	public final static class Block implements (Int,Int)=>Double {

		val min_x:Int;
		val min_y:Int;
		val max_x:Int;
		val max_y:Int;
		private val delta:Int;
		private val offset:Int;
		val raw:Rail[Double]!;
	
		public def this(I:Int, J:Int, bx:Int, by:Int, rand:Random!) {
			min_x = I * bx;
			min_y = J * by;
			max_x = (I + 1) * bx - 1;
			max_y = (J + 1) * by - 1;
			delta = by;
			offset = (I * bx + J) * by;
			val raw = Rail.makeVar[Double](bx * by);
			for(var i:Int=0; i<bx * by; i++) raw(i) = rand.nextDouble()*10;
			this.raw = raw;
		}
	
		@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset) + (#2) * (#0)->FMGL(delta) + (#3)] = (#1)")
		public def set(val:Double, i:Int, j:Int) {
			raw(i * delta + j - offset) = val;
		}
	
		@Native("c++", "(*(#0)->FMGL(raw))[-(#0)->FMGL(offset) + (#1) * (#0)->FMGL(delta) + (#2)]")
		public def apply(i:Int, j:Int) = raw(i * delta + j - offset);
	}

    public static struct ArrayView implements (Int,Int)=>Double {

        val min_x:Int;
        val min_y:Int;
        val max_x:Int;
        val max_y:Int;
        private val array:HPL_Dist{self.location==here};

	    public def this(min_x:Int, min_y:Int, max_x:Int, max_y:Int, array:HPL_Dist{self.location==here}) {
        	this.min_x = min_x;
        	this.min_y = min_y;
        	this.max_x = max_x;
        	this.max_y = max_y;
			this.array = array;
        }

        def block(I:Int, J:Int) = array.block(I, J);
        def blockOf(i:Int, j:Int) = array.blockOf(i, j);
        public def apply(i:Int, j:Int) = array(i, j);
        def empty() = array == null;
    }

    private val bx:Int;
    private val by:Int;
    private val px:Int;
    private val py:Int; 
    private val min_x:Int;
    private val min_y:Int;
    private val ny:Int;
    val data:ValRail[Block!];

    public def this(M:Int, N:Int, bx:Int, by:Int, px:Int, py:Int) {
        this.bx = bx;
        this.by = by;
        this.px = px;
        this.py = py;
        val min_x = here.id / py;
        val min_y = here.id % py;
        val nx = M / (bx * px);
        val ny = (N - by) / (by * py) + (min_y == 0 ? 1 : 0);
        this.min_x = min_x;
        this.min_y = min_y;
        this.ny = ny;

        val rand = new Random(here.id*1000);
        data = ValRail.make[Block!](nx * ny, (k:Int)=>new Block(k / ny * px + min_x, k % ny * py + min_y, bx, by, rand));
    }

    public def apply(i:Int, j:Int) = blockOf(i, j)(i, j);
    public def set(val:Double, i:Int, j:Int) = blockOf(i, j)(i, j) = val;

    def block(I:Int, J:Int) = data((I - min_x) / px * ny + (J - min_y) / py);
    def blockOf(i:Int, j:Int) = block(i / bx, j / by);
    def placeOf(i:Int, j:Int) = placeOfBlock(i / bx, j / by);
    def hasBlock(I:Int, J:Int) = placeOfBlock(I, J) == here.id;
    def hasCol(J:Int) = (J - here.id) % py == 0;
    def hasRow(I:Int) = here.id / py == I % px;
    def placeOfBlock(I:Int, J:Int) = I % px * py + J % py;

    def blocks(min_x:Int, max_x:Int, min_y:Int, max_y:Int) {
    	val a = this;
        val view_min_x = min_x + (px + a.min_x - (min_x % px)) % px;
        val view_min_y = min_y + (py + a.min_y - (min_y % py)) % py;
        val view_max_x = max_x - (px + (max_x % px) - a.min_x) % px;
        val view_max_y = max_y - (py + (max_y % py) - a.min_y) % py;
        val b = (view_max_x < view_min_x || view_max_y < view_min_y) ? null : a;
        return ArrayView(view_min_x, view_min_y, view_max_x, view_max_y, b);
    }

    def getRow(row:Int, min_y:Int, max_y:Int, values:Rail[Double]!) {
    	val brow = row / bx;
        val view = blocks(brow, brow, min_y / by, max_y / by);
        var n:Int = 0;
        for (var J:Int = view.min_y; J<=view.max_y; J+=py) {
            val b = view.block(view.min_x, J);
            for (var j:Int = Math.max(min_y, b.min_y); j<=Math.min(max_y, b.max_y); j++) {
                values(n++) = b(row, j);
            }
        }
        return n;
    }

    def setRow(row:Int, min_y:Int, max_y:Int, values:Rail[Double]!) {
    	val brow = row / bx;
        val view = blocks(brow, brow, min_y / by, max_y / by);
        var n:Int = 0;
        for (var J:Int = view.min_y; J<=view.max_y; J+=py) {
            val b = view.block(view.min_x, J);
            for (var j:Int = Math.max(min_y, b.min_y); j<=Math.min(max_y, b.max_y); j++) {
                b(row, j) = values(n++);
            }
        }
    }

    def swapRow(row:Int, min_y:Int, max_y:Int, values:Rail[Double]!) {
    	val brow = row / bx;
        val view = blocks(brow, brow, min_y / by, max_y / by);
        var n:Int = 0;
        for (var J:Int = view.min_y; J<=view.max_y; J+=py) {
            val b = view.block(view.min_x, J);
            for (var j:Int = Math.max(min_y, b.min_y); j<=Math.min(max_y, b.max_y); j++) {
                val v = b(row, j);
                b(row, j) = values(n);
                values(n++) = v;
            }
        }
        return n;
    }

	public static def make(M:Int, N:Int, bx:Int, by:Int, px:Int, py:Int, unique:Dist) {
		return PlaceLocalStorage.createDistributedObject[HPL_Dist](unique, ()=>new HPL_Dist(M, N, bx, by, px ,py));
	}
}
