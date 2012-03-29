/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.matrix.distblock;

import x10.util.ArrayList;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.Debug;

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;

import x10.matrix.comm.ArrayBcast;
import x10.matrix.comm.ArrayGather;
import x10.matrix.comm.ArrayReduce;
import x10.matrix.comm.ArrayRemoteCopy;

public type DupVector(m:Int)=DupVector{self.M==m};
public type DupVector(v:DupVector)=DupVector{self==v};

/**
 * 
 */
public class DupVector(M:Int) {
	
	//===================================================================================
	public val dupV:PlaceLocalHandle[Vector];
	private val dupData:PlaceLocalHandle[Array[Double](1){rail}];//Repackage vector.d
	//===================================================================================
	public  var tmpData:PlaceLocalHandle[Array[Double](1){rail}];
	private transient var tmpReady:Boolean;
	//===================================================================================
	/*
	 * Time profiling
	 */
	transient var commTime:Long = 0;
	transient var calcTime:Long = 0;
	
	//===================================================================================
	/**
	 * 
	 */
	public def this(vs:PlaceLocalHandle[Vector]) {
		val m = vs().M;
		property(m);
		dupV  = vs;
		tmpReady = false;
		dupData = PlaceLocalHandle.make[Array[Double](1){rail}](Dist.makeUnique(), ()=>vs().d);
	}
	//===================================================================================
	//====================================================================================

	public static def make(v:Vector):DupVector(v.M){
		val m = v.M;
		val hdl = PlaceLocalHandle.make[Vector](Dist.makeUnique(), ()=>Vector.make(m));
		val newDV = new DupVector(hdl);
		newDV.sync();
		return newDV as DupVector(m);
	}
	//------------------------
	
	public static def make(m:Int) {
		val hdl = PlaceLocalHandle.make[Vector](Dist.makeUnique(), ()=>Vector.make(m));
		return new DupVector(hdl) as DupVector(m);
	}
	
	//====================================================================================
	
	public def alloc(m:Int):DupVector(m) = make(m);
	public def alloc() = alloc(M);
	
	public def clone():DupVector(M) {
		val bs = PlaceLocalHandle.make[Vector](Dist.makeUnique(), 
				()=>dupV().clone());	
		return new DupVector(bs) as DupVector(M);
	}
	
	public def reset() {
		finish ateach (Dist.makeUnique()) {
			dupV().reset();
		}
	}

	public def allocTmp() : void {
		if (tmpReady) return;
		tmpReady = true;
		tmpData = PlaceLocalHandle.make[Array[Double](1){rail}](Dist.makeUnique(), ()=>new Array[Double](dupV().M));
	}
	//====================================================================================
	public def init(dv:Double) : DupVector(this) {
		
		finish ateach (Dist.makeUnique()) {
			dupV().init(dv);
		}
		return this;
	}
	
	public def initRandom() : DupVector(this) {
		dupV().initRandom();
		sync();
		return this;
	}
	
	public def initRandom(lo:Int, up:Int) : DupVector(this) {
		dupV().initRandom(lo, up);
		sync();
		return this;
	}
	
	public def init(f:(Int)=>Double) : DupVector(this) {
		dupV().init(f);
		sync();
		return this;
	}
	
	//==================================================================================
	public def copyTo(den:DenseMatrix):void {
		dupV().copyTo(den);
	}

	public def copyTo(dst:DupVector(M)):void {
		finish ateach (Dist.makeUnique()) {
			dupV().copyTo(dst.dupV());
		}
	}
	
	//==================================================================================
	
	public  operator this(x:Int):Double = dupV()(x);

	public operator this(x:Int)=(dv:Double):Double {
		finish ateach (Dist.makeUnique()) {
			//Remote capture: x, y, d
			dupV()(x) = dv;	
		}
		return dv;
	}
	
	public def local() = dupV() as Vector(M);

	//==================================================================================

	/**
	 * Scaling method. All copies are updated concurrently
	 */
	public def scale(a:Double) {
		finish ateach(Dist.makeUnique()) {
			local().scale(a);
		}
		return this;
	}
	//-------------------------------------------------------------
	
	/**
	 * Cellwise multiplication. 
	 */
	public def cellAdd(A:Vector(M)) {
		val v = local();
		v.cellAdd(A as Vector(v.M));
		sync();
		return this;
	}

	/**
	 * Concurrently perform cellwise addition on all copies.
	 */
	public def cellAdd(that:DupVector(M))  {
		//Debug.assure(this.M==A.M&&this.N==A.N);
		finish ateach(Dist.makeUnique()) {
			val dstv = local() as Vector(M);
			val srcv = that.local() as Vector(M);
			dstv.cellAdd(srcv);
		}
		return this;
	}

	public def cellAdd(dv:Double)  {
		finish ateach(Dist.makeUnique()) {
			local().cellAdd(dv);
		}
		return this;
	}

	//--------------------------------
	// Cellwise subtraction
	//--------------------------------
	public def cellSub(A:Vector(M)) {
		val v = local() as Vector(M);
		v.cellSub(A);
		sync();
		return this;
	}
	/**
	 * Concurrently perform cellwise subtraction on all copies
	 */
	public def cellSub(A:DupVector(M)) {
		finish ateach(Dist.makeUnique()) {
			val dstv = local() as Vector(M);
			val srcv = A.local() as Vector(M);
			dstv.cellSub(srcv);
		}
		return this;
	}

	
	/**
	 * Perform cell-wise subtraction  this = this - dv.
	 */
	public def cellSub(dv:Double):DupVector(this) {
		finish ateach (Dist.makeUnique()) {
			local().cellSub(dv);
		}
		return this;
	}
	
	/**
	 * this = dv - this
	 */
	protected def cellSubFrom(dv:Double):DupVector(this) {
		finish ateach (Dist.makeUnique()) {
			local().cellSubFrom(dv);
		}
		return this;
	}

	//--------------------------------
	// Cellwise multiplication
	//--------------------------------
	/**
	 * Cellwise multiplication. 
	 */
	public def cellMult(A:Vector(M)) {
		val dstv = local();
		dstv.cellMult(A);
		sync();
		return this;
	}
	
	/**
	 * Cellwise multiplication. All copies are modified with
	 * the corresponding vector copies.
	 */
	public def cellMult(A:DupVector(M)) {
		finish ateach(Dist.makeUnique()) {
			local().cellMult(A.local());
		}
		return this;
	}

	//--------------------------------
	// Cellwise division
	//--------------------------------
	/**
	 * this = this / A
	 */
	public def cellDiv(A:Vector(M)) {
		local().cellDiv(A);
		sync();
		return this;
	}
	

	/**
	 * Cellwise division. All copies are modified with
	 * the corresponding vector copies.
	 */	
	public def cellDiv(A:DupVector(M)) {
		finish ateach (Dist.makeUnique()) {
			local().cellDiv(A.local());
		}
		return this;
	}
	
	//====================================================================
	// Operator overloading cellwise operations
	//====================================================================
	// 
	public operator - this            = clone().scale(-1.0) as DupVector(M);
	public operator (v:Double) + this = clone().cellAdd(v)  as DupVector(M);
	public operator this + (v:Double) = clone().cellAdd(v)  as DupVector(M);
	public operator this - (v:Double) = clone().cellSub(v)  as DupVector(M);
	public operator (v:Double) - this = clone().cellSubFrom(v) as DupVector(M);
	public operator this / (v:Double) = clone().scale(1.0/v)   as DupVector(M);
	
	public operator this * (alpha:Double) = clone().scale(alpha) as DupVector(M);
	public operator this * (alpha:Int)    = clone().scale(alpha  as Double) as DupVector(M);
	public operator (alpha:Double) * this = this * alpha;
	public operator (alpha:Int) * this    = this * alpha;
	
	public operator this + (that:DupVector(M)) = clone().cellAdd(that)  as DupVector(M);
	public operator this - (that:DupVector(M)) = clone().cellSub(that)  as DupVector(M);
	public operator this * (that:DupVector(M)) = clone().cellMult(that) as DupVector(M);
	public operator this / (that:DupVector(M)) = clone().cellDiv(that)  as DupVector(M);
		
	//====================================================================
	// Multiplication operations 
	//====================================================================
	public def mult(mA:DistBlockMatrix(this.M), vB:DistVector(mA.N), plus:Boolean):DupVector(this) =
		DistDupVectorMult.comp(mA, vB, this, plus);
	
	public def mult(vB:DistVector, mA:DistBlockMatrix(vB.M, this.M), plus:Boolean):DupVector(this) =
		DistDupVectorMult.comp(vB, mA, this, plus);
	
	//-------------------------
	public operator this % (that:DistBlockMatrix(M)) = 
		DistDupVectorMult.comp(this, that, DistVector.make(that.N, that.getGrid().colBs), false);

	public operator (that:DistBlockMatrix{self.N==this.M}) % this = 
		DistDupVectorMult.comp(that , this, DistVector.make(that.M,that.getGrid().rowBs), false);

	//==================================================================================
	public def sync():void {
		/* Timing */ val st = Timer.milliTime();
		ArrayBcast.bcast(dupData);
		/* Timing */ commTime += Timer.milliTime() - st;
		//Debug.flushln("Calling sync "+commTime);
	}
	//==================================================================================
	
	public def reduce(opFunc:(Array[Double](1){rail},Array[Double](1){rail},Int)=>Int): void {
		allocTmp();
		/* Timing */ val st = Timer.milliTime();
		ArrayReduce.reduce(dupData, tmpData, this.M, opFunc);
		/* Timing */ commTime += Timer.milliTime() - st;
	}
	
	public def reduceSum(): void {
		allocTmp();
		/* Timing */ val st = Timer.milliTime();
		ArrayReduce.reduceSum(dupData, tmpData, this.M);
		/* Timing */ commTime += Timer.milliTime() - st;
	}
	
	public def allReduceSum(): void {
		allocTmp();
		/* Timing */ val st = Timer.milliTime();
		ArrayReduce.allReduceSum(dupData, tmpData, this.M);
		/* Timing */ commTime += Timer.milliTime() - st;
	}	
	//==================================================================================

	public def likeMe(that:DupVector): Boolean = (this.M==that.M);
		
	//==================================================================================

	public def checkSync():Boolean {
		val rootvec  = dupV();
		var retval:Boolean = true;
		for (var p:Int=0; p < Place.MAX_PLACES && retval; p++) {
			val pid = p;
			if (p == here.id()) continue;
			retval &= at (Dist.makeUnique()(pid)) {
					val vec = dupV();
					rootvec.equals(vec as Vector(rootvec.M))
			};
		}
		return retval;
	}
	
	public def equals(dv:DupVector(this.M)):Boolean {
		var ret:Boolean = true;
		for (var p:Int=0; p<Place.MAX_PLACES &&ret; p++) {
			val pid = p;
			ret &= at (Dist.makeUnique()(pid)) this.local().equals(dv.local());
		}
		return ret;
	}
	
	public def equals(dval:Double):Boolean {
		var ret:Boolean = true;
		for (var p:Int=0; p<Place.MAX_PLACES &&ret; p++) {
			val pid = p;
			ret &= at (Dist.makeUnique()(pid)) this.local().equals(dval);
		}
		return ret;
	}
	
	//===============================================================================
	public def getCalcTime() = calcTime;
	public def getCommTime() = commTime;
	
	//==================================================================================
	
	public def toString() :String {
		var output:String = "---Duplicated Vector:["+M+"], local copy---\n";
		output += dupV().toString();
		output += "--------------------------------------------------\n";
		return output;
	}
	//
	public def print()  { this.print("");}
	public def print(msg:String) {
		Console.OUT.print(msg);
		Console.OUT.print(this.toString());
		Console.OUT.flush();
	}
	public def printAllCopies() {
		var output:String = "-------- Duplicate vector :["+M+"] ---------\n";
		for (p in Place.places()) {
			output += "Copy at place " + p.id() +"\n";
			output += at (p) { dupV().toString()};
		}
		output += "--------------------------------------------------\n";
		Console.OUT.print(output);
		Console.OUT.flush();
	}	
}

