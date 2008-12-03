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

import polyglot.ast.Cast;
import polyglot.ast.Instanceof;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.NullableNode;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10TypeNode;
import polyglot.frontend.Job;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 * Instanceof and cast need the depexpr containedin the the type (if any) as a DepExpr, so that
 * code can be generated from it for runtime checks.
 * So the depexpr must be retrieved and stored as an Expr before TypeElaboration has run.
 * This separate pass does that. (This can also be handled during parsing.)
 * @author vj 12/2007
 *
 */
public class CastRewriter extends ContextVisitor {

	/**
	 * @param job
	 * @param ts
	 * @param nf
	 */
	public CastRewriter(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
		Node result = n;
		
		if (n instanceof Cast) {
			Cast nn = (Cast) n;
			X10TypeNode xn = (X10TypeNode) nn.castType();
			DepParameterExpr e = null;
			if (xn instanceof NullableNode) {
				NullableNode xnNode = (NullableNode) xn;
				X10TypeNode real = (X10TypeNode)xnNode.base();
				e =  real.dep();
				if (e != null)
					e = (DepParameterExpr) e.copy();
				// Do not remove the dep clause from the the type node since it is needed
				 // for static type checking.
				//xn = xnNode.base(real.dep(null));
			} else {
				e =  xn.dep();
				if (e != null)
					e = (DepParameterExpr) e.copy();
				//	Do not remove the dep clause from the the type node since it is needed
				 // for static type checking.
				//xn = xn.dep(null);
			}
			//Report.report(1, "CastRewriter: Cast..xn=" + xn + " e=" + e);
			if (e != null) {
				result =((X10NodeFactory)nf).DepCast(n.position(), xn, e, nn.expr());
				//Report.report(1, "CastRewriter: Cast..xn=" + xn + " e=" + e + " returning " + result);
				return result;
			}
		}
		if (n instanceof Instanceof) {
			Instanceof nn = (Instanceof) n;
			X10TypeNode xn = (X10TypeNode) nn.compareType();
			DepParameterExpr e = null;
			if (xn instanceof NullableNode) {
				NullableNode xnNode = (NullableNode) xn;
				X10TypeNode real = (X10TypeNode)xnNode.base();
				e =  real.dep();
				if (e != null)
					e = (DepParameterExpr) e.copy();
				
				//xn = xnNode.base(real.dep(null));
			} else {
				e =  xn.dep();
				if (e != null)
					e = (DepParameterExpr) e.copy();
				//xn = xn.dep(null);
			}
			
			if (e!=null) {
				result = ((X10NodeFactory)nf).DepInstanceof(n.position(), xn, e, nn.expr());
				
			}
		}
		return result;
	}   

}
