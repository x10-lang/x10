/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
 
package x10.runtime;

import x10.util.Stack;
import x10.util.concurrent.atomic.AtomicInteger;

/**
 * @author tardieu 
 */
class RemoteFinish(place:Place) implements FinishState {
	/**
	 * The Exception Stack is used to collect exceptions 
	 * issued when activities associated with this finish state terminate abruptly. 
	 */
    private var exceptions:Stack[Throwable];

	/**
	 * The monitor is used to serialize updates to the finish state. 
	 */
    private val lock = new Lock();
    
	/**
	 * Keep track of the number of activities associated with this finish state.
	 */
    private val counts = Rail.makeVar[Int](Place.MAX_PLACES, (Int)=>0);
    
    private var count:AtomicInteger = new AtomicInteger(0);
    
    private val key:Int;
    
    def this(place:Place, key:Int) {
        property(place);
        this.key = key;
    }
    
    public def key():Int = key;
    
    public def incr():Void {
        count.getAndIncrement();
    }
    
	/** 
	 * An activity created under this finish has been created. Increment the count
	 * associated with the finish.
	 */
    public def notifySubActivitySpawn(place:Place):Void {
        lock.lock();
        counts(place.id)++;
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
        val c = counts as ValRail[Int];
        for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
        val e = exceptions;
        exceptions = null;
        lock.unlock();
        val k = key;
        if (null != e) {
            val t:Throwable;
            if (e.size() == 1) {
                t = e.peek();
            } else {
                t = new MultipleExceptions(e);
            }
            NativeRuntime.runAt(place.id, ()=>Runtime.findRoot(k).notify(c, t));
        } else {
            NativeRuntime.runAt(place.id, ()=>Runtime.findRoot(k).notify(c));
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
