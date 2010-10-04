package rc7;

import x10.compiler.*;
import util.Comm;

@NativeCPPInclude("essl_natives.h")
@NativeCPPCompilationUnit("essl_natives.cc")
class LU {

    @Native("c++", "blockTriSolve((#1)->raw(), (#2)->raw(), #3)")
        native static def blockTriSolve(me:Rail[Double], diag:Rail[Double], B:Int):Void;

    @Native("c++", "blockBackSolve((#1)->raw(), (#2)->raw(), #3)")
        native static def blockBackSolve(me:Rail[Double], diag:Rail[Double], B:Int):Void;

    @Native("c++", "blockMulSub((#1)->raw(), (#2)->raw(), (#3)->raw(), #4)")
        native static def blockMulSub(me:Rail[Double], left:Rail[Double], upper:Rail[Double], B:Int):Void;

    @Native("c++", "blockMulSubRow((#1)->raw(), (#2)->raw(), #3, #4, #5)")
        native static def blockMulSubRow(me:Rail[Double], diag:Rail[Double], B:Int, j:Int, cond:boolean):Void;
    
    static def runAt(id:Int, c:()=>Void) {
         x10.lang.Runtime.runAtNative(id, c);
         x10.lang.Runtime.dealloc(c);
    }

    const unique = Dist.makeUnique();

    val M:Int;
    val N:Int;
    val B:Int;
    val MB:Int;
    val NB:Int;
    val px:Int;
    val py:Int;
    val A:PlaceLocalHandle[BlockedArray];
    val A_here:BlockedArray!;
    val world:Comm!;
    val col:Comm!;
    val row:Comm!;
    var ready:Boolean;
    val pivot:Rail[Int]!;
    val rowForBroadcast:Rail[Double]!;
    val rowBuffer:Rail[Double]!;
    val colBuffer:Rail[Double]!;
    val colBuffers:ValRail[Rail[Double]!];
    val rowBuffers:ValRail[Rail[Double]!];
    val buffer:Rail[Double]!;
    val buffers:PlaceLocalHandle[Rail[Double]];

    def computeRowSum() {
        val sum = Rail.make[Double](B);

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
                sum(k) = row.sum(sum(k));
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
        return col.indexOfAbsMax(max, id);
    }

    def exchange(row:Int, row2:Int, min:Int, max:Int, dest:Int) {
        val source = here; 
        ready = false;
        val size = A_here.getRow(row, min, max, buffer);
        val _buffers = buffers;
        val _A = A;
        buffers.copyTo[Double](Place.places(dest), size, ()=>{
            val size = _A().swapRow(row2, min, max, _buffers());
            _buffers.copyTo[Double](source, size, ()=>{
                A_here.setRow(row, min, max, buffer);
                atomic ready=true;
            });
        });
        await ready;
    }

    def panel(J:Int, timer:Timer!) {
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
                    val row = LUCol;
                    pivot(n) = row2;
                    if (row2 != row) {
                        val dest = A_here.placeOf(row2, LUCol);
                        if (dest == here.id) {
                            val b0 = A_ext_panel_j.blockOf(row, LUCol);
                            val b1 = A_ext_panel_j.blockOf(row2, LUCol);
                            for (var j:Int = J*B; j < J*B + B; ++j) {
                                var tmp:Double = b0(row, j); 
                                b0(row, j) =  b1(row2, j);
                                b1(row2, j) =  tmp;
                            }
                        } else {
                            exchange(row, row2, LUColStart, LUColStart + B - 1, dest);
                        }
                    }
                    val block = A_here.block(J, J);
                    for (var i:Int = 0; i < B; ++i) rowForBroadcast(i) = block(LUCol, J*B+i);
                }
                timer.stop(6);
                timer.start(7);
                timer.start(11);
                col.broadcast_d(rowForBroadcast, J%px); 
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
    
    def swapRows(J:Int, timer:Timer!) {
        timer.start(10);
        row.broadcast(pivot, J%py);
        timer.stop(10);

        val row_panel = A_here.blocks(J, J, J + 1, NB);
        if (!row_panel.empty()) {
            var n:Int = 0;
            for (var row:Int = J * B; row < (J + 1) * B; ++row) {
                val row2 = pivot(n++);
                if (row2 == row) continue;
                val dest = A_here.placeOf(row2, row_panel.min_y * B);
                if (dest == here.id) {
                    for (var j:Int = (J + 1) * B; j < N; j += B) {
                        if (A_here.placeOf(row, j) == here.id) {
                            val b1 = A_here.blockOf(row, j);
                            val b2 = A_here.blockOf(row2, j);
                            for (var k:Int = j; k < j + B; ++k) {
                                var tmp:Double = b1(row, k); 
                                b1(row, k) = b2(row2, k);
                                b2(row2, k) = tmp;
                            }  
                        }
                    }
                } else {        
                    exchange(row, row2, (J + 1) * B, N - 1, dest);
                }
            } 
        }
    }

    def triSolve(J:Int, timer:Timer!) {
        if (A_here.hasRow(J)) {
            var tmp:Rail[Double]!;
            if (A_here.hasCol(J)) tmp = A_here.block(J, J).raw; else tmp = colBuffer;
            val diag = tmp;
            timer.start(10);
            row.broadcast_d(diag, J%py);
            timer.stop(10);
            for (var cj:Int = J + 1; cj <= NB; ++cj) if (A_here.hasCol(cj)) {
                blockTriSolve(A_here.block(J, cj).raw, diag, B);
            }
        }
    }

    def update(J:Int, timer:Timer!) {
        val A_U = A_here.blocks(0, MB, J + 1, NB);
        if (!A_U.empty()) {
            for (var cj:Int = A_U.min_y; cj <= A_U.max_y; cj += py) {
                val block = A_here.hasBlock(J, cj) ? A_U.block(J, cj).raw : colBuffers(cj/py);
                timer.start(11);
                col.broadcast_d(block,  J%px);
                timer.stop(11);
            }
        }

        world.barrier();

        val A_L = A_here.blocks(J + 1, MB, 0, NB);
        if (!A_L.empty()) {
            for (var ci:Int = A_L.min_x; ci <= A_L.max_x; ci += px) {
                val block = A_here.hasBlock(ci, J) ? A_L.block(ci, J).raw : rowBuffers(ci/px);
                timer.start(10);
                row.broadcast_d(block, J%py);
                timer.stop(10);
            }
        }
        
        world.barrier();

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

    def solve(timer:Timer!) {
        progressInc:Int = 2;
        var nextJ:Int = progressInc;

        computeRowSum(); world.barrier();

        timer.start(9);

        for (var J:Int = 0; J < NB; J++){
            timer.start(1); panel(J, timer);            world.barrier(); timer.stop(1);
            timer.start(2); swapRows(J, timer);         world.barrier(); timer.stop(2);
            timer.start(3); triSolve(J, timer);         world.barrier(); timer.stop(3);
            timer.start(4); if (J != NB - 1) update(J, timer); world.barrier(); timer.stop(4);

            /* Progress meter */
            if(0 == here.id && J > nextJ) {
                timer.stop(9);
                Console.OUT.println(J + " of " + NB + " complete " + 
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
            val _rowBuffer = rowBuffer;
            val _B = B;
            runAt(A_here.placeOfBlock(I, J), ()=>{
                _A().block(I, J).raw.copyTo(0, _rowBuffer, 0, _B * _B, ()=>{
                    atomic ready=true;
                });
            });
            await ready;
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
                var tmp:Rail[Double]!;
                if (A_here.hasRow(I)) tmp = A_here.block(I, NB).raw; else tmp = colBuffer;
                val bufferY = tmp;
                col.broadcast_d(bufferY, I%px);
                for (var ci:Int = 0; ci < I; ++ci) if (A_here.hasRow(ci)) {
                    blockMulSub(A_here.block(ci, NB).raw, memget(ci, I), bufferY, B);
                }
                col.barrier();
            }
        }
        world.barrier();
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
        return col.max(max);
    }

    public static def main(args:Rail[String]!) {
        if (args.length < 4) {
            Console.OUT.println("Usage: LU M B (px py)");
            Console.OUT.println("M = Matrix size,");
            Console.OUT.println("B = Block size, where B should perfectly divide M");
            Console.OUT.println("px py = Processor grid, where px*py = nplaces");
            return;
        }
        val M = Int.parseInt(args(0));
        val B = Int.parseInt(args(1));
        val N = M + B;
        val px = Int.parseInt(args(2));
        val py = Int.parseInt(args(3));
        val A = BlockedArray.make(M, N, B, B, px, py);
        val buffers = PlaceLocalHandle.make[Rail[Double]](unique, ()=>Rail.make[Double](N));        
        val lus = PlaceLocalHandle.make[LU](unique, ()=>new LU(M, N, B, px, py, A, buffers));
        Console.OUT.println ("LU Starting: M " + M + " B " + B + " px " + px + " py " + py);
        start(lus);
    }

    def this(M:Int, N:Int, B:Int, px:Int, py:Int, A:PlaceLocalHandle[BlockedArray], buffers:PlaceLocalHandle[Rail[Double]]) { 
        this.M = M; this.N = N; this.B = B; this.px = px; this.py = py;
        this.A = A; A_here = A();
        this.buffers = buffers; buffer = buffers();
        MB = M / B - 1;
        NB = N / B - 1;
        val world = Comm.WORLD();
        this.world = world;
        val pi = here.id / py;
        val pj = here.id % py;
        col = world.split(pj, pi);
        row = world.split(pi, pj);
        pivot = Rail.make[Int](B);
        rowForBroadcast = Rail.make[Double](B);
        val rowBuffers = ValRail.make[Rail[Double]!](M / B / px + 1, (Int)=>Rail.make[Double](B * B));
        val colBuffers = ValRail.make[Rail[Double]!](N / B / py + 1, (Int)=>Rail.make[Double](B * B));
        this.rowBuffers = rowBuffers;
        this.colBuffers = colBuffers;
        rowBuffer = rowBuffers(0);
        colBuffer = colBuffers(0);
    }

    static def start(lus:PlaceLocalHandle[LU]) {
        Console.OUT.println("Main loop starting ...");

        var t:Long = -System.nanoTime();

        finish ateach (p in unique) {
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
