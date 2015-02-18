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

package x10.matrix;

import x10.compiler.Inline;
import x10.util.RailUtils;
import x10.util.StringBuilder;

import x10.matrix.blas.BLAS;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.util.Debug;
import x10.matrix.util.MathTool;
import x10.matrix.util.RandTool;
import x10.matrix.util.ElemTypeTool;
import x10.util.resilient.Snapshottable;
import x10.util.resilient.DistObjectSnapshot;
import x10.util.resilient.VectorSnapshotInfo;
public type Vector(m:Long)=Vector{self.M==m};
public type Vector(v:Vector)=Vector{self==v};

/**
 * A vector of length M.
 * This implementation has a dense, single-place representation, using
 * a Rail as backing storage.
 * <p> Methods include:
 * <p> 1) product of two vectors: Mx1 * 1xM,
 * <p> 2) product of vector and a scalar: Mx1 * 1,
 * <p> 3) product of a scalar and a vector: 1 * 1xM,
 * <p> 4) addition of two vectors: Mx1 + Mx1,
 * <p> 5) subtraction of two vectors: Mx1 - Mx1,
 * <p> 6) addition of a vector and a scalar: Mx1 + 1,
 * <p> 7) subtraction of a scalar from a vector: Mx1 - 1,
 * <p> 8) inverse of a vector: Mx1
 * <p> 9) norm of a vector: Mx1
 */
public class Vector(M:Long) implements (Long) => ElemType, Snapshottable {
    /** Vector data */
    public val d:Rail[ElemType]{self!=null,self.size==M};

    public def this(x:Rail[ElemType]{self!=null}):Vector(x.size) {
        property(x.size);
        this.d=x;
    }

    /** Copy constructor */    
    public def this(src:Vector):Vector(src.M) {
        property(src.M);
        this.d = new Rail[ElemType](src.d);
    }

    /**
     * Construct a Vector of size M initialized to the result of evaluating 
     * init for each element
     * @param M the size of the vector
     * @param init the function to use to compute the initial value of each element
     */
    public def this(M:Long, f:(Long)=>ElemType) {
        property(M);
        val raw = Unsafe.allocRailUninitialized[ElemType](M);
        for (i in 0..(M-1)) raw(i) = f(i);
        this.d = raw;
    }

    public static def make(n:Long, v:ElemType) {
        val d = new Rail[ElemType](n, v);
        return new Vector(d);
    }

    public static def make(n:Long) {
        val d = new Rail[ElemType](n);
        return new Vector(d);
    }

    /**
     * Initialize all elements of the vector with a constant value.
     * @param  iv     the constant value
     */    
    public def init(iv:ElemType): Vector(this) {
         for (i in 0..(M-1))    
            this.d(i) = iv;
        return this;
    }

    /**
     * Initialize vector with random values between 0.0 and 1.0.
     */    
    public def initRandom(): Vector(this) {
        val rgen = RandTool.getRandGen();
         for (i in 0..(M-1))
            this.d(i) = RandTool.nextElemType[ElemType](rgen);
        return this;
    }
    
    /**
     * Initialize this vector with random 
     * values in the specified range.
     * @param min lower bound of random values
     * @param max upper bound of random values
     */    
    public def initRandom(min:Long, max:Long):Vector(this) {
        val len = Math.abs(max-min)+1;
        val rgen = RandTool.getRandGen();
         for (i in 0..(M-1))
            this.d(i) = rgen.nextLong(len)+min;
        return this;
    }
    
    /**
     * Init with function
     */
    public def init(f:(Long)=>ElemType): Vector(this) {
         for (i in 0..(M-1))
            this.d(i) = f(i);
        return this;
    }

    public def rail():Rail[ElemType] = d;

    public def clone():Vector(M) {
        val nv = new Rail[ElemType](this.d);
        return new Vector(nv);
    }

    // Data access and set
    public def apply(i:Long)=d(i);
    public operator this(i:Long) = d(i);

    public operator this(i:Long)=(v:ElemType):ElemType {
        this.d(i) = v;
        return v;
    }
    
    /** 
     * Subset of vector from off with length of len
     */
    public def subset(off:Long, len:Long):Vector(len) {
        val na = new Rail[ElemType](len);
        Rail.copy(this.d, off, na, 0L, len);
        return new Vector(na);
    }
    
    public def reset():void {
        d.clear(0, M);
    }

    // Copy all data from vector v to local at dst_off
    public static def copyTo(src:Vector, soff:Long, dst:Vector, doff:Long, len:Long) {
        Debug.assure(soff+len<=src.M && doff+len<=dst.M, "Buffer overflow in vector copy");
        Rail.copy(src.d, soff, dst.d, doff, len);
    }
    
    // Copy part of data from vector v to local starting from index 0
    public  def copyTo(v:Vector) {
        copyTo(this, 0L, v, 0L, this.M);
    }
    
    public def copyTo(mat:DenseMatrix) {
        Rail.copy(this.d, 0L, mat.d, 0L, M);
    }


    // Cell-wise operations
    
    /**
     * this *= alpha
     * Product of a vector and a scalar: Mx1 * 1
     */
    public def scale(alpha:ElemType)
        = map((x:ElemType)=>{alpha * x});

    /**
     * this = alpha * V
     */
    public def scale(alpha:ElemType, V:Vector(M))
        = map(V, (v:ElemType)=> {alpha * v});

    /**
     * this += alpha * V
     */
    public def scaleAdd(alpha:ElemType, V:Vector(M))
        = map(this, V, (x:ElemType, v:ElemType)=> {x + alpha * v});

    /**
     * Cell-wise mulitply of two vectors
     */
    public def cellMult(V:Vector(M))
        = map(this, V, (x:ElemType, v:ElemType)=> {x * v});

    /**
     * Addition of two vectors: Mx1 + Mx1
     */
    public def cellAdd(V:Vector(M))
        = map(this, V, (x:ElemType, v:ElemType)=> {x + v});

    public def cellAdd(d:ElemType)
        = map((x:ElemType)=> {x + d});

    /**
     * this = A + B
     * Cellwise addition of two vectors, storing the result in this vector.
     */
    public def cellAdd(A:Vector(M), B:Vector(M))
        = map(A, B, (a:ElemType, b:ElemType)=> {a + b});

    /** 
     * Subtract vector V from this vector
     */
    public def cellSub(V:Vector(M))
        = map(this, V, (x:ElemType, v:ElemType)=> {x - v});

    /**
     * Subtract the scalar d from this vector
     */
    public def cellSub(d:ElemType)
        = map((x:ElemType)=> {x - d});

    /**
     * cellwise division: this = this / d;
     */
    public  def cellDiv(d:ElemType)
        = map((x:ElemType)=> {x / d});

    public def cellDiv(V:Vector(this.M))
        = map(this, V, (x:ElemType, v:ElemType)=> {x / v});

    /**
     * cellwise division: this = d / this;
     */
    public def cellDivBy(d:ElemType)
        = map((x:ElemType)=> {d / x});

    /**
     * Product transition of a vector: Mx1 * (Mx1)^T
     * Return this^T * x.
     */
    public def blasTransProduct(x:Vector):ElemType =
        BLAS.compDotProd(this.M, this.d, x.d);
    

    /**
     * Dot (scalar) product of this vector with another vector
     */
    public def dot(v:Vector(M)):ElemType {
        var d:ElemType = ElemTypeTool.zero;
         for (i in 0..(M-1))
            d += this.d(i) * v.d(i);
        return d;
    }

    public def dotProd(v:Vector(M)) = dot(v);

    // Using Blas routines: self = op(A)* b, self += op(A) * b,

    public def mult(A:Matrix(M), B:Vector(A.N), plus:Boolean): Vector(this) =
        VectorMult.comp(A, B, this, plus);
 
    public def transMult(A:Matrix{self.N==this.M}, B:Vector(A.M), plus:Boolean) =
        VectorMult.comp(B, A, this, plus);
    
    public def mult(A:Matrix(M), B:Vector(A.N)): Vector(this) = VectorMult.comp(A, B, this, false);
    public def transMult(A:Matrix{self.N==this.M}, B:Vector(A.M)) = VectorMult.comp(B, A, this, false);


    public def mult(B:Vector, A:Matrix(B.M,this.M), plus:Boolean)      = 
        VectorMult.comp(B, A, this, plus);
    public def multTrans(B:Vector, A:Matrix(this.M,B.M), plus:Boolean) = 
        VectorMult.comp(A, B, this, plus);

    public def mult(B:Vector, A:Matrix(B.M,this.M))      = VectorMult.comp(B, A, this, false);
    public def multTrans(B:Vector, A:Matrix(this.M,B.M)) = VectorMult.comp(A, B, this, false);
    

    // Dense-vector multiply

    public def mult(A:DenseMatrix(this.M), B:Vector(A.N), plus:Boolean) = 
        VectorMult.comp(A, B, this, plus);    
    public  def transMult(A:DenseMatrix{self.N==this.M}, B:Vector(A.M), plus:Boolean) = 
        VectorMult.comp(B, A, this, plus);
    
    public def mult(A:DenseMatrix(this.M), B:Vector(A.N))               = VectorMult.comp(A, B, this, false);    
    public def transMult(A:DenseMatrix{self.N==this.M}, B:Vector(A.M)) = VectorMult.comp(B, A, this, false);


    public def mult(B:Vector, A:DenseMatrix(B.M,this.M), plus:Boolean)      = 
        VectorMult.comp(B, A, this, plus);
    public def multTrans(B:Vector, A:DenseMatrix(this.M,B.M), plus:Boolean) = 
        VectorMult.comp(A, B, this, plus);
        
    public def mult(B:Vector, A:DenseMatrix(B.M,this.M))      =VectorMult.comp(B, A, this, false);
    public def multTrans(B:Vector, A:DenseMatrix(this.M,B.M)) =VectorMult.comp(A, B, this, false);

    // Symmetric-vector multiply

    public  def mult(A:SymDense(this.M), B:Vector(A.N), plus:Boolean) =
        VectorMult.comp(A, B, this, plus);
    
    public  def transMult(A:SymDense(this.M), B:Vector(A.N), plus:Boolean) =
        VectorMult.comp(B, A, this, plus);

    public  def mult(A:SymDense(this.M), B:Vector(A.N))      = VectorMult.comp(A, B, this, false);
    public  def transMult(A:SymDense(this.M), B:Vector(A.N)) = VectorMult.comp(B, A, this, false);


    public def mult(B:Vector, A:SymDense(B.M,this.M), plus:Boolean)      = 
        VectorMult.comp(B, A, this, plus);
    public def multTrans(B:Vector, A:SymDense(this.M,B.M), plus:Boolean) = 
        VectorMult.comp(A, B, this, plus);

    public def mult(B:Vector, A:SymDense(B.M,this.M))      = VectorMult.comp(B, A, this, false);
    public def multTrans(B:Vector, A:SymDense(this.M,B.M)) = VectorMult.comp(A, B, this, false);


    // Triangular-vector multiply

    // this = A * this
    public  def mult(A:TriDense(this.M)) =
        VectorMult.comp(A, this);
    
    public  def transMult(A:TriDense(this.M)) =
        VectorMult.comp(this, A);
    


    // Operand overloading

    // Operator add
    public  operator this + (that:Vector(M)) = this.clone().cellAdd(that) as Vector(M);
    public  operator this + (dv:ElemType)      = this.clone().cellAdd(dv)   as Vector(M);
    // Operator sub
    public  operator this - (that:Vector(M)) = this.clone().cellSub(that)   as Vector(M);
    public  operator this - (dv:ElemType)      = this.clone().cellSub(dv)     as Vector(M);
    
    // Operator cellwise multiply
    public  operator this * (dv:ElemType)      = this.clone().scale(dv)      as Vector(M);
    public  operator (dv:ElemType) * this      = this.clone().scale(dv)      as Vector(M);
    public  operator this * (that:Vector(M)) = this.clone().cellMult(that) as Vector(M);

    // Operator cellwise div
    public  operator this / (dv:ElemType)           = this.clone().cellDiv(dv)   as Vector(M);
    public  operator this / (that:Vector(this.M)) = this.clone().cellDiv(that) as Vector(M);
    public  operator (dv:ElemType) / this           = this.clone().cellDivBy(dv) as Vector(M);

    //Righ-side Operand overload
    public  operator this % (that:Matrix(M)) = 
        VectorMult.comp(this, that, Vector.make(that.N), false) as Vector(that.N);
    public  operator this % (that:DenseMatrix(M)) =
        VectorMult.comp(this, that, Vector.make(that.N), false) as Vector(that.N);
    public  operator this % (that:SymDense(M)) =
        VectorMult.comp(this, that, Vector.make(that.N), false) as Vector(that.N);
    public  operator this % (that:TriDense(M)) =
        VectorMult.comp(this.clone(), that) as Vector(that.N);

    //Left-side operand overload
     public  operator (that:Matrix{self.N==this.M}) % this =
         VectorMult.comp(that, this, Vector.make(that.M), false) as Vector(that.M);
     public  operator (that:DenseMatrix{self.N==this.M}) % this =
         VectorMult.comp(that, this, Vector.make(that.M), false) as Vector(that.M);
     public  operator (that:SymDense{self.N==this.M}) % this =
         VectorMult.comp(that, this, Vector.make(that.M), false) as Vector(that.M);
     public  operator (that:TriDense{self.N==this.M}) % this =
         VectorMult.comp(that, this.clone()) as Vector(that.M);

     /** Inverse of this vector */
     public def inverse() = this.clone().cellDivBy(ElemTypeTool.unit) as Vector(M);

    /**
     * L1-norm (Manhattan norm, taxicab norm) of this vector,
     * i.e. the sum of the absolute values of all elements of this vector.
     */
     public def l1Norm():ElemType {
         var d:ElemType = ElemTypeTool.zero;
         for (i in 0..(M-1))
             d += Math.abs(this.d(i));
         return d;
     }

    /**
     * Manhattan distance ||a - b||_1 (L1-distance, taxicab distance)
     * between vectors a and b
     */
     public static def manhattanDistance(a:Vector, b:Vector(a.M)):ElemType {
         var d:ElemType = ElemTypeTool.zero;
         for (i in 0..(a.M-1))
             d += Math.abs(a.d(i)-b.d(i));
         return d;
     }

    /* Manhattan distance between this vector and another vector V */
    public def manhattanDistance(V:Vector(M)) = manhattanDistance(this, V);

    /**
     * L2-norm (Euclidean norm) of this vector, i.e. the square root of the
     * sum of squares of all elements
     */
    public def norm():ElemType = BLAS.compNorm(this.M, this.d);
    public def l2Norm() = norm();
     
    /*
     * Euclidean distance ||a - b||_2 (L2-distance) between vectors a and b
     */
    public static def distance(a:Vector, b:Vector(a.M)):ElemType {
        var d:ElemType = ElemTypeTool.zero;
        for (i in 0..(a.M-1))
            d += (a.d(i)-b.d(i)) * (a.d(i)-b.d(i));
        return Math.sqrt(d);
    }
     
    /* Euclidean distance between this vector and another vector V */
    public def distance(V:Vector(M)) = distance(this, V);

    /**
     * L_{Inf} norm (uniform norm, Chebyshev norm) of this vector, i.e.
     * the maximum absolute value of all elements of this vector
     */
    public def maxNorm():ElemType {
        var max:ElemType = ElemTypeTool.zero;
        for (i in 0..(M-1))
            max = Math.max(Math.abs(d(i)), max);
        return max;
    }

    public def lInfNorm() = maxNorm();

    /**
     * Chebyshev distance ||a - b||_{Inf} (L_{Inf}-distance, maximum metric)
     * between vectors a and b
     */
    public static def chebyshevDistance(a:Vector, b:Vector(a.M)):ElemType {
        var d:ElemType = ElemTypeTool.zero;
        for (i in 0..(a.M-1))
            d = Math.max(d, Math.abs(a.d(i)-b.d(i)));
        return d;
    }

    /** Sum of all elements of this vector */
    public def sum():ElemType = reduce((a:ElemType,b:ElemType)=> {a+b}, ElemTypeTool.zero);
     
     /**
      * Solve equation A &#42 x = this, wehre A is triangular matrix.
      * The solution x is overwritten on this object
      * 
      * @param A   Triangular matrix
      * @return    this object, overwritten by solution vector.
      */
     public def solveTriMultSelf(A:TriDense(M,M)):Vector(this) {
         DenseMatrixBLAS.solveTriMultVec(A, this);
         return this;
     }
     
     public def likeMe(v:Vector):Boolean {
         return this.M==v.M;
     }
     
     public def equals(dval:ElemType):Boolean {
         for (c in 0..(M-1))
             if (MathTool.isZero(this.d(c) - dval) == false) {
                 Console.OUT.println("Diff found [" + c + "] : "+ 
                         this.d(c) + " <> "+ dval);
                 return false;
             }
         return true;
     }
     
    public def equals(v:Vector(M)):Boolean {
         for (c in 0..(M-1))
            if (MathTool.isZero(this.d(c) - v.d(c)) == false) {
                Console.OUT.println("Diff found [" + c + "] : "+ 
                                    this.d(c) + " <> "+ v.d(c));
		Console.OUT.println(" the values are of type v.d(.) " + v.d(c).typeName() + " d(.) " + d(c).typeName());
                return false;
            }
        return true;
    }
    
    public def equals(mat:Matrix) :Boolean {
        if (mat.M == 1L && mat.N == this.M) {
             for (c in 0..(M-1)) {
                if (MathTool.isZero(this.d(c) - mat(0,c)) == false) {
                    Console.OUT.println("Diff found [" + c + "] : "+ 
                            this.d(c) + " <> "+ mat(0,c));
                    return false;
                }
            }
            return true;
        }
        
        if (mat.N == 1L && mat.M == this.M) {
             for (c in 0..(M-1)) {
                if (MathTool.isZero(this.d(c) - mat(c, 0)) == false) {
                    Console.OUT.println("Diff found [" + c + "] : "+ 
                            this.d(c) + " <> "+ mat(c, 0));
                    return false;
                }
            }
            return true;
        }
        return false;        
    }
    
    /**
     * Apply the map function <code>op</code> to each element of this vector,
     * overwriting the element of this vector with the result.
     * @param op a unary map function to apply to each element of this vector
     * @return this vector, containing the result of the map
     */
    public final @Inline def map(op:(x:ElemType)=>ElemType):Vector(this) {
        RailUtils.map(this.d, this.d, op);
        return this;
    }

    /**
     * Apply the map function <code>op</code> to each element of <code>a</code>,
     * storing the result in the corresponding element of this vector.
     * @param a a vector of the same size as this vector
     * @param op a unary map function to apply to each element of vector <code>a</code>
     * @return this vector, containing the result of the map
     */
    public final @Inline def map(a:Vector(M), op:(x:ElemType)=>ElemType):Vector(this) {
        RailUtils.map(a.d, this.d, op);
        return this;
    }

    /**
     * Apply the map function <code>op</code> to combine each element of vector
     * <code>a</code> with the corresponding element of vector <code>b</code>,
     * overwriting the corresponding element of this vector with the result.
     * @param a first vector of the same size as this vector
     * @param b second vector of the same size as this vector
     * @param op a binary map function to apply to each element of 
     *   <code>a</code> and the corresponding element of <code>b</code>
     * @return this vector, containing the result of the map
     */
    public final @Inline def map(a:Vector(M), b:Vector(M), op:(x:ElemType,y:ElemType)=>ElemType):Vector(this) {
        RailUtils.map(a.d, b.d, this.d, op);
        return this;
    }

    /**
     * Combine the elements of this vector using the provided reducer function.
     * @param op a binary reducer function to combine elements of this vector
     * @param unit the identity value for the reduction function
     * @return the result of the reducer function applied to all elements
     */
    public final @Inline def reduce(op:(a:ElemType,b:ElemType)=>ElemType, unit:ElemType):ElemType
        = RailUtils.reduce(this.d, op, unit);

    public def toString():String {
        val output=new StringBuilder();
        output.add("Vector("+this.M+") [ ");
         for (i in 0..(M-1))
            output.add(this.d(i).toString()+" ");
        output.add("]\n");
        return output.toString();
    }

    /*
     * Snapshot mechanism
     */
    private transient val DUMMY_KEY:Long = 8888L;

    public def makeSnapshot():DistObjectSnapshot {        
        val snapshot = DistObjectSnapshot.make();
        val placeIndex:Long = 0;
        snapshot.save(DUMMY_KEY, new VectorSnapshotInfo(placeIndex,d));
        return snapshot;
    }

    public def restoreSnapshot(snapshot:DistObjectSnapshot) {
        val vectorSnapshotInfo = snapshot.load(DUMMY_KEY) as VectorSnapshotInfo;        
        new Vector(vectorSnapshotInfo.data).copyTo(this);
    }
}
