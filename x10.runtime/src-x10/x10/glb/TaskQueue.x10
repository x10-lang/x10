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
/**
 * <p>
 * A TaskQueue[Queue, Z] (Z is the type of the result, Queue is the concrete type of TaskQueue).
 * The way as it is today (with Queue as one of the required generic types) 
 * is because performance issue, i.e., compiler can replace TaskQueue with Queue
 * at the compile time, thus saving a virtual function loop up whenever it is called within GLB. No semantic benefits.
 * represents the user-defined data-structure maintained at each place by the
 * GLB implementation. It is recommended to have an associated {@link TaskBag} to facilitate the split/merge methods. 
 */
public interface TaskQueue[Queue, R]{ // {Queue<:TaskQueue[Queue, R]}
    
    
    //public abstract def process(n:Long, context:ContextI):Boolean;
    
    public abstract def process(n:Long, context:Context[Queue, R]){Queue<:TaskQueue[Queue, R ]}:Boolean;
    
    /**
     * Split the current TaskBag
     * @return null if TaskBag is too small to return
     *         TaskBag split
     */
    public  def split():TaskBag;
    
    /**
     * Merge TaskBag into the current task bag , thus 
     * changing the state of current taskbag.
     * @param tb incoming TaskBag
     */
    public  def merge(tb:TaskBag):void;
    
    /**
     * Returns the number of task items that have been prcoessed.
     * This method is used to keep track of task item statistics (i.e. a hint), whether it is
     * implemented faithfully is not important.
     */
    public  def count():Long;
    
    /**
     * Returns the computation result.
     * @return computation result
     */
    public def getResult():GLBResult[R];
    
    
    /**
     * Auxiliary function that user can overwrite to print the statistics one cares about,
     * e.g., computation time. This function will be called only after all the calculation
     * and results reduction are done.
     */
    public  def printLog():void;
    
}
