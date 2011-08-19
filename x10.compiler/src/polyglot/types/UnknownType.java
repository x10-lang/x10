/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.util.InternalCompilerError;
import x10.types.X10ClassType;


/**
 * An unknown type.  This is used as a place-holder until types are
 * disambiguated, or as a fake type when recovering from errors.
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

	    /**
	     * In X10, the UnknownType is presumed to be a class type. This is used primarily
	     * in error recovery.
	     */
	    public boolean isClass() {
	    	return true;
	    }
	    public String typeToString() {
		return "<unknown>";
	    }
	    public X10ClassType toClass() {
		    return ts.createFakeClass(QName.make("<unknown>"), new SemanticException("Unknown class"));
		}
}
