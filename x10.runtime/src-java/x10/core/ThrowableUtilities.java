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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import x10.array.Array;
import x10.rtt.Types;
import x10.runtime.impl.java.UnknownJavaThrowable;

public abstract class ThrowableUtilities {
    
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends x10.core.Throwable>> x10RuntimeExceptions = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends x10.core.Throwable>>();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends x10.core.Throwable>> x10Exceptions = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends x10.core.Throwable>>();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends x10.core.Throwable>> x10Errors = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends x10.core.Throwable>>();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends x10.core.Throwable>> x10Throwables = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends x10.core.Throwable>>();
    private static Class<? extends x10.core.Throwable> x10RuntimeException;
    private static Class<? extends x10.core.Throwable> x10Exception;
    private static Class<? extends x10.core.Throwable> x10Error;
    private static Class<? extends x10.core.Throwable> x10Throwable;

    private static Class<? extends x10.core.Throwable> x10UnsupportedOperationException;
    
    static {
        try {
            Class<? extends java.lang.Throwable> javaClass;
            java.lang.String x10Name;
            Class<? extends x10.core.Throwable> x10Class;
            
            // x10RuntimeExceptions
            // N.B. ThrowableUtilities.x10RuntimeExceptions must be sync with TryCatchExpander.x10RuntimeExceptions
            javaClass = java.lang.ArithmeticException.class;
            x10Name = "x10.lang.ArithmeticException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10RuntimeExceptions.put(javaClass, x10Class);

            javaClass = java.lang.ArrayIndexOutOfBoundsException.class;
            x10Name = "x10.lang.ArrayIndexOutOfBoundsException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10RuntimeExceptions.put(javaClass, x10Class);

            javaClass = java.lang.StringIndexOutOfBoundsException.class;
            x10Name = "x10.lang.StringIndexOutOfBoundsException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10RuntimeExceptions.put(javaClass, x10Class);
            
            javaClass = java.lang.ClassCastException.class;
            x10Name = "x10.lang.ClassCastException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10RuntimeExceptions.put(javaClass, x10Class);
            
            javaClass = java.lang.NumberFormatException.class;
            x10Name = "x10.lang.NumberFormatException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10RuntimeExceptions.put(javaClass, x10Class);
                        
            javaClass = java.lang.IllegalArgumentException.class;
            x10Name = "x10.lang.IllegalArgumentException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10RuntimeExceptions.put(javaClass, x10Class);
            
            javaClass = java.util.NoSuchElementException.class;
            x10Name = "x10.util.NoSuchElementException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10RuntimeExceptions.put(javaClass, x10Class);
            
            javaClass = java.lang.NullPointerException.class;
            x10Name = "x10.lang.NullPointerException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10RuntimeExceptions.put(javaClass, x10Class);

            javaClass = java.lang.UnsupportedOperationException.class;
            x10Name = "x10.lang.UnsupportedOperationException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10RuntimeExceptions.put(javaClass, x10Class);
            x10UnsupportedOperationException = x10Class;
            
            // x10Exceptions
            // N.B. ThrowableUtilities.x10Exceptions must be sync with TryCatchExpander.x10Exceptions
            javaClass = java.io.FileNotFoundException.class;
            x10Name = "x10.io.FileNotFoundException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Exceptions.put(javaClass, x10Class);
            
            javaClass = java.io.EOFException.class;
            x10Name = "x10.io.EOFException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.io.NotSerializableException.class;
            x10Name = "x10.io.NotSerializableException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.io.IOException.class;
            x10Name = "x10.io.IOException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.lang.InterruptedException.class;
            x10Name = "x10.lang.InterruptedException";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Exceptions.put(javaClass, x10Class);
            
            javaClass = java.lang.Exception.class;
            x10Name = "x10.lang.Exception";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Exceptions.put(javaClass, x10Class);
            x10Exception = x10Class;
            
            // x10Errors
            // N.B. ThrowableUtilities.x10Errors must be sync with TryCatchExpander.x10Errors
            javaClass = java.lang.OutOfMemoryError.class;
            x10Name = "x10.lang.OutOfMemoryError";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Errors.put(javaClass, x10Class);

            javaClass = java.lang.StackOverflowError.class;
            x10Name = "x10.lang.StackOverflowError";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Errors.put(javaClass, x10Class);

            javaClass = java.lang.AssertionError.class;
            x10Name = "x10.lang.AssertionError";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Errors.put(javaClass, x10Class);

            javaClass = java.lang.Error.class;
            x10Name = "x10.lang.Error";
            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Errors.put(javaClass, x10Class);
            x10Error = x10Class;

            // x10Throwables
            // N.B. ThrowableUtilities.x10Throwables must be sync with TryCatchExpander.x10Throwables
            // N.B. x10.lang.Throwable is @NativeRep'ed to x10.core.Throwable
            javaClass = java.lang.Throwable.class;
//            x10Name = "x10.core.Throwable";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.Throwable.class);
            x10Class = x10.core.Throwable.class;
            x10Throwables.put(javaClass, x10Class);
            x10Throwable = x10Class;
            
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
	
    private static x10.core.Throwable createX10Throwable(Class<? extends x10.core.Throwable> x10Class, java.lang.String message, java.lang.Throwable t) {
        try {
            x10.core.Throwable x10t = x10Class.getConstructor(new Class[] { java.lang.String.class }).newInstance(new Object[] { message });
            if (t != null) {
            	x10t.setStackTrace(t.getStackTrace());
            }
            return x10t;
        } catch (java.lang.Exception e) {
        }
        throw new java.lang.Error(t);
    }
    
	public static x10.core.Throwable getCorrespondingX10Exception(java.lang.RuntimeException e) {
        java.lang.String message = e.getMessage();
        Class<? extends x10.core.Throwable> x10Class = x10RuntimeExceptions.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownThrowable, which will be caught outside of main
            if (e instanceof UnknownJavaThrowable) return (x10.core.Throwable)e; // already wrapped
            else return new UnknownJavaThrowable(e);
            //x10Class = x10RuntimeException;
            //message = e.getClass().getName() + ": " + message;
        }
        
        return createX10Throwable(x10Class, message, e);
    }

	public static x10.core.Throwable getCorrespondingX10Exception(java.lang.Exception e) {
        java.lang.String message = e.getMessage();
        Class<? extends x10.core.Throwable> x10Class = x10Exceptions.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownThrowable, which will be caught outside of main
            return new UnknownJavaThrowable(e);
            //x10Class = x10Exception;
            //message = e.getClass().getName() + ": " + message;
        }

        return createX10Throwable(x10Class, message, e);
    }

	public static x10.core.Throwable getCorrespondingX10Error(java.lang.Error e) {
        java.lang.String message = e.getMessage();
        Class<? extends x10.core.Throwable> x10Class = x10Errors.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownThrowable, which will be caught outside of main
            return new UnknownJavaThrowable(e);
            //x10Class = x10Error;
            //message = e.getClass().getName() + ": " + message;
        }

        return createX10Throwable(x10Class, message, e);
    }

	public static x10.core.Throwable getCorrespondingX10Throwable(java.lang.Throwable e) {
        java.lang.String message = e.getMessage();
        Class<? extends x10.core.Throwable> x10Class = x10Throwables.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownThrowable, which will be caught outside of main
            return new UnknownJavaThrowable(e);
            //x10Class = x10Throwable;
            //message = e.getClass().getName() + ": " + message;
        }

        return createX10Throwable(x10Class, message, e);
    }

    public static x10.core.Throwable convertJavaRuntimeException(java.lang.RuntimeException e) {
    	return getCorrespondingX10Exception(e);
    }
    public static x10.core.Throwable convertJavaException(java.lang.Exception e) {
    	if (e instanceof java.lang.RuntimeException) {
    		return getCorrespondingX10Exception((java.lang.RuntimeException) e);
    	} else
    	/*if (e instanceof java.lang.Exception)*/ {
    		return getCorrespondingX10Exception(e);
    	}
    }
    public static x10.core.Throwable convertJavaError(java.lang.Error e) {
    	return getCorrespondingX10Error(e);
    }
    public static x10.core.Throwable convertJavaThrowable(java.lang.Throwable e) {
    	if (e instanceof java.lang.RuntimeException) {
    		return getCorrespondingX10Exception((java.lang.RuntimeException) e);
    	} else if (e instanceof java.lang.Exception) {
    		return getCorrespondingX10Exception((java.lang.Exception) e);
    	} else if (e instanceof java.lang.Error) {
    		return getCorrespondingX10Error((java.lang.Error) e);
    	} else
    	/*if (e instanceof java.lang.Throwable)*/ {
    		return getCorrespondingX10Throwable(e);
    	}
    }

    public static <T> T UnsupportedOperationException(java.lang.String message) {
        try {
            throw createX10Throwable(x10UnsupportedOperationException, message, null);
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        }
        return null;
    }

    public static int UnsupportedOperationExceptionInt(java.lang.String message) {
        try {
            throw createX10Throwable(x10UnsupportedOperationException, message, null);
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        }
        return 0;
    }

//    public static Array<java.lang.String> getStackTrace(Throwable t) {
//        StackTraceElement[] elements = t.getStackTrace();
//        java.lang.String str[] = new java.lang.String[elements.length];
//        for (int i=0 ; i<elements.length ; ++i) {
//            str[i] = elements[i].toString();
//        }
//        return x10.core.ArrayFactory.<java.lang.String>makeArrayFromJavaArray(Types.STRING, str);
//    }
    
//    public static void printStackTrace(Throwable t, x10.io.Printer p) {
//        x10.core.io.OutputStream os = p.getNativeOutputStream();
//        java.io.PrintStream ps = null;
//        if (os.stream instanceof java.io.PrintStream) {
//            ps = (java.io.PrintStream) os.stream;
//        } else {
//            ps = new java.io.PrintStream(os.stream);
//        }
//        t.printStackTrace(ps);
//    }
    
}
