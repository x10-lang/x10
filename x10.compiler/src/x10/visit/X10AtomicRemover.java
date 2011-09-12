package x10.visit;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Atomic_c;

public class X10AtomicRemover extends ErrorHandlingVisitor {

	public X10AtomicRemover(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}
	
	@Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
		if(n instanceof Atomic_c) {
			//remove it
			Atomic_c atomic = (Atomic_c)n;
			if(atomic.identifiers != null) {
				System.out.println("  remove atomic: " + atomic.position());
			    return atomic.body();
			}
		}
		return n;
	}

}
