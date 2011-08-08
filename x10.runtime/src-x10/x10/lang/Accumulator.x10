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

/**
 * Accumulator object used for accumulating a result during a reduction.
 */
public final class Accumulator[T] extends Acc {
    private root = new GlobalRef[Acc](this);
	private var curr:T;
	private transient var sr:FinishState.StatefulReducer[T];
	private val red:Reducible[T];

	public def this(red:Reducible[T]) {
		this.curr = red.zero();
		this.red = red;
	}

	public def supply(t:T) {
	    if (root.home==here) {
	        val _root = root as GlobalRef[Acc]{self.home==here};
	        val other = _root();
	        if (other!=this) {
	            (other as Accumulator[T]).supply(t);
	            return;
	        }
	    }
	    if (sr==null) {
	        Runtime.activity().finishState().registerAcc(this);
		    sr = new FinishState.StatefulReducer[T](red);
		}
	    sr.accept(t,Runtime.workerId());
	}
	public def result():T {
		return curr;
	}

    public def acceptResult(a:Any):void {
        curr = red(curr,a as T);
    }        
    public def calcResult():Any {
        if (sr==null) throw new RuntimeException("Error in Runtime for accumulators!");
        sr.placeMerge();
        val res = sr.result();
        sr = null;
        return res;
    }
    public def getRoot():GlobalRef[Acc] = root;
    public def home():Place = root.home;
}