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

import x10.util.Pair;
import x10.util.ArrayList;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.MathTool;

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

import x10.matrix.block.Grid;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.BlockMatrix;
import x10.matrix.block.DenseBlock;
import x10.matrix.block.SparseBlock;

import x10.matrix.comm.ArrayBcast;
import x10.matrix.comm.ArrayGather;
import x10.matrix.comm.ArrayScatter;
import x10.matrix.comm.ArrayReduce;
import x10.matrix.comm.ArrayRemoteCopy;

public type DistVector(m:Int)=DistVector{self.M==m};
public type DistVector(v:DistVector)=DistVector{self==v};

/**
 * 
 */
public class DistVector(M:Int) {
	
	//===================================================================================
	public val distV:PlaceLocalHandle[Vector];
	//Repack dist vector to dist array
	public val distData:PlaceLocalHandle[Array[Double](1){rail}];
	//===================================================================================
	public transient val segSize:Array[Int](1){rail};
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
	public def this(m:Int, vs:PlaceLocalHandle[Vector], segsz:Array[Int](1){rail}) {
		property(m);
		distV  = vs;
		segSize = segsz;
		distData = PlaceLocalHandle.make[Array[Double](1){rail}](Dist.makeUnique(), ()=>vs().d);
	}
	//===================================================================================
	//====================================================================================

	public static def make(m:Int, segNum:Int):DistVector(m) {
		val hdv = PlaceLocalHandle.make[Vector](Dist.makeUnique(),
							()=>Vector.make(Grid.compBlockSize(m, segNum, here.id())));
		val slst = new Array[Int](segNum, (i:Int)=>Grid.compBlockSize(m, segNum, i));
		return new DistVector(m, hdv, slst) as DistVector(m);
	}
	
	public static def make(m:Int) = make (m, Place.MAX_PLACES);

	public static def make(m:Int, segsz:Array[Int](1){rail}):DistVector(m) {
		val hdv = PlaceLocalHandle.make[Vector](Dist.makeUnique(), ()=>Vector.make(segsz(here.id())));
		return new DistVector(m, hdv, segsz) as DistVector(m);
	}

	// public static def make(m:Int, numBlk:Int, numPlz:Int):DistVector(m) {
	// 	Debug.assure(m>=numBlk&&numBlk>=numPlz, "Creating dist vector fails. Unsatisfied Vsize <= num blocks < num places");
	// 	val szlst = new Array[Int](numPlz, (i:Int)=>0);
	// 	
	// 	var bcnt:Int = 0;
	// 	for (var p:Int=0; p<numPlz; p++) {
	// 		val nblk = Grid.compBlockSize(numBlk, numPlz, p);
	// 		for (var b:Int=0; b<nblk; b++, bcnt++) {
	// 			szlst(p) += Grid.compBlockSize(m, numBlk, bcnt);
	// 		}
	// 	}
	// 	Debug.flushln("DistVector size:"+szlst.toString());
	// 	return make(m, szlst);
	// }
	//====================================================================================
	
	public def alloc(m:Int):DistVector(m) = make(m);
	public def alloc() = alloc(M);
	
	public def clone():DistVector(M) {
		val dv = PlaceLocalHandle.make[Vector](Dist.makeUnique(), 
				()=>distV().clone());	
		return new DistVector(M, dv, segSize) as DistVector(M);
	}
	
	public def reset() {
		finish ateach (Dist.makeUnique()) {
			distV().reset();
		}
	}

	//====================================================================================
	public def init(dv:Double) : DistVector(this) {
		
		finish ateach (Dist.makeUnique()) {
			distV().init(dv);
		}
		return this;
	}
	
	public def initRandom() : DistVector(this) {
		finish ateach (Dist.makeUnique()) {
			distV().initRandom();
		}
		return this;
	}
	
	public def initRandom(lo:Int, up:Int) : DistVector(this) {
		finish ateach (Dist.makeUnique()) {
			distV().initRandom(lo, up);
		}
		return this;
	}
	
	public def init(f:(Int)=>Double) : DistVector(this) {
		finish ateach (Dist.makeUnique()) {
			distV().init(f);
		}
		return this;
	}
	
	//==================================================================================
	public def copyTo(dst:DistVector(M)):void {
		finish ateach (Dist.makeUnique()) {
			distV().copyTo(dst.distV());
		}
	}

	public def copyTo(vec:Vector(M)):void {
		ArrayGather.gather(distData, vec.d, segSize);
	}

	public def copyFrom(vec:Vector(M)): void {
		ArrayScatter.scatter(vec.d, distData, segSize);
	}
	/**
	 * For debug and verification use only. 
	 * This method is not efficient for actual computation.
	 * Use copyTo(...) method instead.
	 */
	public def toVector():Vector(M) {
		val nv = Vector.make(M);
		for (var i:Int=0; i<M; i++) nv(i) = this(i);
		return nv;
	}
	//==================================================================================
	protected def find(var pos:Int):Pair[Int, Int] {
		var seg:Int=0;
		var idx:Int=0;
		Debug.assure(pos<M, "Vector data access out of bound");
		for (var i:Int=0; i<segSize.size; i++) {
			if (pos < segSize(i))
				return new Pair[Int,Int](i, pos);
			pos -= segSize(i);
		}
		Debug.exit("Error in searching index in vector");
		return new Pair[Int,Int](-1, -1);
	}
	
	public  operator this(x:Int):Double {
		val loc = find(x);
		val seg = loc.first;
		val off = loc.second;
		val dat = at (Dist.makeUnique()(seg)) distV()(off);
		return dat;
	}

	public operator this(x:Int)=(dv:Double):Double {
		val loc = find(x);
		val seg = loc.first;
		val off = loc.second;
		at (Dist.makeUnique()(seg)) distV()(off)=dv;
		return dv;
	}

	//==================================================================================

	/**
	 * Scaling method. All copies are updated concurrently
	 */
	public def scale(a:Double) {
		finish ateach(Dist.makeUnique()) {
			distV().scale(a);
		}
		return this;
	}
	//-------------------------------------------------------------


	/**
	 * Concurrently perform cellwise addition on all copies.
	 */
	public def cellAdd(that:DistVector(M))  {
		//Debug.assure(this.M==A.M&&this.N==A.N);
		finish ateach(Dist.makeUnique()) {
			val dst = distV();
			val src = that.distV() as Vector(dst.M);
			dst.cellAdd(src);
		}
		return this;
	}

	public def cellAdd(dv:Double)  {
		finish ateach(Dist.makeUnique()) {
			distV().cellAdd(dv);
		}
		return this;
	}

	//--------------------------------
	// Cellwise subtraction
	//--------------------------------

	/**
	 * Concurrently perform cellwise subtraction on all copies
	 */
	public def cellSub(A:DistVector(M)) {
		finish ateach(Dist.makeUnique()) {
			val dst = distV();
			val src = A.distV() as Vector(dst.M);
			dst.cellSub(src);
		}
		return this;
	}

	
	/**
	 * Perform cell-wise subtraction  this = this - dv.
	 */
	public def cellSub(dv:Double):DistVector(this) {
		finish ateach (Dist.makeUnique()) {
			distV().cellSub(dv);
		}
		return this;
	}
	
	/**
	 * this = dv - this
	 */
	protected def cellSubFrom(dv:Double):DistVector(this) {
		finish ateach (Dist.makeUnique()) {
			distV().cellSubFrom(dv);
		}
		return this;
	}

	//--------------------------------
	// Cellwise multiplication
	//--------------------------------
	
	/**
	 * Cellwise multiplication. All copies are modified with
	 * the corresponding vector copies.
	 */
	public def cellMult(A:DistVector(M)) {
		finish ateach(Dist.makeUnique()) {
			val dst = this.distV();
			val src = A.distV() as Vector(dst.M);
			dst.cellMult(src);
		}
		return this;
	}

	//--------------------------------
	// Cellwise division
	//--------------------------------
	/**
	 * Cellwise division. All copies are modified with
	 * the corresponding vector copies.
	 */	
	public def cellDiv(A:DistVector(M)) {
		finish ateach (Dist.makeUnique()) {
			val dst = this.distV();
			val src = A.distV() as Vector(dst.M);
			dst.cellDiv(src);
		}
		return this;
	}
	
	//====================================================================
	// Operator overloading cellwise operations
	//====================================================================
	// 
	public operator - this            = clone().scale(-1.0) as DistVector(M);
	public operator (v:Double) + this = clone().cellAdd(v)  as DistVector(M);
	public operator this + (v:Double) = clone().cellAdd(v)  as DistVector(M);
	public operator this - (v:Double) = clone().cellSub(v)  as DistVector(M);
	public operator (v:Double) - this = clone().cellSubFrom(v) as DistVector(M);
	public operator this / (v:Double) = clone().scale(1.0/v)   as DistVector(M);
	
	public operator this * (alpha:Double) = clone().scale(alpha) as DistVector(M);
	public operator this * (alpha:Int)    = clone().scale(alpha  as Double) as DistVector(M);
	public operator (alpha:Double) * this = this * alpha;
	public operator (alpha:Int) * this    = this * alpha;
	
	public operator this + (that:DistVector(M)) = clone().cellAdd(that)  as DistVector(M);
	public operator this - (that:DistVector(M)) = clone().cellSub(that)  as DistVector(M);
	public operator this * (that:DistVector(M)) = clone().cellMult(that) as DistVector(M);
	public operator this / (that:DistVector(M)) = clone().cellDiv(that)  as DistVector(M);
		
	//====================================================================
	// Multiplication operations 
	//====================================================================
	public def mult(mA:DistBlockMatrix(M), vB:DupVector(mA.N), plus:Boolean):DistVector(this) =
		DistDupVectorMult.comp(mA, vB, this, plus);
	
	public def mult(vB:DupVector, mA:DistBlockMatrix(vB.M, this.M), plus:Boolean):DistVector(this) =
		DistDupVectorMult.comp(vB, mA, this, plus);
	
	//------------
	public operator this % (that:DistBlockMatrix(this.M)) = 
	 	DistDupVectorMult.comp(this, that, DupVector.make(that.N), false);
	public operator (that:DistBlockMatrix{self.N==this.M}) % this = 
		DistDupVectorMult.comp(that, this, DupVector.make(that.M), false);
	
	//==================================================================================
	//==================================================================================

	public def likeMe(that:DistVector): Boolean  {
		if (this.M!=that.M) return false;
		for (var i:Int=0; i<segSize.size; i++)
			if (this.segSize(i) != that.segSize(i)) return false;
		return true;
	}
		
	//-------------------------------------------------
	public def equals(dv:DistVector(this.M)):Boolean {
		var ret:Boolean = true;
		for (var p:Int=0; p<Place.MAX_PLACES &&ret; p++) {
			val pid = p;
			ret &= at (Dist.makeUnique()(pid)) {
				val srcv = distV();
				val tgtv = dv.distV() as Vector(srcv.M);
				srcv.equals(tgtv)
			};
		}
		return ret;
	}
	public def equals(that:Vector(this.M)):Boolean {
		var ret:Boolean = true;
		var i:Int=0;
		for (; i<M&&ret; i++) ret &= MathTool.isZero(this(i)-that(i));
		if (!ret) {
			Debug.flushln("Diff found at index:"+i+" value: "+this(i)+" <> "+that(i));
		}
		return ret;
	}
	
	public def equals(dval:Double):Boolean {
		var ret:Boolean = true;
		for (var p:Int=0; p<Place.MAX_PLACES &&ret; p++) {
			val pid = p;
			ret &= at (Dist.makeUnique()(pid)) distV().equals(dval);
		}
		return ret;
	}
	//==================================================================================
	//===============================================================================
	public def getCalcTime() = calcTime;
	public def getCommTime() = commTime;
	
	//==================================================================================
	public def toString() :String {
		var output:String = "---Distributed Vector:["+M+"], ---\n[ ";
		for (var i:Int=0; i<M-1; i++) output += this(i).toString()+",";
		
		output += this(M-1).toString()+" ]\n--------------------------------------------------\n";
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
		var output:String = "-------- Distributed vector :["+M+"] ---------\n";
		for (p in Place.places()) {
			output += "Segment vector at place " + p.id() +"\n";
			output += at (p) { distV().toString()};
		}
		output += "--------------------------------------------------\n";
		Console.OUT.print(output);
		Console.OUT.flush();
	}	
}

