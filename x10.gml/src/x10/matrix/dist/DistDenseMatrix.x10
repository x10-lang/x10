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

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Uninitialized;

import x10.io.Console;
import x10.util.Random;
import x10.util.Timer;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.VerifyTools;

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.dist.summa.SummaDense;
import x10.matrix.dist.summa.SummaDenseMultSparse;
import x10.matrix.dist.summa.SummaSparseMultDense;
import x10.matrix.dist.summa.SummaSparse;

import x10.matrix.comm.CommHandle;


public type DistDenseMatrix(M:Int,N:Int)=DistDenseMatrix{self.M==M, self.N==N};
public type DistDenseMatrix(M:Int)=DistDenseMatrix{self.M==M};
public type DistDenseMatrix(C:Matrix)=DistDenseMatrix{self==C};

/**
 * This class provides distributed dense block matrix over all places.
 * Current implementation supports unique mapping only. 
 * 
 * <p> A partitioning grid is used to map place id to the pair of row block id 
 * and column block id in the 2D partition.
 */
public class DistDenseMatrix(grid:Grid){grid.M==M,grid.N==N} extends Matrix {

	//==================================================================

	public val dist:Dist(1);
	public val distBs:DistArray[DenseBlock](1);
  
    //protected var isTransposed:Boolean=false;
	//public def isTransposed() = isTransposed;

	public var comm:CommHandle;
	
	//
	//==================================================================
	/**
	 * Construct distributed dense matrix using specified matrix partitioning. 
	 * The block distribution is unique.
	 *
	 * @param  g      matrix partitioning
	 * @param dbs     distributed dense block array
	 */
	public def this(g:Grid, dbs:DistArray[DenseBlock](1)) {
		super(g.M, g.N);
		property(g);
		dist = dbs.dist;	
		distBs = dbs;
		comm = new CommHandle();
		//Debug.assure(dbs.dist.region.size() == g.size, 
		//			 "Partition block number and distribution region's size not match");		
		//Removing partition size = distribution size constrain 
		//Allowing multiple blocks map to the same place.
		//TODO: change all iteration methods.
	}
	//=============================================================
	// Instance makers
	//=============================================================
	/**
	 * Create distributed dense matrix using specified matrix partitioning and
	 * block distribution
	 *
	 * @param  g  grid partitioning
	 * @param  d  dense block array distribution
	 */
	public static def make(g:Grid, d:Dist(1)):DistDenseMatrix(g.M, g.N) {

		val ddb = DistArray.make[DenseBlock](d);
		finish for(val [p] :Point in ddb.dist) {
			val rid = g.getRowBlockId(p);
			val cid = g.getColBlockId(p);
			val m   = g.rowBs(rid);
			val n   = g.colBs(cid);
			at (ddb.dist(p)) async {
				ddb(p) = DenseBlock.make(rid, cid, m, n);
			}
		}
		return new DistDenseMatrix(g, ddb) ;
	}

	/**
	 * Create distributed dense matrix using specified matrix partitioning
	 *
	 * @param  g  matrix partitioning
	 */
	public static def make(g:Grid) : DistDenseMatrix(g.M,g.N) {
		val d   = Dist.makeUnique();
		return make(g, d);
	}

	/**
	 * For testing purpose. 
	 *
	 * <p> Create distributed dense matrix in specified matrix dimension.
	 * The matrix is partitioned in blocks of square or near square.
	 * and each place has one block.  Elements in the matrix
	 * are assigned with random values.
	 *
	 * @param m  number of rows
	 * @param n  number of columns
	 */		
	public static def makeRand(m:Int, n:Int) : DistDenseMatrix(m,n) {
		val dstmat = make(m, n); 
		dstmat.initRandom();
		return dstmat;
	}
	
	/**
	 * For testing purpose. 
	 *
	 * <p> Create distributed dense matrix in specified partitioning and
	 * elements are assigned with random values.
	 */
	public static def makeRand(g:Grid) : DistDenseMatrix(g.M,g.N) {
		val d   = Dist.makeUnique();
		val dstmat = make(g, d);
		dstmat.initRandom();
		return dstmat;
	}

	/**
	 * Create dist dense(m,n) with square or close to square block
	 * matrix partitioning and unique distribution of blocks among
	 * all places.
	 *
	 * @param  m  number of rows in the matrix
	 * @param  n  number of columns in the matrix
	 */
	public static def make(m:Int, n:Int) : DistDenseMatrix(m,n) {
		val g =  Grid.make(m, n, Place.MAX_PLACES);
		return make(g);
	}
	
	/**
	 * Create dist dense matrix using specified DistArray and partitioning
	 * 
	 * @param  gp     matrix paritinging
	 * @param  da     Distributed arrays in all placese
	 */
	public static def make(gp:Grid, 
						da:DistArray[Array[Double](1){rail}](1)): DistDenseMatrix(gp.M,gp.N) {
		val ddb = DistArray.make[DenseBlock](da.dist);
		finish for(val [p] :Point in da.dist) {
			val rid = gp.getRowBlockId(p);
			val cid = gp.getColBlockId(p);
			val m   = gp.rowBs(rid);
			val n   = gp.colBs(cid);
			at (ddb.dist(p)) async {
				val den = new DenseMatrix(m, n, da(p) as Array[Double](1){rail});
				ddb(p) = new DenseBlock(rid, cid, den);
			}
		}
		return new DistDenseMatrix(gp, ddb) ;		
	}
 
	/**
	 * Create dist dense matrix using specified DistArray and partitioning
	 * 
	 * @param  gp     matrix paritinging
	 * @param  da     Distributed arrays in PlaceLocalHandle
	 */
	public static def make(gp:Grid, 
			da:PlaceLocalHandle[Array[Double](1){rail}]): DistDenseMatrix(gp.M,gp.N) {
		val ddb = DistArray.make[DenseBlock](Dist.makeUnique());
		finish for(val [p] :Point in ddb.dist) {
			val rid = gp.getRowBlockId(p);
			val cid = gp.getColBlockId(p);
			val m   = gp.rowBs(rid);
			val n   = gp.colBs(cid);
			at (ddb.dist(p)) async {
				val den = new DenseMatrix(m, n, da() as Array[Double](1){rail});
				ddb(p) = new DenseBlock(rid, cid, den);
			}
		}
		return new DistDenseMatrix(gp, ddb) ;		
	}
	
	
	//-----------------------------------------------------------------

	/**
	 * Initialize distributed dense-block matrix with a constant value
	 * 
	 * @param ival     initial value for all elements in matrix
	 */
	public def init(ival:Double):DistDenseMatrix(this) {
		
		finish ateach (val [p] :Point in distBs) {
			distBs(p).dense.init(ival);
		}
		return this;
	}

	/**
	 * Initialize distributed dense-block matrix with random values
	 * 
	 * @param lb  --- lower bound of random value
	 * @param up  --- upper bound of random value
	 */
	public def initRandom(lb:Int, ub:Int):DistDenseMatrix(this) {
		finish ateach (val [p] :Point in distBs) {
			distBs(p).dense.initRandom(lb, ub);
		}
		return this;
	}
	
	public def initRandom():DistDenseMatrix(this) {
		finish ateach (val [p] :Point in distBs) {
			distBs(p).dense.initRandom();
		}
		return this;
	}
	//==================================================================
	// Matrix data allocation
	//==================================================================
	/**
	 * Make a copy of myself. 
	 */
	public  def clone():DistDenseMatrix(M,N) {
		val dbs  = DistArray.make[DenseBlock](this.dist);
		val dden = new DistDenseMatrix(this.grid, dbs);

		finish ateach(val [p] :Point in this.dist) {
			dbs(p) = this.distBs(p).clone();
		}
		return dden;
	}
	
	/**
	 * Create a new distributed dense-block instance.
	 *
	 * @param m number of rows in matrix.
	 * @param n number of columns in matrix
	 */
	public  def alloc(m:Int, n:Int):DistDenseMatrix(m,n) {
		//Debug.exit("Allocation fail, matrix partition is unknown");
		val g =  Grid.make(m, n, Place.MAX_PLACES);
		val nm = DistDenseMatrix.make(g);
		return nm;
    }
	/**
	 * Create a new distributed dense-block instance using the same
	 * matrix dimension and partitioning
	 */
	public  def alloc():DistDenseMatrix(M,N) {

		val ddb = DistArray.make[DenseBlock](dist);
		finish ateach(val [p] :Point in ddb.dist) {
			ddb(p) = this.distBs(p).alloc();
		}
		return new DistDenseMatrix(grid, ddb);
    }
	//-------------------------------------------------------
	/**
	 * Copy data from distributed dense matrix in all places
	 * to dense block matrix at here.
	 *
	 * @param   blkden   the target dense block matrix.
	 */
	public def copyTo(blkden:DenseBlockMatrix(M,N)):void {
		Debug.assure(this.grid.equals(blkden.grid), "partitioning is not same");
			
		/* Timing */ val stt = Timer.milliTime();
		comm.gather(this.distBs, blkden.listBs);
		/* Timing */ blkden.listBs(here.id()).commTime += Timer.milliTime() - stt;
	}

	/**
	 * Copy data from distributed dense matrix in all places to dense matrix 
	 * at here, via using temporary dense block matrix to store gathered data. 
	 *
	 * @param  tmpdbm  temporary memory space to store matrix blocks.
	 * @param  denmat  the target dense matrix.
	 */
	public def copyTo(tmpdbm:DenseBlockMatrix(M,N), 
					  denmat:DenseMatrix(M,N)):void {
		copyTo(tmpdbm);
		tmpdbm.copyTo(denmat);		
	}

	/**
	 * Copy data from distributed matrix of all places to dense matrix 
	 * at here.
	 *
	 * @param  denmat   the target dense matrix.
	 */
	public def copyTo(denmat:DenseMatrix(M,N)):void {
		Debug.assure(grid.numRowBlocks==1||N==1, 
				"Source matrix is not single row block partitioning or matrix is not a vector");

		comm.gatherRowBs(grid, distBs, denmat);
	}

	
	/**
	 * Copy data from distributed dense-block matrix in all places to duplicated dense matrix
	 *
	 * @param ddm      duplicated dense matrix
	 */
	public def copyTo(dupden:DupDenseMatrix(M,N)):void {
		Debug.assure(grid.numRowBlocks==1||N==1,
					"Number of row blocks is not 1 or matrix is not a vector");

		/* Timing */ val stt = Timer.milliTime();
		//Debug.flushln("Starting gathering row Bs");
		comm.gatherRowBs(grid, distBs, dupden.local());
		//Debug.flushln("Starting bcast gathered result");
		comm.bcast(dupden.dupMs);
		//Debug.flushln("Done bcast");
		/* Timing */ dupden.commTime += Timer.milliTime() - stt;
	}

	/**
	 * Copy data from dense block matrix at here to dist block dense matrix
	 * 
	 * @param bdm      block dense matrix
	 */
	public def copyFrom(bdm:DenseBlockMatrix(M,N)):void {
		Debug.assure(grid.equals(bdm.grid),	"block partitioning mismatch");

		/* Timing */ val stt = Timer.milliTime();
		comm.scatter(bdm.listBs, distBs);
		/* Timing */ distBs(here.id()).commTime += Timer.milliTime() - stt;
	}
	
	/**
	 * Copy data from dense matrix at here to dist block dense matrix in single row 
	 * block partitioning
	 * 
	 * @param den      source dense matrix
	 */
	public def copyFrom(den:DenseMatrix(M,N)):void {
		
			Debug.assure(grid.numRowBlocks==1||N==1,	
					"block partitioning is not single row block partitioning or matrix is not a vector");
		/* Timing */ val stt = Timer.milliTime();
		comm.scatterRowBs(grid, den, distBs);
		/* Timing */ distBs(here.id()).commTime += Timer.milliTime() - stt;
	}
	//================================================================
	// Data access 
	//================================================================	
	/**
	 * Return matrix at the specified index in the DistArray list
	 */
	public def getBlock(i:Int):DenseBlock {
		val sb = at(this.distBs.dist(i)) this.distBs(i);
		return sb;
	}
	//--------
	/**
	 * Return dense block at the specified row and column block.
	 */
	public def getBlock(rp:Int, cp:Int): DenseBlock {
		val bid = this.grid.getBlockId(rp, cp);
		return getBlock(bid);
	}
	//--------
	public def setBlock(i:Int, dm:DenseMatrix) : void {
		val r = grid.getRowBlockId(i);
		val c = grid.getColBlockId(i);
		at (this.distBs.dist(i)) {
			distBs(i) = new DenseBlock(r, c, dm);
		}
	}
	
	/**
	 *Return dense matrix at the specified partition coordinate.  
	 *
	 * @param  x      the x block in row blocks in grid partition
	 * @param  y      the y block in column blocks in grid partition
	 */
	public def getMatrix(x:Int, y:Int) <: DenseMatrix = 
		this.distBs(grid.getBlockId(x, y)).getMatrix();

	/**
	 * Return the local portion of distributed matrix at here
	 */
	public def local() <: DenseMatrix = this.distBs(here.id()).getMatrix();


	/**
	 * Return matrix at place p. Must be at place p to call this method. 
	 *
	 * @param  p     the index p at the distributed array
	 */
	public def getMatrix(p:Int) <: DenseMatrix = this.distBs(p).getMatrix();

	public def getMatrix() <: DenseMatrix = this.distBs(here.id()).getMatrix();
	
	/**
	 * Return element value at (x, y)
	 */
    public  operator this(x:Int, y:Int):Double {
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		val dv = at (this.distBs.dist(bid)) this.distBs(bid)(loc(2), loc(3));
		return dv;
	}

	/**
	 * Set value v at (x, y) 
	 */
	public  operator this(x:Int,y:Int)=(v:Double):Double {
		val loc = grid.find(x, y);
		val bid = grid.getBlockId(loc(0), loc(1));
		at (this.distBs.dist(bid)) this.getMatrix(bid)(loc(2), loc(3))=v;
		return v;
	}

	/**
	 * Reset the whole matrix
	 */
	public  def reset():void {
		finish ateach (val [p]:Point in this.distBs) {
			local().reset();
		}
	}
	
	/**
	 * For profiling purpose. Reset commu and computing time stamps.
	 */
	public def resetTime():void {
		finish ateach (val [p]:Point in this.distBs) {
			distBs(here.id()).reset();
		}
	}
	
 	//====================================================================
	// Cellwise operation
	//====================================================================

	/**
	 * For each i,j, replace this(i,j) with this(i,j)*a.
	 */
 	public def scale(a:Double) {
		finish ateach(val [p] :Point in this.distBs) {
			/* Timing */ val st:Long = Timer.milliTime();
			local().scale(a);
			/* Timing */ distBs(p).calcTime += Timer.milliTime() - st;
		}	
		return this;
    }

	//--------------------------------------
	// Cellwise addition
	//--------------------------------------

	/**
	 * Cellwise addition. A must be a DistDenseMatrix instance
	 */
	public def cellAdd(A:Matrix(M,N)) {//:  DistDenseMatrix(this) {
	    if (! likeMe(A))
	        throw new UnsupportedOperationException("Distributed matrix not compatible");
	    return cellAdd(A as DistDenseMatrix(M,N));
	}

	/**
	 * A must be defined over the same grid as this.
	 * 
	 * <p> For each i,j in the domain of this, replace this(i,j) with 
	 * this(i,j) + A(i,j).
	 */
	public def cellAdd(A:DistDenseMatrix(M,N)) {//: DistDenseMatrix(this) {
		if (! this.grid.equals(A.grid)) 
			throw new UnsupportedOperationException("Partitioning of matrices are not same!");

	    finish ateach([p]  in this.distBs) {
			/* Timing */ val st:Long = Timer.milliTime();
	        val d = local();
	        d.cellAdd(A.local() as DenseMatrix(d.M, d.N));
			/* Timing */ distBs(p).calcTime += Timer.milliTime() - st;
	    }
	    return this;
	}

	public def cellAdd(d:Double) {

		finish ateach([p]  in this.distBs) {
			/* Timing */ val st:Long = Timer.milliTime();
			val mat = local();
			mat.cellAdd(d);
			/* Timing */ distBs(p).calcTime += Timer.milliTime() - st;
		}
		return this;
	}
	
	protected def cellAddTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		throw new UnsupportedOperationException("Not implemented");
	}

	//-------------------------------
	// Cellwise subtraction
	//-------------------------------

	/**
	 * Cellwise subtraction. A must be a DistDenseMatrix instance
	 */
	public def cellSub(A:Matrix(M,N)) {//:  DistDenseMatrix(this) {
	    if (! likeMe(A))
	        throw new UnsupportedOperationException("Distributed matrix not compatible");
	    return cellSub(A as DistDenseMatrix(M,N));
	}

	/**
	 * Cellwise subtraction. A must has the same Grid
	 */
	public def cellSub(A:DistDenseMatrix(M,N)) {//:  DistDenseMatrix(this) {
		if (! this.grid.equals(A.grid)) 
			throw new UnsupportedOperationException("Partitioning grid is not compatible");
	    finish ateach([p] in this.dist) {
			/* Timing */ val st:Long = Timer.milliTime();
	        val d = this.local();
	        d.cellSub(A.local() as DenseMatrix(d.M, d.N));
			/* Timing */ distBs(p).calcTime += Timer.milliTime() - st;
	    }
	    return this;
	}

	/**
	 * Perform cell-wise subtraction  x = x - this.
	 *
	 * @param x     first input and output in subtraction operation
	 * @return      result matrix
	 */
	protected def cellSubFrom(x:DenseMatrix(M,N)):DenseMatrix(x) {
		throw new UnsupportedOperationException("Not implemented");
	}

	//-------------------------------
	// Cellwise multiplication
	//-------------------------------

	/**
	 * Cellwise multiplication. Input A must be a DistDenseMatrix instance
	 */
	public def cellMult(A:Matrix(M,N)) {//: DistDenseMatrix(this) {
		if (! likeMe(A)) 
			throw new UnsupportedOperationException("Distributed matrix not compatible");		
		return cellMult(A as DistDenseMatrix(M,N));
	}

	/**
	 * A must be defined over the same grid as this.
 	 * 
 	 * <p> For each i,j in the domain of this, replace this(i,j) with 
 	 * this(i,j) * A(i,j).
	 */
	public def cellMult(A:DistDenseMatrix(M,N)) {//: DistDenseMatrix(this) {
		if (! this.grid.equals(A.grid)) 
			throw new UnsupportedOperationException("Partitioning of matrices are not same!");

	    finish ateach([p]  in this.distBs) {
			/* Timing */ val st:Long = Timer.milliTime();
	        val d = this.local();
	        d.cellMult(A.local() as DenseMatrix(d.M, d.N));
			/* Timing */ distBs(p).calcTime += Timer.milliTime() - st;
	    }
	    return this;
	}

	/**
	 * Perform cell-wise multiply operation x = this &#42 x 
	 */
	protected def cellMultTo(x:DenseMatrix(M,N)):DenseMatrix(x) {
		throw new UnsupportedOperationException("Not implemented");
	}	

	//-------------------------------
	// Cellwise division
	//-------------------------------
	
	/**
	 * Perform cell-wise division. Input A must be defined over the same grid as this.
	 * 
	 * <p> For each i,j in the domain of this, replace this(i,j) with 
	 * this(i,j) / A(i,j).
	 */
	public def cellDiv(A:DistDenseMatrix(M,N)) {//:  DistDenseMatrix(this) {
		if (! this.grid.equals(A.grid)) 
			throw new UnsupportedOperationException("Partitioning grid is not compatible");
	    finish ateach([p] in this.distBs) {
			/* Timing */ val st:Long = Timer.milliTime();
	        val d = this.local();
	        d.cellDiv(A.local() as DenseMatrix(d.M, d.N));
			/* Timing */ distBs(p).calcTime += Timer.milliTime() - st;
	    }
	    return this;
	}

	public def cellDiv(A:Matrix(M,N))  {
		if (! likeMe(A)) 
			throw new UnsupportedOperationException("Distributed matrix not compatible");		
		return cellDiv(A as DistDenseMatrix(M,N));
	}

	/**
	 * Perform cellwise return x = this / x 
	 */	
	protected def cellDivBy(dst:DenseMatrix(M,N)):DenseMatrix(dst) {
		throw new UnsupportedOperationException("Not implemented");
	}

	//====================================================================
	// Multiplication: matrix * matrix
	//====================================================================
	/**
	 * Perform this = A &#42 B or this += A &#42 B using SUMMA. Only distributed matrices are supported. 
	 */
	public  def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):DistDenseMatrix(this) {
		
		if ((A instanceof DistDenseMatrix) && ( B instanceof DistDenseMatrix)) {
			return mult(A as DistDenseMatrix(this.M), B as DistDenseMatrix(A.N,this.N), plus);
		} else if ((A instanceof DistDenseMatrix) && ( B instanceof DistSparseMatrix)) {
			return mult(A as DistDenseMatrix(this.M), B as DistSparseMatrix(A.N,this.N), plus);
		} else if ((A instanceof DistSparseMatrix) && ( B instanceof DistSparseMatrix)) {
			return mult(A as DistSparseMatrix(this.M), B as DistSparseMatrix(A.N,this.N), plus);
		} else if ((A instanceof DistSparseMatrix) && ( B instanceof DistDenseMatrix)) {
			return mult(A as DistSparseMatrix(this.M), B as DistDenseMatrix(A.N,this.N), plus);
		}
		throw new UnsupportedOperationException("Not support");
	}

	/**
	 * Not supported.  
	 */
	public def transMult(
			A:Matrix{self.N==this.M}, 
			B:Matrix(A.M,this.N),
			plus:Boolean):DistDenseMatrix(this) {
			
		throw new UnsupportedOperationException("Not supported. Only the second matrix is allowed to transposed in SUMMA");
    }

	/**
	 * Perform this = A &#42 B<sup>T</sup> or this += A &#42 B<sup>T</sup> using SUMMA. 
	 * Only distributed matrices are supported. 
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):DistDenseMatrix(this){ 
		
		if ((A instanceof DistDenseMatrix) && ( B instanceof DistDenseMatrix)) {
			return multTrans(A as DistDenseMatrix(this.M), B as DistDenseMatrix(this.N,A.N), plus);
		} else if ((A instanceof DistDenseMatrix) && ( B instanceof DistSparseMatrix)) {
			return multTrans(A as DistDenseMatrix(this.M), B as DistSparseMatrix(this.N,A.N), plus);
		} else if ((A instanceof DistSparseMatrix) && ( B instanceof DistSparseMatrix)) {
			return multTrans(A as DistSparseMatrix(this.M), B as DistSparseMatrix(this.N,A.N), plus);
		} else if ((A instanceof DistSparseMatrix) && ( B instanceof DistDenseMatrix)) {
			return multTrans(A as DistSparseMatrix(this.M), B as DistDenseMatrix(this.N,A.N), plus);
		}
		
		throw new UnsupportedOperationException("Not supported");
    }     
	//==================================================================
	// DistDense * DistDense in SUMMA
	//=================================================================	
	/**
	 * Multiply two distributed dense matrices using SUMMA.
	 * The panel size is dertermined by the size of input matrices
	 */
	public  def mult(
			A:DistDenseMatrix(this.M), 
			B:DistDenseMatrix(A.N,this.N), 
			plus:Boolean):DistDenseMatrix(this) {
		
		val ps = SummaDense.estPanelSize(A, B);
		SummaDense.mult(ps, plus?1.0:0.0, A, B, this);
		return this;
	}
	
	public  def multTrans(
			A:DistDenseMatrix(this.M), 
			B:DistDenseMatrix(this.N,A.N), 
			plus:Boolean):DistDenseMatrix(this) {
		
		val ps = SummaDense.estPanelSize(A, B);
		SummaDense.multTrans(ps, plus?1.0:0.0, A, B, this);
		return this;
	}	
	
	//==================================================================
	// DistDense * DistSparse in SUMMA
	//=================================================================	
	/**
	 * Perform matrix multiplication between distributed dense and distributed sparse.
	 * Using SUMMA algorithm, the panel size is dertermined by the size of input matrices
	 */
	public  def mult(
			A:DistDenseMatrix(this.M), 
			B:DistSparseMatrix(A.N,this.N), 
			plus:Boolean):DistDenseMatrix(this) {
		
		val ps = SummaDenseMultSparse.estPanelSize(A, B);
		SummaDenseMultSparse.mult(ps, plus?1.0:0.0, A, B, this);
		return this;
	}
	
	/**
	 * Perform distributed dense and distributed sparse matrix 
	 * multiplication with the second matrix transposed.
	 */
	public  def multTrans(
			A:DistDenseMatrix(this.M), 
			B:DistSparseMatrix(this.N,A.N), 
			plus:Boolean):DistDenseMatrix(this) {
		
		val ps = SummaDenseMultSparse.estPanelSize(A, B);
		SummaDenseMultSparse.multTrans(ps, plus?1.0:0.0, A, B, this);
		return this;
	}	
	//==================================================================
	// DistSparse * DistSparse in SUMMA
	//=================================================================	
	/**
	 * Perform matrix multiplication between two distributed sparse matrix
	 * Using SUMMA algorithm, the panel size is dertermined by the size of input matrices
	 */
	public  def mult(
			A:DistSparseMatrix(this.M), 
			B:DistSparseMatrix(A.N,this.N), 
			plus:Boolean):DistDenseMatrix(this) {
		
		val ps = SummaSparse.estPanelSize(A, B);
		SummaSparse.mult(ps, plus?1.0:0.0, A, B, this);
		return this;
	}		
	
	/**
	 * Perform distributed sparse matrix multiplication with the second matrix
	 * is transposed in multiplication.
	 */
	public  def multTrans(
			A:DistSparseMatrix(this.M), 
			B:DistSparseMatrix(this.N,A.N), 
			plus:Boolean):DistDenseMatrix(this) {
		
		val ps = SummaSparse.estPanelSize(A, B);
		SummaSparse.multTrans(ps, plus?1.0:0.0, A, B, this);
		return this;
	}	
	
	//==================================================================
	// DistSparse * DistDense in SUMMA
	//=================================================================	
	/**
	 * Perform matrix multiplication between distributed sparse and distributed dense matrix.
	 * Using SUMMA algorithm, the panel size is dertermined by the size of input matrices
	 */
	public  def mult(
			A:DistSparseMatrix(this.M), 
			B:DistDenseMatrix(A.N,this.N), 
			plus:Boolean):DistDenseMatrix(this) {
		
		val ps = SummaSparseMultDense.estPanelSize(A, B);
		SummaSparseMultDense.mult(ps, plus?1.0:0.0, A, B, this);
		return this;
	}

	/**
	 * Perform matrix multiplication between distributed sparse and distributed dense matrix,
	 * while the second matrix is transposed in multiplication.
	 */
	public  def multTrans(
			A:DistSparseMatrix(this.M), 
			B:DistDenseMatrix(this.N,A.N), 
			plus:Boolean):DistDenseMatrix(this) {
		
		val ps = SummaSparseMultDense.estPanelSize(A, B);
		SummaSparseMultDense.multTrans(ps, plus?1.0:0.0, A, B, this);
		return this;
	}	
	
	//==================================================================
	// DistDense * DupDense					 
	//=================================================================	
	/**
	 * Perform distributed dense times duplicated dense matrix.
	 * The 
	 */
	public def mult(
			A:DistDenseMatrix(this.M), 
			B:DupDenseMatrix(A.N,this.N), 
			plus:Boolean): DistDenseMatrix(this) {
		DistMultDupToDist.comp(A, B, this, plus);
		return this;
	}

	public def mult(
			A:DistDenseMatrix(this.M), 
			B:DupDenseMatrix(A.N,this.N)) = mult(A, B, false);
	
	// DistSparse * DupDense					 
	public def mult(
			A:DistSparseMatrix(this.M), 
			B:DupDenseMatrix(A.N, this.N), 
			plus:Boolean): DistDenseMatrix(this) {
		DistMultDupToDist.comp(A, B, this, plus);
		return this;
	}

	public def mult(
			A:DistSparseMatrix(this.M), 
			B:DupDenseMatrix(A.N, this.N)) = mult(A, B, false);
	
	//				
	public def multTrans(
			A:DistSparseMatrix(this.M), 
			B:DupDenseMatrix(this.N,A.N),
			plus:Boolean):DistDenseMatrix(this){
		
		DistMultDupToDist.compMultTrans(A, B, this, plus);			
		return this;		
	}
	
	public def multTrans(
			A:DistSparseMatrix(this.M), 
			B:DupDenseMatrix(this.N,A.N)):DistDenseMatrix(this) 			
		= multTrans(A, B, false);
		
	//==================================================================
	// Profiling
	//==================================================================
	public def getCalcTime():Long = this.distBs(here.id()).calcTime;
	public def getCommTime():Long = this.distBs(here.id()).commTime;


	//==================================================================
	// Utils
	//==================================================================
	/**
	 * Check matrix is DistDenseMatrix object or not
	 */
	public def likeMe(A:Matrix):Boolean =
	    ((A instanceof DistDenseMatrix)                  &&
		 ((A as DistDenseMatrix).grid.equals(this.grid)) &&
		 ((A as DistDenseMatrix).dist.equals(dist)));


	public def equalAll(m:DistDenseMatrix(M,N)):Boolean {
		if (! this.grid.equals(m.grid)) {
			Console.OUT.println("Grid dist differs!");
		}
		return VerifyTools.testSame(this as Matrix(M,N), m as Matrix(M,N));
	}

	public def equals(m:DistDenseMatrix(M,N)) =
		VerifyTools.testSame(this as Matrix(M,N), m as Matrix(M,N));
	
	public def equals(m:DenseMatrix(M,N)) = 
		VerifyTools.testSame(this as Matrix(M,N), m as Matrix(M,N));
	

	public def toStringBlock() :String {
		var output:String = "-------- Dist Dense Matrix size:["+M+"x"+N+"] ---------\n";
		for (val [p]:Point in this.dist) {
			output += "At place " + p +": ";
			output += at (distBs.dist(p)) { this.distBs(p).getMatrix().toString()};
		}
		output += "--------------------------------------------------\n";
		return output;
	}

	public def print() {
		print("");
	}
	
	public def print(msg:String) {
		Console.OUT.print(msg+toStringBlock());
		Console.OUT.flush();
	}
	
	public def printBlock() { printBlock("");}
	public def printBlock(msg:String) {
		Console.OUT.print(msg);
		Console.OUT.print(this.toStringBlock());
		Console.OUT.flush();
	}

	public def debugPrintBlock() { debugPrintBlock("");}
	public def debugPrintBlock(msg:String) {
		if (Debug.disable) return;
		val dbstr:String = msg+ this.toStringBlock();
		Debug.println(dbstr);
		Debug.flush();
	}

}
