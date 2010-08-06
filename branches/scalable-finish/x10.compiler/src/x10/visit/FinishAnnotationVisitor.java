package x10.visit;

import java.util.ArrayList;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Ext;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.NoClassException;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.Async;
import x10.ast.Async_c;
import x10.ast.AtStmt;
import x10.ast.ClosureCall;
import x10.ast.Finish;
import x10.ast.X10Call;
import x10.ast.X10NodeFactory;
import x10.emitter.Emitter;
import x10.extension.X10Ext;
import x10.finish.table.CallTableUtil;
import x10.types.X10ParsedClassType_c;
import x10.types.X10TypeSystem;


//TODO: figure out how to check if an async is in the same place as the enclosing finish
//      or async or at if any. 

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
			//return visitAnnotation((AnnotationNode) n);
		}
		if (n instanceof Finish){
			return visitFinishAnnotation((Finish)n);
		}
		if(n instanceof Async){
			//System.out.println(((Async_c)n).placeTerm.constraint().getClass());
		}
		return n;
		
	}
	private Node visitFinishAnnotation(Finish n){
        Position pos = n.position();
        try {
            Type annotation = (Type) xts.systemResolver().find(QName.make("x10.compiler.FinishAsync"));
            if (!((X10Ext) n.ext()).annotationMatching(annotation).isEmpty()) {
            	List<AnnotationNode> allannots = ((X10Ext)(n.ext())).annotations();
    	        for(int i=0;i<allannots.size();i++){
    	        	AnnotationNode a = allannots.get(i);
    	        	System.out.println(a);
    	        }
            }
        } catch (SemanticException e) { 
            /* Ignore exception when looking for annotation */  
        }
		return n;
	}
	private Node visitAnnotation(AnnotationNode n) {
		System.out.println("Annotation:" + n.toString());
		return n;
	}
}