/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;


/**
 * WARNING ! Do not change the name of this method without changing
 * the Cast code generation of x10 compiler.
 *
 * Runtime representation of a clause from a dependent type constraint.
 * @author vcave
 */
public class RuntimeConstraint {
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