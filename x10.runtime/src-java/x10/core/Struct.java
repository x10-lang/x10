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

package x10.core;

import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;

// Base class for all X10 structs
public abstract class Struct implements StructI {

    private static final long serialVersionUID = 1L;

    // not used
//    // constructor just for allocation
//    public Struct(java.lang.System[] $dummy) {}
	
    // not used
//    public Struct $init() {return this;}

    // N.B. this is called implicitly by all subclasses of Struct	
    public Struct() {}

    // default implementation
    @Override
    public boolean equals(Object o) {
        return _struct_equals$O(o);
    }

    public static final RuntimeType<Struct> $RTT = RuntimeType.<Struct> make(Struct.class, new Type[] { Types.STRUCT });
    public RuntimeType<?> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {return null;}

    @Override
    public java.lang.String toString() {
        return Types.typeName(this) + "@" + Integer.toHexString(System.identityHashCode(this));
    }

}
