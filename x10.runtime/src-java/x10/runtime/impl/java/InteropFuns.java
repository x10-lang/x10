/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.runtime.impl.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InteropFuns {
    public static Object invokeAndUnwrapExceptions(Method m, Object obj, Object[] args) throws Throwable {
	try {
	    return m.invoke(obj, args);
	} catch (InvocationTargetException e) {
	    Throwable cause = e.getCause();
	    // TODO CHECKED_THROWABLE replace UnknownJavaThrowable with x10.lang.WrappedThrowable
//	    while (cause instanceof UnknownJavaThrowable) {
	    while (cause instanceof x10.lang.WrappedThrowable) {
		cause = cause.getCause();
	    }
	    throw cause;
	} catch (IllegalArgumentException e) {
	    throw e;
	} catch (IllegalAccessException e) {
	    throw e;
	}
    }
}
