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

import polyglot.util.Position;
import x10.types.constants.BooleanValue;
import x10.types.constants.ConstantValue;

/**
 * An immutable representation of a Java language <code>while</code>
 * statement.  It contains a statement to be executed and an expression
 * to be tested indicating whether to reexecute the statement.
 */ 
public abstract class Loop_c extends Stmt_c implements Loop
{
    public Loop_c(Position pos) {
	super(pos);
    }

    public boolean condIsConstant() {
        return cond().isConstant();
    }

    public boolean condIsConstantTrue() {
        ConstantValue cv = cond().constantValue();
        if (cv instanceof BooleanValue) {
            return ((BooleanValue) cv).value();
        } else {
            return false;
        }
    }
}
