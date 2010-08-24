/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.util.Position;

/**
 * A <code>ReferenceType</code> represents a reference type --
 * a type on which contains methods and fields and is a subtype of
 * Object.
 */
public abstract class ReferenceType_c extends Type_c implements ReferenceType
{
    protected ReferenceType_c() {
	super();
    }

    public ReferenceType_c(TypeSystem ts) {
	this(ts, null);
    }

    public ReferenceType_c(TypeSystem ts, Position pos) {
	super(ts, pos);
    }

    public boolean isReference() { return true; }
    public ReferenceType toReference() { return this; }

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

        for (Iterator<MethodInstance> i = methods().iterator(); i.hasNext(); ) {
            MethodInstance mi = (MethodInstance) i.next();
            if (mi.name().equals(name)) {
                l.add(mi);
            }
        }

        return l;
    }

    public List<MethodInstance> methods(Name name, List<Type> argTypes, Context context) {
        List<MethodInstance> l = new ArrayList<MethodInstance>();

        for (Iterator<MethodInstance> i = methodsNamed(name).iterator(); i.hasNext(); ) {
            MethodInstance mi = (MethodInstance) i.next();
            if (mi.hasFormals(argTypes, context)) {
                l.add(mi);
            }
        }

        return l;
    }
}
