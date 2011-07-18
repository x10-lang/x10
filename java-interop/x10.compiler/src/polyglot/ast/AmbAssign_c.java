/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
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
