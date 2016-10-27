/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 *  (C) Copyright Sara Salem Hamouda 2014-2016.
 */

package x10.util.resilient.localstore;

import x10.util.HashMap;

public class TransKeyLog {

    /*A copy of the value, used to isolate the transaction updates for the actual value*/
    private var value:Cloneable;
    
    /*A flag to indicate if the value was used for read only operations*/
    private var readOnly:Boolean = true;

    /*A flag to differentiate between setting a NULL and deleting an object*/
    private var deleted:Boolean = false;

    public def this(initValue:Cloneable) {
        this.value = initValue;
    }
    
    public def update(n:Cloneable) {
        value = n;
        readOnly = false;
        if (deleted)
            deleted = false;
    }
    
    public def delete() {
        readOnly = false;
        deleted = true;
    }
    
    public def getValue() = value;
    public def readOnly() = readOnly;
    public def isDeleted() = deleted;
    
}