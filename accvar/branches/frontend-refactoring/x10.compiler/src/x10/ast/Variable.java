/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package x10.ast;

import x10.types.Flags;

/** 
 * An interface representing a variable.  A Variable is any expression
 * that can appear on the left-hand-side of an assignment.
 */
public interface Variable extends Expr
{
    /** Return the access flags of the variable, or Flags.NONE */
    public Flags flags();
}
