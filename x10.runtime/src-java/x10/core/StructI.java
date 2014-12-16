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

package x10.core;

import x10.rtt.RuntimeType;
import x10.rtt.Types;

// Base interface for all X10 structs
public interface StructI extends Any, Cloneable {
    public static final RuntimeType<StructI> $RTT = Types.STRUCT;
    public boolean _struct_equals$O(Object o);
}
