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