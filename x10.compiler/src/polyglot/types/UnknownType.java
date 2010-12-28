/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.util.InternalCompilerError;
import x10.types.X10TypeSystem_c;

/**
 * An unknown type.  This is used as a place-holder until types are
 * disambiguated.
 */
public class UnknownType extends Type_c {
	   private static final long serialVersionUID = 2713953048091574093L;

	    /** Used for deserializing types. */
	    protected UnknownType() { }
	    
	    /** Creates a new type in the given a TypeSystem. */
	    public UnknownType(TypeSystem ts) {
	        super(ts);
	    }

	    public String translate(Resolver c) {
		throw new InternalCompilerError("Cannot translate an unknown type.");
	    }

	    public String toString() {
		return "<unknown>";
	    }
	    public ClassType toClass() {
		    return ((X10TypeSystem_c)ts).createFakeClass(QName.make("<unknown>"), new SemanticException("Unknown class"));
		}
}
