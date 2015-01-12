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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;

/**
 * An unknown package.  This is used as a place-holder until types are
 * disambiguated.
 */
public class UnknownPackage_c extends Package_c implements UnknownPackage
{
    private static final long serialVersionUID = 7081521055209225641L;

    /** Used for deserializing types. */
    protected UnknownPackage_c() { }
    
    /** Creates a new type in the given a TypeSystem. */
    public UnknownPackage_c(TypeSystem ts) {
        super(ts);
    }

    public String translate(Resolver c) {
	throw new InternalCompilerError("Cannot translate an unknown package.");
    }

    public String toString() {
	return "<unknown>";
    }
    public void print(CodeWriter w) {
	w.write(toString());
    }
}
