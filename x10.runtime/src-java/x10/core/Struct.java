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

package x10.core;

import java.io.Serializable;

import x10.rtt.Types;

// Base class for all X10 structs
@SuppressWarnings("serial")
public abstract class Struct implements StructI, Serializable {

    public Struct() {}

    // default implementation
    @Override
    public boolean equals(Object o) {
        return _struct_equals$O(o);
    }

    @Override
    public String toString() {
        return Types.typeName(this) + "@" + Integer.toHexString(System.identityHashCode(this));
    }
}
