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

public abstract class GLBResult[R] {
    
    /**
     * Backing storage of result.
     */
    var result: Rail[R] = null;
    
    /**
     * Reduction operator. Numbered the same as the operator defined in {@link x10.util.Team}
     */
    var op:Int = -1n;
    
    /**
     * Return the operator. See {@link x10.util.Team}
     */
    public abstract def getReduceOperator():Int;
    
    /**
     * Returns the result from local {@link TaskQueue}. User must not reclaim the Rail returned by this method
     * @return Local computation result
     */
    public abstract def getResult():Rail[R];
    
    //public abstract def setResult(result):void;
    
    /**
     * User-defined display method for the result.
     * @param result to display
     */
    public abstract def display(Rail[R]):void;
    
    /**
     * Internal method used by {@link GLB}, it only retrieves the result from user once,
     * and the following references are all via the backing Rail.
     */
    def submitResult():Rail[R]{
        if(this.result == null){
            this.result = getResult();
        }
        return this.result;
    }
}
