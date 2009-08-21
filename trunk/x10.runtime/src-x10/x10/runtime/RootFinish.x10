/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
 
package x10.runtime;

import x10.util.Stack;

/**
 * @author tardieu 
 */
class RootFinish extends Latch implements FinishState {
    private val counts = Rail.makeVar[Int](Place.MAX_PLACES, (Int)=>0);
    
    private var exceptions:Stack[Throwable];

    var rid:RID = new RID(here, -1);
    
    public def rid():RID = rid; 
    
    public def incr():Void {}
    
    def this() {
        counts(here.id) = 1;
    }
    
    def waitForFinish(safe:Boolean):Void {
        if (!NativeRuntime.NO_STEALS && safe) Runtime.pool.join(this);
        await();
        Runtime.removeRoot(this);
        if (null != exceptions) {
            if (exceptions.size() == 1) {
                val t = exceptions.peek();
                if (t instanceof Error) {
                    throw t as Error;
                }
                if (t instanceof RuntimeException) {        
                    throw t as RuntimeException;
                }
            }
            throw new MultipleExceptions(exceptions);
        }
    }

    def notify(rail:ValRail[Int]):Void {
        var b:Boolean = true;
        lock();
        for(var i:Int=0; i<Place.MAX_PLACES; i++) {
            counts(i) += rail(i);
            if (counts(i) != 0) b = false;
        }
        if (b) set();
        unlock();
    }

    def notify(rail:ValRail[Int], t:Throwable):Void {
        pushException(t);
        notify(rail);
    }

    public def notifySubActivitySpawn(place:Place):Void {
        lock();
        counts(place.id)++;
        unlock();
    }
    
    public def notifySubActivityTermination():Void {
        lock();
        counts(here.id)--;
        for(var i:Int=0; i<Place.MAX_PLACES; i++) {
            if (counts(i) != 0) {
                unlock();
                return;
            }
        }
        set();
        unlock();
    }
    
    public def pushException(t:Throwable):Void {
        lock();
        if (null == exceptions) exceptions = new Stack[Throwable]();
        exceptions.push(t);
        unlock();
    }
}
