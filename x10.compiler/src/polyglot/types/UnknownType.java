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
