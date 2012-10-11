/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.runtime.impl.java;


import java.util.HashMap;
import java.util.Map;


public abstract class ThrowableUtils {
    
    // N.B. ThrowableUtils.x10{RuntimeException,Exception,Error,Throwable}s must be sync with TryCatchExpander.knownJava{RuntimeException,Exception,Error,Throwable}s
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10Exceptions = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>>();
    static {
        try {
            Class<? extends java.lang.Throwable> javaClass;
            java.lang.String x10Name;
            Class<? extends java.lang.RuntimeException> x10Class;
            
            javaClass = java.io.NotSerializableException.class;
            x10Name = "x10.io.NotSerializableException";
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.lang.InterruptedException.class;
            x10Name = "x10.lang.InterruptedException";
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
        
    private static java.lang.RuntimeException createX10Throwable(Class<? extends java.lang.RuntimeException> x10Class, java.lang.String message, java.lang.Throwable t) {
        try {
            java.lang.RuntimeException x10t = x10Class.getConstructor(new Class[] { java.lang.String.class }).newInstance(new Object[] { message });
            if (t != null) {
                x10t.setStackTrace(t.getStackTrace());
            }
            return x10t;
        } catch (java.lang.Exception e) {
            throw new java.lang.Error(e);
        }
    }
    
    public static java.lang.RuntimeException getCorrespondingX10Throwable(java.lang.Exception e) {
        if (e instanceof x10.lang.WrappedThrowable) return (java.lang.RuntimeException) e; // already wrapped
        Class<? extends java.lang.RuntimeException> x10Class = x10Exceptions.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            return new x10.lang.WrappedThrowable(e);
        }
        java.lang.String message = e.getMessage();
        return createX10Throwable(x10Class, message, e);
    }

    public static java.lang.RuntimeException convertJavaThrowable(java.lang.Throwable e) {
        if (e instanceof java.lang.RuntimeException) {
            return (java.lang.RuntimeException) e;
        } else if (e instanceof java.lang.Exception) {
            return getCorrespondingX10Throwable((java.lang.Exception) e);
        } else if (e instanceof java.lang.Error) {
            return new x10.lang.WrappedThrowable(e);
        } else
        /*if (e instanceof java.lang.Throwable)*/ {
            return new x10.lang.WrappedThrowable(e);
        }
    }

    public static java.lang.String toString(java.lang.Throwable e) {
        java.lang.String typeName = x10.rtt.Types.typeName(e);
        java.lang.String message = e.getMessage();
        return message == null ? typeName : typeName + ": " + message;
    }

    public static x10.array.Array<java.lang.String> getStackTrace(java.lang.Throwable e) {
        StackTraceElement[] elements = e.getStackTrace();
        java.lang.String str[] = new java.lang.String[elements.length];
        for (int i = 0; i < elements.length; ++i) {
            str[i] = elements[i].toString();
        }
        return x10.runtime.impl.java.ArrayUtils.<java.lang.String>makeArrayFromJavaArray(x10.rtt.Types.STRING, str);
    }
    
    public static void printStackTrace(java.lang.Throwable t, x10.io.Printer p) {
        x10.core.io.OutputStream os = p.getNativeOutputStream();
        java.io.PrintStream ps = null;
        if (os.stream instanceof java.io.PrintStream) {
            ps = (java.io.PrintStream) os.stream;
        } else {
            ps = new java.io.PrintStream(os.stream);
        }
        t.printStackTrace(ps);
    }

}
