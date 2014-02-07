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

package polyglot.ast;

import polyglot.types.Flags;
import polyglot.types.LocalDef;

/**
 * A <code>Formal</code> represents a formal parameter to a method
 * or constructor or to a catch block.  It consists of a type and a variable
 * identifier.
 */
public interface Formal extends VarDecl
{
    /** Get the flags of the formal. */
    public FlagsNode flags();

    /** Set the flags of the formal. */
    public Formal flags(FlagsNode flags);
    
    /** Get the type node of the formal. */
    public TypeNode type();

    /** Set the type node of the formal. */
    public Formal type(TypeNode type);
    
    /** Get the name of the formal. */
    public Id name();
    
    /** Set the name of the formal. */
    public Formal name(Id name);

    /** Get the local instance of the formal. */
    public LocalDef localDef();

    /** Set the local instance of the formal. */
    public Formal localDef(LocalDef li);
}
