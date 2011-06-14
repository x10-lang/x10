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

import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;

// XTENLANG-2686: Now x10/lang/Throwable.x10 is mapped to x10/core/X10Throwable.java which extends x10/core/Throwable.java.
//                This makes it possible to have non-X10-catchable exceptions (e.g. UnknownJavaException) under x10.core.Throwable.
public class X10Throwable extends x10.core.Throwable implements RefI {

	private static final long serialVersionUID = 1L;

	public X10Throwable(java.lang.System[] $dummy) {
	    super();
	}

	public X10Throwable $init() {return this;}
    
    public X10Throwable() {
        super();
    }

    // TODO
    // public X10Throwable $init(java.lang.String message) {return this;}
    
    public X10Throwable(java.lang.String message) {
        super(message);
    }

    // TODO
    // public X10Throwable $init(java.lang.Throwable cause) {return this;}
    
    public X10Throwable(java.lang.Throwable cause) {
        super(cause);
    }

    // TODO
    // public X10Throwable $init(java.lang.String message, java.lang.Throwable cause) {return this;}
    
    public X10Throwable(java.lang.String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    public static final RuntimeType<Throwable> $RTT = new NamedType<Throwable>(
        "x10.lang.Throwable",
        Throwable.class,
        new Type[] { x10.rtt.Types.OBJECT }
    );
    public RuntimeType<?> $getRTT() {
        return $RTT;
    }
    public Type<?> $getParam(int i) {
        return null;
    }

}
