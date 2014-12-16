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

import polyglot.util.Position;
import x10.types.MethodInstance;

/**
 * A <code>ReferenceType</code> represents a reference type --
 * a type on which contains methods and fields and is a subtype of
 * Object.
 */
public abstract class ReferenceType_c extends Type_c implements ObjectType
{
    private static final long serialVersionUID = 145029430265768256L;

    protected ReferenceType_c() {
	super();
    }

    public ReferenceType_c(TypeSystem ts) {
	this(ts, null, null);
    }

    public ReferenceType_c(TypeSystem ts, Position pos, Position errorPos) {
	super(ts, pos, errorPos);
    }

    public boolean isReference() { return true; }
    public ObjectType toReference() { return this; }

    /** Get a list of all the type's MemberInstances. */
    public List<MemberInstance<?>> members() {
        List<MemberInstance<?>> l = new ArrayList<MemberInstance<?>>();
        l.addAll(methods());
        l.addAll(fields());
        return l;
    }

    /**
     * Returns a list of MethodInstances for all the methods declared in this.
     * It does not return methods declared in supertypes.
     */
    public abstract List<MethodInstance> methods();

    /**
     * Returns a list of FieldInstances for all the fields declared in this.
     * It does not return fields declared in supertypes.
     */
    public abstract List<FieldInstance> fields();

    /** Get a field of the class by name. */
    public FieldInstance fieldNamed(Name name) {
        for (FieldInstance fi : fields()) {
	    if (fi.name().equals(name)) {
	        return fi;
	    }
	}

	return null;
    }

    /** 
     * Returns the supertype of this class.  For every class except Object,
     * this is non-null.
     */
    public abstract Type superClass();

    /**
     * Returns a list of the types of this class's interfaces.
     */
    public abstract List<Type> interfaces();

    /** Return true if t has a method mi */
    public boolean hasMethod(MethodInstance mi, Context context) {
        for (MethodInstance mj : methods()) {

            if (ts.isSameMethod(mi, mj, context)) {
                return true;
            }
        }

        return false;
    }

    public List<MethodInstance> methodsNamed(Name name) {
        List<MethodInstance> l = new ArrayList<MethodInstance>();

        for (MethodInstance mi : methods()) {
            if (mi.name().equals(name)) {
                l.add(mi);
            }
        }

        return l;
    }

    public List<MethodInstance> methods(Name name, List<Type> argTypes, Context context) {
        List<MethodInstance> l = new ArrayList<MethodInstance>();

        for (MethodInstance mi : methodsNamed(name)) {
            if (mi.hasFormals(argTypes, context)) {
                l.add(mi);
            }
        }
        return l;
    }
}
