package rc7;

import x10.compiler.*;
import x10.util.Team;

@NativeCPPInclude("essl_natives.h")
@NativeCPPCompilationUnit("essl_natives.cc")
class LU {

    @NativeCPPExtern
        native static def blockTriSolve(me:Array[Double], diag:Array[Double], B:Int):void;

    @NativeCPPExtern
        native static def blockBackSolve(me:Array[Double], diag:Array[Double], B:Int):void;

    @NativeCPPExtern
        native static def blockMulSub(me:Array[Double], left:Array[Double], upper:Array[Double], B:Int):void;

    @NativeCPPExtern
        native static def blockMulSubRow(me:Array[Double], diag:Array[Double], B:Int, j:Int, cond:boolean):void;
    
    /*
    static def runAt(id:Int, c:()=>void) {
         x10.lang.Runtime.runClosureAt(id, c);
         x10.lang.Runtime.dealloc(c);
    }
    */

    static unique = Dist.makeUnique();

    val M:Int;
    val N:Int;
    val B:Int;
    val MB:Int;
    val NB:Int;
    val px:Int;
    val py:Int;
    val A:PlaceLocalHandle[BlockedArray];
    val A_here:BlockedArray;
    val colRole:Int;
    val rowRole:Int;
    val col:Team;
    val row:Team;
    var ready:Boolean;
    val pivot:Array[Int](1);
    val rowForBroadcast:Array[Double](1){rail,self!=null};
    val rowBuffer:Array[Double]{self!=null};
    val colBuffer:Array[Double]{self!=null};
    val colBuffers:Array[Array[Double]{self!=null}](1){rail,self!=null};
    val rowBuffers:Array[Array[Double]{self!=null}](1){rail,self!=null};
    val buffer:Array[Double](1){rail,self!=null};
    val buffers:PlaceLocalHandle[Array[Double](1){rail,self!=null}];

    def computeRowSum() {
        val sum = new Array[Double](B);

        for (var I:Int = 0; I <= MB; ++I) if (A_here.hasRow(I)) {
            val IB = I * B;
            for (var k:Int = 0; k < B; ++k) sum(k) = 0.0;
            for (var J:Int = 0; J <= MB; ++J) if (A_here.hasCol(J)) {
                val JB = J * B;
                val b = A_here.block(I, J);
                for (var i:Int = 0; i < B; ++i) {
                    for (var j:Int = 0; j < B; ++j) {
                        sum(i) += b(IB + i, JB + j);
                    }
                }
            }
            for (var k:Int = 0; k < B; ++k) { 
                // [DC] This probably ought to be optimised to sum the whole array, instead of each element with its own collective op
                sum(k) = row.allreduce(rowRole,sum(k),Team.ADD);
                if (A_here.hasCol(NB)) A_here(IB + k, M) = sum(k);
            }
        }
    }

    def computeLocalMax(J:Int, LUCol:Int) {
        var max:Double = 0;
        var id:Int = -1;

        for (var I:Int = J; I <= MB; ++I) if (A_here.hasRow(I)) {
            val IB = I * B;
            val b = A_here.block(I, LUCol / B);
            for (var i:Int = Math.max(IB, LUCol); i < IB + B; i++) {
                if (Math.abs(b(i, LUCol)) >= Math.abs(max) || id == -1) {
                    max = b(i, LUCol);
                    id = i;
                }
            }
        }
        return col.indexOfMax(colRole, Math.abs(max), id);
    }

    def exchange(row1:Int, row2:Int, min:Int, max:Int, dest:Int) {
        val source = here; 
        ready = false;
        val size = A_here.getRow(row1, min, max, buffer);        
        val remoteBuffer = new RemoteArray(buffer);
        val _buffers = buffers; // this is done so that we don't serialize the object that contains buffers
        val _A = A;
        
        @Pragma(Pragma.FINISH_ASYNC_AND_BACK) finish{
        	async at (Place(dest)){
        	    @Pragma(Pragma.FINISH_ASYNC) finish{
        			Array.asyncCopy[Double](remoteBuffer, 0, _buffers(), 0, size);
        		}
        		val size2 = _A().swapRow(row2, min, max, _buffers());
       			Array.asyncCopy[Double](_buffers(), 0, remoteBuffer, 0, size2);
        	}        	
        }
        A_here.setRow(row1, min, max, buffer);
    }

    def panel(J:Int, timer:Timer) {
        val A_panel_j = A_here.blocks(J, MB, J, J);
        val A_ext_panel_j = A_here.blocks(0, MB, J, J);
        if (!A_ext_panel_j.empty()) {
            var n:Int = 0;
            val LUColStart:Int = J * B;
            for (var LUCol:Int = LUColStart; LUCol<LUColStart + B; LUCol++) {
                timer.start(5);
                val row2:Int = computeLocalMax(J, LUCol);
                timer.stop(5);
                timer.start(6);
                if (A_here.hasBlock(J, J)) {
                    val row1 = LUCol;
                    pivot(n) = row2;
                    if (row2 != row1) {
                        val dest = A_here.placeOf(row2, LUCol);
                        if (dest == here.id) {
                            val b0 = A_ext_panel_j.blockOf(row1, LUCol);
                            val b1 = A_ext_panel_j.blockOf(row2, LUCol);
                            for (var j:Int = J*B; j < J*B + B; ++j) {
                                var tmp:Double = b0(row1, j); 
                                b0(row1, j) = b1(row2, j);
                                b1(row2, j) = tmp;
                            }
                        } else {
                            exchange(row1, row2, LUColStart, LUColStart + B - 1, dest);
                        }
                    }
                    val block = A_here.block(J, J);
                    for (var i:Int = 0; i < B; ++i) rowForBroadcast(i) = block(LUCol, J*B+i);
                }
                timer.stop(6);
                timer.start(7);
                timer.start(11);
                col.bcast(colRole, J%px, rowForBroadcast, 0, rowForBroadcast, 0, rowForBroadcast.size);
                timer.stop(11);
                timer.stop(7);
                if(!A_panel_j.empty()) {
                    timer.start(8);
                    for (var I:Int = A_panel_j.min_x; I <= A_panel_j.max_x; I += px) {
                        blockMulSubRow(A_here.block(I, J).raw, rowForBroadcast, B, LUCol - J * B, I == J);
                    }
                    timer.stop(8);
                }
                n++;
            }
        }
    }
    
    def swapRows(J:Int, timer:Timer) {
        timer.start(10);
        row.bcast(rowRole, J%py, pivot, 0, pivot, 0, pivot.size);
        timer.stop(10);

        val row_panel = A_here.blocks(J, J, J + 1, NB);
        if (!row_panel.empty()) {
            var n:Int = 0;
            for (var row1:Int = J * B; row1 < (J + 1) * B; ++row1) {
                val row2 = pivot(n++);
                if (row2 == row1) continue;
                val dest = A_here.placeOf(row2, row_panel.min_y * B);
                if (dest == here.id) {
                    for (var j:Int = (J + 1) * B; j < N; j += B) {
                        if (A_here.placeOf(row1, j) == here.id) {
                            val b1 = A_here.blockOf(row1, j);
                            val b2 = A_here.blockOf(row2, j);
                            for (var k:Int = j; k < j + B; ++k) {
                                var tmp:Double = b1(row1, k); 
                                b1(row1, k) = b2(row2, k);
                                b2(row2, k) = tmp;
                            }  
                        }
                    }
                } else {        
                    exchange(row1, row2, (J + 1) * B, N - 1, dest);
                }
            } 
        }
    }

    def triSolve(J:Int, timer:Timer) {
        if (A_here.hasRow(J)) {
            var tmp:Array[Double];
            if (A_here.hasCol(J)) tmp = A_here.block(J, J).raw; else tmp = colBuffer;
            val diag = tmp;
            timer.start(10);
            row.bcast(rowRole, J%py, diag, 0, diag, 0, diag.size);
            timer.stop(10);
            for (var cj:Int = J + 1; cj <= NB; ++cj) if (A_here.hasCol(cj)) {
                blockTriSolve(A_here.block(J, cj).raw, diag, B);
            }
        }
    }

    def update(J:Int, timer:Timer) {
        val A_U = A_here.blocks(0, MB, J + 1, NB);
        if (!A_U.empty()) {
            for (var cj:Int = A_U.min_y; cj <= A_U.max_y; cj += py) {
                val block = A_here.hasBlock(J, cj) ? A_U.block(J, cj).raw : colBuffers(cj/py);
                timer.start(11);
                col.bcast(colRole, J%px, block, 0, block, 0, block.size);
                timer.stop(11);
            }
        }

        Team.WORLD.barrier(here.id);

        val A_L = A_here.blocks(J + 1, MB, 0, NB);
        if (!A_L.empty()) {
            for (var ci:Int = A_L.min_x; ci <= A_L.max_x; ci += px) {
                val block = A_here.hasBlock(ci, J) ? A_L.block(ci, J).raw : rowBuffers(ci/px);
                timer.start(10);
                row.bcast(rowRole, J%py, block, 0, block, 0, block.size);
                timer.stop(10);
            }
        }
        
        Team.WORLD.barrier(here.id);

        val A_trail = A_here.blocks(J + 1, MB, J + 1, NB);

        if (!A_trail.empty()) {
            for (var ci:Int = A_trail.min_x; ci <= A_trail.max_x ; ci += px){
                for (var cj:Int = A_trail.min_y; cj <= A_trail.max_y; cj += py) {
                    val left = A_here.hasCol(J) ? A_L.block(ci, J).raw : rowBuffers(ci/px);
                    val upper = A_here.hasRow(J) ? A_U.block(J, cj).raw : colBuffers(cj/py);
                    blockMulSub(A_trail.block(ci, cj).raw, left, upper, B);
                }
            }
        } 
    }

    def solve(timer:Timer) {
        progressInc:Int = 2;
        var nextJ:Int = progressInc;

        computeRowSum(); Team.WORLD.barrier(here.id);

        timer.start(9);

        for (var J:Int = 0; J < NB; J++){
            timer.start(1); panel(J, timer);            Team.WORLD.barrier(here.id); timer.stop(1);
            timer.start(2); swapRows(J, timer);         Team.WORLD.barrier(here.id); timer.stop(2);
            timer.start(3); triSolve(J, timer);         Team.WORLD.barrier(here.id); timer.stop(3);
            timer.start(4); if (J != NB - 1) update(J, timer); Team.WORLD.barrier(here.id); timer.stop(4);

            /* Progress meter */
            if(0 == here.id && J > nextJ) {
                timer.stop(9);
                Console.OUT.println("" + J + " of " + NB + " complete " + 
                        "last " + progressInc + " iterations took " + 
                        (timer.total(9) as Double)/1e9 + " seconds");
                nextJ += progressInc;
                timer.clear(9);
                timer.start(9);
            }
        }
    }

    def memget(I:Int, J:Int) {
        if (A_here.hasBlock(I, J)) {
            return A_here.block(I, J).raw;
        } else {
            val source = here;
            ready = false;
            val _A = A;
            val remoteRowBuffer = new RemoteArray(rowBuffer);
            val _B = B;
            @Pragma(Pragma.FINISH_ASYNC_AND_BACK) finish{
				async at(Place(A_here.placeOfBlock(I, J))){
					Array.asyncCopy(_A().block(I, J).raw, 0, remoteRowBuffer, 0, _B * _B);
				}
	    	}
            return rowBuffer;
        }
    }

    def backsolve() {
        val A_last_panel = A_here.blocks(0, MB, NB, NB);
        if (!A_last_panel.empty()) {
            for (var I:Int = MB; I >= 0; --I) {
                if (A_here.hasRow(I)) {
                    blockBackSolve(A_here.block(I, NB).raw, memget(I, I), B);
                }
                var tmp:Array[Double];
                if (A_here.hasRow(I)) tmp = A_here.block(I, NB).raw; else tmp = colBuffer;
                val bufferY = tmp;
                col.bcast(colRole, I%px, bufferY, 0, bufferY, 0, bufferY.size);
                for (var ci:Int = 0; ci < I; ++ci) if (A_here.hasRow(ci)) {
                    blockMulSub(A_here.block(ci, NB).raw, memget(ci, I), bufferY, B);
                }
                col.barrier(colRole);
            }
        }
        Team.WORLD.barrier(here.id);
    }

    def check() {
        var max:Double = 0.0;
        for (var i:Int = 0; i < M; ++i) {
            if (A_here.placeOf(i, M) == here.id) {
                val v = 1.0 - A_here(i, M);
                max = Math.max(max, v * v);
            }
        }
        Console.OUT.println("diff " + max + " " + here.id);
        return col.allreduce(colRole,max,Team.MAX);
    }

    public static def main(args:Array[String](1)) {
        if (args.size < 4) {
            Console.OUT.println("Usage: LU M B (px py)");
            Console.OUT.println("M = Matrix size,");
            Console.OUT.println("B = Block size, where B should perfectly divide M");
            Console.OUT.println("px py = Processor grid, where px*py = nplaces");
            return;
        }
        val M = Int.parse(args(0));
        val B = Int.parse(args(1));
        val N = M + B;
        val px = Int.parse(args(2));
        val py = Int.parse(args(3));
        val A = BlockedArray.make(M, N, B, B, px, py);
        val buffers = PlaceLocalHandle.makeFlat[Array[Double](1){rail,self!=null}](Dist.makeUnique(), ()=>new Array[Double](N));        
        val lus = PlaceLocalHandle.makeFlat[LU](Dist.makeUnique(), ()=>new LU(M, N, B, px, py, A, buffers));
        Console.OUT.println ("LU Starting: M " + M + " B " + B + " px " + px + " py " + py);
        start(lus);
    }

    def this(M:Int, N:Int, B:Int, px:Int, py:Int, A:PlaceLocalHandle[BlockedArray], buffers:PlaceLocalHandle[Array[Double](1){rail,self!=null}]) { 
        this.M = M; this.N = N; this.B = B; this.px = px; this.py = py;
        this.A = A; A_here = A();
        this.buffers = buffers; buffer = buffers();
        MB = M / B - 1;
        NB = N / B - 1;
        colRole = here.id / py;
        rowRole = here.id % py;
        col = Team.WORLD.split(here.id, rowRole, colRole);
        row = Team.WORLD.split(here.id, colRole, rowRole);
        pivot = new Array[Int](B);
        rowForBroadcast = new Array[Double](B);
        val rowBuffers = new Array[Array[Double]{self!=null}](M / B / px + 1, (Int)=>new Array[Double](B * B));
        val colBuffers = new Array[Array[Double]{self!=null}](N / B / py + 1, (Int)=>new Array[Double](B * B));
        this.rowBuffers = rowBuffers;
        this.colBuffers = colBuffers;
        rowBuffer = rowBuffers(0);
        colBuffer = colBuffers(0);
    }

    static def start(lus:PlaceLocalHandle[LU]) {
        Console.OUT.println("Main loop starting ...");

        var t:Long = -System.nanoTime();

        @Pragma(Pragma.FINISH_ATEACH_UNIQUE) finish ateach (p in unique) {
            val lu = lus();
            val timer = new Timer(15);

            timer.start(0);

            lu.solve(timer);
            lu.backsolve();
            val r = lu.check();

            timer.stop(0);

            if (here.id == 0) {
                Console.OUT.println ("difference " + r);
                Console.OUT.println(((r < 0.01?" ok)":" fail ") + " diff=" + r));
                Console.OUT.println ("Timer(0) TOTAL #invocations=" + timer.count(0) +
                  " Time=" + (timer.total(0) as Double) / 1e9 + " seconds");
                Console.OUT.println ("Timer(1) PANEL #invocations=" + timer.count(1) +
                  " Time=" + (timer.total(1) as Double)/1e9 + " seconds");
                Console.OUT.println ("Timer(2) SWAPROWS #invocations=" + timer.count(2) +
                  " Time=" + (timer.total(2) as Double)/1e9 + " seconds");
                Console.OUT.println ("Timer(3) TRISOLVE #invocations=" + timer.count(3) +
                  " Time=" + (timer.total(3) as Double)/1e9 + " seconds");
                Console.OUT.println ("Timer(4) UPDATE #invocations=" + timer.count(4) +
                  " Time=" + (timer.total(4) as Double)/1e9 + " seconds");
                Console.OUT.println ("Timer(5) PANEL-PIVOT #invocations=" + timer.count(5) +
                  " Time=" + (timer.total(5) as Double)/1e9 + " seconds");
                Console.OUT.println ("Timer(6) PANEL-SWAP #invocations=" + timer.count(6) +
                  " Time=" + (timer.total(6) as Double)/1e9 + " seconds");
                Console.OUT.println ("Timer(7) PANEL-BCAST #invocations=" + timer.count(7) +
                  " Time=" + (timer.total(7) as Double)/1e9 + " seconds");
                Console.OUT.println ("Timer(8) PANEL-UPDATE #invocations=" + timer.count(8) +
                  " Time=" + (timer.total(8) as Double)/1e9 + " seconds");
                Console.OUT.println ("Timer(10) ROW-BROADCAST # invocations=" + timer.count(10) +
                  " Time=" + (timer.total(10) as Double)/1e9 + " seconds");
                Console.OUT.println ("Timer(11) COL-BROADCAST # invocations=" + timer.count(11) +
                  " Time=" + (timer.total(11) as Double)/1e9 + " seconds");
              } 
        }
        t += System.nanoTime();

        Console.OUT.println();
        Console.OUT.println(" Time= "+ t/1e9 + " seconds" + " Rate= " + flops(lus().N)/t + " GFlops");
    }

    static def flops(n:Int) = ((4.0*n-3.0)*n-1.0)*n/6.0;
}
