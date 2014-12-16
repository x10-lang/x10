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
