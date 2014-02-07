/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.QualifierNode;
import polyglot.ast.TypeCheckTypeGoal;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.LazyRef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType;
import polyglot.types.Ref;
import polyglot.types.LazyRef_c;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
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

	public Node disambiguate(ContextVisitor ar) {
		SemanticException ex;
		NodeFactory nf = ar.nodeFactory();

		// Dereference--this will cause type inference to be performed.

        final LazyRef_c<? extends Type> ref = (LazyRef_c) typeRef();
        if (ref.isThrowResolver())
            return this;
        
        Type t = ref.get();
				
		
		if (t instanceof UnknownType) {
		    return this;
		}
		
		return nf.CanonicalTypeNode(position(), ref);
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
		return "(#" + hashCode() + ")_"; // todo: using hashCode leads to non-determinism in the output of the compiler
	}
}
