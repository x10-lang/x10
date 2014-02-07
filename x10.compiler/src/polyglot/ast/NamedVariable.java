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
