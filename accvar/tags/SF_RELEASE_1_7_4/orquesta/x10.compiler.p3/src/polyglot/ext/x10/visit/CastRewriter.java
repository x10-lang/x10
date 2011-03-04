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
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.AmbDepTypeNode;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.frontend.Job;
import polyglot.frontend.SetResolverGoal;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
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
	
	// DISABLE the pass
	@Override
	public Node override(Node parent, Node n) {
	    return n;
	}

	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
	    X10NodeFactory nf = (X10NodeFactory) this.nf;
		
	    Node result = n;
		
//		if (n instanceof X10Cast) {
//			X10Cast nn = (X10Cast) n;
//			TypeNode xn = (TypeNode) nn.castType();
//			DepParameterExpr e = null;
//			
//			if (xn instanceof AmbDepTypeNode) {
//			    AmbDepTypeNode adtn = (AmbDepTypeNode) xn;
//			    TypeNode base = nf.X10AmbTypeNode(xn.position(), adtn.prefix(), adtn.name());
//			    base = base.typeRef(Types.lazyRef(ts.unknownType(base.position()), new SetResolverGoal(job)));
//			    DepParameterExpr dep = adtn.constraint();
//			    return nf.DepCast(n.position(), base, dep, nn.expr(), nn.convert());
//			}
//		}
//		if (n instanceof Instanceof) {
//			Instanceof nn = (Instanceof) n;
//                        TypeNode xn = (TypeNode) nn.compareType();
//                        DepParameterExpr e = null;
//                        
//                        if (xn instanceof AmbDepTypeNode) {
//                            AmbDepTypeNode adtn = (AmbDepTypeNode) xn;
//                            TypeNode base = nf.X10AmbTypeNode(xn.position(), adtn.prefix(), adtn.name());
//                            base = base.typeRef(Types.lazyRef(ts.unknownType(base.position()), new SetResolverGoal(job)));
//                            DepParameterExpr dep = adtn.constraint();
//                            return nf.DepInstanceof(n.position(), base, dep, nn.expr());
//                        }
//		}
		return result;
	}   

}
