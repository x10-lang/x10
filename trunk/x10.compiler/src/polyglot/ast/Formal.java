/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
