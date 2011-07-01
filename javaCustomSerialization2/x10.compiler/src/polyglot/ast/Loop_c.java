/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
