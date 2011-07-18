/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.Collections;
import java.util.List;

import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.types.constants.ConstantValue;

/**
 * A <code>Binary</code> represents a Java binary expression, an
 * immutable pair of expressions combined with an operator.
 */
public abstract class Binary_c extends Expr_c implements Binary
{

    public Binary_c(Position pos, Expr left, Operator op, Expr right) {
    	super(pos);
    }
    
    public abstract boolean isConstant();
    
    public abstract ConstantValue constantValue();
    
    /** Type check the expression. */
    public abstract Node typeCheck(ContextVisitor tc);
}
