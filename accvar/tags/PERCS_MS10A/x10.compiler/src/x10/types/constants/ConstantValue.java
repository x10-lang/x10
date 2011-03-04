/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.types.constants;

import polyglot.types.Type;

/**
 * @author Bowen Alpern
 *
 */
public abstract class ConstantValue {
    
    protected Object value = null;
    protected Type   type  = null;
    
    ConstantValue(Object v, Type t) {
        value = v;
        type  = t;
    }
    
    public String toString() {
        return value.toString();
    }
    
    public Object value() {
        return value;
    }
    
    public boolean equals(Object o) {
        if (null == o) return false;
        if (o instanceof ConstantValue) 
            return ((ConstantValue) o).value.equals(value)
                && type.typeSystem().typeEquals(type, ((ConstantValue) o).type, type.typeSystem().emptyContext());
        return value.equals(o);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (null == value) return 0;
        return (value.hashCode());
    }
    
}
