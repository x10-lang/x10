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


public abstract class ThrowableUtils {

    public static final boolean supportConversionForJavaErrors = false;

    private static final java.util.Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10Exceptions = new java.util.HashMap<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>>();
    private static Class<? extends java.lang.RuntimeException> x10_io_IOException;
    static {
        try {
            Class<? extends java.lang.Throwable> javaClass;
            java.lang.String x10Name;
            Class<? extends java.lang.RuntimeException> x10Class;

            javaClass = java.io.NotSerializableException.class;
            x10Name = "x10.io.NotSerializableException";
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.io.EOFException.class;
            x10Name = "x10.io.EOFException";
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.io.FileNotFoundException.class;
            x10Name = "x10.io.FileNotFoundException";
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.io.IOException.class;
            x10Name = "x10.io.IOException";
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);
            x10_io_IOException = x10Class;

            javaClass = java.lang.InterruptedException.class;
            x10Name = "x10.lang.InterruptedException";
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);
        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
        
    private static java.lang.RuntimeException asX10Exception(Class<? extends java.lang.RuntimeException> x10Class, java.lang.String message, java.lang.Throwable t) {
        try {
            java.lang.RuntimeException xe = x10Class.getConstructor(java.lang.String.class).newInstance(message);
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
        java.lang.String message = e.getMessage();
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
            java.lang.String message = e.getMessage();
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

    public static java.lang.String toString(java.lang.Throwable e) {
        java.lang.String typeName = x10.rtt.Types.typeName(e);
        java.lang.String message = e.getMessage();
        return message == null ? typeName : typeName + ": " + message;
    }

    public static x10.array.Array<java.lang.String> getStackTrace(java.lang.Throwable e) {
        java.lang.StackTraceElement[] elements = e.getStackTrace();
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
