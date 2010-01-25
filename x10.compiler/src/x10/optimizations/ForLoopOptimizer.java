/**
 * 
 */
package x10.optimizations;


import java.util.List;

import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Await;
import x10.ast.Finish;
import x10.ast.ForEach;
import x10.ast.ForLoop;
import x10.ast.ForLoop_c;
import x10.ast.Future;
import x10.ast.Here;
import x10.ast.Next;
import x10.ast.RegionMaker;
import x10.ast.SettableAssign_c;
import x10.ast.When;
import x10.ast.X10Binary_c;
import x10.ast.X10Formal;
import x10.ast.X10NodeFactory;
import x10.ast.X10Unary_c;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.X10Context;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.util.Synthesizer;

/**
 * @author vj
 *
 */
public class ForLoopOptimizer extends ContextVisitor {
	
	private final X10TypeSystem xts;
	private final X10NodeFactory xnf;

	public ForLoopOptimizer(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		xts = (X10TypeSystem) ts;
		xnf = (X10NodeFactory) nf;
	}

	 public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
	        if (n instanceof ForLoop)
	            return visitForLoop((ForLoop_c) n);
	        return n;
	    }
	 
	 public Node visitForLoop(ForLoop_c loop) throws SemanticException {
		    Synthesizer syn = new Synthesizer(xnf, xts);
			Type domainType = loop.domainType();
			XVar var = XTerms.makeEQV("self");
			Type regionType =  syn.addRankConstraint(xts.Region(), var, 1, xts);
			regionType = syn.addRectConstraint(regionType, var);
			X10TypeMixin.setSelfVar(regionType, var);
			
			
			if (xts.isSubtypeWithValueInterfaces(domainType, regionType, context())) {
				// Now check if domain is actually a RegionMaker, i.e. a parsing of e1..e2
				if (loop.domain() instanceof RegionMaker) {
					List<Expr> args = ((RegionMaker) loop.domain()).arguments();
					if (args.size() == 2) {
						Expr low = args.get(0);
						Expr high = args.get(1);
						X10Formal xf = (X10Formal) loop.formal();
						// Only handle the case |for ((i) in e1..e2) S| for now
						if (xf.isUnnamed()) {
							X10Formal index = (X10Formal) xf.vars().get(0);
							Node n = syn.makeForLoop(loop.position(),  index, low, high, 
									loop.body(),
									 (X10Context) context());
							return n;
						}
					
					}
					
				}
			}
			return loop;
		}
}
