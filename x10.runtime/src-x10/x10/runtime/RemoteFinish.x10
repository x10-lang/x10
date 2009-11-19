/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
 
package x10.runtime;

import x10.util.Pair;
import x10.util.Stack;
import x10.util.concurrent.atomic.AtomicInteger;
import x10.compiler.Native;

/**
 * @author tardieu 
 */
class RemoteFinish implements FinishState {
	/**
	 * The Exception Stack is used to collect exceptions 
	 * issued when activities associated with this finish state terminate abruptly. 
	 */
    private var exceptions:Stack[Throwable]!;

	/**
	 * The monitor is used to serialize updates to the finish state. 
	 */
    private val lock = new Lock();
    
	/**
	 * Keep track of the number of activities associated with this finish state.
	 */
    private val counts = Rail.make[Int](Place.MAX_PLACES, (Int)=>0) as Rail[Int]!;
    
    private val message = Rail.make[Int](Place.MAX_PLACES, (Int)=>here.id) as Rail[Int]!;
    private var length:Int = 1;

    private var count:AtomicInteger = new AtomicInteger(0); 
    
    private val rid:RID;
    
    def this(rid:RID) {
        this.rid = rid;
    }
    
    public def rid():RID = rid;
    
    public def incr():Void {
        count.getAndIncrement();
    }
    
	/** 
	 * An activity created under this finish has been created. Increment the count
	 * associated with the finish.
	 */
    public def notifySubActivitySpawn(place:Place):Void {
        lock.lock();
        if (counts(place.id)++ == 0 && here.id != place.id) {
        	message(length++) = place.id;
        }
        lock.unlock();
    }
    
	/** 
	 * An activity created under this finish has terminated.
	 */
    public def notifySubActivityTermination():Void {
        lock.lock();
        counts(here.id)--;
        if (count.decrementAndGet() > 0) {
            lock.unlock();
            return;
        }
        val e = exceptions;
        exceptions = null;
        if (2*length > Place.MAX_PLACES) {
	        val m = counts as ValRail[Int];
	        for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
	        length = 1;
	        lock.unlock();
	        val r = rid;
	        if (null != e) {
	            val t:Throwable;
	            if (e.size() == 1) {
	                t = e.peek();
	            } else {
	                t = new MultipleExceptions(e);
	            }
	            val closure = () => { Runtime.findRoot(r).notify(m, t); NativeRuntime.deallocObject(m); };
	            NativeRuntime.runAt(rid.place.id, closure);
	            NativeRuntime.dealloc(closure);
	        } else {
	            val closure = () => { Runtime.findRoot(r).notify(m) ; NativeRuntime.deallocObject(m); };
	            NativeRuntime.runAt(rid.place.id, closure);
	            NativeRuntime.dealloc(closure);
	        }
	        NativeRuntime.deallocObject(m);
        } else {
        	val m = ValRail.make[Pair[Int,Int]](length, (i:Nat)=>Pair[Int,Int](message(i), counts(message(i))));
	        for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
	        length = 1;
	        lock.unlock();
	        val r = rid;
	        if (null != e) {
	            val t:Throwable;
	            if (e.size() == 1) {
	                t = e.peek();
	            } else {
	                t = new MultipleExceptions(e);
	            }
	            val closure = () => { Runtime.findRoot(r).notify2(m, t); NativeRuntime.deallocObject(m); };
	            NativeRuntime.runAt(rid.place.id, closure);
	            NativeRuntime.dealloc(closure);
	        } else {
	            val closure = () => { Runtime.findRoot(r).notify2(m) ; NativeRuntime.deallocObject(m); };
	            NativeRuntime.runAt(rid.place.id, closure);
	            NativeRuntime.dealloc(closure);
	        }
	        NativeRuntime.deallocObject(m);
	    }
	}
    
	/** 
	 * Push an exception onto the stack.
	 */
    public def pushException(t:Throwable):Void {
        lock.lock();
        if (null == exceptions) exceptions = new Stack[Throwable]();
        exceptions.push(t);
        lock.unlock();
    }
}
