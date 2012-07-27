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

import java.util.HashMap;
import java.util.Map;

// TODO CHECKED_THROWABLE replace UnknownJavaThrowable with x10.lang.WrappedThrowable
//import x10.runtime.impl.java.UnknownJavaThrowable;

public abstract class ThrowableUtilities {
    
    // N.B. ThrowableUtilities.x10{RuntimeException,Exception,Error,Throwable}s must be sync with TryCatchExpander.knownJava{RuntimeException,Exception,Error,Throwable}s
	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends x10.core.X10Throwable>> x10RuntimeExceptions = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends x10.core.X10Throwable>>();
//    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends x10.core.X10Throwable>> x10Exceptions = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends x10.core.X10Throwable>>();
//    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends x10.core.X10Throwable>> x10Errors = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends x10.core.X10Throwable>>();
//    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends x10.core.X10Throwable>> x10Throwables = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends x10.core.X10Throwable>>();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10RuntimeExceptions = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>>();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10Exceptions = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>>();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10Errors = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>>();
    private static final Map<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>> x10Throwables = new HashMap<Class<? extends java.lang.Throwable>,Class<? extends java.lang.RuntimeException>>();

    // TODO CHECKED_THROWABLE stop converting Java exception types that are mapped (i.e. not wrapped) to x10 exception types. 
//    private static Class<? extends x10.core.X10Throwable> x10UnsupportedOperationException;
    
    static {
        try {
            Class<? extends java.lang.Throwable> javaClass;
            java.lang.String x10Name;
        	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//            Class<? extends x10.core.X10Throwable> x10Class;
            Class<? extends java.lang.RuntimeException> x10Class;
            
            // x10RuntimeExceptions
            // TODO CHECKED_THROWABLE stop converting Java exception types that are mapped (i.e. not wrapped) to x10 exception types. 
//            javaClass = java.lang.ArithmeticException.class;
//            x10Name = "x10.lang.ArithmeticException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//
//            javaClass = java.lang.ArrayIndexOutOfBoundsException.class;
//            x10Name = "x10.lang.ArrayIndexOutOfBoundsException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//
//            javaClass = java.lang.StringIndexOutOfBoundsException.class;
//            x10Name = "x10.lang.StringIndexOutOfBoundsException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//            
//            javaClass = java.lang.IndexOutOfBoundsException.class;
//            x10Name = "x10.lang.IndexOutOfBoundsException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//            
//            javaClass = java.lang.ClassCastException.class;
//            x10Name = "x10.lang.ClassCastException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//            
//            javaClass = java.lang.NumberFormatException.class;
//            x10Name = "x10.lang.NumberFormatException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//                        
//            javaClass = java.lang.IllegalArgumentException.class;
//            x10Name = "x10.lang.IllegalArgumentException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//            
//            javaClass = java.util.NoSuchElementException.class;
//            x10Name = "x10.util.NoSuchElementException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//            
//            javaClass = java.lang.NullPointerException.class;
//            x10Name = "x10.lang.NullPointerException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//
//            javaClass = java.lang.UnsupportedOperationException.class;
//            x10Name = "x10.lang.UnsupportedOperationException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
//            x10UnsupportedOperationException = x10Class;

	    // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//            javaClass = java.lang.RuntimeException.class;
//            x10Name = "x10.lang.RuntimeException";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10RuntimeExceptions.put(javaClass, x10Class);
            
            // x10Exceptions
            javaClass = java.io.NotSerializableException.class;
            x10Name = "x10.io.NotSerializableException";
            // TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);

            // N.B. subtypes of java.io.IOException should be caught and converted to the corresponding x10 exceptions in XRJ
//            javaClass = java.io.FileNotFoundException.class;
//            x10Name = "x10.io.FileNotFoundException";
//            // TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
////            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
//            x10Exceptions.put(javaClass, x10Class);
//            
//            javaClass = java.io.EOFException.class;
//            x10Name = "x10.io.EOFException";
//            // TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
////            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
//            x10Exceptions.put(javaClass, x10Class);
//
//            javaClass = java.io.IOException.class;
//            x10Name = "x10.io.IOException";
//            // TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
////            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
//            x10Exceptions.put(javaClass, x10Class);

            javaClass = java.lang.InterruptedException.class;
            x10Name = "x10.lang.InterruptedException";
        	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
            x10Class = Class.forName(x10Name).asSubclass(java.lang.RuntimeException.class);
            x10Exceptions.put(javaClass, x10Class);
            
	    // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//            javaClass = java.lang.Exception.class;
//            x10Name = "x10.lang.Exception";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10Exceptions.put(javaClass, x10Class);
            
            // x10Errors
            // TODO CHECKED_THROWABLE stop converting Java exception types that are mapped (i.e. not wrapped) to x10 exception types. 
//            javaClass = java.lang.OutOfMemoryError.class;
//            x10Name = "x10.lang.OutOfMemoryError";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10Errors.put(javaClass, x10Class);
//
//            javaClass = java.lang.StackOverflowError.class;
//            x10Name = "x10.lang.StackOverflowError";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10Errors.put(javaClass, x10Class);
//
//            // XTENLANG-3090 stop converting j.l.AssertionError to x.l.AssertionError (switched back to use java assertion)
//            javaClass = java.lang.AssertionError.class;
//            x10Name = "x10.lang.AssertionError";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10Errors.put(javaClass, x10Class);

	    // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//            javaClass = java.lang.Error.class;
//            x10Name = "x10.lang.Error";
//            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10Errors.put(javaClass, x10Class);

            // x10Throwables
            // N.B. x10.lang.Throwable is @NativeRep'ed to x10.core.X10Throwable
	    // XTENLANG-2871 stop converting j.l.{Throwable,Exception,RuntimeException,Error} to x.l.{Throwable,Exception,RuntimeException,Error}
//            javaClass = java.lang.Throwable.class;
////            x10Name = "x10.core.X10Throwable";
////            x10Class = Class.forName(x10Name).asSubclass(x10.core.X10Throwable.class);
//            x10Class = x10.core.X10Throwable.class;
//            x10Throwables.put(javaClass, x10Class);
            
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
	
	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//    private static x10.core.X10Throwable createX10Throwable(Class<? extends x10.core.X10Throwable> x10Class, java.lang.String message, java.lang.Throwable t) {
    private static java.lang.RuntimeException createX10Throwable(Class<? extends java.lang.RuntimeException> x10Class, java.lang.String message, java.lang.Throwable t) {
        try {
        	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//            x10.core.X10Throwable x10t = x10Class.getConstructor(new Class[] { java.lang.String.class }).newInstance(new Object[] { message });
        	java.lang.RuntimeException x10t = x10Class.getConstructor(new Class[] { java.lang.String.class }).newInstance(new Object[] { message });
            if (t != null) {
            	x10t.setStackTrace(t.getStackTrace());
            }
            return x10t;
        } catch (java.lang.Exception e) {
        }
        throw new java.lang.Error(t);
    }
    
	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//	public static x10.core.Throwable getCorrespondingX10Throwable(java.lang.RuntimeException e) {
	public static java.lang.RuntimeException getCorrespondingX10Throwable(java.lang.RuntimeException e) {
		// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//		if (e instanceof UnknownJavaThrowable) return (x10.core.Throwable) e; // already wrapped
		// TODO CHECKED_THROWABLE replace UnknownJavaThrowable with x10.lang.WrappedThrowable
//		if (e instanceof UnknownJavaThrowable) return e; // already wrapped
		if (e instanceof x10.lang.WrappedThrowable) return e; // already wrapped
        java.lang.String message = e.getMessage();
    	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//        Class<? extends x10.core.X10Throwable> x10Class = x10RuntimeExceptions.get(e.getClass());
        Class<? extends java.lang.RuntimeException> x10Class = x10RuntimeExceptions.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownJavaThrowable, which will be caught outside of main
            // TODO CHECKED_THROWABLE replace UnknownJavaThrowable with x10.lang.WrappedThrowable
//            return new UnknownJavaThrowable(e);
            return new x10.lang.WrappedThrowable(e);
        }
        
        return createX10Throwable(x10Class, message, e);
    }

	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//	public static x10.core.Throwable getCorrespondingX10Throwable(java.lang.Exception e) {
	public static java.lang.RuntimeException getCorrespondingX10Throwable(java.lang.Exception e) {
		// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//		if (e instanceof UnknownJavaThrowable) return (x10.core.Throwable) e; // already wrapped
		// TODO CHECKED_THROWABLE replace UnknownJavaThrowable with x10.lang.WrappedThrowable
//		if (e instanceof UnknownJavaThrowable) return (java.lang.RuntimeException) e; // already wrapped
		if (e instanceof x10.lang.WrappedThrowable) return (java.lang.RuntimeException) e; // already wrapped
        java.lang.String message = e.getMessage();
    	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//        Class<? extends x10.core.X10Throwable> x10Class = x10Exceptions.get(e.getClass());
        Class<? extends java.lang.RuntimeException> x10Class = x10Exceptions.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownJavaThrowable, which will be caught outside of main
            // TODO CHECKED_THROWABLE replace UnknownJavaThrowable with x10.lang.WrappedThrowable
//            return new UnknownJavaThrowable(e);
            return new x10.lang.WrappedThrowable(e);
        }

        return createX10Throwable(x10Class, message, e);
    }

	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//	public static x10.core.Throwable getCorrespondingX10Throwable(java.lang.Error e) {
	public static java.lang.RuntimeException getCorrespondingX10Throwable(java.lang.Error e) {
        java.lang.String message = e.getMessage();
    	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//        Class<? extends x10.core.X10Throwable> x10Class = x10Errors.get(e.getClass());
        Class<? extends java.lang.RuntimeException> x10Class = x10Errors.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownJavaThrowable, which will be caught outside of main
            // TODO CHECKED_THROWABLE replace UnknownJavaThrowable with x10.lang.WrappedThrowable
//            return new UnknownJavaThrowable(e);
            return new x10.lang.WrappedThrowable(e);
        }

        return createX10Throwable(x10Class, message, e);
    }

	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//	public static x10.core.Throwable getCorrespondingX10Throwable(java.lang.Throwable e) {
	public static java.lang.RuntimeException getCorrespondingX10Throwable(java.lang.Throwable e) {
		// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//		if (e instanceof UnknownJavaThrowable) return (x10.core.Throwable) e; // already wrapped
		// TODO CHECKED_THROWABLE replace UnknownJavaThrowable with x10.lang.WrappedThrowable
//		if (e instanceof UnknownJavaThrowable) return (java.lang.RuntimeException) e; // already wrapped
		if (e instanceof x10.lang.WrappedThrowable) return (java.lang.RuntimeException) e; // already wrapped
        java.lang.String message = e.getMessage();
    	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//        Class<? extends x10.core.X10Throwable> x10Class = x10Throwables.get(e.getClass());
        Class<? extends java.lang.RuntimeException> x10Class = x10Throwables.get(e.getClass());
        if (x10Class == null) {
            // no corresponding x10 exceptions defined
            // XTENLANG-2686: wrap unknown Java exception with UnknownJavaThrowable, which will be caught outside of main
            // TODO CHECKED_THROWABLE replace UnknownJavaThrowable with x10.lang.WrappedThrowable
//            return new UnknownJavaThrowable(e);
            return new x10.lang.WrappedThrowable(e);
        }

        return createX10Throwable(x10Class, message, e);
    }

	// TODO CHECKED_THROWABLE not used
//    public static x10.core.Throwable convertJavaRuntimeException(java.lang.RuntimeException e) {
//    	return getCorrespondingX10Throwable(e);
//    }
//    public static x10.core.Throwable convertJavaException(java.lang.Exception e) {
//    	if (e instanceof java.lang.RuntimeException) {
//    		return getCorrespondingX10Throwable((java.lang.RuntimeException) e);
//    	} else
//    	/*if (e instanceof java.lang.Exception)*/ {
//    		return getCorrespondingX10Throwable(e);
//    	}
//    }
//    public static x10.core.Throwable convertJavaError(java.lang.Error e) {
//    	return getCorrespondingX10Throwable(e);
//    }
	// TODO CHECKED_THROWABLE x10.lang.Exception is mapped to java.lang.RuntimeException rather than x10.core.X10Thowable.
//    public static x10.core.Throwable convertJavaThrowable(java.lang.Throwable e) {
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

    public static java.lang.RuntimeException getUncheckedCause(java.lang.Throwable e) {
    	java.lang.Throwable cause = e.getCause();
    	return cause instanceof java.lang.RuntimeException ? ((java.lang.RuntimeException) cause) : new x10.lang.WrappedThrowable(cause);
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
        return x10.core.ArrayFactory.<java.lang.String>makeArrayFromJavaArray(x10.rtt.Types.STRING, str);
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
        // TODO CHECKED_THROWABLE stop converting Java exception types that are mapped (i.e. not wrapped) to x10 exception types. 
//        try {
//            throw createX10Throwable(x10UnsupportedOperationException, message, null);
//        } catch (IllegalArgumentException e) {
//        } catch (SecurityException e) {
//        }
//        return null;
    	throw new java.lang.UnsupportedOperationException(message);
    }

    public static int UnsupportedOperationExceptionInt(java.lang.String message) {
        // TODO CHECKED_THROWABLE stop converting Java exception types that are mapped (i.e. not wrapped) to x10 exception types. 
//        try {
//            throw createX10Throwable(x10UnsupportedOperationException, message, null);
//        } catch (IllegalArgumentException e) {
//        } catch (SecurityException e) {
//        }
//        return 0;
    	throw new java.lang.UnsupportedOperationException(message);
    }

}
