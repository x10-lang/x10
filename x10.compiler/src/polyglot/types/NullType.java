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
