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

import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.types.LazyRef;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.TypeSystem;
import x10.types.UnknownType;
import x10.visit.X10TypeChecker;

public class UnknownTypeNode_c extends TypeNode_c implements UnknownTypeNode {
	public UnknownTypeNode_c(Position pos) {
		super(pos);
	}

	@Override
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("_");
	}


	public Node visitChildren(NodeVisitor v) {
		return this;
	}

	public Node disambiguate(ContextVisitor ar) throws SemanticException {
		SemanticException ex;
		NodeFactory nf = ar.nodeFactory();

		// Dereference--this will cause type inference to be performed.
		
		Type t = typeRef().get();
				
		
		if (t instanceof UnknownType) {
		    return this;
		}
		
		return nf.CanonicalTypeNode(position(), typeRef());
	}

	
	public void setResolver(Node parent, final TypeCheckPreparer v) {
	    if (typeRef() instanceof LazyRef<?>) {
		final LazyRef<Type> r = (LazyRef<Type>) typeRef();
		final TypeSystem ts = v.typeSystem();
		if (r.resolver() == null) {
		    r.setResolver(new Runnable() {
			public void run() {
			    r.update(ts.unknownType(position()));
			}
		    });
		}
	    }
	}
		
	public String toString() {
		return "(#" + hashCode() + ")_";
	}
}
