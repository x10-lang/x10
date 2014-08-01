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

package x10.glb;
import x10.compiler.Inline;
/**
 * <p> Context object that can be passed to user code. User code can interact with GLB library via an Context object.
 * For now, the only provided method is yield() method. User can stop processing and probe the network during computation,
 * if necessary, by calling yield() 
 * </p>
 */
public class Context[Queue,R]{Queue<:TaskQueue[Queue, R]} implements ContextI{
    
    /**
     * PlaceLocalHandle of {@link Worker}
     */
    var st:PlaceLocalHandle[Worker[Queue, R]];
    
    /**
     * @param st PlaceLocalHandle 
     */
    public def this(st:PlaceLocalHandle[Worker[Queue, R]]){Queue<:TaskQueue[Queue, R]}{
        this.st = st;
    }
    
    /**
     * Used by the user code, yield back to GLB scheduler.
     */
    @Inline public def yield():void{
        this.st().getYieldPoint()(this.st); 
    }
    
    /**
     * Implements the ConTextI interface, for potentially simplified usage of yield by the user code,
     * i.e., user doesn't have to provide type of {@link TaskQueue} and type of computation result. 
     * However, this programming style is not adopted in this version of GLB because we might need to 
     * expose TaskQueue, R type for the future usage.
     */
    public def yielding():void{
        this.yield();
    }
    
    
}
