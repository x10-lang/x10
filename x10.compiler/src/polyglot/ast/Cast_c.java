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

/**
 * A <code>Cast</code> is an immutable representation of a casting
 * operation.  It consists of an <code>Expr</code> being cast and a
 * <code>TypeNode</code> being cast to.
 */ 
public abstract class Cast_c extends Expr_c implements Cast
{
    

    public Cast_c(Position pos, TypeNode castType, Expr expr) {
    	super(pos);
    }

    /** Get the precedence of the expression. */
    public abstract Precedence precedence();

    /** Type check the expression. */
    public abstract Node typeCheck(ContextVisitor tc);
  
    public abstract String toString();

    public abstract List<Type> throwTypes(TypeSystem ts);
}
