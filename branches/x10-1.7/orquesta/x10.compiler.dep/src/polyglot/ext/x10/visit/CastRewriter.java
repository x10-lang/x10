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
import polyglot.ext.x10.ast.NullableNode;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.frontend.Job;
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
			TypeNode xn = (TypeNode) nn.castType();
			DepParameterExpr e = null;
			
			NullableNode nullable = null;
			if (xn instanceof NullableNode) {
			    nullable = (NullableNode) xn;
			    xn = nullable.base();
			}
			
			if (xn instanceof AmbDepTypeNode) {
			    AmbDepTypeNode adtn = (AmbDepTypeNode) xn;
			    TypeNode base = adtn.base();
			    DepParameterExpr dep = adtn.dep();
			    
			    if (nullable != null)
			        base = nullable.base(base);
			    
			    return ((X10NodeFactory) nf).DepCast(n.position(), base, dep, nn.expr());
			}
		}
		if (n instanceof Instanceof) {
			Instanceof nn = (Instanceof) n;
                        TypeNode xn = (TypeNode) nn.compareType();
                        DepParameterExpr e = null;
                        
                        NullableNode nullable = null;
                        if (xn instanceof NullableNode) {
                            nullable = (NullableNode) xn;
                            xn = nullable.base();
                        }
                        
                        if (xn instanceof AmbDepTypeNode) {
                            AmbDepTypeNode adtn = (AmbDepTypeNode) xn;
                            TypeNode base = adtn.base();
                            DepParameterExpr dep = adtn.dep();
                            
                            if (nullable != null)
                                base = nullable.base(base);
                            
                            return ((X10NodeFactory) nf).DepInstanceof(n.position(), base, dep, nn.expr());
                        }
		}
		return result;
	}   

}
