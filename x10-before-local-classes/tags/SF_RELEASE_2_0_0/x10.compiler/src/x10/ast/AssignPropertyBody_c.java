/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
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
