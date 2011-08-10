/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;
import x10.io.CustomSerialization;
import x10.io.SerialData;
import x10.compiler.NonEscaping;

/**
 * Accumulator object used for accumulating a result during a reduction.
 */
public final class Accumulator[T] extends Acc implements CustomSerialization {
    @NonEscaping private val root:GlobalRef[Acc];
	private var curr:T;
	private var sr:FinishState.StatefulReducer[T];
	private val red:Reducible[T];

	public static def MYPRINT(msg:String):void {
	    Runtime.println("Worker="+Runtime.workerId()+" "+msg);
	}

	public def this(red:Reducible[T]) {
		this.red = red;
		this.root = new GlobalRef[Acc](this);
		this.curr = red.zero();
		this.sr = new FinishState.StatefulReducer[T](red);
	}

    private def this(data:SerialData) {
        val arr:Array[Any](1) = data.data as Array[Any](1);
        this.red = arr(0) as Reducible[T];
		this.root = arr(1) as GlobalRef[Acc];
		this.curr = red.zero();
        this.sr = new FinishState.StatefulReducer[T](red);
    }
    public def serialize():SerialData = new SerialData([red as Any, root as Any], null);

	public def supply(t:T) {
	    if (root.home==here) {
	        val _root = root as GlobalRef[Acc]{self.home==here};
	        val other = _root();
	        if (other!=this) {
	            //MYPRINT("passing to root: t="+t);
	            (other as Accumulator[T]).supply(t);
	            return;
	        }
	    }
        //MYPRINT("supply: t="+t);
        val id = Runtime.workerId();
        if (!sr.workerFlag(id))
	        Runtime.activity().finishState().registerAcc(this);
	    sr.accept(t,id);
	}
	public def result():T {
		return curr;
	}

    public def acceptResult(a:Any):void {
        curr = red(curr,a as T);
        //MYPRINT("acceptResult: a="+a+" curr="+curr);
    }        
    public def calcResult():Any {
        if (sr==null) throw new RuntimeException("Error in Runtime for accumulators!");
        sr.placeMerge();
        val res = sr.result();
        //MYPRINT("calcResult res="+res);
        sr = new FinishState.StatefulReducer[T](red);
        return res;
    }
    public def getRoot():GlobalRef[Acc] = root;
    public def home():Place = root.home;
}