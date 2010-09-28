/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
