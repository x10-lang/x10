package x10.lang;

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

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * Method used to do dynamic nullcheck when nullable is casted away.
	 */
	public static boolean isObjectNotNull(java.lang.Object o) {
		if (o == null)
			throw new ClassCastException("Cast of value 'null' to non-nullable type failed.");

		return true;
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static java.lang.Object throwClassCastException() throws ClassCastException {
		throw new ClassCastException("Expression is either not an instance of cast type or Constraints are not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation from the x10 compiler.
	 * Helper method Implementation to check dependent type constraint while performing dynamic cast.
	 * @param cTab
	 * @param nullableCheck
	 * @param obj
	 * @return
	 */
	public static java.lang.Object checkCast(RuntimeConstraint [] cTab, boolean nullableCheck, java.lang.Object objToCast, Class toClassType) {
//		Class toClassType;

//		try {
//			toClassType = Class.forName(toClassTypeName);
//		} catch (ClassNotFoundException e1) {
//			throw new ClassCastException("Class of type " + toClassTypeName + " is not found");
//		}

		// get class for reflexion
		Class fromClassType = objToCast.getClass();

		if (!toClassType.isAssignableFrom(fromClassType))
			throw new ClassCastException("Type " + fromClassType + " is not assignable to type " + toClassType);

		if (nullableCheck) {
			RuntimeCastChecker.isObjectNotNull(objToCast);
		}
		boolean correct = true;
		int i = 0;
		try {
			// for each constraints invoke class properties using reflexion
			while ((i < cTab.length) && correct) {
				RuntimeConstraint constraint = cTab[i];
//				System.out.println(constraint);
				// every property has a getter defined
				Method leftHand = fromClassType.getMethod(constraint.name, (Class[])null);
				if (constraint instanceof RuntimeConstraintOnSelf) {
					Method rightHand = fromClassType.getMethod((String) constraint.value, (Class[])null);
					// FIX bug while accessing nested class which are not visible by RuntimeCastChecker
					boolean accessibleR = rightHand.isAccessible();
					leftHand.setAccessible(true);
					boolean accessibleL = leftHand.isAccessible();
					rightHand.setAccessible(true);
					correct = (leftHand.invoke(objToCast, (java.lang.Object[])null))
						.equals(rightHand.invoke(objToCast, (java.lang.Object[])null));
					leftHand.setAccessible(accessibleL);
					rightHand.setAccessible(accessibleR);
				} else {
					// if here then method exists
					// FIX bug while accessing nested class which are not visible by RuntimeCastChecker
					boolean accessibleL = leftHand.isAccessible();
					leftHand.setAccessible(true);
					correct = (leftHand.invoke(objToCast, (java.lang.Object[])null)).equals(constraint.value);
					leftHand.setAccessible(accessibleL);
				}
				i++;
			}
		}
		catch(java.lang.Exception e) {
			// if an accessor is not found then type does not meet constraints
			e.printStackTrace(System.err);
			throw new ClassCastException("Reflect error while checking constraint " + cTab[i]);
		}

		// everything is ok
		if (correct)
			return objToCast;

		// deptype are equivalent however a constraint is not meet.
		throw new ClassCastException("Constraint " + cTab[i-1] + " is not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation from the x10 compiler.
	 * Helper method Implementation to check dependent type constraint while performing dynamic cast.
	 * @param cTab
	 * @param nullableCheck
	 * @param obj
	 * @return
	 */
	public static boolean isInstanceOf(RuntimeConstraint [] cTab, boolean nullableCheck, java.lang.Object obj, Class toClassType) {
		try {
			RuntimeCastChecker.checkCast(cTab, false, obj, toClassType);
		} catch(Throwable t) {
			return false;
		}
		return true;
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation from the x10 compiler.
	 * Dynamic check of Character primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the byte.
	 * @return the char value or an exception if cast does not meet constraints.
	 */
	public static char checkPrimitiveType(RuntimeConstraint [] cTab, boolean nullableCheck, char value, String toType) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Character) constraint.value).charValue());
			i++;
		}

		// everything is ok
		if (correct)
			return value;

		// deptype are equivalent however a constraint is not meet.
		throw new ClassCastException("Constraint " + cTab[i-1] + " is not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation from the x10 compiler.
	 * Dynamic check of Byte primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the byte.
	 * @return the byte value or an exception if cast does not meet constraints.
	 */
	public static byte checkPrimitiveType(RuntimeConstraint [] cTab, boolean nullableCheck, byte value, String toType) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Byte) constraint.value).byteValue());
			i++;
		}

		// everything is ok
		if (correct)
			return value;

		// deptype are equivalent however a constraint is not meet.
		throw new ClassCastException("Constraint " + cTab[i-1] + " is not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation from the x10 compiler.
	 * Dynamic check of Short primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the short.
	 * @return the short value or an exception if cast does not meet constraints.
	 */
	public static short checkPrimitiveType(RuntimeConstraint [] cTab, boolean nullableCheck, short value, String toType) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Short) constraint.value).shortValue());
			i++;
		}

		// everything is ok
		if (correct)
			return value;

		// deptype are equivalent however a constraint is not meet.
		throw new ClassCastException("Constraint " + cTab[i-1] + " is not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation from the x10 compiler.
	 * Dynamic check of Integer primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the integer.
	 * @return the integer value or an exception if cast does not meet constraints.
	 */
	public static int checkPrimitiveType(RuntimeConstraint [] cTab, boolean nullableCheck,
			int value, String toType) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Integer) constraint.value).intValue());
			i++;
		}

		// everything is ok
		if (correct)
			return value;

		// deptype are equivalent however a constraint is not meet.
		throw new ClassCastException("Constraint " + cTab[i-1] + " is not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation from the x10 compiler.
	 * Dynamic check of Long primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the long.
	 * @return the long value or an exception if cast does not meet constraints.
	 */
	public static long checkPrimitiveType(RuntimeConstraint [] cTab, boolean nullableCheck, long value, String toType) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Long) constraint.value).longValue());
			i++;
		}

		// everything is ok
		if (correct)
			return value;

		// deptype are equivalent however a constraint is not meet.
		throw new ClassCastException("Constraint " + cTab[i-1] + " is not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation from the x10 compiler.
	 * Dynamic check of Double primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the double.
	 * @return the double value or an exception if cast does not meet constraints.
	 */
	public static double checkPrimitiveType(RuntimeConstraint [] cTab, boolean nullableCheck, double value, String toType) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Double) constraint.value).doubleValue());
			i++;
		}

		// everything is ok
		if (correct)
			return value;

		// deptype are equivalent however a constraint is not meet.
		throw new ClassCastException("Constraint " + cTab[i-1] + " is not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation from the x10 compiler.
	 * Dynamic check of Float primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the float.
	 * @return the float value or an exception if cast does not meet constraints.
	 */
	public static float checkPrimitiveType(RuntimeConstraint [] cTab, boolean nullableCheck, float value, String toType) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Float) constraint.value).floatValue());
			i++;
		}

		// everything is ok
		if (correct)
			return value;

		// deptype are equivalent however a constraint is not meet.
		throw new ClassCastException("Constraint " + cTab[i-1] + " is not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 *
	 * Runtime representation of a clause from a dependent type constraint.
	 * @author vcave
	 */
	public static class RuntimeConstraint {
		public final String name;
		public final java.lang.Object value;

		public RuntimeConstraint(String n, java.lang.Object v) {
			this.name = n;
			this.value = v;
		}

		public RuntimeConstraint(String n, char v) {
			this(n, new java.lang.Character(v));
		}

		public RuntimeConstraint(String n, byte v) {
			this(n, new java.lang.Byte(v));
		}

		public RuntimeConstraint(String n, short v) {
			this(n, new java.lang.Short(v));
		}

		public RuntimeConstraint(String n, int v) {
			this(n, new java.lang.Integer(v));
		}

		public RuntimeConstraint(String n, long v) {
			this(n, new java.lang.Long(v));
		}

		public RuntimeConstraint(String n, double v) {
			this(n, new java.lang.Double(v));
		}

		public RuntimeConstraint(String n, float v) {
			this(n, new java.lang.Float(v));
		}

		public String toString() {
			return "[" +name + ","+ value+"]";
		}
	}

	public static class RuntimeConstraintOnSelf extends RuntimeConstraint {
		public RuntimeConstraintOnSelf(String n, String selfReference) {
			super(n,selfReference);
		}

		public String getSelfReference() {
			return (String) super.value;
		}
	}
}
