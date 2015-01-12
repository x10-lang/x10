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

package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Inline;
import x10.compiler.Uninitialized;
import x10.compiler.Header;

import x10.util.GrowableRail;

//For collecting Finish frame usage

abstract public class CollectingFinish[T] extends FinishFrame {
    @Uninitialized public val reducer:Reducible[T];
    @Uninitialized public val resultRail:Rail[T];
    @Ifdef("__CPP__") @Uninitialized public var result:T; //Only for CPP

    public def this(up:Frame, rd:Reducible[T]) {
        super(up);
        this.reducer = rd;
        @Ifdef("__CPP__") {this.result = rd.zero();}
        @Ifndef("__CPP__"){ //java path 
            val size = Runtime.NTHREADS;
            this.resultRail = Unsafe.allocRailUninitialized[T](size);
            for(var i:Int = 0n; i < size; i++){
                resultRail(i) = reducer.zero();
            }
        }
    }

    @Ifdef("__CPP__")
    public def this(x:Int, o:CollectingFinish[T]) {
        super(x, o);
        //delay copy the result after fully remapped
        this.reducer = o.reducer;
        val size = Runtime.NTHREADS;
        this.resultRail = Unsafe.allocRailUninitialized[T](size);
        for(var i:Int = 0n; i < size; i++){
            resultRail(i) = reducer.zero();
        }
    }
    
    @Ifdef("__CPP__")
    public def realloc() {
        if (null != redirect) return redirect;
        val tmp = remap();
        tmp.redirect = tmp;
        if (null != exceptions) {
            tmp.exceptions = new GrowableRail[CheckedThrowable]();
            Runtime.atomicMonitor.lock();
            while (!exceptions.isEmpty()) tmp.exceptions.add(exceptions.removeLast());
            exceptions = null;
            Runtime.atomicMonitor.unlock();
        }
        atomic redirect = tmp; //use atomic to refresh
        //Use atomic to refresh the redirect field. Then we could copy the result safely
        Frame.cast[FinishFrame, CollectingFinish[T]](tmp).result = result;
        return tmp;
    }
    
    public final def accept(t:T, worker:Worker){
        @Ifdef("__CPP__"){
            if(redirect == null){
               result = reducer(result, t);
            }
            else {
               val id = worker.id;
               val rr = cast[FinishFrame, CollectingFinish[T]](redirect).resultRail;
               rr(id) = reducer(rr(id), t);
            }
        } 
        @Ifndef("__CPP__"){
            val id = worker.id;
            resultRail(id) = reducer(resultRail(id), t);
        }
    }
    
    //In finish frame's fast path, the result is stored here.
    public final def fastResult(worker:Worker):T {
        @Ifdef("__CPP__"){
            return result;
        }
        @Ifndef("__CPP__"){
            val result = resultRail(worker.id);
            Unsafe.dealloc(resultRail);
            return result;
        }
    
    }
    
    public final def result():T {
        //do merge
        @Ifdef("__CPP__"){
            val size = Runtime.NTHREADS;
            for(var i:Int = 0n; i < size; i++){
                result = reducer(result, resultRail(i));
            }
            Unsafe.dealloc(resultRail);
            return result;
        }
        @Ifndef("__CPP__"){
            var result:T = resultRail(0);
            val size = Runtime.NTHREADS;
            for(var i:Int = 1n; i < size; i++){
                result = reducer(result, resultRail(i));
            }
            Unsafe.dealloc(resultRail);
            return result;
        }
    }
}
