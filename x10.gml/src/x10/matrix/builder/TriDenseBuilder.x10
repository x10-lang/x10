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

package x10.matrix.builder;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.TriDense;
import x10.matrix.ElemType;

import x10.matrix.util.Debug;
import x10.matrix.util.RandTool;
import x10.matrix.util.MathTool;

public type TriDenseBuilder(blder:TriDenseBuilder)=TriDenseBuilder{self==blder};
public type TriDenseBuilder(m:Long)=TriDenseBuilder{self.M==m,self.N==m};

/**
 * Builder/Initializer of symmetric dense matrix.
 */
public class TriDenseBuilder extends DenseBuilder{self.M==self.N} implements MatrixBuilder {
    
    val upper:Boolean;
    public def this(tri:TriDense) {
        super(tri as DenseMatrix{self.M==self.N});
        upper=tri.upper;
    }
    
    public def this(up:Boolean, den:DenseMatrix{self.M==self.N}) {
        super(den);
        upper=up;
    }
    
    /**
     * Creat dense builder and dense matrix
     * @param m   rows, leading dimension
     * @param n   columns
     */
    public static def make(up:Boolean, m:Long): TriDenseBuilder(m) {
        val bdr = new TriDenseBuilder(TriDense.make(up, m));
        return bdr as TriDenseBuilder(m);
    }
    
    /**
     * Initial triangular dense matrix with initial function.
     */
    public def initUpper(initFunc:(Long,Long)=>ElemType):TriDenseBuilder(this) {
        var i:Long =0;
        for (var c:Long=0; c<this.N; c++) {
            var r:Long=0;
            for (; r<=c; r++, i++ )   dense.d(i) = initFunc(r, c);
            for (;r<this.M; r++, i++) dense.d(i) = (0.0 as ElemType);
        }
        return this;
    }
    
    public def initLower(initFunc:(Long,Long)=>ElemType):TriDenseBuilder(this) {
        var i:Long =0;
        for (var c:Long=0; c<this.N; c++ ) {
            var r:Long=0;
            for (; r<c; r++, i++ )    dense.d(i) = (0.0 as ElemType);
            for (;r<this.M; r++, i++) dense.d(i) = initFunc(r, c);
        }
        return this;
    }
    
    public def init(initFunc:(Long,Long)=>ElemType):TriDenseBuilder(this) =
        upper?initUpper(initFunc):initLower(initFunc);
    
    // replicated from superclass to workaround xlC bug with using & itables
    public def init(srcden:DenseMatrix) = init(0, 0, srcden);
    public def init(rowOff:Long, colOff:Long, srcden:DenseMatrix) : DenseBuilder(this) {
        Debug.assure(rowOff+srcden.M<=dense.M, "Dense builder cannot using given matrix to initialize. Row overflow");
        Debug.assure(colOff+srcden.N<=dense.N, "Dense builder cannot using given matrix to initialize. Column overflow");
        var stt:Long = rowOff;
        for (var c:Long=colOff; c<colOff+srcden.N; c++, stt+= dense.M)
            Rail.copy[ElemType](srcden.d, 0, dense.d, stt, srcden.M);
        return this;
    }
    
    
    /**
     * Initial symmetric dense matrix with initial function and location in dense matrix is generated randomly.
     * @param nzDensity    nonzero sparsity.
     * @param initFunc     nonzero value generating function.
     */
    public def initUpperRandom(nzDensity:Float, initFunc:(Long,Long)=>ElemType):TriDenseBuilder(this) {
        val maxdst:Long = ((1.0/nzDensity) as Int) * 2 - 1;
        var i:Long= RandTool.nextLong(maxdst/2);
        var stt:Long=0;
        for (var c:Long=0; c<this.N; c++, stt+=dense.M, i+=(this.M-c)  ) {
            var r:Long = i - stt;
            for (;r<=c; i+= RandTool.nextLong(maxdst)+1, r=i-stt) 
                dense.d(i) = initFunc(r, c);
        }
        return this;
    }
    
    public def initLowerRandom(nzDensity:Float, initFunc:(Long,Long)=>ElemType):TriDenseBuilder(this) {
        val maxdst:Long = ((1.0/nzDensity) as Int) * 2 - 1;
        var i:Long= RandTool.nextLong(maxdst/2);
        var stt:Long=0;
        for (var c:Long=0; c<this.N; c++, stt+=dense.M, i+=c  ) {
            var r:Long = i - stt;
            for (;r<this.M; i+= RandTool.nextLong(maxdst)+1, r=i-stt) 
                dense.d(i) = initFunc(r, c);
        }
        return this;
    }
    
    public def initRandom(nzDensity:Float, initFunc:(Long,Long)=>ElemType):TriDenseBuilder(this) =
        upper?initUpperRandom(nzDensity, initFunc):initLowerRandom(nzDensity, initFunc);
    
    public def initRandom(nzDensity:Float): DenseBuilder(this) =
        initRandom(nzDensity, (Long,Long)=>RandTool.nextElemType[ElemType]());
    
    
    public def set(r:Long, c:Long, dv:ElemType) : void {
        if ((upper && r<=c)||(upper==false && r>=c))
            dense(r, c) = dv;
        else {
            Debug.flushln("Warning seting triangular zero part");
        }
    }
    
    public def reset(r:Long, c:Long) : Boolean {
        dense(r, c) =(0.0 as ElemType);
        return true;
    }
    
    /**
     * copy from upper or lower triangular and its mirror part.
     */
    public def initFrom(src:DenseMatrix) {
        if (upper)
            copyUpper(src, this.dense as DenseMatrix(src.N));
        else
            copyLower(src, this.dense as DenseMatrix(src.M));
    }
    
    public static def copyUpper(src:DenseMatrix, dst:DenseMatrix(src.N)) {
        var srcoff:Long=0;
        var dstoff:Long=0;
        for (var len:Long=1; len<=src.N; len++, srcoff+=src.M, dstoff+=dst.M) {
            Rail.copy[ElemType](src.d, srcoff, dst.d, dstoff, len);
        }
    }
    
    public static def copyLower(src:DenseMatrix, dst:DenseMatrix(src.M)) {
        var srcoff:Long=0;
        var dstoff:Long=0;
        for (var len:Long=src.M; len>0; len--, srcoff+=src.M+1, dstoff+=dst.M+1) {
            Rail.copy[ElemType](src.d, srcoff, dst.d, dstoff, len);
        }               
    }
    
    
    
    public def isUpperZero():Boolean {
        var ret:Boolean = true;
        for (var c:Long=0; c<M&&ret; c++)
            for (var r:Long=0; r<c&&ret; r++)
                ret &= MathTool.isZero(dense(r, c));
        return ret;
    }
    
    public def isLowerZero():Boolean {
        var ret:Boolean = true;
        for (var c:Long=0; c<M&&ret; c++)
            for (var r:Long=c+1; r<M&&ret; r++)
                ret &= MathTool.isZero(dense(r, c));
        return ret;
    }
    
    /**
     * Convert to symmetric dense builder use the same memory space.
     */
    public def toSymDenseBuilder():SymDenseBuilder(M) {
        val symbld = new SymDenseBuilder(dense as DenseMatrix{self.M==self.N});
        if (upper)
            symbld.mirrorToLower();
        else
            symbld.mirrorToUpper();
        return symbld as SymDenseBuilder(M);
    }
    
    public def toMatrix():Matrix = dense as Matrix;
    public def toTriDense() : TriDense(M) {
        val tri = new TriDense(upper, M, dense.d);
        return tri as TriDense(M);
    }
                                                                  }
