/**
 * 
 */
package polyglot.ext.x10.visit;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.InnerClassRemover;

public class X10InnerClassRemover extends InnerClassRemover {
	public X10InnerClassRemover(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}
	
	@Override
	protected ContextVisitor localClassRemover() {
		return new X10LocalClassRemover(job, ts, nf);
	}
}