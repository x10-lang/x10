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

package x10.runtime.impl.java;


public abstract class ThrowableUtils {

    public static final boolean supportConversionForJavaErrors = false;

    private static final java.util.Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10Exceptions = new java.util.HashMap<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>>();
    private static final Class<? extends java.lang.RuntimeException> x10_io_IOException;
    static {
            Class<? extends java.lang.Throwable> javaClass;
            Class<? extends java.lang.RuntimeException> x10Class;

            javaClass = java.io.NotSerializableException.class;
            x10Class = x10.io.NotSerializableException.class;
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.io.EOFException.class;
            x10Class = x10.io.EOFException.class;
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.io.FileNotFoundException.class;
            x10Class = x10.io.FileNotFoundException.class;
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.io.IOException.class;
            x10Class = x10.io.IOException.class;
            x10Exceptions.put(javaClass, x10Class);
            x10_io_IOException = x10Class;

            javaClass = java.lang.InterruptedException.class;
            x10Class = x10.xrx.InterruptedException.class;
            x10Exceptions.put(javaClass, x10Class);
    }
        
    private static java.lang.RuntimeException asX10Exception(Class<? extends java.lang.RuntimeException> x10Class, String message, java.lang.Throwable t) {
        try {
            java.lang.RuntimeException xe = x10Class.getConstructor(String.class).newInstance(message);
            if (t != null) {
                xe.setStackTrace(t.getStackTrace());
            }
            return xe;
        } catch (java.lang.Exception e) {
            throw new java.lang.Error(e);
        }
    }

    public static java.lang.RuntimeException ensureX10Exception(java.io.IOException e) {
        Class<? extends java.lang.RuntimeException> x10Class = x10Exceptions.get(e.getClass());
        String message = e.getMessage();
        if (x10Class != null) {
            return asX10Exception(x10Class, message, e);
        }
        // no corresponding x10 exception is defined
        return asX10Exception(x10_io_IOException, message, e);                
    }

    public static java.lang.RuntimeException ensureX10Exception(java.lang.Throwable e) {
        if (e instanceof java.lang.RuntimeException) {
            return (java.lang.RuntimeException) e;
        } else if (e instanceof java.io.IOException) {
            return ensureX10Exception((java.io.IOException) e);
        } else if (e instanceof java.lang.Exception) {
            Class<? extends java.lang.RuntimeException> x10Class = x10Exceptions.get(e.getClass());
            String message = e.getMessage();
            if (x10Class != null) {
                return asX10Exception(x10Class, message, e);
            }
            // no corresponding x10 exception is defined
            return new x10.lang.WrappedThrowable(e);
        } else if (!supportConversionForJavaErrors && e instanceof java.lang.Error) {
            throw (java.lang.Error) e;
        } else {
            return new x10.lang.WrappedThrowable(e);
        }
    }

    public static String toString(java.lang.Throwable e) {
        String typeName = x10.rtt.Types.typeName(e);
        String message = e.getMessage();
        return message == null ? typeName : typeName + ": " + message;
    }

    public static x10.core.Rail<String> getStackTrace(java.lang.Throwable e) {
        java.lang.StackTraceElement[] elements = e.getStackTrace();
        String str[] = new String[elements.length];
        for (int i = 0; i < elements.length; ++i) {
            str[i] = elements[i].toString(); //TODO replace the use of j.l.StackTraceElement#getClassName() with x10.rtt.Types#typeName(Object)
        }
        return x10.runtime.impl.java.ArrayUtils.<String>makeRailFromJavaArray(x10.rtt.Types.STRING, str);
    }

    private static void printStackTrace(java.lang.Throwable t, java.io.PrintStream ps) {
        if (t instanceof x10.lang.MultipleExceptions) {
            x10.lang.MultipleExceptions me = (x10.lang.MultipleExceptions) t;
            x10.core.Rail<java.lang.Throwable> exceptions = (x10.core.Rail<java.lang.Throwable>) me.exceptions;
            for (java.lang.Throwable ct : exceptions.getGenericArray()) {
                printStackTrace(ct, ps);
            }
        } else {
            t.printStackTrace(ps); //TODO replace the use of j.l.StackTraceElement#getClassName() with x10.rtt.Types#typeName(Object)
        }
    }

    public static void printStackTrace(java.lang.Throwable t, x10.io.Printer p) {
        x10.core.io.OutputStream os = p.getNativeOutputStream();
        java.io.PrintStream ps = null;
        if (os.stream instanceof java.io.PrintStream) {
            ps = (java.io.PrintStream) os.stream;
        } else {
            ps = new java.io.PrintStream(os.stream);
        }
        printStackTrace(t, ps);
    }

}
