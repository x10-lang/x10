/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.visit;

import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.FutureNode_c;
import polyglot.ext.x10.ast.NullableNode_c;
import polyglot.ext.x10.ast.X10FieldDecl_c;
import polyglot.ext.x10.ast.X10LocalDecl_c;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10LocalInstance;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * Runs after TypeElaborator, updating the base types for type checking so that they
 * accurately reflect any depclauses attached to the type. The AST and type nodes
 * whose type needs to be adjusted are TypeNodes which contain TypeNodes
 * (including FutureNode, NullableNode), and FieldInstance, MethodInstance, LocalInstances.
 * Now the TypeChecking phase may be run.
 * @author vj
 *
 */
public class TypePropagator extends TypeChecker {

	/**
	 * @param job
	 * @param ts
	 * @param nf
	 */
	public TypePropagator(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	@Override
	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
		if (! (v instanceof TypePropagator)) return n;
		TypePropagator tp = (TypePropagator) v;
		if (n instanceof X10LocalDecl_c ) {
			X10LocalDecl_c m = (X10LocalDecl_c) n;
			m.pickUpTypeFromTypeNode(tp);
			((X10LocalInstance) m.localInstance()).setSelfClauseIfFinal();
			return n;
		}
		if (n instanceof X10FieldDecl_c) {
			// Ensure that the FieldInstance type is updated to reflect
			// any deptype.
			X10FieldDecl_c m = (X10FieldDecl_c) n;
			((X10FieldInstance) m.fieldInstance()).setDepType(m.declType());
		}
		if (n instanceof NullableNode_c) {
			NullableNode_c m = (NullableNode_c) n;
			n =	m.propagateTypeFromBase();
		}
		if (n instanceof FutureNode_c) {
			FutureNode_c m = (FutureNode_c) n;
			n =	m.propagateTypeFromBase();
		}
		return n;
	}   
}
