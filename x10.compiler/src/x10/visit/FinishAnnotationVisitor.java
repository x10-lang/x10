package x10.visit;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.ClosureCall;
import x10.ast.Finish;
import x10.ast.X10Call;
import x10.ast.X10NodeFactory;
import x10.types.X10ParsedClassType_c;
import x10.types.X10TypeSystem;

public class FinishAnnotationVisitor extends ContextVisitor {
	final String theLanguage;
	final X10TypeSystem xts;
	final X10NodeFactory xnf;

	public FinishAnnotationVisitor(Job job, TypeSystem ts, NodeFactory nf,
			String theLanguage) {
		super(job, job.extensionInfo().typeSystem(), job.extensionInfo()
				.nodeFactory());
		xts = (X10TypeSystem) ts;
		xnf = (X10NodeFactory) nf;
		this.theLanguage = theLanguage;
	}

	public Node leaveCall(Node old, Node n, NodeVisitor v)
			throws SemanticException {

		if (n instanceof AnnotationNode) {
			return visitAnnotation((AnnotationNode) n);
		}
		return n;
	}

	private Node visitAnnotation(AnnotationNode n) {
		System.out.println("Annotation:" + n.toString());
		System.out.println(n.annotationType());
		/*X10ParsedClassType_c xpct = (X10ParsedClassType_c)n.annotationType().typeRef().getCached();
		List<Expr> inits = xpct.propertyInitializers();
		for(int i=0;i<inits.size();i++){
			Expr e = inits.get(i);
			System.out.println(e.getClass());
		}*/
		return n;
	}
}