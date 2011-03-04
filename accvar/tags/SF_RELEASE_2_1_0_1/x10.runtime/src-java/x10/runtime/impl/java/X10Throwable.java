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

package x10.runtime.impl.java;

import x10.core.Any;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class X10Throwable extends java.lang.RuntimeException implements Any {

    public X10Throwable() {
        super();
    }

    public X10Throwable(String message) {
        super(message);
    }

    public X10Throwable(java.lang.Throwable cause) {
        super(cause);
    }

    public X10Throwable(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    // XTENLANG-1858: every Java class that could be an (non-static) inner class must have constructors with the outer instance parameter
    public X10Throwable(Object out$) {
        super();
    }

    public X10Throwable(Object out$, String message) {
        super(message);
    }

    public X10Throwable(Object out$, java.lang.Throwable cause) {
        super(cause);
    }

    public X10Throwable(Object out$, String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    static public X10Throwable getCorrespondingX10Exception(java.lang.RuntimeException e) {
        String newExcName = "x10.lang.RuntimeException";
        if (e instanceof java.lang.ArithmeticException) {
            newExcName = "x10.lang.ArithmeticException";
        } else if (e instanceof java.lang.ArrayIndexOutOfBoundsException) {
            newExcName = "x10.lang.ArrayIndexOutOfBoundsException";
        } else if (e instanceof java.lang.ClassCastException) {
            newExcName = "x10.lang.ClassCastException";
        } else if (e instanceof java.lang.IllegalArgumentException) {
            newExcName = "x10.lang.IllegalArgumentException";
        } else if (e instanceof java.util.NoSuchElementException) {
            newExcName = "x10.util.NoSuchElementException";
        } else if (e instanceof java.lang.NullPointerException) {
            newExcName = "x10.lang.NullPointerException";
        } else if (e instanceof java.lang.UnsupportedOperationException) {
            newExcName = "x10.lang.UnsupportedOperationException";
        } else {
            // no corresponding x10 exceptions defined
        }

        try {
            X10Throwable t = (X10Throwable) Class.forName(newExcName).getConstructor(new Class[] { String.class }).newInstance(new Object[] { e.getMessage() });
            t.setStackTrace(e.getStackTrace());
            return t;
        } catch (java.lang.ClassNotFoundException e1) {
        } catch (java.lang.InstantiationException e2) {
        } catch (java.lang.IllegalAccessException e3) {
        } catch (java.lang.NoSuchMethodException e4) {
        } catch (java.lang.reflect.InvocationTargetException e5) {
        }
        throw new java.lang.Error();
    }

    static public X10Throwable getCorrespondingX10Error(java.lang.Error e) {
        String newExcName = "x10.lang.Error";
        if (e instanceof java.lang.OutOfMemoryError) {
            newExcName = "x10.lang.OutOfMemoryError";
        } else {
            // no corresponding x10 errors defined
        }

        try {
            X10Throwable t = (X10Throwable) Class.forName(newExcName).getConstructor(new Class[] { String.class }).newInstance(new Object[] { e.getMessage() });
            t.setStackTrace(e.getStackTrace());
            return t;
        } catch (java.lang.ClassNotFoundException e1) {
        } catch (java.lang.InstantiationException e2) {
        } catch (java.lang.IllegalAccessException e3) {
        } catch (java.lang.NoSuchMethodException e4) {
        } catch (java.lang.reflect.InvocationTargetException e5) {
        }
        throw new java.lang.Error();
    }

    public static final RuntimeType<X10Throwable> _RTT = new RuntimeType<X10Throwable>(
        X10Throwable.class
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
