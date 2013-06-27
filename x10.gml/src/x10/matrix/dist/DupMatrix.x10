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

import x10.util.Timer;
//
import x10.matrix.MathTool;
import x10.matrix.Debug;
import x10.matrix.Matrix;
//

public type DupMatrix(M:Long)=DupMatrix{self.M==M};
public type DupMatrix(M:Long,N:Long)=DupMatrix{self.M==M,self.N==N};
public type DupMatrix(C:DupMatrix)=DupMatrix{self==C};

/**
 * Abstract duplicated matrix. Imcomplete.
 */
public abstract class DupMatrix extends Matrix{



	public var calcTime:Long=0;
	public var commTime:Long=0;


	public def this(m:Long, n:Long) {
		super(m, n);
	}


	//abstract public def alloc(m:Long, n:Long):DupMatrix;
	//abstract public def clone():DupMatrix;


    //abstract public  def apply(x:Long, y:Long):Double;
	//abstract public  def set(v:Double, x:Long, y:Long):void;
	//abstract public  def reset():void;

	public def scal(a:Double):Matrix {// Self-modified scaling 
		throw new IllegalOperationException("Not implemented method"); 
	}

    //abstract public  def T():Matrix;

	public def add(that:Matrix):Matrix {
		throw new IllegalOperationException("Not implemented method"); 
	}

	public def sub(that:Matrix):Matrix {
		throw new IllegalOperationException("Not implemented method"); 
	}

	/**
	 * Not implemented
	 */	
	public def mult(
			A:Matrix(this.M), 
			B:Matrix(A.N,this.N), 
			plus:Boolean):DupMatrix(this) {
		
		throw new IllegalOperationException("Not implemented method"); 
	}
	
	/**
	 * Not implemented
	 */	
	public def transMult(
			A:Matrix{self.N==this.M}, 
			B:Matrix(A.M,this.N), 
			plus:Boolean):DupMatrix(this) {
		
		throw new IllegalOperationException("Not implemented method"); 
	}
			
	/**
	 * Not implemented
	 */
	public def multTrans(
			A:Matrix(this.M), 
			B:Matrix(this.N, A.N), 
			plus:Boolean):DupMatrix(this) {
		throw new IllegalOperationException("Not implemented method"); 
	}




}
