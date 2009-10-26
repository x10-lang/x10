package rc7;

import x10.util.Random;
import x10.util.StringBuilder;
import x10.io.Console;
import x10.compiler.Native;
import x10.runtime.PlaceLocalHandle;
import x10.runtime.PlaceLocalStorage;

public value class LU
{
    public const RAND_MAX = 10;
    @Native("c++", "void blockTriSolve(double*, double*, signed int);\nblockTriSolve((#1)->raw(), (#2)->raw(), #3)")
        native static def blockTriSolve(var me: Rail[double], var diag: Rail[double], var B: Int): void;
    @Native("c++", "void blockBackSolve(double*, double*, int);\nblockBackSolve((#1)->raw(), (#2)->raw(), #3)")
        native static def blockBackSolve(var me: Rail[double], var diag: Rail[double], var B: Int): void;
    @Native("c++", "void blockMulSub(double*, double*, double*, int);\nblockMulSub((#1)->raw(), (#2)->raw(), (#3)->raw(), #4)")
        native static def blockMulSub(var me: Rail[double], var left: Rail[double], var upper: Rail[double], var B: Int): void;
    @Native("c++", "void blockMulSubRow(double*, double*, int, int, bool);\nblockMulSubRow((#1)->raw(), (#2)->raw(), #3, #4, #5)")
        native static def blockMulSubRow(var me: Rail[double], var diag: Rail[double], var B: Int, var j: Int, var cond: boolean): void;

    public  def dump(): String = { 
        val A_here = A.blockFullHere();
        var sb: StringBuilder! = new StringBuilder();
        sb.add("\n");
        for (var i: Int = 0; i < M; i++) {
            for (var j: Int = 0; j < M; j++) 
                if (A.placeOf(i,j)==here.id) { 
                    var prefix: String = " [" + i + "," + j + "]";
                    (sb.add(prefix) as StringBuilder!).add(A_here(i,j).toString());
                }
            if (A.placeOf(i, M) == here.id)
                (sb.add(" | ") as StringBuilder!).add(A_here(i, M).toString());
            sb.add("\n");
        }
        return sb.toString();
    }

    public  def mulSubBackSolve(val L: Rail[double]!, val diag: Rail[double]!): void = {
        if (C_MODE) blockBackSolve(L, diag, B);
        else for (var k: Int = B-1; k >= 0; k--) {
            L(k*B+0) /= diag(k*B+k);
            for (var i: Int = 0; i < k; ++i) {
                L(i*B+0) -= diag(i*B+k)*L(k*B+0);
            }
        }
    }
    public def computeYDiff(): double = {
        val A_here = A.blockFullHere();
        world.barrier();
        var max: double = 0.0;
        for (var i: Int = 0; i < M; ++i) {
            if (A.placeOf(i, M)==here.id) {
                var val: double = 1.0 - A_here(i, M);
                val *= val;
                max = Math.max(max, val);
            }
        }
        Console.OUT.println("diff " + max + " " + here.id);
        val r: double = col().max(max);
        return r;
    }

	static val unique = Dist.makeUnique();

    val M: Int;
    val N: Int;
    val B: Int;
    val px: Int;
    val py: Int;

    val A: HPL_Dist;
    val world: Comm;

    public val block_region_max0: Int;
    public val block_region_max1: Int;

    val reporting: boolean;
    val vandermonde: boolean;
    val C_MODE: boolean;
    
    val state:PlaceLocalHandle[State];

    public  def pow(var i: Int, var j: Int): Int = {
        var result: Int = i;
        for (var k: Int = 1; k < j; k++) result *= i;
        return result;
    }

    public  def mulSub(val subblock: Rail[double]!, val left: Rail[double]!, val upper: Rail[double]!): void = {
        if (C_MODE) { //Console.OUT.println ("helo"); 
            blockMulSub(subblock, left, upper, B); }
        else for (val p(i,j): Point in [0..B-1, 0..B-1]) {
            var r: double = 0.0; for (val p1(k): Point in [0..B-1]) r += left(i*B+k) * upper(k*B+j);
            subblock(i*B+j) -=r;
        }
    }

    public  def mulSubLeft(val subblock: Rail[double]!, val diag: Rail[double]!): void = {

        if (C_MODE) blockTriSolve(subblock, diag, B); 
        else for (val p(i,j): Point in [0..B-1, 0..B-1]) {
            var r: double = 0.0; for (val p1(k): Point in [0..i-1]) r += diag(i*B+k) * subblock(k*B+j);
            subblock(i*B+j) -= r;
        }
    }

    public  def computeRowSum(): void = {
        var sum: Rail[double]! = Rail.makeVar[double](B);
        val A_here = A.blockFullHere();

        //x10.io.Console.OUT.println("point 1");
        for (var I: Int = 0; I < M; I += B) {
            //x10.io.Console.OUT.println("point 2");

            for (var k: Int = 0; k < B; k++) sum(k) = 0.0;

            for (var J: Int = 0 ; J < M; J += B) {
                //x10.io.Console.OUT.println("point 3");
                if (A.placeOf (I, J) == here.id) {
                    val b = A_here.blockOf(I, J);

                    var k: Int = 0;
                    for (var i: Int = I; i < I + B; i++, k++) { 
                        for (var j: Int = J; j < J + B; j++) 
                            sum(k) += b(i, j);
                    }
                }
            }
            //x10.io.Console.OUT.println("point 4");
            for (var k: Int = 0; k < B; k++) { 
                sum(k) = row().sum(sum(k));
                if (A.placeOf(I+k, M) == here.id) {
                    A_here(I+k, M) = sum(k);
                }
            }
            //x10.io.Console.OUT.println("point 5");
        }
        //x10.io.Console.OUT.println(world);
        world.barrier();
        //x10.io.Console.OUT.println("point 6");
    }

    public  def triSolve(var J: Int): void = {

        var N: Int = block_region_max1;
        val A_here = A.blockFullHere();

        if (A.hasRow(J)) {
            var diag: Rail[double] = A.hasBlock(J, J)? A_here.block(J, J).raw : (state.get()).colBuffer;

            row().broadcast_d(diag, J%py);

            for (var cj: Int = J+1; cj <= N; cj++) if (A.hasBlock(J, cj)) mulSubLeft( A_here.block(J, cj).raw, diag);

        }
        //world.barrier();
    }

    public  def update(val J: Int): void = {

        var M: Int = block_region_max0;
        var N: Int = block_region_max1;

        val A_U = A.block_restrict_here(0, block_region_max0, J+1, block_region_max1);

        if (!A_U.empty()){
            for (var cj: Int = A_U.min_y; cj <= A_U.max_y;
                    cj+= py) {
                if (A.placeOfBlock(J, cj) == here.id) {
                    col().broadcast_d(A_U.block(J, cj).raw,  J%px);
                }
                else  {
                    val stateHere = state.get(); // XXX: Fix for now
                    var rawBlock : Rail[double] = stateHere.colBuffers(cj/py);
                    col().broadcast_d(rawBlock, J%px);
                }
            }
        }

        world.barrier();

        val A_L = A.block_restrict_here(J+1, block_region_max0, 0, block_region_max1);
        if (!A_L.empty()) {
            for (var ci: Int = A_L.min_x; ci <= A_L.max_x; ci += px) {
                val stateHere = state.get();
                row().broadcast_d(A.placeOfBlock(ci,J) == here.id ? A_L.block(ci, J).raw : stateHere.rowBuffers(ci/px), J%py);
            }
        }
        
        world.barrier();

        val A_trail = A.block_restrict_here(J+1, block_region_max0, J+1, block_region_max1);

        if (!A_trail.empty()){

            for (var ci: Int = A_trail.min_x; ci <= A_trail.max_x
                    ; ci += px){
                for (var cj: Int = A_trail.min_y; cj <= A_trail.max_y;
                        cj += py) {
                    val stateHere = state.get();
                    var left: Rail[double] =  (A.hasCol(J) ? A_L.block(ci, J).raw 
                            : stateHere.rowBuffers(ci/px));
                    var above: Rail[double] = (A.hasRow(J) ? A_U.block(J, cj).raw 
                            : stateHere.colBuffers(cj/py));
                    mulSub( A_trail.block(ci, cj).raw, left, above);
                }
            }
        } 
		
        //world.barrier();
    }

    public  def computeLocalMax(var J: Int, var LUCol: Int) = {
        val A_here = A.blockFullHere();
        var max: double = 0;
        var id: Int = -1;

        for (var I: int = J*B; I < M; I += B) {
            if (A.placeOf(I, LUCol) == here.id ) {
                val b = A_here.blockOf(I, LUCol);
                for (var i: int = Math.max(I, LUCol); i < I + B; i++)
                    if (Math.abs(b(i, LUCol)) >= Math.abs(max) || id ==-1) {
                        max = b(i, LUCol);
                        id = i;
                    }
            }
        }

        //if (id==-1) Console.OUT.println (M + " " + J + " " + LUCol );	

        return col().indexOfAbsMax(max, id);
    }

    public  def swapRows(val J: Int): void = {

        val I = J;

        row().broadcast(state.get().pivot, J%py);

        //world.barrier();

        val row_panel = A.block_restrict_here(J, J, J+1, block_region_max1);
        if (!row_panel.empty()) {

            var n: Int = 0;
            for (var _myrow:Int = row_panel.flat_region_min_x(); _myrow<=row_panel.flat_region_max_x(); _myrow++) {
            	val myrow = _myrow;

                val stateHere = state.get();
                val row2 = stateHere.pivot(n++);

                if (row2==myrow) continue;

                var dest: Int = A.placeOf(row2, row_panel.flat_region_min_y());

                if (dest == here.id) {
			        val A_here = A.blockFullHere();
                    for (var j: Int = (J+1)*B; j < N; j += B) {
                        if (A.placeOf(myrow, j) == here.id) {
                            val b1 = A_here.blockOf(myrow, j);
                            val b2 = A_here.blockOf(row2, j);
                            for (var k: Int = j; k < j + B; k++) {
                                var tmp: double = b1(myrow,k); 
                                b1(myrow,k) = b2(row2,k);
                                b2(row2,k) =  tmp;
                            }  
                        }
                    }
                } else {	    
                    val source = here; 
                    //x10.io.Console.OUT.println("before arrayview here");
                    stateHere.ready = false;
			        val b = stateHere.swapBuffer;
                    val size = A.array_restrict_here(myrow, (J+1)*B, (M+B-1), b);
                    //val myrowl: int = myrow;
                  	copyTo(b, Place.places(dest), ()=>state.get().swapBuffer, size, ()=>{
                    	val buf = state.get().swapBuffer;
                        val size = A.array_restrict_here_swap(row2, (J+1)*B, (M+B-1), buf);
                    	copyTo(buf, source, ()=>b, size, ()=>{
                            //x10.io.Console.OUT.println(dest + "signalling " + here.id);
                            A.array_restrict_here_update(myrow, (J+1)*B, (M+B-1), b);
                            atomic state.get().ready=true;
                        });
                    });

                    //x10.io.Console.OUT.println(here.id + "waiting for" +  dest );
                    await stateHere.ready; // remote copies have also completed.
                }
            } 
        }

        //world.barrier();
    }

	public static def copyTo(rail:Rail[Double]!, dest:Place, finder:()=>Rail[Double], size:Int, notifier:()=>Void) {
		rail.copyTo(0, dest, finder, size, notifier);
		dealloc(finder); 
		dealloc(notifier); 
	}

    @Native("c++", "x10aux::dealloc(#4.operator->())")
    public static def dealloc[T](x:T) { }
    
    public static def runAt(id:Int, c:()=>Void) {
    	 x10.runtime.NativeRuntime.runAt(id, c);
    	 dealloc(c);
    }

    public  def memget(val I: Int, val J: Int): void = {
        val source = here;
        val stateHere = state.get();
        stateHere.ready = false;
        runAt(A.placeOfBlock(I, J), ()=>{
        	val tmp = A.blockFullHere().block(I, J).raw;
        	copyTo(tmp, source, ()=>state.get().rowBuffer, B*B, ()=>{
            	atomic state.get().ready=true;
        	});
        });
        await stateHere.ready;
    }

    public  def backsolve(): void = {
        world.barrier();
        var N: Int = block_region_max1;
        var M: Int = block_region_max0;
        val A_here = A.blockFullHere();
        val A_last_panel = A.block_restrict_here(0, block_region_max0, 
                block_region_max1, block_region_max1);
        if (!A_last_panel.empty()) {
            val stateHere = state.get();
            var bufferY: Rail[double] =  stateHere.colBuffer;

            for (var I: Int = M; I >=0 ; I--) {
                if (A.placeOfBlock(I, N)  == here.id) {
                    var bufferX: Rail[double] =  stateHere.rowBuffer;
                    if (A.placeOfBlock(I, I) == here.id) {
                        bufferX = A_here.block(I,I).raw;
                    } else {
                        memget(I, I);
                    }
                    mulSubBackSolve( A_here.block(I, N).raw, bufferX);
                }

                col().broadcast_d(A.placeOfBlock(I, N) == here.id?  A_here.block(I, N).raw : bufferY, I%px);

                for (var ci: Int = 0; ci < I ; ci++) {
                    if (!A.hasRow(ci)) continue;
                    var below: Rail[double] = A.hasRow(I)? A_here.block(I, N).raw : bufferY;
                    var bufferX: Rail[double]  =  stateHere.rowBuffer;

                    val dest = A.placeOf(ci*B, I*B);
                    if (dest==here.id) {
                        bufferX =  A_here.block(ci, I).raw;
                    } else{
                     memget(ci,I);
                   }

                    mulSub( A_here.block(ci, N).raw, bufferX, below);
                }
                col().barrier();
            }
        }
        world.barrier();
    }


    public  def mulSubRow(var I: Int, var J: Int, var LUCol: Int, val diag: Rail[double]!): void = {

        val A_here = A.blockFullHere();
        if (C_MODE) blockMulSubRow(A_here.block(I, J).raw, diag, B, LUCol-J*B, I==J);
        else if (I==J) {	    
            var j: Int = LUCol;
            for (var i: Int = j+1; i < J*B+B; ++i){
                A_here(i, j) = A_here(i,j)/A_here(j,j); 
                for (var k: Int = j+1; k < J*B+B; k++) {
                    var tmp: double = A_here(i, j) * A_here(j, k); 
                    A_here(i, k) = A_here(i, k) - tmp;
                }
            } 
        } else {
            val b = A_here.block(I,J);
            for (var i: Int = b.min_x; i <= b.max_x; ++i) {
                A_here(i, LUCol) = A_here(i, LUCol) / diag(LUCol-J*B);
                for (var k: Int = LUCol+1; k < J*B + B; k++) {		    
                    var tmp: double = A_here(i, LUCol) * diag(k-J*B);
                    A_here(i, k) = A_here(i, k) - tmp;
                }
            }

        }
    }

    public  def memSwap(val LUColStart : Int, val row: Int, val row2: Int): void = {
        val source = here;
        val stateHere = state.get();
        stateHere.ready = false;
        val b = stateHere.swapBuffer;
        val size = A.array_restrict_here(row, LUColStart, LUColStart+B-1, b);
        val dest = A.placeOf(row2, LUColStart);
        copyTo(b, Place.places(dest), ()=>state.get().swapBuffer, size, ()=>{
        	val buf = state.get().swapBuffer;
			val size = A.array_restrict_here_swap(row2, LUColStart, LUColStart+B-1, buf);
            copyTo(buf, source, ()=>b, size, ()=>{
                A.array_restrict_here_update(row, LUColStart, LUColStart+B-1, b);
                atomic state.get().ready=true;
            });
        });
        await stateHere.ready;
    }

    public  def panel(var J: Int, var timer:Timer!): void = {

        /* Panel J */	
        val A_panel_j = A.block_restrict_here(J, block_region_max0, J, J);

        /* Extended Panel -- due to use of collective broadcasts */
        val A_ext_panel_j = A.block_restrict_here(0, block_region_max0, J, J);
        if (!A_ext_panel_j.empty()){
            //Console.OUT.println("panel " + " rank " + here.id);
            var n: Int = 0;
            val LUColStart: int = A_ext_panel_j.flat_region_min_y();
            /* there is no  projection implemented for union region */
            for (var _LUCol:Int = LUColStart; _LUCol<=A_ext_panel_j.flat_region_max_y(); _LUCol++) {
                timer.start(5);
            	val LUCol = _LUCol;
                var localMaxIndex: Int = computeLocalMax(J, LUCol);
                timer.stop(5);
                //col().barrier();
                    //x10.io.Console.OUT.println("LUCol: " + LUCol + " " + localMaxIndex);
                timer.start(6);
                if (A.placeOfBlock(J,J) == here.id) {
                    val row2 = localMaxIndex;
                    val row = LUCol;
                    val stateHere = state.get();
                    stateHere.pivot(n) = row2;
                    if (row2 != row) {
                        val dest = A.placeOf(row2, LUCol);
                        if (dest==here.id) {
                            val b0 = A_ext_panel_j.blockOf(row, LUCol);
                            val b1 = A_ext_panel_j.blockOf(row2, LUCol);
                            for (var j: Int = J*B; j < J*B + B; ++j) {
                                var tmp: double = b0(row,j); 
                                b0(row,j) =  b1(row2,j);
                                b1(row2,j) =  tmp;
                            }
                        } else {
                            memSwap(LUColStart, row, row2);
                        }
                    }
                    val b = A_ext_panel_j.block(J, J);
                    for (var i: Int = 0; i < B; ++i) stateHere.rowForBroadcast(i) = b(LUCol,J*B+i);
                }
                timer.stop(6);

                val stateHere = state.get();

                //Console.OUT.println("before broadcast "  + here.id + " " + stateHere.rowForBroadcast);
                timer.start(7);
                col().broadcast_d(stateHere.rowForBroadcast, J%px); 
                timer.stop(7);
                //Console.OUT.println("after broadcast "  + here.id + " " + stateHere.rowForBroadcast);

                if(!A_panel_j.empty()) {
                    if(reporting)  {
                        var s: String = dump();
                        Console.OUT.println("before mulSubRow:\n" + s + block_region_max0); 
                    }

                    timer.start(8);
                    for (var I: int = A_panel_j.min_x; 
                            I <= A_panel_j.max_x; 
                            I += px) {
                        mulSubRow(I, J, LUCol, stateHere.rowForBroadcast);
                        //Console.OUT.println("I: " + I);
                    }
                    timer.stop(8);

                    if (reporting) {
                        var s: String = dump();
                        Console.OUT.println("after  mulSubRow:\n" + s);
                    }
                }
                n++;
            }

        }
	
        //world.barrier();

    }

    public  def panel_par(var timer:Timer!): void = {

        var s: String;
        if (reporting) {
            s = dump();
            Console.OUT.println("after init " + here.id + " " + s);
        }

        computeRowSum();

        if (reporting) {
            s = dump();
            Console.OUT.println("after rowsum" + s);
        }

        world.barrier();

        var progressInc:Double = (block_region_max1 as Double)/10.0;
        if(0 == here.id) Console.OUT.println("block_region_max1 " + 
                block_region_max1 + " progressInc " + progressInc);

        for (var J:Int = 0; J<block_region_max1; J++){
            {
                if(reporting) {
                    s = dump();
                    Console.OUT.println("before panel" + s + " " + J);
                }

                timer.start(1);
                panel(J, timer);
                world.barrier();
                timer.stop(1);

                if (reporting) {
                    s = dump();
                    Console.OUT.println("before swaprows" + s);
                }

                timer.start(2);
                swapRows(J);
                world.barrier();
                timer.stop(2);

                if (reporting) {
                    s = dump();
                    Console.OUT.println("before trisolve" + s);
                }

                timer.start(3);
                triSolve(J);
                world.barrier();
                timer.stop(3);

                if (reporting) {
                    s = dump();		
                    Console.OUT.println("before update" + s);
                }

                timer.start(4);
                if (J!=(block_region_max1-1)) update(J);
                world.barrier();
                timer.stop(4);

                /* Progress meter */
                if(0 == here.id) {
                    if((J as Double)/(block_region_max1 as Double)) {
                    }
                }
            }
        }
    }

    public  static def main(var a: Rail[String]!): void = {

        if (a.length < 4) {
            Console.OUT.println("Usage: LU M B (px py) [reporting] [vandermonde] [C_MODE]--\n  M = Matrix size, \n B = Block size , where B should perfectly divide M \n (px py) = Processor grid, where px*py = nplaces, \n reporting = true=print matrices for debugging, otherwise not (default no print),\n vandermonde = true=vandermonde, otherwise random input (default random),\n C_MODE = true=use BLAS, otherwise not (default NOT blas)\n");
            return;
        }

        val M = int.parseInt(a(0));
        val B = int.parseInt(a(1));
        val N = M + B;
        val px = int.parseInt(a(2));
        val py = int.parseInt(a(3));
        val reporting = a.length > 4 ? boolean.parseBoolean(a(4)) : false;
        val vandermonde = a.length > 5 ? boolean.parseBoolean(a(5)) : false;
        val C_MODE = a.length > 6 ? boolean.parseBoolean(a(6)) : false;

        val lu = new LU(M, N, B, px, py, reporting, vandermonde, C_MODE);

        lu.start();
    }

    def this(M: Int, N: Int, B: Int, px: Int, py: Int, reporting: boolean, vandermonde: boolean, C_MODE: boolean): LU = { 

        this.M = M;
        this.N = N;
        this.B = B;
        this.px = px;
        this.py = py;
        this.reporting = reporting;
        this.vandermonde = vandermonde;
        this.C_MODE = C_MODE;

        A = new HPL_Dist(M, N, B, B, px, py, unique);

        block_region_max0 = M/B - 1;
        block_region_max1 = N / B -1;
        this.world = Comm.WORLD();

        val NN = (N+B) / B;
        val MM = M / B;
        val MM0 = MM / px;
        val NN0 = NN / py + 1;
        Console.OUT.println ("LU Starting: M " + M + " B " + B + " px " + px + " py " + py);
        state = PlaceLocalStorage.createDistributedObject[State](unique, ()=>new State(NN0, MM0, B));        
    }

    public  def start(): void = {

        finish ateach (place: Point in unique) {
            /* initialize the communicators */
            initComms(world);

            /* initialize the array */
            var rand: Random! = new Random(here.id*1000);
            val A_here = A.blockFullHere();
            for (var i:Int=0; i<M; i++)
            for (var j:Int=0; j<N; j++)
            if (A.placeOf(i,j)==here.id) {
                if(vandermonde) A_here(i, j) = pow(i+1, j+1);
                else A_here(i, j) = rand.nextDouble()*10;
            }
        }

        Console.OUT.println("Main loop starting ...");

        /* main loop */ 
        var t: long = -System.nanoTime();
        finish ateach (place: Point in unique) {
            val timer = new Timer();

            timer.start(0);
            /* panel factorization */
            panel_par(timer);

            if (reporting) {
                val s = dump();
                Console.OUT.println("after panel" + s);
            }

            /*  backsolve */
            backsolve();

            if (reporting) {
                val s = dump();
                Console.OUT.println("after backsolve" + s);
            }

            /* checking */
            var r: double = computeYDiff();

            timer.stop(0);

            if (here.id == 0) {
                Console.OUT.println ("difference " + r);
                Console.OUT.println(((r < 0.01?" ok)":" fail ") + " diff=" + r));
                Console.OUT.println ("Timer(0) TOTAL #invocations=" + timer.count(0) +
                  " Time=" + (timer.total(0) as double) / 1e9 + " seconds");
                Console.OUT.println ("Timer(1) PANEL #invocations=" + timer.count(1) +
                  " Time=" + (timer.total(1) as double)/1e9 + " seconds");
                Console.OUT.println ("Timer(2) SWAPROWS #invocations=" + timer.count(2) +
                  " Time=" + (timer.total(2) as double)/1e9 + " seconds");
                Console.OUT.println ("Timer(3) TRISOLVE #invocations=" + timer.count(3) +
                  " Time=" + (timer.total(3) as double)/1e9 + " seconds");
                Console.OUT.println ("Timer(4) UPDATE #invocations=" + timer.count(4) +
                  " Time=" + (timer.total(4) as double)/1e9 + " seconds");
                Console.OUT.println ("Timer(5) PANEL-PIVOT #invocations=" + timer.count(5) +
                  " Time=" + (timer.total(5) as double)/1e9 + " seconds");
                Console.OUT.println ("Timer(6) PANEL-SWAP #invocations=" + timer.count(6) +
                  " Time=" + (timer.total(6) as double)/1e9 + " seconds");
                Console.OUT.println ("Timer(7) PANEL-BCAST #invocations=" + timer.count(7) +
                  " Time=" + (timer.total(7) as double)/1e9 + " seconds");
                Console.OUT.println ("Timer(8) PANEL-UPDATE #invocations=" + timer.count(8) +
                  " Time=" + (timer.total(8) as double)/1e9 + " seconds");
              } 
        }
        t += System.nanoTime();

        Console.OUT.println();
        var flops: double = flops(N)/(t);
        Console.OUT.println(" Time= "+(t)/1e9 + " seconds" + " Rate= " + flops+ " GFlops");
    }

    def flops(var n: Int): double = { return ((4.0*n-3.0)*n-1.0)*n/6.0; }

    def initComms(val world: Comm): void = {
        var pi: int = here.id / py;
        var pj: int = here.id % py;
        state.get().col = world.split(pj, pi);
        state.get().row = world.split(pi, pj);
    }

    def col(): Comm = state.get().col;

    def row(): Comm = state.get().row;    
};
