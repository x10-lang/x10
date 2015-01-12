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
