/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;

/**
 * An <code>AmbTypeNode</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a type.
 */
public abstract class AmbTypeNode_c extends TypeNode_c implements AmbTypeNode {

//  protected Expr dummy;
  
	public AmbTypeNode_c(Position pos, Prefix qual, Id name) {
		super(pos);
	}
	
	public abstract Node disambiguate(ContextVisitor ar);
	
	public abstract void prettyPrint(CodeWriter w, PrettyPrinter tr);
	
	public abstract String toString();
}
