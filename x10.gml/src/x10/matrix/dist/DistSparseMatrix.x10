/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.matrix.dist;

import x10.io.Console;
import x10.util.Random;
import x10.util.Timer;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.CompressArray;

import x10.matrix.block.Grid;
import x10.matrix.block.SparseBlock;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlockMatrix;

import x10.matrix.comm.CommHandle;


public type DistSparseMatrix(M:Int)=DistSparseMatrix{self.M==M};
public type DistSparseMatrix(M:Int,N:Int)=DistSparseMatrix{self.M==M, self.N==N};
public type DistSparseMatrix(C:Matrix)=DistSparseMatrix{self==C};
public type DistSparseMatrix(C:DistSparseMatrix)=DistSparseMatrix{self==C};


public class DistSparseMatrix(grid:Grid){grid.M==M,grid.N==N} extends Matrix/*(grid.M,grid.N)*/ {
	
	//==================================================================
	//public val grid:Grid; // in super
	public val dist:Dist(1);
	public val distBs:DistArray[SparseBlock](1);
	public var comm:CommHandle;
	//
	//==================================================================

	/**
	 * Construct Distributed sparse block matrix.
	 *
	 * @param  g      The sparse matrix partitioning grid
	 * @param dbs     The distributed sparse block array
	 */
	public def this(g:Grid, 
					dbs:DistArray[SparseBlock](1)
					):DistSparseMatrix(g.M,g.N){
		super(g.M, g.N);
		property(g);
		dist = dbs.dist;
		distBs = dbs;
		comm = new CommHandle();
		//
		//Debug.assure(dbs.dist.region.size() == g.size, 
		//			 "Partition sparse blocks and distribution region's size not match");
	}
	//-----------------
	// Each block is not allocated yet
	/**
	 * Constructor of distributed sparse-block matrix without data memory allocation.
	 *
	 * @param  g       block partitioning.
	 * @param  d       partition block distribution.
	 */
	public def this(g:Grid, d:Dist(1)):DistSparseMatrix(g.M,g.N) {
		super(g.M, g.N);
		property(g);
		dist   = d;
		comm = new CommHandle();
		distBs = DistArray.make[SparseBlock](d);
		//Debug.flushln("dist size:"+ d.region.size()+" grid size:"+ g.size);
		//Debug.assure(d.region.size() == g.size, 
		//			 "Partition block number and distribution region's size not match");		
	}

	// Each block is not allocated yet
	/**
	 * Construct Distributed sparse block matrix. No block data allocation.
	 *
	 * @param  g      The sparse matrix partitioning grid
	 */
	public def this(g:Grid):DistSparseMatrix(g.M,g.N) {
		super(g.M, g.N);
		property(g);
		comm = new CommHandle();
		dist   = Dist.makeUnique();
		Debug.assure(Place.numPlaces() == g.size,
					 "Partition size and block size do not match");
		distBs = DistArray.make[SparseBlock](dist);

	}

	//-----------------------------------------------------------------

	// Have memory allocated given Nonzero percentage

	/**
	 * Create distributed sparse matrix based on specified partition grid
	 * and nonzero sparsity. Every sparse block has the same nonzero density.
	 *
	 * @param  d        blocks partition distribution 
	 * @param  g        matrix partitioning grid
	 * @param  nzd     nonzero elements sparsity
	 */
	public static def make(g:Grid, d:Dist(1), nzd:Double) {
		val dsm:DistSparseMatrix(g.M, g.N) = new DistSparseMatrix(g, d);
		finish for(val [p] :Point in dsm.dist) {
			val r = dsm.grid.getRowBlockId(p);
			val c = dsm.grid.getColBlockId(p);
			at (dsm.dist(p)) async {
				dsm.distBs(p) = SparseBlock.make(dsm.grid, r, c, nzd);
			}
		}
		return dsm;
	}

	/**
	 * Create distributed sparse matrix based on specified partition grid
	 * and nonzero sparsity. Every sparse block has the same nonzero density.
	 *
	 * @param  d        blocks partition distribution 
	 * @param  nzd     nonzero elements sparsity
	 */
	public static def make(g:Grid, nzd:Double) =
		make(g,  Dist.makeUnique(), nzd);
	
	/**
	 * Create distributed sparse matrix based on specified number of rows and columns
	 * and nonzero sparsity. Every sparse block has the same nonzero density.
	 * 
	 * @param  m       matrix rows
	 * @param  n       matrix columns
	 * @param  nzd     nonzero elements sparsity
	 */
	public static def make(m:Int, n:Int, nzd:Double) =
		make(Grid.make(m, n, Place.MAX_PLACES), 
			 Dist.makeUnique(), nzd);

	/**
	 * Create dist sparse matrix using specified DistArray and partitioning
	 * 
	 * @param  gp     matrix paritinging
	 * @param  da     Distributed arrays in all placese
	 */
	public static def make(gp:Grid, da:DistArray[CompressArray](1)): DistSparseMatrix(gp.M,gp.N) {
		val ddb = DistArray.make[SparseBlock](da.dist);
		finish for(val [p] :Point in da.dist) {
			val rid = gp.getRowBlockId(p);
			val cid = gp.getColBlockId(p);
			val m   = gp.rowBs(rid);
			val n   = gp.colBs(cid);
			at (ddb.dist(p)) async {
				val sps = new SparseCSC(m, n, da(p));
				ddb(p) = new SparseBlock(rid, cid, sps);
			}
		}
		return new DistSparseMatrix(gp, ddb) ;		
	}
	
	/**
	 * Create dist sparse matrix using specified PlaceLocalHandle and partitioning
	 * 
	 * @param  gp     matrix paritinging
	 * @param  da     Distributed arrays in PlaceLocalHandle
	 */
	public static def make(gp:Grid, da:PlaceLocalHandle[CompressArray]): DistSparseMatrix(gp.M,gp.N) {
		val ddb = DistArray.make[SparseBlock](Dist.makeUnique());
		finish for(val [p] :Point in ddb.dist) {
			val rid = gp.getRowBlockId(p);
			val cid = gp.getColBlockId(p);
			val m   = gp.rowBs(rid);
			val n   = gp.colBs(cid);
			at (ddb.dist(p)) async {
				val sps = new SparseCSC(m, n, da());
				ddb(p) = new SparseBlock(rid, cid, sps);
			}
		}
		return new DistSparseMatrix(gp, ddb) ;		
	}

	//-----------------------------------------------------
	// Initialization
	//-----------------------------------------------------
	/**
	 * For testing purpose.
	 *
	 * <p> Create a distributed sparse matrix based on specified matrix dimension and 
	 * nonzero sparsity.
	 *
	 * @param m     number of rows
	 * @param n     number of columns
	 * @param nzd     nonzero sparsity 
	 */
	public static def makeRand(m:Int, n:Int, nzd:Double) {
		val g =  Grid.make(m, n, Place.MAX_PLACES);
		val d =  Dist.makeUnique();
		val dsm = make(g, d, nzd);
		dsm.initRandom(nzd);
		return dsm;
	}
	
	//-----------------------------
		
	/**
	 * For testing purpose.
	 *
	 * <p> Create a distributed sparse matrix based on specified partitioning and
	 * nonzero sparsity, and initialize the matrix with random values and nonzero
	 * elements at random positions.
	 *
	 * @param g     block partitioning
	 * @param nzd     nonzero sparsity for all distributed blocks
	 *
	 */
	public static def makeRand(g:Grid,  nzd:Double) {
		val d =  Dist.makeUnique();
		val dsm:DistSparseMatrix(g.M, g.N) = make(g, d, nzd);
		dsm.initRandom();
		return dsm;
	}

	//-------------------------------
	public static def make(rowbs:Array[Int](1){rail}, 
						   colbs:Array[Int](1){rail},
						   nzd:Double) {
		val g = new Grid(rowbs, colbs);
		return DistSparseMatrix.make(g, Dist.makeUnique(), nzd);
	}

	//-----------------------------------------------------------------
	/**
	 * For testing purpose.
	 *
	 * <p> Initialize data in the distributed sparse blocks with a constant value.
	 *
	 * @param   ival        The constant value
	 */
	public def init(ival:Double) : void {
		finish ateach (val [p] :Point in this.dist) {
			distBs(p).init(ival);
		}
	}

	/**
	 * For testing purpose.
	 *
	 * <p> Initialize data in distributed sparse blocks with random values,
	 * while using specified sparsity to compute the random value of distance
	 * between two adjacent nonzero element's row indexes.
	 *
	 * @param lb      lower bound of random value
	 * @param ub      upper bound of random value 
	 * @param sp     sparsity used for initialization
	 */
	public def initRandom(lb:Int, ub:Int, sp:Double) :void {
		finish ateach (val [p] :Point in this.dist) {
			distBs(p).sparse.initRandom(lb, ub, sp);
		}	
	}
	
	public def initRandom(sp:Double) :void {
		finish ateach (val [p] :Point in this.dist) {
			distBs(p).sparse.initRandom(sp);
		}	
	}
	/**
	 * For testing purpose.
	 *
	 * <p> Use the size of available storage space to compute the sparsity and 
	 * then use the sparsity to initialize data in distributed sparse blocks with 
	 * random values
	 *
	 * @param lb     lower bound of random value
	 * @param up     upper bound of random value
	 * @see initRandom(Double)
	 */
	public def initRandom(lb:Int, ub:Int) :void {
		finish ateach (val [p] :Point in this.dist) {
			distBs(p).sparse.initRandom(lb, ub);
		}	
	}

	public def initRandom() :void {
		finish ateach (val [p] :Point in this.dist) {
			distBs(p).sparse.initRandom();
		}	
	}

	//==================================================================
	// Matrix data allocation
	//==================================================================
	/**
	 * Clone distributed matrix, including all partitioned blocks.
	 */
	public  def clone():DistSparseMatrix(M,N) {
		val sbs  = DistArray.make[SparseBlock](this.dist);
		val dspa = new DistSparseMatrix(this.grid, sbs);

		finish ateach(val [p] :Point in this.dist) {
			sbs(p) = this.distBs(p).clone();
		}
		return dspa;
	}

	/**
	 * Allocate memory space for distributed sparse matrix.
	 *
	 */
	public def alloc(m:Int, n:Int, nzd:Double):DistSparseMatrix(m,n) {
		val g =  Grid.make(m, n, Place.MAX_PLACES);
		val nm = DistSparseMatrix.make(g, this.dist, nzd);
		return nm;
    }

	public def alloc(m:Int, n:Int)= alloc(m, n, compSparsity());	


	//-------------------------------------------------------
	//
	/**
	 * Copy data to dense matrix format at here.
	 */
	public def copyTo(dm:DenseMatrix(M,N)):void {
		val sbm = SparseBlockMatrix.make(grid, compSparsity());
		
		copyTo(sbm);
		sbm.copyTo(dm);
	}

	/**
	 * Copy data to distributed dense matrix in same distribution
	 * and data partitioning.
	 *
	 * @param ddm      distributed dense matrix
	 */
	public def copyTo(ddm:DistDenseMatrix(M,N)): void {
		Debug.assure(grid.equals(ddm.grid), "Partitioning mismatch");

		finish ateach(val [p] :Point in this.dist) {
			val dd = ddm.distBs(p).dense;
			val ss = distBs(p).sparse;
			ss.copyTo(dd as DenseMatrix(ss.M, ss.N));
		} 
	}

	/**
	 * Copy data from distributed to here in sparse block matrix 
	 *
	 * @param sbm      sparse block matrix
	 */
	public def copyTo(sbm:SparseBlockMatrix(M,N)): void {
		Debug.assure(grid.equals(sbm.grid), "Partitioning mismatch");
		
		comm.gather(this.distBs, sbm.listBs);
	}


	public def copyTo(spm:SparseCSC(M,N)):void {
		Debug.assure(grid.numRowBlocks==1);
		comm.gatherRowBs(grid, this.distBs, spm);
	}
    
	//public def copyTo(dm:DenseMatrix(M,N)): void {
		// val sbm:SparseBlockMatrix=SparseBlockMatrix(grid, nzdensity);
		// copyTo(sbm);

	//}

	/**
	 * Copy data from sparse block matrix at here to dist block sparse matrix
	 * 
	 * @param sbm      block sparse matrix
	 */
	public def copyFrom(sbm:SparseBlockMatrix(M,N)):void {
		Debug.assure(grid.equals(sbm.grid),	"block partitioning mismatch");

		/* Timing */ val stt = Timer.milliTime();
		comm.scatter(sbm.listBs, distBs);
	 	/* Timing */ distBs(here.id()).commTime += Timer.milliTime() - stt;
	}
	
	/**
	 * Copy data from sparse matrix at here to dist block sparse matrix in single row 
	 * block partitioning
	 * 
	 * @param spa      source sparse matrix
	 */
	public def copyFrom(spa:SparseCSC(M,N)):void {
		Debug.assure(grid.numRowBlocks==1,	"Source matrix is not single row block partitioning");

		/* Timing */ val stt = Timer.milliTime();
		comm.scatterRowBs(grid, spa, distBs);
		/* Timing */ distBs(here.id()).commTime += Timer.milliTime() - stt;
	}	
	
	//================================================================
	// Block access methods 
	//================================================================	
	// Copy remote block to local
	public def getBlock(i:Int):SparseBlock {
		val sb = at(this.distBs.dist(i)) this.distBs(here.id());
		return sb;
	}
	//--------
	public def getBlock(rp:Int, cp:Int): SparseBlock {
		val bid = this.grid.getBlockId(rp, cp);
		return getBlock(bid);
	}
	//--------
	public def setBlock(i:Int, dm:SparseCSC) : void {
		val r = grid.getRowBlockId(i);
		val c = grid.getColBlockId(i);
		at (this.distBs.dist(i)) {
			distBs(here.id()) = new SparseBlock(r, c, dm);
		}
	}

	/**
	 * Return the local portion of matrix at here
	 */
	public def local() <: SparseCSC = this.distBs(here.id()).getMatrix();

    
	public def getMatrix(p:Int) <: SparseCSC = this.distBs(p).getMatrix();
	
	//================================================================
	// Data access methods 
	//================================================================	
	
	/**
	 * Access data at (x,y). Override the super class method for better performance
	 */
    public operator this(x:Int, y:Int):Double {
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		val dv = at (this.distBs.dist(bid)) this.local()(loc(2), loc(3));
		
		return dv;
	}

	/**
	 * Set data value v to position at (x, y). (x, y) is absolute
	 * coordination in the matrix.
	 */
	public operator this(x:Int,y:Int)=(v:Double):Double {
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		
		at (this.distBs.dist(bid)) {
			this.local()(loc(2), loc(3))=v;
		}
		return v;
	}
	//
	public  def reset():void {
		finish ateach (val [p]:Point  in this.dist) {
			local().reset();
		}
	}

	/**
	 * Check type and parition is same or not
	 *
	 * @param A     checking matrix
	 * @return      true if type and partition are same
	 */
	public def likeMe(A:Matrix):Boolean =
	    ((A instanceof DistSparseMatrix) &&
		 ((A as DistSparseMatrix).grid.equals(grid)) &&
		 ((A as DistSparseMatrix).dist.equals(dist)));


	//=====================================================================
	// Cellwise operation
	//=====================================================================
	/**
	 * Scaling method
	 */
 	public def scale(a:Double) {
		finish ateach(val [p] :Point in this.dist) {
			local().scale(a);
		}		
		return this;
    }

    /**
     * Scaling operation return this * double
     */
    public operator this * (dblv:Double):DistSparseMatrix(M,N) {
        val x = clone();
        x.scale(dblv);
        return x;
    }
    /**
     * Scaling operation return this * integer
     */
    public operator this * (intv:Int):DistSparseMatrix(M,N) {
        val x = clone();
        x.scale(intv as Double);
        return x;
    }
	public operator (dblv:Double) * this = this * dblv;
	public operator (intv:Int) * this = this * intv;

	//-----------------------------
	// Cellwise addition
	//-----------------------------
	/**
	 * Cellwise addition in DistSparseMatrix is not supported and
	 */	
	public def cellAdd(A:Matrix(M,N))  {
	    if (! likeMe(A))
	        throw new UnsupportedOperationException();
	    cellAdd(A as DistSparseMatrix(M,N));
	    return this;
	}

	/**
	 * Cellwise addition in DistSparseMatrix is not supported and
	 */
	public def cellAdd(A:DistSparseMatrix(M,N))  {
		Debug.exit("No implementation of cellwise add-in for DistSparseMatrix");
		return this;
	}

	public def cellAdd(d:Double)  {
		Debug.exit("No implementation of cellwise add-in for DistSparseMatrix");
		return this;
	}

	protected def cellAddTo(dst:DenseMatrix(M,N)) {
		Debug.exit("Not available");
		return dst;
	}
	//-----------------------------
	// Cellwise subtraction
	//-----------------------------
	/**
	 * Cellwise subtraction in DistSparseMatrix is not supported and
	 */
	public def cellSub(A:DistSparseMatrix(M,N))  {
		Debug.exit("No implementation of cellwise sub-in for DistSparseMatrix");
		return this;
	}

	/**
	 * Cellwise subtraction in DistSparseMatrix is not supported and
	 */
	public def cellSub(x:Matrix(M,N)) {
	    if (! likeMe(x))
	        throw new UnsupportedOperationException();
	    return cellSub(x as DistSparseMatrix(M,N));
	}

	/**
	 * Perform cell-wise subtraction  x = x - this.
	 */
	protected def cellSubFrom(x:DenseMatrix(M,N)) {
		Debug.exit("Not available");
		return x;
	}

	//-----------------------------
	// Cellwise multiplication
	//-----------------------------

	/**
	 * Cellwise multiplication in DistSparseMatrix is not supported
	 */
	public def cellMult(A:DistSparseMatrix(M,N))  {
		Debug.exit("No implementation of cellwise mult-in for DistSparseMatrix");
		return this;
	}

	/**
	 * Cellwise multiplication in DistSparseMatrix is not supported and
	 */
	public def cellMult(A:Matrix(M,N))  {
	    if (! likeMe(A))
	        throw new UnsupportedOperationException();
	    cellMult(A as DistSparseMatrix(M,N));
	    return this;
	}

	protected def cellMultTo(dst:DenseMatrix(M,N)) {
		Debug.exit("Not available");
		return dst;
	}

	//-----------------------------
	// Cellwise division
	//-----------------------------

	/**
	 * Cellwise division in DistSparseMatrix is not supported and
	 */
	public def cellDiv(A:DistSparseMatrix(M,N))   {
		Debug.exit("No implementation of cellwise div-in for DistSparseMatrix");
		return this;
	}


	/**
	 * Cellwise division in DistSparseMatrix is not supported and
	 */
	public def cellDiv(A:Matrix(M,N)) {
	    if (! likeMe(A))
	        throw new UnsupportedOperationException();
		cellDiv(A as DistSparseMatrix(M,N));
		return this;
	}

	/**
	 * Perform cellwise return x = this / x 
	 */
	protected def cellDivBy(x:DenseMatrix(M,N)) {
		Debug.exit("Not available");
		return x;
	}

	//====================================================================
	// Multiplication
	//====================================================================
	/**
	 * Mult method is not supported
	 */
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):Matrix(this){
		
		 throw new UnsupportedOperationException("Not supported, no implementation");
	}	
 
	/**
	 * Not supported
	 */
	public def transMult(
			A:Matrix{self.N==this.M},
			B:Matrix(A.M,this.N), 
			plus:Boolean) :Matrix(this) {
		 throw new UnsupportedOperationException("Not supported, no implementation");
    }

	/**
	 * Not supported
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):DistSparseMatrix(this) {
		
		 throw new UnsupportedOperationException("Not supported, no implementation");
    }

	//----------------------------------------------------------------------------
	/**
	 * Not supported
	 */
    public def mult(A:DistSparseMatrix, B:DistSparseMatrix, plus:Boolean): Matrix(this) {
		// size check
		Debug.exit("Not support using DistSparseMatrix to store DistSparseMatrix multplication result");
		return this;
	}

	//==================================================================
	// Profiling
	//==================================================================
	public def getCalcTime():Long = this.distBs(here.id()).calcTime;
	public def getCommTime():Long = this.distBs(here.id()).commTime;

	//==================================================================
	/**
	 * Return the average sparisty of all distributed blocks.
	 */
	public def compSparsity():Double = 1.0 * countNonZero()/M/N;

	/**
	 * Compute total number of nonzero in all blocks
	 */
	public def countNonZero():Long {
		var tnz:Long = 0;
		for (val [p]:Point in this.dist) {
			tnz += at(distBs.dist(p)) 
				this.distBs(p).sparse.countNonZero();
		}
		return tnz;
	}
	public def getTotalNonZeroCount():Long = countNonZero();
	
	//
	public def getColNonZeroCount(col:Int):Long {
		var tnz:Long = 0;
		for (val [p]:Point in this.dist) {
			val c = grid.getColBlockId(p);
			if (c == col) {
				tnz += at(distBs.dist(p)) 
					this.getMatrix(p).getColNonZeroCount(col);
			}
		}
		return tnz;
	}
	//
	public def getAvgColumnSize() = 1.0*this.countNonZero() / this.N;
	
	public def getColumnSizeStdDvn() : Double {
		var stdd:Double = 0;
		val avg:Double  = this.getAvgColumnSize();
	    for (var c:Int=0; c<this.N; c++) {
			val d = getColNonZeroCount(c) - avg;
			stdd += d*d;
		}
		return Math.sqrt(stdd/this.N);
	}
	//==================================================================
	// Util
	//==================================================================
	public def toString() :String {
		var output:String = "-------- Dist SparseCSC size:["+M+"x"+N+"] ---------\n";
		for (val [p]:Point in this.dist) {
			output += "At place " + p +": \n";
			output += at (distBs.dist(p)) { 
				this.distBs(p).toString() };
		}
		output += "--------------------------------------------------\n";
		return output;
	}
	//
	public def print() { print("");}
	public def print(msg:String) {
		Console.OUT.print(msg);
		Console.OUT.print(this.toString());
		Console.OUT.flush();
	}
	//
	public def debugPrint() { debugPrint("");}
	public def debugPrint(msg:String) {
		if (Debug.disable) return;
		val dbstr:String = msg+ dataToString();
		Debug.println(dbstr);
		Debug.flush();
	}
	
	public def printBlockMemAlloc() : void {
		var memsz:Int;
		var output:String="";
		for (val [p]:Point in this.dist) {
			memsz = at (distBs.dist(p)) { 
				this.distBs(p).sparse.getStorageSize() };
			val stsz:Double = 8.0 * memsz /1024/1024;	
			output += "At place " + p +" mem alloc:"+stsz+" MB\n";
		}
		Debug.flushln(output);
	}
	
	public def printRandomInfo(): void {
		val nzc = countNonZero();
		val nzd = compSparsity();
		val avgdst = compAvgIndexDst();
		val stdlnz = getColumnSizeStdDvn();//compLineSizeStdDvn();
		Console.OUT.println("Dist sparse matrix ["+M+","+N+"] nz count:"+nzc+" sparsity:"+nzd);
		Console.OUT.println("Mean adjacent nonzero index distance: " + avgdst);
		Console.OUT.println("Compressed column nonzero size std deviation:"+stdlnz);
		Console.OUT.flush();		
	}
	//=================================================================
	// Following methods are used to check the random generation of indexes
	public def printBlockColumnSizeAvgStd():void {

		finish ateach (val [p]:Point in this.dist) {
			val lavg = getMatrix(p).ccdata.compAvgLineSize();
			val lstd = getMatrix(p).ccdata.compLineSizeStdDvn();
			Console.OUT.println("Sparse block "+p+" avg:"+lavg+" std:"+lstd);
			Console.OUT.flush();
		}
	}
	//
	public def compAvgIndexDst():Double {
		var ad:Double = 0;
		for (val [p]:Point in this.dist) {
			ad += at(distBs.dist(p))
				this.getMatrix(p).ccdata.compAvgIndexDst();
		}
		return ad / this.grid.size ;
	}
	//
	public def compIndexDstStdDvn() : Double {
		var tdd:Double = 0.0;
		var cnt:Int = 0;
		val avg:Double = compAvgIndexDst();
		for (val [p]:Point in this.dist) {
			tdd += at(distBs.dist(p))
				this.getMatrix(p).ccdata.compIndexDstSumDvn(avg);
			cnt += at(distBs.dist(p)) 
				this.getMatrix(p).ccdata.getIndexDstCnt();
		}
		return x10.lang.Math.sqrt(tdd/cnt);
	}
	//====================================================================

}
