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

public class Throwable extends java.lang.RuntimeException implements RefI {

    public Throwable() {
        super();
    }

    public Throwable(String message) {
        super(message);
    }

    public Throwable(java.lang.Throwable cause) {
        super(cause);
    }

    public Throwable(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    // XTENLANG-1858: every Java class that could be an (non-static) inner class must have constructors with the outer instance parameter
    public Throwable(Object out$) {
        super();
    }

    public Throwable(Object out$, String message) {
        super(message);
    }

    public Throwable(Object out$, java.lang.Throwable cause) {
        super(cause);
    }

    public Throwable(Object out$, String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    public static final RuntimeType<Throwable> _RTT = new RuntimeType<Throwable>(
        Throwable.class,
        new Type[] { x10.rtt.Types.OBJECT }
    ) {
        @Override
        public String typeName() {
            return "x10.lang.Throwable";
        }
    };
    public RuntimeType<?> getRTT() {
        return _RTT;
    }
    public Type<?> getParam(int i) {
        return null;
    }

}
