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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import polyglot.frontend.Globals;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;

/**
 * A <code>AmbAssign</code> represents a Java assignment expression to
 * an as yet unknown expression.
 */
public abstract class AmbAssign_c extends Assign_c implements AmbAssign
{   
	public AmbAssign_c(NodeFactory nf, Position pos, Expr left, Operator op, Expr right) {
		super(nf, pos, op, right);
	}
	
	@Override
	public abstract Assign visitLeft(NodeVisitor v);
	
	//public abstract Node disambiguate(ContextVisitor ar) throws SemanticException;
}
