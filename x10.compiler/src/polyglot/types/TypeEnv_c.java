/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types;

import java.util.*;

import polyglot.frontend.Globals;
import polyglot.main.Reporter;
import polyglot.types.TypeSystem_c.ConstructorMatcher;
import x10.types.MethodInstance;
import x10.types.X10TypeEnv_c;

/**
 * Typing environment.
 * 
 * For a given typing rule Gamma |- Phi, this is Gamma. Phi is a method of
 * TypeEnv.
 */
public abstract class TypeEnv_c implements TypeEnv, Cloneable {
    protected Context context; // the actual context. Eliminate this and merge
    // with Context.
    protected TypeSystem ts;
    protected Reporter reporter;

    public TypeEnv_c(Context context) {
    	assert context != null; 
	this.context = context;
	this.ts = context.typeSystem();
    this.reporter = this.ts.extensionInfo().getOptions().reporter;
    }

    public X10TypeEnv_c shallowCopy() {
	try {
	    return (X10TypeEnv_c) super.clone();
	}
	catch (CloneNotSupportedException e) {
	    assert false;
	    return null;
	}
    }

    /**
     * Returns true iff the type t can be coerced to a String in the given
     * Context. If a type can be coerced to a String then it can be concatenated
     * with Strings, e.g. if o is of type T, then the code snippet "" + o would
     * be allowed.
     */
    public boolean canCoerceToString(Type t) {
	// every Object can be coerced to a string, as can any primitive,
	// except void.
	return !t.isVoid();
    }

    /**
     * Returns true iff type1 and type2 are equivalent.
     */
    public boolean typeEquals(Type type1, Type type2) {
	if (type1 == type2)
	    return true;
	if (type1 == null || type2 == null)
	    return false;

	if (type1 instanceof JavaArrayType && type2 instanceof JavaArrayType) {
	    JavaArrayType at1 = (JavaArrayType) type1;
	    JavaArrayType at2 = (JavaArrayType) type2;
	    return typeEquals(at1.base(), at2.base());
	}

	return ts.equals((TypeObject) type1, (TypeObject) type2);
    }

    /**
     * Returns true iff type1 and type2 are equivalent.
     */
    public boolean packageEquals(Package type1, Package type2) {
	if (type1 == type2)
	    return true;
	if (type1 == null || type2 == null)
	    return false;
	return type1.packageEquals(type2);
    }

    /**
     * Requires: all type arguments are canonical. ToType is not a NullType.
     * 
     * Returns true iff a cast from fromType to toType is valid; in other words,
     * some non-null members of fromType are also members of toType.
     **/
    public boolean isCastValid(Type fromType, Type toType) {
	if (fromType instanceof NullType) {
	    return toType.isNull() || toType.isReference();
	}

	if (fromType.isJavaPrimitive() && toType.isJavaPrimitive()) {
	    if (fromType.isVoid() || toType.isVoid())
		return false;
	    if (ts.typeEquals(fromType, toType, context))
		return true;
	    if (fromType.isNumeric() && toType.isNumeric())
		return true;
	    return false;
	}

	if (fromType instanceof JavaArrayType && toType instanceof JavaArrayType) {
	    JavaArrayType fromAT = (JavaArrayType) fromType;
	    JavaArrayType toAT = (JavaArrayType) toType;

	    Type fromBase = fromAT.base();
	    Type toBase = toAT.base();

	    if (fromBase.isJavaPrimitive())
		return ts.typeEquals(toBase, fromBase, context);
	    if (toBase.isJavaPrimitive())
		return false;

	    if (fromBase.isNull())
		return false;
	    if (toBase.isNull())
		return false;

	    // Both are reference types.
	    return ts.isCastValid(fromBase, toBase, context);
	}

	if (fromType instanceof JavaArrayType && toType instanceof ObjectType) {
	    // Ancestor is not an array, but child is. Check if the array
	    // is a subtype of the ancestor. This happens when ancestor
	    // is java.lang.Object.
	    return ts.isSubtype(fromType, toType, context);
	}

	if (fromType instanceof ClassType && toType instanceof JavaArrayType) {
	    // From type is not an array, but to type is. Check if the array
	    // is a subtype of the from type. This happens when from type
	    // is java.lang.Object.
	    return ts.isSubtype(toType, fromType, context);
	}

	if (fromType instanceof ClassType && toType instanceof ClassType) {
	    ClassType fromCT = (ClassType) fromType;
	    ClassType toCT = (ClassType) toType;

	    // From and to are neither primitive nor an array. They are
	    // distinct.
	    boolean fromInterface = fromCT.flags().isInterface();
	    boolean toInterface = toCT.flags().isInterface();
	    boolean fromFinal = fromCT.flags().isFinal() || Types.isX10Struct(fromCT);
	    boolean toFinal = toCT.flags().isFinal();

	    // This is taken from Section 5.5 of the JLS.
	    if (fromInterface) {
		// From is an interface
		if (!toInterface && !toFinal) {
		    // To is a non-final class.
		    return true;
		}

		if (toFinal) {
		    // To is a final class.
		    return ts.isSubtype(toType, fromCT, context);
		}

		// To and From are both interfaces.
		return true;
	    }
	    else {
		// From is not an interface.
		if (!toInterface) {
		    // Nether from nor to is an interface.
		    return ts.isSubtype(fromCT, toType, context) || ts.isSubtype(toType, fromCT, context);
		}

		if (fromFinal) {
		    // From is a final class, and to is an interface
		    return ts.isSubtype(fromCT, toType, context);
		}

		// From is a non-final class, and to is an interface.
		return true;
	    }
	}

	if (fromType instanceof ObjectType) {
	    if (!toType.isReference())
		return false;
	    return ts.isSubtype(fromType, toType, context) || ts.isSubtype(toType, fromType, context);
	}

	return false;
    }

    /**
     * Requires: all type arguments are canonical.
     * 
     * Returns true iff an implicit cast from fromType to toType is valid; in
     * other words, every member of fromType is member of toType.
     * 
     * Returns true iff child and ancestor are non-primitive types, and a
     * variable of type child may be legally assigned to a variable of type
     * ancestor.
     * 
     */
    public boolean isImplicitCastValid(Type fromType, Type toType) {
	if (fromType.isJavaPrimitive() && toType.isJavaPrimitive()) {
	    if (toType.isVoid())
		return false;
	    if (fromType.isVoid())
		return false;

	    if (ts.typeEquals(toType, fromType, context))
		return true;

	    if (toType.isBoolean())
		return fromType.isBoolean();
	    if (fromType.isBoolean())
		return false;

	    if (!fromType.isNumeric() || !toType.isNumeric())
		return false;

	    if (toType.isDouble())
		return true;
	    if (fromType.isDouble())
		return false;

	    if (toType.isFloat())
		return true;
	    if (fromType.isFloat())
		return false;

	    if (toType.isLong())
		return true;
	    if (fromType.isLong())
		return false;

	    if (toType.isInt())
		return true;
	    if (fromType.isInt())
		return false;

	    if (toType.isShort())
		return fromType.isShort() || fromType.isByte();
	    if (fromType.isShort())
		return false;

	    if (toType.isChar())
		return fromType.isChar();
	    if (fromType.isChar())
		return false;

	    if (toType.isByte())
		return fromType.isByte();
	    if (fromType.isByte())
		return false;

	    return false;
	}

	if (fromType instanceof JavaArrayType && toType instanceof JavaArrayType) {
	    JavaArrayType fromAT = (JavaArrayType) fromType;
	    Type fromBase = fromAT.base();
	    JavaArrayType toAT = (JavaArrayType) toType;
	    Type toBase = toAT.base();
	    if (fromBase.isJavaPrimitive() || toBase.isJavaPrimitive()) {
		return ts.typeEquals(fromBase, toBase, context);
	    }
	    else {
		return isImplicitCastValid(fromBase, toBase);
	    }
	}

	if (fromType instanceof NullType) {
	    return toType.isNull() || toType.isReference();
	}

	if (fromType instanceof ObjectType) {
	    // This handles classes and also coercions from Array to Object
	    return ts.isSubtype(fromType, toType, context);
	}

	return false;
    }

    /**
     * Returns true if <code>value</code> can be implicitly cast to Primitive
     * type <code>t</code>.
     */
    public boolean numericConversionValid(Type t, Object value) {
	if (value instanceof Float || value instanceof Double)
	    return false;

	long v;

	if (value instanceof Number) {
	    v = ((Number) value).longValue();
	}
	else if (value instanceof Character) {
	    v = ((Character) value).charValue();
	}
	else {
	    return false;
	}

	if (t.isLong())
	    return true;
	if (t.isInt())
	    return Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE;
	if (t.isChar())
	    return Character.MIN_VALUE <= v && v <= Character.MAX_VALUE;
	if (t.isShort())
	    return Short.MIN_VALUE <= v && v <= Short.MAX_VALUE;
	if (t.isByte())
	    return Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE;

	return false;
    }

    // //
    // Functions for one-type checking and resolution.
    // //

    /**
     * Checks whether the member mi can be accessed from Context "context".
     */
    public boolean isAccessible(MemberInstance<?> mi) {
	ClassDef contextClass = context.currentClassDef();
	Type target = mi.container();
	Flags flags = mi.flags();

	if (!target.isClass()) {
	    // public members of non-classes are accessible;
	    // non-public members of non-classes are inaccessible
	    return flags.isPublic();
	}

	if (contextClass == null) {
	    return flags.isPublic();
	}

	ClassType contextClassType = contextClass.asType();

	ClassType targetClass = target.toClass();

	if (!classAccessible(targetClass.def())) {
	    return false;
	}

	if (ts.equals(targetClass.def(), contextClass))
	    return true;

	// If the current class and the target class are both in the
	// same class body, then protection doesn't matter, i.e.
	// protected and private members may be accessed. Do this by
	// working up through contextClass's containers.
	if (ts.isEnclosed(contextClass, targetClass.def()) || ts.isEnclosed(targetClass.def(), contextClass))
	    return true;

	ClassDef cd = contextClass;
	while (!cd.isTopLevel()) {
	    cd = cd.outer().get();
	    if (ts.isEnclosed(targetClass.def(), cd))
		return true;
	}

	// protected
	if (flags.isProtected()) {
	    // If the current class is in a
	    // class body that extends/implements the target class, then
	    // protected members can be accessed. Do this by
	    // working up through contextClass's containers.
	    if (ts.descendsFrom(ts.classDefOf(contextClassType), ts.classDefOf(targetClass))) {
		return true;
	    }

	    ClassType ct = contextClassType;
	    while (!ct.isTopLevel()) {
		ct = ct.outer();
		if (ts.descendsFrom(ts.classDefOf(ct), ts.classDefOf(targetClass))) {
		    return true;
		}
	    }
	}

	return accessibleFromPackage(flags, targetClass.package_(), contextClassType.package_());
    }

    /** True if the class targetClass accessible from the context. */
    public boolean classAccessible(ClassDef targetClass) {
	ClassDef contextClass = context.currentClassDef();

	if (contextClass == null) {
	    return classAccessibleFromPackage(targetClass, context.package_());
	}

	if (targetClass.isMember()) {
	    return isAccessible(targetClass.asType());
	}

	ClassType contextClassType = contextClass.asType();

	// Local and anonymous classes are accessible if they can be named.
	// This method wouldn't be called if they weren't named.
	if (!targetClass.isTopLevel()) {
	    return true;
	}

	// targetClass must be a top-level class

	// same class
	if (ts.equals(targetClass, contextClass))
	    return true;

	if (ts.isEnclosed(contextClass, targetClass))
	    return true;

	return classAccessibleFromPackage(targetClass, contextClassType.package_());
    }

    /** True if the class targetClass accessible from the package pkg. */
    public boolean classAccessibleFromPackage(ClassDef targetClass, Package pkg) {
	// Local and anonymous classes are not accessible from the outermost
	// scope of a compilation unit.
	if (!targetClass.isTopLevel() && !targetClass.isMember())
	    return false;

	Flags flags = targetClass.flags();

	if (targetClass.isMember()) {
	    if (!targetClass.container().get().isClass()) {
		// public members of non-classes are accessible
		return flags.isPublic();
	    }

	    if (!classAccessibleFromPackage(targetClass.container().get().toClass().def(), pkg)) {
		return false;
	    }
	}

	return accessibleFromPackage(flags, Types.get(targetClass.package_()), pkg);
    }

    /**
     * Return true if a member (in an accessible container) or a top-level class
     * with access flags <code>flags</code> in package <code>pkg1</code> is
     * accessible from package <code>pkg2</code>.
     */
    public boolean accessibleFromPackage(Flags flags, Package pkg1, Package pkg2) {
	// Check if public.
	if (flags.isPublic()) {
	    return true;
	}

	// Check if same package.
	if (flags.isPackage() || flags.isProtected()) {
	    if (pkg1 == null && pkg2 == null)
		return true;
	    if (pkg1 == null || pkg2 == null)
		return false;
	    if (pkg1.packageEquals(pkg2))
		return true;
	}

	// Otherwise private.
	return false;
    }

    public boolean isSubtype(Type t1, Type t2) {
	if (typeEquals(t1, t2))
	    return true;

	if (t1.isNull()) {
	    return t2.isNull() || t2.isReference();
	}

	if (t1.isReference() && t2.isNull()) {
	    return false;
	}

	Type child = t1;
	Type ancestor = t2;

	if (child instanceof ObjectType) {
	    ObjectType childRT = (ObjectType) child;

	    // Check subclass relation.
	    if (childRT.superClass() != null) {
		if (isSubtype(childRT.superClass(), ancestor)) {
		    return true;
		}
	    }

	    // Next check interfaces.
	    for (Type parentType : childRT.interfaces()) {
		if (isSubtype(parentType, ancestor)) {
		    return true;
		}
	    }
	}

	return false;
    }

    /**
     * Returns whether method 1 is <i>more specific</i> than method 2, where
     * <i>more specific</i> is defined as JLS 15.11.2.2
     */
    public <T extends ProcedureDef> boolean moreSpecific(ProcedureInstance<T> p1, ProcedureInstance<T> p2) {
	return p1.moreSpecific(null, p2, context);
    }

    /**
     * Requires: all type arguments are canonical. Returns the least common
     * ancestor of Type1 and Type2
     **/
    public Type leastCommonAncestor(Type type1, Type type2)  {
	if (typeEquals(type1, type2))
	    return type1;

	if (type1.isNumeric() && type2.isNumeric()) {
	    if (isImplicitCastValid(type1, type2)) {
		return type2;
	    }

	    if (isImplicitCastValid(type2, type1)) {
		return type1;
	    }

	    if (type1.isChar() && type2.isByte() || type1.isByte() && type2.isChar()) {
		return ts.Int();
	    }

	    if (type1.isChar() && type2.isShort() || type1.isShort() && type2.isChar()) {
		return ts.Int();
	    }
	}

	if (type1.isArray() && type2.isArray()) {
	    if (type1.toArray().base().isReference() && type2.toArray().base().isReference()) {
		return ts.arrayOf(leastCommonAncestor(type1.toArray().base(), type2.toArray().base()));
	    }
	    else {
		return ts.Any();
	    }
	}

	if (type1.isReference() && type2.isNull())
	    return type1;
	if (type2.isReference() && type1.isNull())
	    return type2;

	// Don't consider interfaces.
	if (type1.isClass() && type1.toClass().flags().isInterface()) {
	    return ts.Any();
	}

	if (type2.isClass() && type2.toClass().flags().isInterface()) {
	    return ts.Any();
	}

	if (isSubtype(type1, type2))
	    return type2;
	if (isSubtype(type2, type1))
	    return type1;

	if (type1 instanceof ObjectType && type2 instanceof ObjectType) {
	    // Walk up the hierarchy
	    Type t1 = leastCommonAncestor(((ObjectType) type1).superClass(), type2);
	    Type t2 = leastCommonAncestor(((ObjectType) type2).superClass(), type1);

	    if (typeEquals(t1, t2))
		return t1;

	    return ts.Any();
	}

	return ts.Any(); 
	//throw new SemanticException("No least common ancestor found for types \"" + type1 + "\" and \"" + type2 + "\".");
    }

    /** Return true if t overrides mi */
    public boolean hasMethod(Type t, MethodInstance mi) {
	return t instanceof ContainerType && ((ContainerType) t).hasMethod(mi, context);
    }

    /** Return true if t overrides mi */
    public boolean hasFormals(ProcedureInstance<? extends ProcedureDef> pi, List<Type> formalTypes) {
	return ((ProcedureInstance_c<?>) pi).hasFormals(formalTypes, context);
    }

    public List<MethodInstance> overrides(MethodInstance mi) {
	List<MethodInstance> l = new ArrayList<MethodInstance>();
	ContainerType rt = mi.container();

	while (rt != null) {
	    // add any method with the same name and formalTypes from rt
	    l.addAll(rt.methods(mi.name(), mi.formalTypes(), context));

	    ContainerType sup = null;

	    if (rt instanceof ObjectType) {
		ObjectType ot = (ObjectType) rt;
		if (ot.superClass() instanceof ContainerType) {
		    sup = (ContainerType) ot.superClass();
		}
	    }

	    rt = sup;
	}
	;

	return l;
    }

    public List<MethodInstance> implemented(MethodInstance mi) {
	return implemented(mi, mi.container());
    }

    public List<MethodInstance> implemented(MethodInstance mi, ContainerType st) {
	if (st == null) {
	    return Collections.<MethodInstance> emptyList();
	}

	List<MethodInstance> l = new LinkedList<MethodInstance>();
	l.addAll(st.methods(mi.name(), mi.formalTypes(), context));

	if (st instanceof ObjectType) {
	    ObjectType rt = (ObjectType) st;

	    Type superType = rt.superClass();

	    if (superType instanceof ContainerType) {
		l.addAll(implemented(mi, (ContainerType) superType));
	    }

	    List<Type> ints = rt.interfaces();
	    for (Type t : ints) {
		if (t instanceof ContainerType) {
		    ContainerType rt2 = (ContainerType) t;
		    l.addAll(implemented(mi, rt2));
		}
	    }
	}

	return l;
    }

    /**
     * Assert that <code>ct</code> implements all abstract methods required;
     * that is, if it is a concrete class, then it must implement all interfaces
     * and abstract methods that it or it's superclasses declare, and if it is
     * an abstract class then any methods that it overrides are overridden
     * correctly.
     */
    public void checkClassConformance(ClassType ct) throws SemanticException {
    	assert false;
    	if (ct.flags().isAbstract()) {
    		// don't need to check interfaces or abstract classes
    		return;
    	}

    	// build up a list of superclasses and interfaces that ct
    	// extends/implements that may contain abstract methods that
    	// ct must define.
    	List<Type> superInterfaces = ts.abstractSuperInterfaces(ct);

    	// check each abstract method of the classes and interfaces in
    	// superInterfaces
    	for (Iterator<Type> i = superInterfaces.iterator(); i.hasNext();) {
    		Type it = i.next();
    		// Everything from Any you get 'for free'
    		// [DC] perhaps it == ts.Any() is more appropriate here?
    		if (ts.typeEquals(it, ts.Any(), context)) continue;
    		if (it instanceof ContainerType) {
    			ContainerType rt = (ContainerType) it;
    			for (Iterator<MethodInstance> j = rt.methods().iterator(); j.hasNext();) {
    				MethodInstance mi = j.next();
    				if (!mi.flags().isAbstract()) {
    					// the method isn't abstract, so ct doesn't have to
    					// implement it.
    					continue;
    				}

    				MethodInstance mj = ts.findImplementingMethod(ct, mi, context);
    				if (mj == null) {
    					if (!ct.flags().isAbstract()) {
    						throw new SemanticException(ct.fullName() + " should be declared abstract; it does not define " + mi.signature()+ ", which is declared in " + rt.toClass().fullName(), ct.errorPosition());
    					}
    					else {
    						// no implementation, but that's ok, the class is
    						// abstract.
    					}
    				}
    				else if (!typeEquals(ct, mj.container()) && !typeEquals(ct, mi.container())) {
    					try {
    						// check that mj can override mi, which
    						// includes access protection checks.
    						checkOverride(mj, mi);
    					}
    					catch (SemanticException e) {
    						// change the position of the semantic
    						// exception to be the class that we
    						// are checking.
    						throw new SemanticException(e.getMessage(), ct.errorPosition());
    					}
    				}
    				else {
    					// the method implementation mj or mi was
    					// declared in ct. So other checks will take
    					// care of access issues
    				}
    			}
    		}
    	}
    }

    public boolean canOverride(MethodInstance mi, MethodInstance mj) {
	try {
	    checkOverride(mi, mj);
	    return true;
	}
	catch (SemanticException e) {
	    return false;
	}
    }

    public void checkOverride(MethodInstance mi, MethodInstance mj) throws SemanticException {
	// HACK: Java5 allows return types to be covariant. We'll allow
	// covariant
	// return if we mj is defined in a class file.
	boolean allowCovariantReturn = false;

	if (mj.container() instanceof ClassType) {
	    ClassType ct = (ClassType) mj.container();
	    if (ct.def().fromJavaClassFile()) {
		allowCovariantReturn = true;
	    }
	}

	checkOverride(mi, mj, allowCovariantReturn);
    }

    public void checkOverride(MethodInstance mi, MethodInstance mj, boolean allowCovariantReturn) throws SemanticException {
	if (mi == mj)
	    return;

	if (!(mi.name().equals(mj.name()) && mi.hasFormals(mj.formalTypes(), context))) {
	    throw new SemanticException(mi.signature() 
	                                + " in " + mi.container() + " cannot override " + mj.signature() 
	                                + " in " + mj.container()+ "; incompatible " + "parameter types", mi.errorPosition());
	}

	boolean shouldReport = reporter.should_report(Reporter.types, 3);
	if (allowCovariantReturn ? !isSubtype(mi.returnType(), mj.returnType()) : !typeEquals(mi.returnType(), mj.returnType())) {
	    if (shouldReport)
	        reporter.report(3, "return type " + mi.returnType() + " != " + mj.returnType());
	    throw new SemanticException(mi.signature() 
	                                + " in " + mi.container() + " cannot override " + mj.signature() 
	                                + " in " + mj.container()+ "; attempting to use incompatible return type." 
	                                + "\n\tFound: " + mi.returnType() 
	                                + "\n\tExpected: " + mj.returnType(),mi.errorPosition());
	}

/*	if (!ts.throwsSubset(mi, mj)) {
	    if (Report.should_report(Report.types, 3))
		Report.report(3, mi.throwTypes() + " not subset of " + mj.throwTypes());
	    throw new SemanticException(mi.signature() + " in " + mi.container() + " cannot override " + mj.signature() + " in " + mj.container()
		    + "; the throw set " + mi.throwTypes() + " is not a subset of the " + "overridden method's throw set " + mj.throwTypes() + ".",
					mi.position());
	}
*/
	if (mi.flags().moreRestrictiveThan(mj.flags())) {
	    if (shouldReport)
	        reporter.report(3, mi.flags() + " more restrictive than " + mj.flags());
	    throw new SemanticException(mi.signature() 
	                                + " in " + mi.container() + " cannot override " + mj.signature() 
	                                + " in " + mj.container()+ "; attempting to assign weaker " 
	                                + "access privileges", mi.errorPosition());
	}

	if (mi.flags().isStatic() != mj.flags().isStatic()) {
	    if (shouldReport)
	        reporter.report(3, mi.signature() + " is " + (mi.flags().isStatic() ? "" : "not") + " static but " + mj.signature() + " is "
	                      + (mj.flags().isStatic() ? "" : "not") + " static");
	    throw new SemanticException(mi.signature() + " in " + mi.container() + " cannot override " + mj.signature() + " in " + mj.container()+ "; overridden method is " + (mj.flags().isStatic() ? "" : "not") + "static", mi.errorPosition());
	}

	if (!mi.def().equals(mj.def()) && mj.flags().isFinal()) {
	    // mi can "override" a final method mj if mi and mj are the same
	    // method instance.
	    if (shouldReport)
	        reporter.report(3, mj.flags() + " final");
	    throw new SemanticException(mi.signature() + " in " + mi.container() + " cannot override " + mj.signature() + " in " + mj.container()+ "; overridden method is final", mi.errorPosition());
	}
    }

    /**
     * Returns true iff <m1> is the same method as <m2>
     */
    public boolean isSameMethod(MethodInstance m1, MethodInstance m2) {
	return m1.name().equals(m2.name()) && m1.hasFormals(m2.formalTypes(), context);
    }

    public boolean callValid(ProcedureInstance<? extends ProcedureDef> prototype, Type thisType, List<Type> argTypes) {
	return ((ProcedureInstance_c<?>) prototype).callValid(thisType, argTypes, context);
    }

    public abstract Type findMemberType(Type container, Name name) throws SemanticException;
}
