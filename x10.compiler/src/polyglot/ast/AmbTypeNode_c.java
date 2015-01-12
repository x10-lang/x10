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
