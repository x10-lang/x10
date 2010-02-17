/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

import x10.constraint.XLocal_c;
import x10.constraint.XTerms;

public class X10XLocal_c extends XLocal_c {
    X10LocalDef def;
    
    public X10XLocal_c(X10LocalDef def) {
        super(XTerms.makeName(def));
        this.def = def;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof X10XLocal_c) {
            X10XLocal_c v = (X10XLocal_c) o;
            return def == v.def;
        }
        return false;
    }
    
    public int hashCode() {
        return def.hashCode();
    }
    
    public String toString() {
        return def.name().toString();
    }

}
