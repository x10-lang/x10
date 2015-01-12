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

package x10.lang;

import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;
import x10.compiler.NonEscaping;
import x10.xrx.Runtime;

/**
 * The naive implementation of an accumulator (without any piggybacking on finish or clocks).
 */
public class Accumulator[T] extends Acc implements CustomSerialization {
    //val owner:Activity;
    @NonEscaping private val root:GlobalRef[Acc];
	protected var curr:T;
	protected val red:Reducible[T];

	public static def MYPRINT(msg:String):void {
	    Runtime.println("Worker="+Runtime.workerId()+" "+msg);
	}

    public def this(red:Reducible[T]) {
        //owner = Runtime.activity();
        this.red = red;
        this.root = new GlobalRef[Acc](this);
        this.curr = red.zero();
    }
    public def this(ds:Deserializer) {
        //owner = null;
        this.red = ds.readAny() as Reducible[T];
        this.root = ds.readAny() as GlobalRef[Acc];
        this.curr = red.zero();
    }
    public def serialize(s:Serializer) {
        s.writeAny(red);
        s.writeAny(root);
    }

    /*
    private def isSync():Boolean {
        return Runtime.activity()==owner;
    }
    private def isAsync():Boolean {
        var curr:Activity = Runtime.activity();
        while (curr!=null) {
            if (curr==owner) return true;
            curr = curr.parentActivity;
        }
        return false;
    }
    */

	public def me():Accumulator[T] = (root as GlobalRef[Acc]{self.home==here})() as Accumulator[T];
    /**
     * This method supplies/offers a value to the accumulator.
     */
	public operator this <- (t:T):void {
	    at (root.home) {
	        val me = this.me();
	        //if (!me.isAsync()) throw new IllegalAccAccess();
	        atomic {
	            me.curr = me.red(me.curr,t);
	        }
	    }
	}
    /**
     * This method resets the accumulator.
     */
	public operator this()=(t:T):void {
	    at (root.home) {
	        val me = this.me();
	        //if (!me.isSync()) throw new IllegalAccAccess();
	        atomic {
	            me.curr = t;
	        }
	    }
	}

	private def localGetResult():T {
		//if (!isSync()) throw new IllegalAccAccess();
		return curr;
	}
	public operator this():T {
		return at (root.home) this.me().localGetResult();
	}


    public def supply(t:Any):void {
        this <- (t as T);
    }
    public def reset(t:Any):void {
        this()=(t as T);
    }
    public def result():Any {
        return this();
    }

    // These methods are called by the Runtime. Do not use them.
    public def calcResult():Any { throw new IllegalOperationException(); }
    public def acceptResult(a:Any):void { throw new IllegalOperationException(); }
    public def getRoot():GlobalRef[Acc] = root;
    public def home():Place = root.home;
}