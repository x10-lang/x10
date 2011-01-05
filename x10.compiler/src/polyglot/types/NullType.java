/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.util.InternalCompilerError;

/**
 * A <code>NullType</code> represents the type of the Java <code>null</code>
 * literal.
 */
public class NullType extends Type_c  
{
	private static final long serialVersionUID = 7144927187351231117L;

    /** Used for deserializing types. */
    protected NullType() { }

    public NullType(TypeSystem ts) {
	super(ts);
    }
    
    public String translate(Resolver c) {
	throw new InternalCompilerError("Cannot translate a null type.");
    }

    public String typeToString() {
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
    public boolean equalsNoFlag(Type t2) { return this == t2; }
    public boolean permitsNull() { return true;}
}
