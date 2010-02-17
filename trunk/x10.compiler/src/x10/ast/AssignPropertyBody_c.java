/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.X10ConstructorDef;

public class AssignPropertyBody_c extends StmtSeq_c implements AssignPropertyBody {
	final X10ConstructorDef ci;
	final List<FieldInstance> fi;
	public AssignPropertyBody_c(Position pos, List<Stmt> statements, 
			X10ConstructorDef ci, List<FieldInstance> fi) {
		super(pos, statements);
		this.ci = ci;
		this.fi = fi;
		
	}
	
	public X10ConstructorDef constructorInstance() { return ci; }
	public List<FieldInstance> fieldInstances() { return fi; }

	@Override
	public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
		return this;
	}
}
