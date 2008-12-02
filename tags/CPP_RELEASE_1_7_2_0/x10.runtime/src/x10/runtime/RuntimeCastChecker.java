/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * Helper class to check dependent type constraint while performing dynamic cast at runtime.
 * WARNING ! Do not change the name of any method without changing
 * the Cast code generation from the x10 compiler.
 * @author vcave
 *
 */
public class RuntimeCastChecker {
	
	public interface DepTypeRuntimeChecking {

	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * Method used to do dynamic nullcheck when nullable is casted away.
	 */
	public static java.lang.Object checkCastToNonNullable(java.lang.Object o, Class c) {
		return (o != null) ? o : throwClassCastException(o, "Cannot cast a null instance to a non nullable type");
	}

	private static void throwClassCastException(String errorMsg)  throws ClassCastException {
		throw new ClassCastException(errorMsg);		
	}
	
	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static java.lang.Object throwClassCastException(java.lang.Object o, String errorMsg) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException(errorMsg);
		return o;
	}
	
	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static long throwClassCastException(long l, String errorMsg) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException(errorMsg);
		return l;
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static double throwClassCastException(double d, String errorMsg) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException(errorMsg);
		return d;
	}
	
	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static float throwClassCastException(float f, String errorMsg) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException(errorMsg);
		return f;
	}
}
