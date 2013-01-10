package x10.constraints.tests;

import junit.framework.TestCase;
import x10.constraint.XConstraintSystem;
import x10.constraint.XLit;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;
import x10.constraint.XUQV;
import x10.constraint.smt.XSmtConstraintSystem;
import x10.constraint.xnative.XNativeConstraintSystem;

// Each unit test gets its own type system, types, and constraint system.

public abstract class BaseTest extends TestCase {
	
	public BaseTest(String name) { super(name); }
	
	private static boolean USE_SMT_SOLVER = false;

	public XConstraintSystem<TestType> xcs =  USE_SMT_SOLVER ? new XSmtConstraintSystem<TestType>() : new XNativeConstraintSystem<TestType>();

	
	// Types have reference equality, use intType etc from BaseTest and never clone them
	
	public XTypeSystem<TestType> ts = new XTypeSystem<TestType>() {
		@Override public TestType Boolean() { return booleanType; }
		@Override public TestType Null() { return nullType; }
	};
	
	public abstract class TestType implements XType {
		@Override public boolean isBoolean() { return false; } 
		@Override public boolean isChar() { return false; } 
		@Override public boolean isByte() { return false; } 
		@Override public boolean isShort() { return false; } 
		@Override public boolean isInt() { return false; } 
		@Override public boolean isLong() { return false; } 
		@Override public boolean isFloat() { return false; } 
		@Override public boolean isDouble() { return false; } 
		@Override public boolean isUByte() { return false; } 
		@Override public boolean isUShort() { return false; } 
		@Override public boolean isUInt() { return false; } 
		@Override public boolean isULong() { return false; } 
		@Override public XTypeSystem<TestType> xTypeSystem() { return ts; } 
	}
	
	public TestType intType = new TestType() {
		@Override public boolean isPrimitive() { return true; }
		@Override public boolean isInt() { return true; } 
	};
	
	public TestType booleanType = new TestType() {
		@Override public boolean isPrimitive() { return true; }
		@Override public boolean isBoolean() { return true; } 
	};
	
	public TestType nullType = new TestType() {
		@Override public boolean isPrimitive() { return false; }
	};
	
	public TestType objType = new TestType() {
		@Override public boolean isPrimitive() { return false; }
	};
	

	// Now some literals
	
	XLit<TestType, Integer> zero = xcs.makeLit(intType, new Integer(0));
	XLit<TestType, Integer> one  = xcs.makeLit(intType, new Integer(1));
	XLit<TestType, Integer> two  = xcs.makeLit(intType, new Integer(2));

	XLit<TestType, Boolean> TRUE = xcs.makeLit(booleanType, new Boolean(false));
	XLit<TestType, Boolean> FALSE  = xcs.makeLit(booleanType, new Boolean(true));

	XLit<TestType, Object> NULL = xcs.makeLit(nullType, null);

	
	// And some premade variables that may be useful

	XUQV<TestType> v0 = xcs.makeUQV(objType, "v0");
	XUQV<TestType> v1 = xcs.makeUQV(objType, "v1");
	XUQV<TestType> v2 = xcs.makeUQV(objType, "v2");
	XUQV<TestType> v3 = xcs.makeUQV(objType, "v3");
	XUQV<TestType> v4 = xcs.makeUQV(objType, "v4");
	XUQV<TestType> v5 = xcs.makeUQV(objType, "v5");

}
