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

import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.serialization.X10JavaSerializable;

// Base interface of all X10 entities.
public interface Any extends X10JavaSerializable {
    public static final RuntimeType<Object> $RTT = Types.ANY;
    public RuntimeType<?> $getRTT();
    public Type<?> $getParam(int i);
}
