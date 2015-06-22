/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.matrix.dist;

import x10.regionarray.Dist;
import x10.regionarray.DistArray;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.comm.MatrixBcast;
import x10.matrix.sparse.SparseCSC;

public  type DupSparseMatrix(M:Long)=DupSparseMatrix{self.M==M};
public  type DupSparseMatrix(M:Long, N:Long)=DupSparseMatrix{self.M==M, self.N==N};
public  type DupSparseMatrix(C:Matrix)=DupSparseMatrix{self==C};

/**
 * Implementation of duplicated sparse matrix. All duplicated 
 * sparse matrices are stored in DistArray.  Distribution of dense matrices is unique, one
 * duplicated copy is mapped to one place.
 */
public class DupSparseMatrix extends Matrix {
	/**
	 * Data duplication
	 */
	public val dist:Dist(1);
    public val dupMs:DistArray[SparseCSC](1);

    public var calcTime:Long=0;
    public var commTime:Long=0;

	/**
	 * Construct duplicated sparse matrix. 
	 *
	 * @param dms     distribute array of sparse matrices
	 */
	public def this(dms:DistArray[SparseCSC](1)) {
		super(dms(here.id()).M, dms(here.id()).N);
		//
		dist = dms.dist;
		//count = dms.region.size();
		dupMs = dms;
	}


	// No bcast is performed
	/**
	 * Create duplicated sparse matrix instance using a sparse matrix
	 *
	 * @param mat     sparse matrix (in CSC format)
	 */
	public static def make(mat:SparseCSC) : DupSparseMatrix(mat.M,mat.N) {
		val dist = Dist.makeUnique();
		val m = mat.M;
		val n = mat.N;
		val dms  = DistArray.make[SparseCSC](dist);
		val root = here.id();
		val nzc  = mat.getStorageSize();

		finish ateach(val [p]:Point in dms.dist) {
			val mypid = here.id();
			if (mypid != root) 
				dms(mypid) = SparseCSC.make(m, n, nzc);
		}
		dms(root) = mat;
		val dm  = new DupSparseMatrix(dms) as DupSparseMatrix(mat.M, mat.N);
		dm.sync(); // sync mat 
		return dm;
	}

	//===
	// Only memory space is allocated on all places
	/**
	 * Create duplicated dense matrix for specified number of nonzero elements.
	 *
	 * @param m     number of rows
	 * @param n     number of columns
	 * @param nzcnt     number of nonzero elements.
	 */
	public static def make(m:Long, n:Long, nzcnt:Long): DupSparseMatrix(m,n) {
        val dms = DistArray.make[SparseCSC](Dist.makeUnique());
		finish ateach([p] in dms.dist) {
			val mypid = here.id();
			dms(mypid) = SparseCSC.make(m, n, nzcnt);
		}
        val dm = new DupSparseMatrix(dms) as DupSparseMatrix(m,n);
		return dm;
	}

	/**
	 * Create duplicated dense matrix for specified sparsity.
	 *
	 * @param m     number of rows
	 * @param n     number of columns
	 * @param nzd     nonzero density or sparsity.
	 */
	public static def make(m:Long, n:Long, nzd:Float): DupSparseMatrix(m,n) {
		return make(m, n, (nzd * m * n) as Long);
	}

	/**
	 * For testing purpose.
	 *
	 * <p> Create duplicated sparse matrix instance and initialized matrix data with
	 * random values
	 *
	 * @param m     number of rows
	 * @param n     number of columns
	 * @param nzcnt     number of nonzero elements
	 */
	public static def makeRand(m:Long, n:Long, nzcnt:Long): DupSparseMatrix(m,n) = make(m, n, nzcnt).initRandom();
	
	/**
	 * For testing purpose.
	 *
	 * <p> Create duplicated sparse matrix instance and initialized matrix data with
	 * random values
	 *
	 * @param m     number of rows
	 * @param n     number of columns
	 * @param nzd     sparsity
	 */
	public static def makeRand(m:Long, n:Long, nzd:Float) = make(m, n, nzd).initRandom();

	/**
	 * For testing purpose.
	 *
	 * <p> Initial duplicated sparse matrix and assign each nonzero element
	 * by a constant.						   
	 * 
	 * @param ival     initial constant value.
	 */
	public def init(ival:ElemType) : DupSparseMatrix(this) {
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
	public def init(f:(Long,Long)=>ElemType): DupSparseMatrix(this) {
		finish ateach(val [p]:Point in dupMs.dist) {
			val pid=here.id();
			dupMs(pid).init(f);
		}
		return this;
	}
	
	/**
	 * For testing purpose.
	 *
	 * <p> Initialize duplicated sparse matrix with random values.
	 *
	 * @param nzd     the sparsity used int initialzation.
	 */
	public def initRandom(nzd:Float) : DupSparseMatrix(this) {
		local().initRandom(nzd);
		sync();
		return this;
	}

	/**
	 * For testing purpose.
	 *
	 * <p> Initialize duplicated sparse matrix with random values.
	 */
	public def initRandom() : DupSparseMatrix(this) {
		local().initRandom();
		sync();
		return this;
	}
	
	public def initRandom(lo:Long, up:Long) : DupSparseMatrix(this) {
		local().initRandom(lo,up);
		sync();
		return this;
	}

	// Data copy and reset


	/**
	 * Allocate memory space with same storage for duplicated sparse matrix(m,n)
	 */
	public def alloc(m:Long, n:Long)  = make(m, n, local().getStorageSize());

	/**
	 * Make a copy of all duplicated sparse matrix in all places.
	 * If the source duplicated matrix is not synchronized in all places,
	 * the clone copy is also not synchronized.
	 */	
	public def clone() : DupSparseMatrix(this.M, this.N) {
		val ds  = DistArray.make[SparseCSC](dupMs.dist);
		finish ateach(val [p]:Point in ds) {
			val mypid = here.id();
			ds(mypid) = this.dupMs(mypid).clone();
		}
		val dsm = new DupSparseMatrix(ds) as DupSparseMatrix(M,N);
		return dsm;
	}


	// Copy 

	public  def copyTo(that:DupSparseMatrix(M,N)):void {
		finish ateach(val [p] :Point in this.dist) {
			val mypid=here.id();
			val spa = this.dupMs(mypid);
			spa.copyTo(that.dupMs(p) as SparseCSC(spa.M, spa.N));
		}
	}
	
	/**
	 * Copy element values to the target matrix in same dimension.
	 *
	 * @param dst      the target dense matrix.		
	 */
	public def copyTo(dm:DenseMatrix(M,N)) {
		local().copyTo(dm);
	}


	/**
	 * Copy element values to the target matrix in all places.
	 *
	 * @param dst      the target dense matrix.		
	 */
	public def copyTo(dm:DupDenseMatrix(M,N)) {
		finish ateach(val [p]:Point in dupMs) {
			local().copyTo(dm.local());
		}
	}
	
	public def copyTo(that:Matrix(M,N)): void {
		if (that instanceof DupSparseMatrix)
			copyTo(that as DupSparseMatrix);
		else if (that instanceof DenseMatrix)
			copyTo(that as DenseMatrix);
		else if (that instanceof DupDenseMatrix)
			copyTo(that as DupDenseMatrix);
		else
			throw new UnsupportedOperationException("copyTo: target matrix type is not supported");
	}
	
	/**
	 * Access data at(x, y)
	 */
    public operator this(x:Long, y:Long):ElemType=local()(x, y);

	/**
	 * Assign v to (x, y) in the copy at here. Other copies are not
	 * modified.
	 */
	public operator this(x:Long,y:Long) = (v:ElemType):ElemType {
		//this.dupMs(here.id()).d(y*this.M+x) = v;
		local()(x, y) = v;
		return v;
	}


	/**
	 * Return the matrix copy at here.
	 */
	public def getMatrix():SparseCSC = this.dupMs(here.id()); 
	
	/**
	 * Return the local copy of sparse matrix at here with dimension check.
	 */
	public def local():SparseCSC(M,N) = this.dupMs(here.id()) as SparseCSC(M,N);

	/**
	 * Return the copy of sparse matrix at place p. Must be executed at
	 * place p.
	 */
	//public def getMatrix(p:Int):SparseCSC(M,N) = this.dupMs(p) as SparseCSC(M,N) ;

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
	    (A instanceof DupSparseMatrix &&
		 (A as DupSparseMatrix).dupMs.dist.equals(this.dupMs.dist));
	
	/**
	 * Broadcast the copy of sparse matrix at here to all other copies.
	 */
	public def sync() : void {
		/* Timing */ val st:Long = Timer.milliTime();
		MatrixBcast.bcast(dupMs);
		/* Timing */ commTime += Timer.milliTime() - st;
	}

	/**
	 * Currently not supported.
	 */
	public def T() :DupDenseMatrix(N,M) {
	    throw new UnsupportedOperationException();
	}

	// Cellwise operation

	/**
	 * Scaling method. All copies are updated concurrently
	 */
 	public def scale(alpha:ElemType) {
		/* Timing */ val st= Timer.milliTime();
		finish ateach(val [p] :Point in this.dupMs) {
			local().scale(alpha);
		}
		/* Timing */ calcTime += Timer.milliTime() - st;
		return this;
    }


	// Cellwise addition


	/**
	 * Not support. Cellwise subtraction.
	 */
	public def cellAdd(A:Matrix(M,N)):DupSparseMatrix(this) {
		throw new UnsupportedOperationException("Not support using sparse matrix to store result");
	}

	public def cellAdd(d:ElemType):DupSparseMatrix(this) {
		throw new UnsupportedOperationException("Not support using sparse matrix to store result");
	}

	/**
	 * dst += this
	 */
    public def cellAddTo(dst:DenseMatrix(M,N)) = local().cellAddTo(dst);

	/**
	 * dst += this for all copies
	 */
    public def cellAddTo(dst:DupDenseMatrix(M,N)) {
		finish ateach(val [p] :Point in this.dupMs) {
			local().cellAddTo(dst.local());
		}
		return dst;
	}


	// Cellwise subtraction


	/**
	 * Not support. Cellwise subtraction.
	 */
	public def cellSub(A:Matrix(M,N)):DupSparseMatrix(this) {
		throw new UnsupportedOperationException("Not support use sparse matrix to store result");
	}
	
	/**
	 * dst = dst - this
	 */
	public def cellSubFrom(dst:DenseMatrix(M,N)) = local().cellSubFrom(dst);

	/**
	 * dst = dst - this for all copies
	 */
	public def cellSubFrom(dst:DupDenseMatrix(M,N)) {
		finish ateach(val [p] :Point in this.dupMs) {
			local().cellSubFrom(dst.local());
		}
		return dst;		
	}


	// Cellwise multiplication

	/**
	 * Not support. Concurrently perform cellwise addition on all copies.
	 */
	public def cellMult(A:Matrix(M,N)):DupSparseMatrix(this)  {
		throw new UnsupportedOperationException("Not support using sparse matrix to store result");
	}

	/**
	 * Compute dst = dst &#42 this;
	 */
	public def cellMultTo(dst:DenseMatrix(M,N)) = local().cellMultTo(dst);


	/**
	 * Compute dst = dist &#42 this for all copies
	 */
	public def cellMultTo(dst:DupDenseMatrix(M,N)) {
		finish ateach(val [p] :Point in this.dupMs) {
			local().cellMultTo(dst.local());
		}
		return dst;	
	} 


	// Cellwise division

	/**
	 * Not support. Concurrently perform cellwise subtraction on all copies
	 */	
	public def cellDiv(A:Matrix(M,N)):DupSparseMatrix(this) {
		throw new UnsupportedOperationException("Not support using sparse matrix to store result");
	}

	/**
	 * dst = this / dst
	 */
	public def cellDivBy(dst:DenseMatrix(M,N)) = local().cellDivBy(dst);


	/**
	 * dst = this / dst for all copies
	 */
	public def cellDivBy(dst:DupDenseMatrix(M,N)) {
		finish ateach(val [p] :Point in this.dupMs) {
			local().cellDivBy(dst.local());
		}
		return dst;		
	}


	// Operator overload

	/**
	 * Perform cell-wise addition, return this + that in a new dup dense matrix. 
	 */
	public operator this + (that:DupSparseMatrix(M,N)):DupDenseMatrix(M,N) {
		val ddm = DupDenseMatrix.make(M,N);
		this.copyTo(ddm);
	    that.cellAddTo(ddm);
	    return ddm;
	}
	
	/**
	 * Perform cell-wise subtraction, return this - that in a new dup dense format
	 */
	public operator this - (that:DupSparseMatrix(M,N)):DupDenseMatrix(M,N) {
		val ddm = DupDenseMatrix.make(M,N);
	    that.copyTo(ddm);
		this.cellSubFrom(ddm);
	    return ddm;
	}
    /**
     * Perform cell-wise multiplication, return this * that in dup dense format
     */
	public operator this * (that:DupSparseMatrix(M,N)):DupDenseMatrix(M,N) {
		val ddm = DupDenseMatrix.make(M,N);
		this.copyTo(ddm);
	    that.cellMultTo(ddm);
	    return ddm;
	}

	/**
	 * Perform cell-wise division, return this / that in a new dup dense matrix
	 */
	public operator this / (that:DupSparseMatrix(M,N)):DupDenseMatrix(M,N) {
		val ddm = DupDenseMatrix.make(M,N);
		that.copyTo(ddm);
	    this.cellDivBy(ddm);
	    return ddm;
	}


	// Multiplication operations 


	/**
	 * Multiplication method by using X10 driver. All copies are updated.
	 * this = A * B if plus is false, else this += A * B
	 */
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):DupSparseMatrix(this) {

		throw new UnsupportedOperationException("Not support using sparse matrix to store result");
	}



	public def transMult(
			A:Matrix{self.N==this.M}, 
			B:Matrix(A.M,this.N), 
			plus:Boolean):DupSparseMatrix(this) {
		
		throw new UnsupportedOperationException("Not support using sparse matrix to store result");
	}




	/**
	 * this = A * B^T
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):DupSparseMatrix(this)  {

		throw new UnsupportedOperationException("Not support using sparse matrix to store result");
	}

	public def getCommTime():Long = this.commTime;
	public def getCalcTime():Long = this.calcTime;

	/** Check integrity */
	public def syncCheck():Boolean {
		val m = local();
		for (var p:Long=0; p<Place.numPlaces(); p++) {

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
		var output:String = "---Duplicated sparse matrix size:["+M+"x"+N+"]---\n";
		output += dupMs(here.id()).toString();
		output += "--------------------------------------------------\n";
		return output;
	}

	public def allToString() : String {
		var output:String = "Duplicated sparse matrix size:["+M+"x"+N+"]\n";
		for (var p:Long=0; p<Place.numPlaces(); p++) { 
			val pid = p;
			val mstr = at(dupMs.dist(pid)) dupMs(pid).toString();
			output += "Duplication at place " + pid + "\n"+mstr;
		}
		return output;
	}
}
