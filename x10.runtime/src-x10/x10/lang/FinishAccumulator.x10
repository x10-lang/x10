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

package x10.lang;

import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;
import x10.compiler.NonEscaping;

/**
 * FinishAccumulator can be used only when you can safely piggyback on an existing Finish
 */
public final class FinishAccumulator[T] {}

/*extends Accumulator[T] {
	protected var sr:FinishState.StatefulReducer[T];

	public def this(red:Reducible[T]) {
	    super(red);
		this.sr = new FinishState.StatefulReducer[T](red);
	}
    public def this(ds:Deserializer) {
        super(ds);
        this.sr = new FinishState.StatefulReducer[T](red);
    }
    public def serialize(s:Serializer) {
        super.serialize(s);
    }

    // This method supplies/offers a value to the accumulator.
	public operator this <- (t:T):void {
	    if (getRoot().home==here) {
	        val _root = getRoot() as GlobalRef[Acc]{self.home==here};
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
    // This method resets the accumulator.
	public operator this()=(t:T):void {
	    curr = t;
	}

	public operator this():T {
		return curr;
	}

    public def acceptResult(a:Any):void {
        curr = red(curr,a as T);
        //MYPRINT("acceptResult: a="+a+" curr="+curr);
    }
    public def calcResult():Any {
        if (sr==null) throw new Exception("Error in Runtime for accumulators!");
        sr.placeMerge();
        val res = sr.result();
        //MYPRINT("calcResult res="+res);
        sr = new FinishState.StatefulReducer[T](red);
        return res;
    }
}
*/
