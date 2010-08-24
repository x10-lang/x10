/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.util.InternalCompilerError;

/**
 * A <code>NullType</code> represents the type of the Java keyword
 * <code>null</code>.
 */
public class NullType_c extends Type_c implements NullType
{
    /** Used for deserializing types. */
    protected NullType_c() { }

    public NullType_c(TypeSystem ts) {
	super(ts);
    }
    
    public String translate(Resolver c) {
	throw new InternalCompilerError("Cannot translate a null type.");
    }

    public String toString() {
	return "type(null)";
    }
    
    public boolean equalsImpl(TypeObject t) {
        return t instanceof NullType;
    }

    public int hashCode() {
	return 6060842;
    }

    public boolean isNull() { return true; }
    public NullType toNull() { return this; }
}
