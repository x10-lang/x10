package x10.lang;

import java.lang.reflect.Field;
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
	 * the Cast code generation of x10 compiler.
	 */
	public static <U> U throwClassCastException() throws ClassCastException {
			throw new ClassCastException("Expression is either not an instance of cast type or Constraints are not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing 
	 * the Cast code generation from the x10 compiler.
	 * Helper method Implementation to check dependent type constraint while performing dynamic cast.
	 * @see public static <U> U checkX10ArrayType(RuntimeConstraint [] cTab, Object obj)
	 * @see public static <U> U checkType(RuntimeConstraint [] cTab, Object obj)
	 * @param <U>
	 * @param cTab
	 * @param obj
	 * @param x10array
	 * @return
	 */
	public static <U> U checkCast(RuntimeConstraint [] cTab, java.lang.Object obj) {
		// try regular java cast
		 U castedObj = (U) obj;
		 // get class for reflexion
		 Class cl = castedObj.getClass();
		 boolean correct = true;
		 int i = 0;
		 try {
			 // for each constraints invoke class properties using reflexion
			 while ((i < cTab.length) && (correct)) {
				 RuntimeConstraint constraint = cTab[i];
				 Method m = cl.getMethod(constraint.name, null);
				 // if here then method exists
				 correct = (m.invoke(castedObj,null)).equals(constraint.value);					 
				 i++;
			 }
		 } catch(Throwable t) { 			 
			 // if an accessor is not found then type does not meet constraints
			 throw new ClassCastException("Reflect error while checking constraint " + cTab[i]);
		 }
		 
		 // everything is ok
		 if (correct)
			 return castedObj;
		 
		 // deptype are equivalent however a constraint is not meet.
		 throw new ClassCastException("Constraint " + cTab[i-1] + " is not meet");
	}

	/**
	 * WARNING ! Do not change the name of this method without changing 
	 * the Cast code generation from the x10 compiler.
	 * Helper method Implementation to check dependent type constraint while performing dynamic cast.
	 * @see public static <U> U checkX10ArrayType(RuntimeConstraint [] cTab, Object obj)
	 * @see public static <U> U checkType(RuntimeConstraint [] cTab, Object obj)
	 * @param <U>
	 * @param cTab
	 * @param obj
	 * @param x10array
	 * @return
	 */
	public static <U> boolean isInstanceOf(RuntimeConstraint [] cTab, java.lang.Object obj) {
	
		try {
			RuntimeCastChecker.<U>checkCast(cTab,obj);	
		} catch(Throwable t) {
			return false;
		}
		
		return true;
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
		 System.out.println("invoke check short");
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
	 * Dynamic check of Byte primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the byte.
	 * @return the byte value or an exception if cast does not meet constraints.
	 */
	public static byte checkPrimitiveType(RuntimeConstraint [] cTab, byte value) {
		 boolean correct = true;
		 int i = 0;
		 System.out.println("invoke check byte");
		 // for each constraints
		 while ((i < cTab.length) && (correct)) {
			 RuntimeConstraint constraint = cTab[i];
			 System.out.println(value);
			 System.out.println(constraint.value);
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
	 * Dynamic check of Integer primitive type constraints.
	 * INVARIANT: the value is not changed !
	 * @param cTab The constraints value to check against.
	 * @param value Current value of the integer.
	 * @return the integer value or an exception if cast does not meet constraints.
	 */
	public static int checkPrimitiveType(RuntimeConstraint [] cTab, int value) {
		 boolean correct = true;
		 int i = 0;
		 System.out.println("invoke check integer");
		 // for each constraints
		 while ((i < cTab.length) && (correct)) {
			 RuntimeConstraint constraint = cTab[i];
			 System.out.println(value);
			 System.out.println(constraint.value);
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
	public static long checkPrimitiveType(RuntimeConstraint [] cTab, long value) {
		 boolean correct = true;
		 int i = 0;
		 System.out.println("invoke check long");
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
	public static double checkPrimitiveType(RuntimeConstraint [] cTab, double value) {
		 boolean correct = true;
		 int i = 0;
		 System.out.println("invoke check double");
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
		 System.out.println("invoke check float");
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
		 
		 public String toString() {
			 return "[" +name + ","+ value+"]";
		 }
	}

}
