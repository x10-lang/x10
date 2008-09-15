/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.QualifierNode;
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
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeCheckPreparer;

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
	    if (typeRef() instanceof LazyRef) {
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
		return "_";
	}
}
