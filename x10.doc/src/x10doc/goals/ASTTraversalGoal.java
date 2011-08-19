package x10doc.goals;

import polyglot.ast.Node;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.Job;
import polyglot.frontend.SourceGoal_c;
import x10.ast.X10SourceFile_c;
import x10.visit.X10DelegatingVisitor;

public class ASTTraversalGoal extends SourceGoal_c {

	private final X10DelegatingVisitor v;

	public ASTTraversalGoal(String name, Job job, X10DelegatingVisitor v) {
		super(name, job);
		this.v = v;
	}

	public ASTTraversalGoal(Job job, X10DelegatingVisitor v) {
		super(job);
		this.v = v;
	}

	@Override
	public boolean runTask() {
		Node n = job().ast();
		// System.out.println("n.getClass() = " + n.getClass());
		v.visitAppropriate(n);
		return true;
	}

}
