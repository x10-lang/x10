/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import polyglot.types.Flags;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;

/** 
 * An interface representing a variable.  A Variable is any expression
 * that can appear on the left-hand-side of an assignment.
 */
public interface NamedVariable extends Variable
{
    /** Return the access flags of the variable, or Flags.NONE */
    public Flags flags();

    /** Return the type object for the variable. */
    public VarInstance<? extends VarDef> varInstance();

    /** Return the name of the variable. */
    public Id name();
}
