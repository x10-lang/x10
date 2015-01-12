/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.dist;

import x10.regionarray.Dist;
import x10.regionarray.DistArray;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.comm.MatrixBcast;
import x10.matrix.comm.MatrixReduce;

public  type DupDenseMatrix(M:Long)=DupDenseMatrix{self.M==M};
public  type DupDenseMatrix(M:Long, N:Long)=DupDenseMatrix{self.M==M, self.N==N};
public  type DupDenseMatrix(C:Matrix)=DupDenseMatrix{self==C};

/**
 * Implementation of duplicated dense matrix. All duplicated dense matrices 
 * are stored in DistArray.  Distribution of dense matrices is unique, and one
 * duplication maps to one place.
 */
public class DupDenseMatrix extends Matrix {
	/**
	 * Data duplication
	 */
	public val dist:Dist(1);

	/**
	 * Distributed array of matrix data
	 */
    public val dupMs:DistArray[DenseMatrix](1);

	public val tmpMs:DistArray[DenseMatrix](1);
	private var tmpReady:Boolean;

    public var calcTime:Long=0;
    public var commTime:Long=0;

	/**
	 * Constructor, using DistArray of DenseMatrix. 
	 *
	 * @param dms     distributed copies of dense matrix
	 * @param bcom      broadcast communication object
	 */
	public def this(dms:DistArray[DenseMatrix](1)) {
		super(dms(here.id()).M, dms(here.id()).N);

		dist = dms.dist;
		dupMs = dms;
		tmpMs = DistArray.make[DenseMatrix](dms.dist);//,([p]:Point)=>(null));
		tmpReady=false;
	}

	// No bcast is performed
	/**
	 * Create duplicated dense matrix based on an existing dense matrix.
	 *
	 * @param mat     local dense matrix 
	 */
	public static def make(mat:DenseMatrix) : DupDenseMatrix(mat.M,mat.N) {
		val dist = Dist.makeUnique();
		val m = mat.M;
		val n = mat.N;
		val dms  = DistArray.make[DenseMatrix](dist);
		val root = here.id();
		finish ateach(val [p]:Point in dms.dist) {
			val mypid = here.id();
			if (mypid != root) 
				dms(mypid) = new DenseMatrix(m, n);
		}
		dms(root) = mat;
		val dm  = new DupDenseMatrix(dms) as DupDenseMatrix(mat.M, mat.N);
		dm.sync(); // sync mat 
		return dm;
	}

	// Only memory space is allocated on all places
	/**
	 * Create duplicated dense matrix with specified dimension 
	 *
	 * @param m     number of rows in dense matrix
	 * @param n     number of columns in dense matrix
	 */
	public static def make(m:Long, n:Long): DupDenseMatrix(m,n) {
		val dist = Dist.makeUnique();
		val dms  = DistArray.make[DenseMatrix](dist);
		finish ateach(val [p]:Point in dms.dist) {
			val mypid = here.id();
			dms(mypid) = new DenseMatrix(m, n);
		}
		val dm  = new DupDenseMatrix(dms) as DupDenseMatrix(m,n);
		return dm;
	}

	/**
	 * Create duplicated dense matrix by using specified DistArray storage in all places
	 * 
	 * @param m     number of rows in dense matrix
	 * @param n     number of columns in dense matrix
	 * @param da     distributed arrays of array in double
	 */
	public static def make(m:Long, n:Long, 
						   da:DistArray[Rail[Double]](1)): DupDenseMatrix(m,n) {
		val dms = DistArray.make[DenseMatrix](da.dist);
		finish ateach(val [p]:Point in dms.dist) {
			val mypid = here.id();
			dms(mypid) = new DenseMatrix(m, n, da(mypid) as Rail[Double]{self!=null});
		}
		val dm  = new DupDenseMatrix(dms) as DupDenseMatrix(m,n);
		return dm;
	}	

	/**
	 * Create duplicated dense matrix with specified dimension and
	 * initial the copy at root random values. The copies at other
	 * places are also updated.
	 *
	 * @param m     number of rows in dense matrix
	 * @param n     number of columns in dense matrix
	 */
	public static def makeRand(m:Long, n:Long): DupDenseMatrix(m,n) {
		val ddm = make(m, n);
		ddm.initRandom();
		return ddm;
	}

	/**
	 * For testing purpose.
	 *
	 * <p> Initialize duplicated dense matrix(m,n) with random values.
	 * All copies are synchronized.
	 */
	public def initRandom() : DupDenseMatrix(this) {
		local().initRandom();
		sync();
		return this;
	}
	
	public def initRandom(lo:Long, up:Long) : DupDenseMatrix(this) {
		local().initRandom(lo,up);
		sync();
		return this;
	}

	/**
	 * For testing purpose.
	 *
	 * <p> Create duplicated dense matrix(m,n). Assign each element with
	 * a constant value.
	 *
	 * @param ival     initial value for all elements
	 */
	public def init(ival:Double) : DupDenseMatrix(this) {
		local().init(ival);
		sync();
		return this;
	}
	
	/**
	 * Init with function
	 * 
	 * @param f    The function to use to initialize the matrix, mapping (row, column) => double
	 * @return this object
	 */
	public def init(f:(Long,Long)=>Double): DupDenseMatrix(this) {
		finish ateach(val [p]:Point in dupMs.dist) {
			val pid=here.id();
			dupMs(pid).init(f);
		}
		return this;
	}
	
	/**
	 * Allocate memory space to hold duplicated dense matrix(m,n)
	 */
	public def alloc(m:Long, n:Long)  = make(m, n);

	/**
	 * Make a copy of all duplicated dense matrix in all places.
	 * If the source duplicated matrix is not synchronized in all places,
	 * the clone copy is also not synchronized.
	 */		
	public def clone() : DupDenseMatrix(this.M, this.N) {
		val dds  = DistArray.make[DenseMatrix](dupMs.dist);
		finish ateach(val [p]:Point in dds.dist) {
			val mypid = here.id();
			dds(mypid) = this.dupMs(mypid).clone();
		}
		val ddm = new DupDenseMatrix(dds) as DupDenseMatrix(M,N);
		return ddm;
	}

	/**
	 * Allocate temp space, if not allocated. Temp space is used for
	 * collective communication such as reduce.
	 */
	public def allocTemp():void {
		if (tmpReady) return;
		finish ateach(val [p]:Point in tmpMs.dist) {
			val mypid = here.id();
			tmpMs(mypid) = dupMs(mypid).alloc();
		}		
		tmpReady = true;
	}

	// Copy 
	public  def copyTo(that:DupDenseMatrix(M,N)):void {
		finish ateach(val [p] :Point in this.dist) {
			val mypid=here.id();
			val sden = this.dupMs(p);
			sden.copyTo(that.dupMs(p) as DenseMatrix(sden.M, sden.N));
		}
	}	

    public def copyFrom(that:DenseMatrix(M,N)):void {
        that.copyTo(local());
        sync();
    }	

	/**
	 * Copy data at local copy to another dense matrix.
	 *
	 * @param   dm  the target dense matrix
	 */
	public def copyTo(dm:DenseMatrix(M,N)):void {
		local().copyTo(dm);
	}

	public def copyTo(that:Matrix(M,N)): void {
		if (that instanceof DupDenseMatrix)
			copyTo(that as DupDenseMatrix);
		else if (that instanceof DenseMatrix)
			copyTo(that as DenseMatrix);
		else
			throw new UnsupportedOperationException("CopyTo: target matrix type is not supportede");
	}

	/**
	 * Access data at(x, y)
	 */
    public operator this(x:Long, y:Long):Double=local()(y*this.M+x);

	/**
	 * Assign v to (x, y) in the copy at here. Other copies are not
	 * modified.
	 */
	public operator this(x:Long,y:Long) = (v:Double):Double{
		local()(x, y) = v;
		return v;
	}

	/**
	 * Return the matrix copy at here.
	 */
	public def getMatrix():DenseMatrix = this.dupMs(here.id()); //as DenseMatrix(M,N);
	
	/**
	 * Return the local copy of dense matrix at here with dimension check.
	 */
	public def local():DenseMatrix(M,N) = this.dupMs(here.id()) as DenseMatrix(M,N);

	/**
	 * Reset matrix and all copies.
	 */
	public def reset():void {
		finish ateach(val [p]:Point in this.dupMs.dist) {
			local().reset();
		}
		calcTime=0;
		commTime=0;
	}
	/**
	 * Check Matrix A is duplicated and has the same dist or not
	 */
	public def likeMe(A:Matrix):Boolean =
	    (A instanceof DupDenseMatrix && 
	    		(A as DupDenseMatrix).dupMs.dist.equals(this.dupMs.dist));


	/**
	 * Broadcast the copy of dense matrix from here to all other places.
	 */
	public def sync() : void {
		/* Timing */ val st:Long = Timer.milliTime();
		MatrixBcast.bcast(dupMs);
		/* Timing */ commTime += Timer.milliTime() - st;
	}

	/**
	 * Add all copies of dense matrices to the copy at here.  Copies at other places
	 * are modified, however, data is not synchronized.
	 * NOTE: temporary space is used
	 *
	 */
	public def reduceSum(): void {
		allocTemp();
		/* Timing */ val st:Long = Timer.milliTime();
		MatrixReduce.reduceSum(this.dupMs, tmpMs);
		/* Timing */ commTime += Timer.milliTime() - st;
	}

	/**
	 * Add all copies of duplicated matrices with the matrix at here. The
	 * result is then broadcast to all copies.  This function needs to use
	 * additional duplicated dense matrix. This function is used in distributed 
	 * matrix multiplication. 
	 * 
	 */
	public def allReduceSum(): void {
		allocTemp();
		/* Timing */ val st:Long = Timer.milliTime();
        MatrixReduce.allReduceSum(this.dupMs, tmpMs);
        /* Timing */ commTime += Timer.milliTime() - st;
	}

	/**
	 * Transpose matrix and all copies and store the result at user provided ddm.
	 */
	public def T(ddm:DupDenseMatrix(N,M)): void{
		finish ateach(val [p]:Point in this.dupMs) {
			val tm = ddm.local();
			val m  = local();
			assert (m.M==tm.N && m.N==tm.M);
			local().T(tm);
		}
	}

	/**
	 * Transpose matrix and all copies and store a new DupDenseMatrix instance.
	 */
	public def T():DupDenseMatrix(N,M) {
		val tm = DupDenseMatrix.make(this.N, this.M);
		this.T(tm);
		return tm;
	}

	/**
	 * Scaling method. All copies are updated concurrently
	 */
 	public def scale(a:Double) {
		finish ateach(val [p] :Point in this.dupMs) {
			this.local().scale(a);
		}
		return this;
    }


	/**
	 * Cellwise addition. 
	 */
	public def cellAdd(A:Matrix(M,N)) {
		local().cellAdd(A);
		sync();
		return this;
	}

	/**
	 * Cellwise addition. All copies are updated
	 */
	public def cellAdd(A:DenseMatrix(M,N)) {
		val dm = local();
		dm.cellAdd(A);
		sync();
		return this;
	}

	/**
	 * Concurrently perform cellwise addition on all copies.
	 */
	public def cellAdd(A:DupDenseMatrix(M,N))  {
		//assert (this.M==A.M && this.N==A.N);
	    finish ateach([p]  in this.dupMs) {
			val sm = A.local();
	        val dm = local();
	        dm.cellAdd(sm);
	    }
		return this;
	}

	public def cellAdd(d:Double)  {
		//assert (this.M==A.M && this.N==A.N);
	    finish ateach([p]  in this.dupMs) {
	        val dm = local();
	        dm.cellAdd(d);
	    }
		return this;
	}

	/**
	 * Perform cellwise operation x = x + this 
	 *
	 * @param x     input and output matrix
	 * @return      the addition result
	 */
	protected def cellAddTo(x:DenseMatrix(M,N)) {
		x.cellAdd(local());
		return x;
	}

	/**
	 * Cellwise subtraction. 
	 */
	public def cellSub(A:Matrix(M,N)) {
		local().cellSub(A);
		sync();
		return this;
	}

	/**
	 * Cellwise subtraction. All copies are updated
	 */
	public def cellSub(A:DenseMatrix(M,N))  {
		local().cellSub(A);
		sync();
		return this;
	}

	/**
	 * Concurrently perform cellwise subtraction on all copies
	 */
	public def cellSub(A:DupDenseMatrix(M,N)) {
	    finish ateach([p] in this.dupMs) {
			val sm = A.local();
	        val dm = local();
	        dm.cellSub(sm);
	    }
		return this;
	}
	
	/**
	 * Perform cell-wise subtraction  x = x - this.
	 */
	public def cellSubFrom(x:DenseMatrix(M,N)) {
		x.cellSub(local());
		return x;
	}
	
	/**
	 * Perform cell-wise subtraction  x = x - this.
	 */
	public def cellSubFrom(x:DupDenseMatrix(M,N)) {
		//assert (this.M==A.M && this.N==A.N);
		/* Timing */ val st= Timer.milliTime();
		finish ateach([p] in this.dupMs) {
			val sm = x.local();
			val dm = local();
			dm.cellSubFrom(sm);
		}
		/* Timing */ calcTime += Timer.milliTime() - st;
		return this;
	}

	/**
	 * Cellwise multiplication. 
	 */
	public def cellMult(A:Matrix(M,N))  {
		local().cellMult(A);
		sync();
		return this;
	}
	
	/**
	 * Cellwise multiplication.
	 */
	public def cellMult(A:DenseMatrix(M,N)) {
		val dm = local();
		dm.cellMult(A);
		sync();
		return this;
	}

	/**
	 * Cellwise multiplication. All copies are modified with
	 * the corresponding dense matrix copies.
	 */
	public def cellMult(A:DupDenseMatrix(M,N))  {
		//assert (this.M==A.M && this.N==A.N);
		/* Timing */ val st= Timer.milliTime();
		finish ateach(val [p]:Point in this.dupMs) {
			val sm = A.local();
			val dm = local();
			dm.cellMult(sm);
		}
		/* Timing */ calcTime += Timer.milliTime() - st;
		return this;
	}

	/**
	 * Perform cell-wise multiply operation x = this &#42 x 
	 */
	protected def cellMultTo(dst:DenseMatrix(M,N)) {
		dst.cellMult(local());
		return dst;
	}

	/**
	 * Cellwise division. 
	 */
	public def cellDiv(A:Matrix(M,N)) { 
		local().cellDiv(A);
		sync();
		return this;
	}	

	/**
	 * Cellwise division. All copies are updated
	 */
	public def cellDiv(A:DenseMatrix(M,N)) { 
		val dm = local();
		dm.cellDiv(A);
		sync();
		return this;
	}

	/**
	 * Cellwise division. All copies are modified with
	 * the corresponding dense matrix copies.
	 */	
	public def cellDiv(A:DupDenseMatrix(M,N)) {
		//assert (this.M==A.M && this.N==A.N);
		/* Timing */ val st= Timer.milliTime();
		finish ateach(val [p]:Point in this.dupMs) {
			val sm = A.local();
			val dm = local();			
			dm.cellDiv(sm);
		}
		/* Timing */ calcTime += Timer.milliTime() - st;
		return this;
	}

	/**
	 * Perform cellwise return x = this / x 
	 */
	protected def cellDivBy(x:DenseMatrix(M,N)) {
		x.cellDiv(local());
		return x;
	}

	/**
	 * Perform cell-wise addition, return this + that in a new dup dense matrix. 
	 */
	public operator this + (that:DupDenseMatrix(M,N)):DupDenseMatrix(M,N) {
	    val x = clone();
	    x.cellAdd(that);
	    return x;
	}
	/**
	 * Perform cell-wise subtraction, return this - that in a new dup dense format
	 */
	public operator this - (that:DupDenseMatrix(M,N)):DupDenseMatrix(M,N) {
	    val x = clone();
	    x.cellSub(that);
	    return x;
	}
    /**
     * Perform cell-wise multiplication, return this &#42 that in dup dense format
     */
	public operator this * (that:DupDenseMatrix(M,N)):DupDenseMatrix(M,N) {
	    val x = clone();
	    x.cellMult(that);
	    return x;
	}

	/**
	 * Perform cell-wise division, return this / that in a new dup dense matrix
	 */
	public operator this / (that:DupDenseMatrix(M,N)):DupDenseMatrix(M,N) {
	    val x = clone();
	    x.cellDiv(that);
	    return x;
	}

	/**
	 * Multiplication method by using X10 driver. All copies are updated.
	 * this = A &#42 B if plus is false, else this += A &#42 B
	 */
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):DupDenseMatrix(this) {

		if (A instanceof DenseMatrix(A) && B instanceof DenseMatrix(B) )
			return mult(A as DenseMatrix(A), B as DenseMatrix(B), plus);
		else if (A instanceof DupDenseMatrix(A) && B instanceof DupDenseMatrix(B))
			return mult(A as DupDenseMatrix(A), B as DupDenseMatrix(B), plus);

		throw new UnsupportedOperationException("Not support using Matrix instances as parameters");
	}

	/**
	 * this += A &#42 B if plus is true, else this = A %#42 B
	 * @param A      the first operand of dense matrix
	 * @param B      the second operand of dense matrix
	 * @param plus     result plus flag
	 */
	public def mult(
			A:DenseMatrix(this.M), 
			B:DenseMatrix(A.N,this.N), 
			plus:Boolean) =  
        DupMultToDup.comp(A, B, this, plus);
	
	/**
	 * this += A &#42 B if plus is true, results are synchronized at every place
	 * 
	 * @param A      the first operand of duplicated dense matrix
	 * @param B      the second operand of duplicated dense matrix
	 * @param plus   result plus flag	 
	 */	
	public def mult(
			A:DupDenseMatrix(this.M), 
			B:DupDenseMatrix(A.N,this.N), 
			plus:Boolean) = 
		DupMultToDup.comp(A, B, this, plus);



	/**
	 * this += A<sup>T</sup> &#42 B if plus is true. Result copies are 
	 * synchronized in every place.
	 * 
	 * @param A      the first matrix operand
	 * @param B      the second matrix operand
	 * @param plus     result plus flag	 
	 */
	public def transMult(
			A:Matrix{self.N==this.M},
			B:Matrix(A.M,this.N),
			plus:Boolean):DupDenseMatrix(this) {

		if (A instanceof DenseMatrix(A) && B instanceof DenseMatrix(B) )
			return transMult(A as DenseMatrix(A), B as DenseMatrix(B), plus);
		else if (A instanceof DupDenseMatrix(A) && B instanceof DupDenseMatrix(B))
			return transMult(A as DupDenseMatrix(A), B as DupDenseMatrix(B), plus);
		else if (A instanceof DistDenseMatrix(A) && B instanceof DistDenseMatrix(B))
			return transMult(A as DistDenseMatrix(A), B as DistDenseMatrix(B), plus);
		else if (A instanceof DistDenseMatrix(A) && B instanceof DistSparseMatrix(B))
			return transMult(A as DistDenseMatrix(A), B as DistSparseMatrix(B), plus);
		
		throw new UnsupportedOperationException("Not support using Matrix instances as parameters");
	}

	/**
	 * this += A<sup>T</sup> &#42 B if plus is true. Result copies are 
	 * synchronized in every place.
	 * 
	 * @param A      first operand of dense matrix
	 * @param B      second operand of dense matrix 
	 * @param plus     result plus flag	 
	 */	
	public def transMult(
			A:DenseMatrix{self.N==this.M}, 
			B:DenseMatrix(A.M,this.N), 
			plus:Boolean) =
        DupMultToDup.compTransMult(A, B, this, plus);


	/**
	 * this += A<sup>T</sup> &#42 B if plus is true, results are synchronized 
	 * in every place.
	 * 
	 * @param A      first operand of dense matrix
	 * @param B      second operand of dense matrix
	 * @param plus     result plus flag	 
	 */	
	public def transMult(
			A:DupDenseMatrix{self.N==this.M}, 
			B:DupDenseMatrix(A.M,this.N), 
			plus:Boolean) = 
        DupMultToDup.compTransMult(A, B, this, plus);

	/**
	 * this += A<sup>T</sup> &#42 B if plus is true, result copies are 
	 * synchronized in every place
	 * Additional memory space is used for reduce collective communication.
	 *
	 * @param A      first operand of distributed dense matrix
	 * @param B      second operand of distributed sparse matrix in CSC
	 * @param plus     result plus flag	 
	 */	
	public def transMult(
			A:DistDenseMatrix{self.N==this.M}, 
			B:DistSparseMatrix(A.M,this.N), 
			plus:Boolean) =
		DistMultDistToDup.compTransMult(A, B, this, plus);

	/**
	 * this += A<sup>T</sup> &#42 B if plus is true, result copies are 
	 * synchronized in every place.
	 *
	 * @param A      Distributed dense matrix
	 * @param B      Distributed sparse matrix in CSC
	 */	
	public def transMult(
			A:DistDenseMatrix{self.N==this.M}, 
			B:DistSparseMatrix(A.M,this.N)) =
		DistMultDistToDup.compTransMult(A, B, this, false);

	/**
	 * this += A<sup>T</sup> &#42 B if plus is true, result copies are 
	 * synchronized in every place.
	 * 
	 * @param A      first operand of distributed sparse matrix in CSC
	 * @param B      second operand of distributed dense matrix 
	 * @param tmp     Temporary space used reduce collective communication
	 */		
	public def transMult(
			A:DistSparseMatrix{self.N==this.M}, 
			B:DistDenseMatrix(A.M,this.N), 
			plus:Boolean) =
		DistMultDistToDup.compTransMult(A, B, this, plus); 
	
	/**
	 * this += A<sup>T</sup> &#42 B if plus is true, results are synchronized 
	 * in every place.
	 * 
	 * @param A      first operand of distributed dense matrix
	 * @param B      second operand of distributed dense matrix
	 * @param plus     result plus flag	 
	 */	
	public def transMult(
			A:DistDenseMatrix{self.N==this.M}, 
			B:DistDenseMatrix(A.M,this.N), 
			plus:Boolean) =
		DistMultDistToDup.compTransMult(A, B, this, plus);

	/**
	 * this = A<sup>T</sup> &#42 B if plus is true. Results are synchronized in 
	 * every place.
	 * 
	 * @param A      first operand of distributed dense matrix
	 * @param B      second operand of distributed dense matrix
	 * @param tmp     Temporary space used reduce collective communication
	 */	
	public def transMult(
			A:DistDenseMatrix{self.N==this.M}, 
			B:DistDenseMatrix(A.M,this.N)) =
		DistMultDistToDup.compTransMult(A, B, this, false);

	/**
	 * this = A &#42 B<sup>T</sup>
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):DupDenseMatrix(this){

		if (A instanceof DenseMatrix(A) && B instanceof DenseMatrix(B) )
			return multTrans(A as DenseMatrix(A), B as DenseMatrix(B), plus);
		else if (A instanceof DupDenseMatrix(A) && B instanceof DupDenseMatrix(B))
			return multTrans(A as DupDenseMatrix(A), B as DupDenseMatrix(B), plus);
		
		throw new UnsupportedOperationException("Not support using Matrix instances as parameters");
	}

	public def multTrans(
			A:DenseMatrix(this.M), 
			B:DenseMatrix(this.N,A.N),
			plus:Boolean ) =
		DupMultToDup.compMultTrans(A, B, this, plus);

	public def multTrans(
			A:DenseMatrix(this.M), 
			B:DenseMatrix(this.N,A.N)) =
		DupMultToDup.compMultTrans(A, B, this, false);	

	public def multTrans(
			A:DupDenseMatrix(this.M), 
			B:DupDenseMatrix(this.N,A.N),
			plus:Boolean) =
		DupMultToDup.compMultTrans(A, B, this, plus);

	public def multTrans(
			A:DupDenseMatrix(this.M), 
			B:DupDenseMatrix(this.N,A.N)) =
		DupMultToDup.compMultTrans(A, B, this, false);

	/**
	 * this = A &#42 B, results are synchronized at every place
	 */
	public def mult(
			A:DupDenseMatrix(this.M), 
			B:DupDenseMatrix(A.N,this.N)) =
		DupMultToDup.comp(A, B, this, false);
	
	public def transMult(
			A:DenseMatrix{self.N==this.M}, 
			B:DenseMatrix(A.M,N)) =
		DupMultToDup.compTransMult(A, B, this, false);

	/**
	 * Operator % performs duplicated dense matrix multiplication
	 */
	public operator this % (that:DupDenseMatrix{self.M==this.N}):DupDenseMatrix(this.M,that.N) {
		val dm = DupDenseMatrix.make(this.M, that.N);
		DupMultToDup.comp(this, that, dm, false);
		return dm;
	}

	/**
	 * Operator % performs duplicated dense matrix multiplication
	 */
	public  operator this % (that:DenseMatrix{self.M==this.N}):DupDenseMatrix(this.M,that.N) {
		val dm = DupDenseMatrix.make(this.M, that.N);
		DupMultToDup.comp(this.local(), that, dm, false);
		return dm;
	}

	public def getCommTime():Long = this.commTime;
	public def getCalcTime():Long = this.calcTime;

	/** Check integrity */
	public def syncCheck():Boolean {
		val m = local();
		for (var p:Long=0; p<Place.numPlaces(); p++) {
			//if (p == here.id()) Clock.advanceAll();
			val pid = p;
			val dm = at(dupMs.dist(pid)) local();
			if (!m.equals(dm)) {
				Console.OUT.println("Integrity check found differences between the copy at here and copy at "+pid);
				Console.OUT.flush();
				return false;
			}
		}
		return true;
	}

	public def toString() :String {
		var output:String = "---Duplicated Dense Matrix size:["+M+"x"+N+"]---\n";
		output += dupMs(here.id()).toString();
		output += "--------------------------------------------------\n";
		return output;
	}

	public def allToString() : String {
		var output:String = "Duplicated Dense Matrix size:["+M+"x"+N+"]\n";
		for (var p:Long=0; p<Place.numPlaces(); p++) { 
			val pid = p;
			val mstr = at(dupMs.dist(pid)) dupMs(pid).toString();
			output += "Duplication at place " + pid + "\n"+mstr;
		}
		return output;
	}
}
