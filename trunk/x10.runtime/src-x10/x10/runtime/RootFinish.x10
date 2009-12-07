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

/**
 * @author tardieu 
 */
class RootFinish extends Latch implements FinishState, Mortal {
    private val counts:Rail[Int]!;
    private var exceptions:Stack[Throwable]!;

    def this() {
		val c = Rail.make[Int](Place.MAX_PLACES, (Int)=>0);
	    c(here.id) = 1;
		counts = c;
    }

    private def notifySubActivitySpawnLocal(place:Place):Void {
        lock();
        counts(place.parent().id)++;
        unlock();
    }
    
    private def notifyActivityTerminationLocal():Void {
        lock();
        counts(here.id)--;
        for(var i:Int=0; i<Place.MAX_PLACES; i++) {
            if (counts(i) != 0) {
                unlock();
                return;
            }
        }
        release();
        unlock();
    }
    
    private def pushExceptionLocal(t:Throwable):Void {
        lock();
        if (null == exceptions) exceptions = new Stack[Throwable]();
        exceptions.push(t);
        unlock();
    }
    
    public def waitForFinish(safe:Boolean):Void {
        if (!NativeRuntime.NO_STEALS && safe) Runtime.join(this);
        await();
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

   def notify(rail:ValRail[Int]!):Void {
        var b:Boolean = true;
        lock();
        for(var i:Int=0; i<Place.MAX_PLACES; i++) {
            counts(i) += rail(i);
            if (counts(i) != 0) b = false;
        }
        if (b) release();
        unlock();
    }

    def notify2(rail:ValRail[Pair[Int,Int]]!):Void {
        lock();
        for(var i:Int=0; i<rail.length; i++) {
            counts(rail(i).first) += rail(i).second;
        }
        for(var i:Int=0; i<Place.MAX_PLACES; i++) {
            if (counts(i) != 0) {
                unlock();
                return;
            }
        }
        release();
        unlock();
    }

    def notify(rail:ValRail[Int]!, t:Throwable):Void {
        pushExceptionLocal(t);
        notify(rail);
    }

    def notify2(rail:ValRail[Pair[Int,Int]]!, t:Throwable):Void {
        pushExceptionLocal(t);
        notify2(rail);
    }

    public global def notifySubActivitySpawn(place:Place):Void {
    	if (here.equals(home)) {
    		(this as RootFinish!).notifySubActivitySpawnLocal(place);
    	} else {
    		Runtime.proxy(this).notifySubActivitySpawn(place);
    	}
    }

    public global def notifyActivityCreation():Void {
    	if (!here.equals(home)) Runtime.proxy(this).notifyActivityCreation();
    }
    
    public global def notifyActivityTermination():Void {
    	if (here.equals(home)) {
    		(this as RootFinish!).notifyActivityTerminationLocal();
    	} else {
    		Runtime.proxy(this).notifyActivityTermination(this);
    	}
    }
    
    public global def pushException(t:Throwable):Void {
    	if (here.equals(home)) {
    		(this as RootFinish!).pushExceptionLocal(t);
    	} else {
    		Runtime.proxy(this).pushException(t);
    	}
    }
}
