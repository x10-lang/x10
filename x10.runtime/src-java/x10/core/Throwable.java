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
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;
// XTENLANG-2686: Now x10.core.Throwable is a superclass of x10.lang.Throwable (mapped to x10.core.X10Throwable),
//                and also a superclass of x10.runtime.impl.java.UnknownJavaThrowable.
abstract public class Throwable extends java.lang.RuntimeException {

    private static final long serialVersionUID = 1L;
    public java.lang.String message = null;

    // constructor just for allocation
    public Throwable(java.lang.System[] $dummy) {
        super();
    }

    public Throwable $init() {return this;}
    
    public Throwable() {
        super();
    }

    // TODO
    // public Throwable $init(java.lang.String message) {return this;}
    
    public Throwable(java.lang.String message) {
        super(message);
        this.message = message;
    }

    // TODO
    // public Throwable $init(java.lang.Throwable cause) {return this;}
    
    public Throwable(java.lang.Throwable cause) {
        super(cause);
        message = (cause==null ? null : cause.toString());
    }

    // TODO
    // public Throwable $init(java.lang.String message, java.lang.Throwable cause) {return this;}
    
    public Throwable(java.lang.String message, java.lang.Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    /* TODO to be removed
    // XTENLANG-1858: every Java class that could be an (non-static) inner class must have constructors with the outer instance parameter
    public Throwable(Object out$) {
        super();
    }

    // TODO
    // public Throwable $init(Object out$, java.lang.String message) {return this;}

    public Throwable(Object out$, java.lang.String message) {
        super(message);
    }

    // TODO
    // public Throwable $init(Object out$, java.lang.Throwable cause) {return this;}

    public Throwable(Object out$, java.lang.Throwable cause) {
        super(cause);
    }

    // TODO
    // public Throwable $init(Object out$, java.lang.String message, java.lang.Throwable cause) {return this;}
    
    public Throwable(Object out$, java.lang.String message, java.lang.Throwable cause) {
        super(message, cause);
    }
    */

    // XTENLANG-2680
	public java.lang.String getMessage$O() {
        if (message != null) {
            return message;
        }
		return super.getMessage();
	}

    public java.lang.String getMessage() {
        if (message != null) {
            return message;
        }
		return super.getMessage();
	}

    public final x10.array.Array<java.lang.String> $getStackTrace() {
        StackTraceElement[] elements = getStackTrace();
        java.lang.String str[] = new java.lang.String[elements.length];
        for (int i=0 ; i<elements.length ; ++i) {
            str[i] = elements[i].toString();
        }
        return x10.core.ArrayFactory.<java.lang.String>makeArrayFromJavaArray(Types.STRING, str);
    }

    // XTENLANG-2680
//	public void printStackTrace(x10.io.Printer p) {
//	    // See @Native annotation in Throwable.x10
//		x10.core.ThrowableUtilities.printStackTrace(this, p);
//	}
    public void printStackTrace(x10.io.Printer p) {
        x10.core.io.OutputStream os = p.getNativeOutputStream();
        java.io.PrintStream ps = null;
        if (os.stream instanceof java.io.PrintStream) {
            ps = (java.io.PrintStream) os.stream;
        } else {
            ps = new java.io.PrintStream(os.stream);
        }
        printStackTrace(ps);
    }

    /* XTENLANG-2686: RTT is not necessary any more since this class is not mapped to x10.lang.Throwable
    public static final RuntimeType<Throwable> $RTT = new NamedType<Throwable>(
        "x10.lang.Throwable",
        Throwable.class,
        new Type[] { Types.OBJECT }
    );
    public RuntimeType<?> $getRTT() {
        return $RTT;
    }
    public Type<?> $getParam(int i) {
        return null;
    }
    */

    @Override
    public java.lang.String toString() {
        java.lang.String msg = this.getMessage();
        java.lang.String name = Types.typeName(this);
        if (msg == null) {
            return name;
        } else {
            int length = name.length() + 2 + msg.length();
            java.lang.StringBuilder buffer = new java.lang.StringBuilder(length);
            return buffer.append(name).append(": ").append(msg).toString();
        }
    }
}
