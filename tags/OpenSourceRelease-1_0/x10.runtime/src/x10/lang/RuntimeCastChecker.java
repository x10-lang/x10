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
	public static java.lang.Object checkCastToNonNullable(java.lang.Object o, Class c) {
		return (o != null) ? 
				(c.isAssignableFrom(o.getClass()) ? o : throwClassCastException(o)) : 
				throwClassCastException(o);
	}

	private static void throwClassCastException()  throws ClassCastException {
		throw new ClassCastException("Expression is either not an instance of cast type or Constraints are not meet");		
	}
	
	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static java.lang.Object throwClassCastException(java.lang.Object o) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException();
		return o;
	}
	
	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static short throwClassCastException(short p) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException();
		return p;
	}
	
	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */	
	public static int throwClassCastException(int p) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException();
		return p;
	}
	
	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static long throwClassCastException(long p) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException();
		return p;
	}
	
	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static double throwClassCastException(double p) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException();
		return p;
	}
	
	/**
	 * WARNING ! Do not change the name of this method without changing
	 * the Cast code generation of x10 compiler.
	 */
	public static float throwClassCastException(float p) throws ClassCastException {
		RuntimeCastChecker.throwClassCastException();
		return p;
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
	public static java.lang.Object checkCast(RuntimeConstraint [] cTab, boolean exprMustBeNotNull, boolean toTypeIsNullable,
			java.lang.Object objToCast, Class toClassType) {

		// if we try to cast from a nullable Type to a non nullable Type we must
		// first check expr to cast is not null
		// ((T) expr) whatever the type of expr, if it is a null at runtime the cast fails. 
		if ((exprMustBeNotNull) && (objToCast == null)) {
					throw new ClassCastException("Cast from nullable to non-nullable failed because instance is null.");
		}
		
		// (nullable<T> expr) if expr is null cast is valid
		if ((toTypeIsNullable) && (objToCast == null)) {
			return objToCast;
		}
		
		// else we have to check anyway if the expr is not null
		// but exception to throw is different and it avoids an ugly 
		// NullPointerException when trying to access expr class just here after.
		if (objToCast == null) {
			throw new ClassCastException("Expression to cast is null");
		}
		
		return checkDepType(cTab, objToCast, toClassType);
	}

	private static java.lang.Object checkDepType(RuntimeConstraint [] cTab, java.lang.Object objToCast, Class toClassType) {
		
		// get class for reflexion
		Class fromClassType = objToCast.getClass();

		if (!toClassType.isAssignableFrom(fromClassType))
			throw new ClassCastException("Type " + fromClassType + " is not assignable to type " + toClassType);

		boolean correct = true;
		int i = 0;
		try {
			// for each constraint invoke class property using reflexion
			while ((i < cTab.length) && correct) {
				RuntimeConstraint constraint = cTab[i];
				// every property has a getter defined
				Method leftHand = fromClassType.getMethod(constraint.name, (Class[])null);
				if (constraint.isConstraintOnSelf) {
					// both part of the expression are constraint on self. Ex: 'self.p == self.q'
					Method rightHand = fromClassType.getMethod((String) constraint.value, (Class[])null);
					// FIXED bug while accessing nested class which are not visible by RuntimeCastChecker
					boolean accessibleR = rightHand.isAccessible();
					leftHand.setAccessible(true);
					boolean accessibleL = leftHand.isAccessible();
					rightHand.setAccessible(true);
					// compare instance's property value to another instance's property
					correct = (leftHand.invoke(objToCast, (java.lang.Object[])null))
						.equals(rightHand.invoke(objToCast, (java.lang.Object[])null));
					leftHand.setAccessible(accessibleL);
					rightHand.setAccessible(accessibleR);
				} else {
					// Left part of the expression is a constraint on self and the other 
					// anything else except a constraint on self  Ex: 'self.p == self.q'
					// Ex: 'self.p == 4' 'self.p == i' 'self.p == obj.i'
					// FIXED bug while accessing nested class which are not visible by RuntimeCastChecker
					boolean accessibleL = leftHand.isAccessible();
					leftHand.setAccessible(true);
					// compare an instance property to its value declared in the cast. 
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
	public static boolean isInstanceOf(RuntimeConstraint [] cTab, boolean exprMustBeNotNull, boolean toTypeIsNullable,
			java.lang.Object obj, Class toClassType) {
		
		// null is never an instanceof something.
		if (obj == null)
			return false;
		
		try {
			RuntimeCastChecker.checkDepType(cTab, obj, toClassType);
		} catch(ClassCastException t) {
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
	public static char checkPrimitiveType(RuntimeConstraint [] cTab, char value) {
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
	public static byte checkPrimitiveType(RuntimeConstraint [] cTab, byte value) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Number) constraint.value).byteValue());
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
	public static short checkPrimitiveType(RuntimeConstraint [] cTab, short value) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Number) constraint.value).shortValue());
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
	public static int checkPrimitiveType(RuntimeConstraint [] cTab, int value) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Number) constraint.value).intValue());
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
	public static long checkPrimitiveType(RuntimeConstraint [] cTab, long value) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Number) constraint.value).longValue());
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
	public static double checkPrimitiveType(RuntimeConstraint [] cTab, double value) {
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
	public static float checkPrimitiveType(RuntimeConstraint [] cTab, float value) {
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
	 * the Cast code generation from the x10 compiler.
	 * Dynamic check of Boolean primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the boolean.
	 * @return the boolean value or an exception if cast does not meet constraints.
	 */
	public static boolean checkPrimitiveType(RuntimeConstraint [] cTab, boolean value) {
		boolean correct = true;
		int i = 0;
		// for each constraints
		while ((i < cTab.length) && (correct)) {
			RuntimeConstraint constraint = cTab[i];
			correct = (value == ((java.lang.Boolean) constraint.value).booleanValue());
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
		public final boolean isConstraintOnSelf;

		/**
		 * Left value is something like 'self.property()' and right value anything else
		 * except a constraint on self.
		 * @param n
		 * @param v
		 */
		public RuntimeConstraint(String n, java.lang.Object v) {
			this.name = n;
			this.value = v;
			this.isConstraintOnSelf = false;
		}
		
		/**
		 * Both left and right value are something like 'self.property()'
		 * @param n
		 * @param v
		 * @param rightValueIsConstraintOnSelf
		 */
		public RuntimeConstraint(String n, java.lang.Object v, 
				boolean rightValueIsConstraintOnSelf) {
			// right value constraint is on 'self' property: self.p
			// Hence checking code should load p using reflexion
			assert(rightValueIsConstraintOnSelf == true);
			this.name = n;
			this.value = v;
			this.isConstraintOnSelf = rightValueIsConstraintOnSelf;
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

		public RuntimeConstraint(String n, boolean v) {
			this(n, new java.lang.Boolean(v));
		}

		public String toString() {
			return "[" +name + ","+ value+"]";
		}
	}
}
