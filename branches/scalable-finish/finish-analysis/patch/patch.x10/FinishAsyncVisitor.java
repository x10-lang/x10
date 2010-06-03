package x10.visit;

import polyglot.ast.ClassDecl;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;

public class FinishAsyncVisitor extends ContextVisitor{
		public static class Count { public int val = 0; } 
		public Count count = new Count(); 
		public FinishAsyncVisitor(Job job) {
		
			super(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory());
		}
		public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException{
			if(n instanceof Async) count.val++;
			if(n instanceof ClassDecl) {
				System.err.print("Class "+((ClassDecl) n).name()+ 
						" contains "+count.val+" asyncs");
				count.val = 0;
			}
			return n;
		}
			
		
}
