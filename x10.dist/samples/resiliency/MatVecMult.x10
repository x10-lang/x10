import x10.io.Console;
import x10.io.*;
import x10.util.*;
import x10.compiler.*;

public class MatVecMult {

    // A square matrix block in the CSC format, see wiki:
    // https://en.wikipedia.org/wiki/Sparse_matrix#Compressed_sparse_column_.28CSC_or_CCS.29
    // This is a slight modification, written to match the M3R CSCBlock code.
    // The idea is to not waste storage on empty columns, by introducing another array.
    public static final class MatrixBlockCSC {

        // size of the matrix (in elements)
        public val matrixSize : Int;

        // coordinates of the block, within the matrix
        public val blockRow : Int, blockCol : Int;

        // size of the block
        public val size : Int;

        // Each non-zero element, in column-major order
        public val values : Rail[Double];

        // The corresponding row of each element of values, i.e. if M(c,r) == values[i] then valueRows[i] == r
        public val valueRows : Rail[Int];

        // The column index of each non-zero column (if every column has at least one non-zero, this is 0, 1, 2, .. (size-1))
        public val colIndexes : Rail[Int];
        
        // The number of non-zero elements in the column
        public val colSizes : Rail[Int];

        public def this(matrixSize:Int, blockRow:Int, blockCol:Int, size:Int, sparsity:Float) {
            val rand = new Random(0);

            val values = new ArrayList[Double]();
            val valueRows = new ArrayList[Int]();
            val colIndexes = new ArrayList[Int]();
            val colSizes = new ArrayList[Int]();

            for (col in 0n..(size-1n)) {
                var col_size:Int = 0n;
                for (row in 0n..(size-1n)) {
                    if (rand.nextFloat() < sparsity) {
                        val v = rand.nextDouble();
                        values.add(v);
                        valueRows.add(row);
                        col_size++;
                    }
                }
                if (col_size > 0) {
                    colSizes.add(col_size);
                    colIndexes.add(col);
                }
            }

            this.matrixSize = matrixSize;
            this.blockRow = blockRow;
            this.blockCol = blockCol;
            this.size = size;
            this.values = values.toRail();
            this.valueRows = valueRows.toRail();
            this.colIndexes = colIndexes.toRail();
            this.colSizes = colSizes.toRail();
        }

        public def this(fr:FileReader) {

            val MH = fr.readInt();
            val MW = fr.readInt();
            assert MW == MH : "("+MW+","+MH+")";
            assert MW > 0 : MW;
            this.matrixSize = MW;

            val H = fr.readInt();
            val W = fr.readInt();
            assert W == H : "("+W+","+H+")";
            assert W > 0 : W;
            this.size = W;

            this.blockRow = fr.readInt();
            assert this.blockRow < this.matrixSize : this.blockRow + "," + this.matrixSize;
            this.blockCol = fr.readInt();
            assert this.blockCol < this.matrixSize : this.blockCol + "," + this.matrixSize;

            val nz = fr.readInt();
            val nc = fr.readInt();

            assert nc <= this.size : nc;

            this.colIndexes = new Rail[Int](nc);
            for (i in 0..(nc-1)) {
                this.colIndexes(i) = fr.readInt();
                assert this.colIndexes(i) < this.size : "this.colIndexes("+i+") = "+this.colIndexes(i);
            }

            this.colSizes = new Rail[Int](nc);
            for (i in 0..(nc-1)) {
                this.colSizes(i) = fr.readInt();
                assert this.colSizes(i) < this.size : "this.colSizes("+i+") = "+this.colSizes(i);
            }

            assert nz <= this.size * this.size : nz;

            this.valueRows = new Rail[Int](nz);
            for (i in 0..(nz-1)) {
                this.valueRows(i) = fr.readInt();
                assert this.valueRows(i) < this.size : "this.valueRows("+i+") = "+this.valueRows(i);
            }

            this.values = new Rail[Double](nz);
            for (i in 0..(nz-1)) {
                this.values(i) = fr.readDouble();
            }
        }

        public def print() {
            var offset : Int = 0n;
            Console.OUT.println("Matrix block (size "+size+","+size+") is ("+blockCol+","+blockRow+") of ("+matrixSize+","+matrixSize+")");
            for (nz_col_index in colIndexes.range()) {
                val col = colIndexes(nz_col_index);
                val col_size = colSizes(nz_col_index);
                for (col_el in 1..col_size) {
                    val v = values(offset);
                    val row = valueRows(offset);
                    offset++;
                    Console.OUT.println("    M("+col+","+row+") = "+v);
                }
            }
        }

        public def size() = size;
    }

    public static class VectorBlockDense {
        val vectorSize : Int;
        val vectorBlockOffset : Int;
        val values : Rail[Double];
        public def this (vectorSize:Int, vectorBlockOffset:Int, size:Int) {
            this.vectorSize = vectorSize;
            this.vectorBlockOffset = vectorBlockOffset;
            val rand = new Random(0);
            values = new Rail[Double](size);
            for (row in values.range()) {
                values(row) = rand.nextDouble();
            }
        }
        public def this (vectorSize:Int, vectorBlockOffset:Int, size:Int, v:Double) {
            this.vectorSize = vectorSize;
            this.vectorBlockOffset = vectorBlockOffset;
            values = new Rail[Double](size, v);
        }

        public def setAll (v:Double) {
            for (i in values.range()) values(i) = v;
        }

        public def this(fr:FileReader) {

            val MH = fr.readInt();
            val MW = fr.readInt();
            assert 1n == MW : MW;
            assert MH > 0n : MH;
            this.vectorSize = MH;

            val H = fr.readInt();
            val W = fr.readInt();
            assert W == 1n : W;
            assert H > 0n : W;
            val size = H;

            this.vectorBlockOffset = fr.readInt();
            assert this.vectorBlockOffset < this.vectorSize : this.vectorBlockOffset + "," + this.vectorSize;
            val block_col = fr.readInt();
            assert block_col == 0n : block_col;

            val redundant = fr.readInt();
            assert redundant == size : redundant + "," + size;

            this.values = new Rail[Double](size);
            for (i in 0n..(size-1n)) {
                this.values(i) = fr.readDouble();
            }
        }

        public def print() {
            Console.OUT.println("Vector block (size "+values.size+") is "+vectorBlockOffset+" of "+vectorSize+")");
            for (row in values.range()) {
                val v = values(row);
                Console.OUT.println("    V("+row+") = "+v);
            }
        }
        public def multiplyIn(M:MatrixBlockCSC, V:VectorBlockDense) {
            assert V.size() == M.size();
            assert V.size() == this.size();
            var offset : Int = 0n;
            for (nz_col_index in M.colIndexes.range()) {
                val col = M.colIndexes(nz_col_index);
                val col_size = M.colSizes(nz_col_index);
                for (col_el in 1n..col_size) {
                    val v = M.values(offset);
                    val row = M.valueRows(offset);
                    offset++;
                    this.values(row) += v * V(col);
                }
            }
        }
        public def addIn(V:VectorBlockDense) {
            assert V.size() == this.size();
            for (i in values.range()) values(i) += V.values(i);
        }
        public operator this(row:Int) = values(row);
        public def size() : Int = values.size as Int;

        public def writeToDisk(fw:FileWriter) {

            fw.writeInt(vectorSize); // MH
            fw.writeInt(1n); // MW

            fw.writeInt(values.size as Int); // H
            fw.writeInt(1n); // W

            fw.writeInt(vectorBlockOffset); // height offset
            fw.writeInt(0n); // width offset

            fw.writeInt(values.size as Int); // nonzeros

            for (i in values.range()) {
                fw.writeDouble(values(i)); // data
            }
        }

    }

    
    public static class Config {
        var gName : String;
        var vName : String;
        var outName : String;
        var matrixSize : Int;
        var blockSize : Int;
        var numBlocks : Int;
        var numSplits : Int;
        var iterations : Int;
        var verbose : Boolean;
        var quiet : Boolean;
        var killTest : Int;
    }

    public static interface Task {
        public def isPlaceActive(p:Place) : Boolean;
    }

    static def child1(n:Long) = 2*n+1;
    static def child2(n:Long) = 2*n+2;

    static def launchTree[T](p:Long, plh:PlaceLocalHandle[T], cl:()=>void) { T <: Task, T isref } {
        if (p >= Place.numPlaces()) return;
        if (plh().isPlaceActive(Place(p))) {
            at (Place(p)) async {
                launchTree[T](child1(p), plh, cl);
                launchTree[T](child2(p), plh, cl);
                //Console.OUT.println("++++ Beginning work at "+here);
                cl();
                //Console.OUT.println("---- Ending work at "+here);
            }
        } else {
            launchTree[T](child1(p), plh, cl);
            launchTree[T](child2(p), plh, cl);
        }
    }

    public static class PerPlaceState implements Task {

        public cfg : Config;
        private matrixSplits : Rail[Rail[MatrixBlockCSC]];
        private var splits : ArrayList[Long];
        private var activePlaces : Rail[Boolean];
        
        def this(cfg : Config) {
            this.cfg = cfg;
            this.matrixSplits = new Rail[Rail[MatrixBlockCSC]](cfg.numSplits); // filled in with setSplits
        }

        public def isPlaceActive(p:Place) = activePlaces(p.id);

        def setSplits (splits:ArrayList[Long], active_places:Rail[Boolean]) {
            this.splits = splits;
            this.activePlaces = active_places;
            var after : Long, before : Long;
            before = System.nanoTime();
            for (split in splits) {
                if (matrixSplits(split) == null) {
                    if (cfg.verbose)
                        Console.OUT.println("Read in matrix part "+split+" at "+here);
                    matrixSplits(split) = readMatrixSplit(cfg.gName+"/part-"+String.format("%05d",[split as Any]));
                }
            }
            after = System.nanoTime();
            if (cfg.verbose)
                Console.OUT.println("Read in matrix time (place "+here+"): "+(after-before)/1E9+" seconds");
        }

        def calcNewVector (srcVectorBlocks:Rail[VectorBlockDense]) {
            var after : Long, before : Long;

            before = System.nanoTime();
            val dst_vector_blocks = new Rail[VectorBlockDense](cfg.numBlocks);
            var count : Long = 0;
            for (split_index in splits) {
                for (g_block in matrixSplits(split_index)) {
                    val v_src_block = srcVectorBlocks(g_block.blockCol / cfg.blockSize);
                    var v_dst_block : VectorBlockDense = dst_vector_blocks(g_block.blockRow / cfg.blockSize);
                    if (v_dst_block == null) {
                        v_dst_block = new VectorBlockDense(cfg.matrixSize, g_block.blockRow, cfg.blockSize, 0.0);
                        dst_vector_blocks(g_block.blockRow / cfg.blockSize) = v_dst_block;
                    }
                    v_dst_block.multiplyIn(g_block, v_src_block);
                    count++;
                    if (count%30 == 0) Runtime.probe();
                }
            }
            after = System.nanoTime();
            if (cfg.verbose)
                Console.OUT.println("Iteration at "+here+" processed "+count+" blocks, took "+(after-before)/1E9+" seconds");
            return dst_vector_blocks;
        }
    }

    public static def main (args : Rail[String]) {here==Place.FIRST_PLACE} {
        val opts = new OptionsParser(args, [
            Option("q","quiet","just print time taken"),
            Option("v","verbose","print out each iteration"),
            Option("h","help","this information")
        ], [
            Option("i","iterations","number of iterations"),
            Option("k","kill","kill place 1 at this iteration"),
            Option("G","inputMatrix","location of input directory"),
            Option("V","inputVector","location of input directory"),
            Option("o","outVector","location of output file"),
            Option("b","blockSize","size of block"),
            Option("n","size","size of matrix"),
            Option("s","splits","number of files inputs are split into")
        ]);
        if (opts.filteredArgs().size!=0) {
            Console.ERR.println("Unexpected arguments: "+opts.filteredArgs());
            Console.ERR.println("Use -h or --help.");
            System.setExitCode(1n);
            return;
        }
        if (opts("-h")) {
            Console.OUT.println(opts.usage(""));
            return;
        }

        val cfg = new Config();

        cfg.gName = opts("-G", "G");
        cfg.vName = opts("-V", "V");
        cfg.outName = opts("-o", "v.out");
        cfg.matrixSize = opts("-n", 100000n);
        cfg.blockSize = opts("-b", 1000n);
        cfg.numBlocks = cfg.matrixSize / cfg.blockSize;
        cfg.iterations = opts("-i",50n);
        cfg.verbose = opts("-v");
        cfg.quiet = opts("-q");
        cfg.killTest = opts("-k", -1n);
        cfg.numSplits = opts("-s", 100n);

        if (cfg.matrixSize % cfg.blockSize != 0n) {
            Console.ERR.println("ERROR: size must be a multiple of blockSize");
            System.setExitCode(1n);
            return;
        }
            
        Console.OUT.println("MatVecMult started.  Loading matrix...");

        val before_all = System.nanoTime();

        var before:Long=0, after:Long=0; // for timings

        val plh = PlaceLocalHandle.make[PerPlaceState](Place.places(), ()=>new PerPlaceState(cfg));

        var need_set_splits : Boolean = true;

        // initial task assignment -- assumes no places are dead yet
        val splits_assignments = new Rail[ArrayList[Long]](Place.numPlaces(), new ArrayList[Long]());
        val active_places = new Rail[Boolean](Place.numPlaces(), true);
        var last_end : Long = 0L;
        for (i in active_places.range()) {
            val splits = new ArrayList[Long]();
            val num_assigned_splits = (cfg.numSplits+i)/Place.numPlaces();
            val start = last_end;
            val end = last_end + num_assigned_splits;
            for (split in start..(end-1)) splits.add(split);
            splits_assignments(i) = splits;
            last_end = last_end + num_assigned_splits;
        }
            

        var vector_blocks : Rail[VectorBlockDense] = new Rail[VectorBlockDense](cfg.numBlocks);
        before = System.nanoTime();
        for (split_index in 0..(cfg.numSplits-1)) {
            val split = readVectorSplit(cfg.vName+"/part-"+String.format("%05d",[split_index as Any]));
            for (vect in split) {
                vector_blocks(vect.vectorBlockOffset / cfg.blockSize) = vect;
            }
        }
        for (i in vector_blocks.range()) {
            if (vector_blocks(i) == null) {
                Console.ERR.println("ERROR: While reading input vector, no block at index "+i);
                System.setExitCode(1n);
                return;
            }
        }
        after = System.nanoTime();
        Console.OUT.println("Read in vector time: "+(after-before)/1E9+" seconds");

        var recover_place : Long = 0;

        for (var iter:Int=0n ; iter<cfg.iterations ; ++iter) {
            val dst_vector_blocks = new Rail[VectorBlockDense](cfg.numBlocks, (i:Long)=>new VectorBlockDense(cfg.matrixSize, (i as Int)*cfg.blockSize, cfg.blockSize, 0.0));
            val dst_vector_blocks_gr = GlobalRef[Rail[VectorBlockDense]](dst_vector_blocks);
            val src_vector_blocks = vector_blocks;
            try {
                if (need_set_splits) {
                    before = System.nanoTime();
                    finish for (i in active_places.range()) {
                        if (!active_places(i)) continue;
                        val splits = splits_assignments(i);
                        at (Place(i)) async plh().setSplits(splits, active_places);
                    }
                    after = System.nanoTime();
                    Console.OUT.println("Combined read in matrix time: "+(after-before)/1E9+" seconds");
                }
                need_set_splits = false;
                before = System.nanoTime();
                val iter_ = iter;
                finish launchTree[PerPlaceState](0, plh, () => {
                    val state = plh();
                    if (here.id==1 && state.cfg.killTest==iter_) System.killHere();
                    val local_dst_vector_blocks = state.calcNewVector(src_vector_blocks);
                    val block_size = state.cfg.blockSize;
                    at (Place.FIRST_PLACE) async {
                        for (blk in local_dst_vector_blocks) {
                            if (blk == null) continue;
                            dst_vector_blocks_gr()(blk.vectorBlockOffset / block_size).addIn(blk);
                        }
                    }
                });
                after = System.nanoTime();
                Console.OUT.println("Iteration ("+iter+") time: "+(after-before)/1E9+" seconds");
                vector_blocks = dst_vector_blocks_gr();
            } catch (e:MultipleExceptions) {
                val filtered = e.filterExceptionsOfType[DeadPlaceException]();
                if (filtered != null) throw filtered;
                val deadPlaceExceptions = e.getExceptionsOfType[DeadPlaceException]();
                for (dpe in deadPlaceExceptions) {
                    val dead_place = dpe.place;
                    if (!active_places(dead_place.id)) continue; // this can happen if we get more than one DPE for a given place
                    active_places(dead_place.id) = false;
                    Console.OUT.println("Place died: "+dead_place+", reassigning work to the remaining places...");
                    val lost_splits = splits_assignments(dead_place.id);
                    splits_assignments(dead_place.id) = null;

                    // reassign lost splits
                    for (split in lost_splits) {
                        // find next place with
                        while (!active_places(recover_place)) recover_place = (recover_place+1) % Place.numPlaces();
                        if (cfg.verbose) {
                            Console.OUT.println("Place "+recover_place+" now gets split: "+split);
                        }
                        splits_assignments(recover_place).add(split);
                        recover_place = (recover_place+1) % Place.numPlaces();
                    }

                    need_set_splits = true;
                }
                Console.OUT.println("Will now redo iteration ("+iter+").");
                iter--; // redo last iteration
            }
        }
        
        before = System.nanoTime();
        writeVector(vector_blocks, cfg.outName);
        after = System.nanoTime();
        Console.OUT.println("Write out vector time: "+(after-before)/1E9+" seconds");

        val after_all = System.nanoTime();

        Console.OUT.println("Total time: "+(after_all-before_all)/1E9+" seconds");
    }

    static def readMatrixSplit(name:String) : Rail[MatrixBlockCSC] {
        val r = new GrowableRail[MatrixBlockCSC]();
        val file = new File(name);
        val fr = file.openRead();
        val magic = fr.readInt();
        if (magic != 0xdc00dc00n) throw new Exception("File does not look like a sparse matrix block: "+name);
        while (true) {
            try {
                val magic2 = fr.readInt();
                if (magic2 != 0xdcdcdcdcn) throw new Exception("File corrupted midway through: "+name);
            } catch (e:EOFException) { break; }
            r.add(new MatrixBlockCSC(fr));
            if (r.size() % 10 == 0) Runtime.probe();
        }
        return r.toRail();
    }
    static def readVectorSplit(name:String) : Rail[VectorBlockDense] {
        val r = new GrowableRail[VectorBlockDense]();
        val file = new File(name);
        val fr = file.openRead();
        val magic = fr.readInt();
        if (magic != 0xdc00dc00n) throw new Exception("File does not look like a dense vector block: "+name);
        while (true) {
            try {
                val magic2 = fr.readInt();
                if (magic2 != 0xdcdcdcdcn) throw new Exception("File corrupted midway through: "+name);
            } catch (e:EOFException) { break; }
            r.add(new VectorBlockDense(fr));
        }
        return r.toRail();
    }
    static def writeVector(vector: Rail[VectorBlockDense], name:String) {
        val file = new File(name);
        val fw = file.openWrite();
        fw.writeInt(0xdc00dc00n);
        for (vblk in vector) {
            fw.writeInt(0xdcdcdcdcn);
            vblk.writeToDisk(fw);
        }
    }



}

// vim: shiftwidth=4:tabstop=4:expandtab

