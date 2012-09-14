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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public abstract class ThrowableUtils {
    
    // N.B. ThrowableUtils.x10{RuntimeException,Exception,Error,Throwable}s must be sync with TryCatchExpander.knownJava{RuntimeException,Exception,Error,Throwable}s
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10RuntimeExceptions = Collections.emptyMap();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10Exceptions = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>>();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10Errors = Collections.emptyMap();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10Throwables = Collections.emptyMap();
    
    static {
        try {
            Class<? extends java.lang.Throwable> javaClass;
            java.lang.String x10Name;
            Class<? extends java.lang.RuntimeException> x10Class;
            
            // x10RuntimeExceptions

            // x10Exceptions
            javaClass = java.io.NotSerializableException.class;
            x10Name = "x10.io.NotSerializableException";
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.lang.InterruptedException.class;
            x10Name = "x10.lang.InterruptedException";
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);

            // N.B. subtypes of java.io.IOException should be caught and converted to the corresponding x10 exceptions in XRJ
//            javaClass = java.io.FileNotFoundException.class;
//            x10Name = "x10.io.FileNotFoundException";
//            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
//            x10Exceptions.put(javaClass, x10Class);
//            
//            javaClass = java.io.EOFException.class;
//            x10Name = "x10.io.EOFException";
//            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
//            x10Exceptions.put(javaClass, x10Class);
//
//            javaClass = java.io.IOException.class;
//            x10Name = "x10.io.IOException";
//            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
//            x10Exceptions.put(javaClass, x10Class);
                        
            // x10Errors

            // x10Throwables
            
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
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
    
    public static java.lang.RuntimeException getCorrespondingX10Throwable(java.lang.RuntimeException e) {
        if (e instanceof x10.lang.WrappedThrowable) return e; // already wrapped
        java.lang.String message = e.getMessage();
        Class<? extends java.lang.RuntimeException> x10Class = x10RuntimeExceptions.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            return e;
        }
        return createX10Throwable(x10Class, message, e);
    }

    public static java.lang.RuntimeException getCorrespondingX10Throwable(java.lang.Exception e) {
        if (e instanceof x10.lang.WrappedThrowable) return (java.lang.RuntimeException) e; // already wrapped
        java.lang.String message = e.getMessage();
        Class<? extends java.lang.RuntimeException> x10Class = x10Exceptions.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownJavaThrowable, which will be caught outside of main
            return new x10.lang.WrappedThrowable(e);
        }
        return createX10Throwable(x10Class, message, e);
    }

    public static java.lang.RuntimeException getCorrespondingX10Throwable(java.lang.Error e) {
        java.lang.String message = e.getMessage();
        Class<? extends java.lang.RuntimeException> x10Class = x10Errors.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownJavaThrowable, which will be caught outside of main
            return new x10.lang.WrappedThrowable(e);
        }
        return createX10Throwable(x10Class, message, e);
    }

    public static java.lang.RuntimeException getCorrespondingX10Throwable(java.lang.Throwable e) {
        if (e instanceof x10.lang.WrappedThrowable) return (java.lang.RuntimeException) e; // already wrapped
        java.lang.String message = e.getMessage();
        Class<? extends java.lang.RuntimeException> x10Class = x10Throwables.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownJavaThrowable, which will be caught outside of main
            return new x10.lang.WrappedThrowable(e);
        }
        return createX10Throwable(x10Class, message, e);
    }

    // not used
//    public static java.lang.RuntimeException convertJavaRuntimeException(java.lang.RuntimeException e) {
//      return getCorrespondingX10Throwable(e);
//    }

    public static java.lang.RuntimeException convertJavaException(java.lang.Exception e) {
        if (e instanceof java.lang.RuntimeException) {
            return getCorrespondingX10Throwable((java.lang.RuntimeException) e);
        } else
        /*if (e instanceof java.lang.Exception)*/ {
            return getCorrespondingX10Throwable(e);
        }
    }

    // not used
//    public static java.lang.RuntimeException convertJavaError(java.lang.Error e) {
//      return getCorrespondingX10Throwable(e);
//    }

    public static java.lang.RuntimeException convertJavaThrowable(java.lang.Throwable e) {
        if (e instanceof java.lang.RuntimeException) {
            return getCorrespondingX10Throwable((java.lang.RuntimeException) e);
        } else if (e instanceof java.lang.Exception) {
            return getCorrespondingX10Throwable((java.lang.Exception) e);
        } else if (e instanceof java.lang.Error) {
            return getCorrespondingX10Throwable((java.lang.Error) e);
        } else
        /*if (e instanceof java.lang.Throwable)*/ {
            return getCorrespondingX10Throwable(e);
        }
    }

    // not used
//    public static java.lang.RuntimeException getUncheckedCause(java.lang.Throwable e) {
//        java.lang.Throwable cause = e.getCause();
//        return cause instanceof java.lang.RuntimeException ? ((java.lang.RuntimeException) cause) : new x10.lang.WrappedThrowable(cause);
//    }

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

    public static <T> T UnsupportedOperationException(java.lang.String message) {
        throw new java.lang.UnsupportedOperationException(message);
    }

    public static int UnsupportedOperationExceptionInt(java.lang.String message) {
        throw new java.lang.UnsupportedOperationException(message);
    }

}
