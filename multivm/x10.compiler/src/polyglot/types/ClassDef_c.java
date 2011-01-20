/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.frontend.Source;
import x10.types.X10ClassType;

/**
 * ParsedClassType
 *
 * Overview: 
 * A ParsedClassType represents a information that has been parsed (but not
 * necessarily type checked) from a .java file.
 **/
public abstract class ClassDef_c extends Def_c implements ClassDef
{
	private static final long serialVersionUID = 2296095550182599867L;

	public abstract X10ClassType asType();
    
    protected ClassDef_c() {
	super();
    }

    public ClassDef_c(TypeSystem ts, Source fromSource) {
        super(ts);
    }
     
    /** Get the class's flags. */
    public abstract Flags flags();
        
    public abstract String toString();
}
