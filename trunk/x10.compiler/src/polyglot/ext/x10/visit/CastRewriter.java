/**
 * 
 */
package polyglot.ext.x10.visit;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.Instanceof;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.NullableNode;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.ext.x10.ast.X10LocalDecl_c;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10TypeNode;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * @author vj
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
			
			if (e != null) {
				result =((X10NodeFactory)nf).DepCast(n.position(), xn, e, nn.expr());
				return result;
			}
		}
		if (n instanceof Instanceof) {
			Instanceof nn = (Instanceof) n;
			X10TypeNode xn = (X10TypeNode) nn.compareType();
			DepParameterExpr e =  (xn instanceof NullableNode) ?
					((X10TypeNode)((NullableNode) xn).base()).dep() : xn.dep();
					if (e!=null) {
						result = ((X10NodeFactory)nf).DepInstanceof(n.position(), xn, e, nn.expr());
					}
		}
		return result;
	}   

}
