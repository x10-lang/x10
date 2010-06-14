package x10.visit;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import java.util.Iterator;


import java.util.LinkedList;


import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile_c;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.*;
import x10.finish.util.OutputUtil;
import x10.finish.util.CallTableUtil;
import x10.finish.table.*;

public class FinishAsyncVisitor extends ContextVisitor {
	private static HashMap<CallTableKey, LinkedList<CallTableVal>> calltable =
		OutputUtil.loadCallTable("/Users/blshao/calltable.dat");
	private String src_package = null;
	private String src_path = null;
 	static {
		System.out.println("table:");
		CallTableUtil.dumpCallTable(calltable);	 
	}
	
	public FinishAsyncVisitor(Job job) {

		super(job, job.extensionInfo().typeSystem(), job.extensionInfo()
				.nodeFactory());
	       
	}
	public NodeVisitor enterCall(Node n){
		if(n instanceof SourceFile_c){
			visitSourceFile((SourceFile_c)n);
		}
		if (n instanceof X10MethodDecl_c){
			//visitMethodDecl((X10MethodDecl_c)n);
		}
		if (n instanceof Async){
			visitEnterAsync((Async)n);
		}
		//TODO:
		if (n instanceof Finish){
			visitEnterFinish((Finish)n);
		}
		if (n instanceof AtStmt){
			visitEnterAt((AtStmt)n);
		}
		return this;
		
	}
	public Node leaveCall(Node old, Node n, NodeVisitor v)
			throws SemanticException {
		if (n instanceof Async){
			return visitExitAsync((Async)n);
		}
		if (n instanceof ClosureCall){    
			return visitClosure((ClosureCall)n);
		}
		if (n instanceof X10Call){
			return visitCall((X10Call)n);
		}
		if (n instanceof AnnotationNode){
			return visitAnnotation((AnnotationNode)n);
		}
		return n;
	}
	
	
	private void visitSourceFile(SourceFile_c n){
		src_package = n.package_().toString();
		src_path = n.source().path();
		String file = n.toString();
                //file = "<<<< name.x10 >>>>"
		int s = file.indexOf(" ");
		int e = file.indexOf(".x10");
		String name = file.substring(s+1, e);
		src_package = src_package + "." + name;
		src_path = src_path.substring(1,src_path.length()-4);
		src_path = src_path.replace('/', '.');
		//System.out.println("path:"+src_path);
		
	}
	private void visitMethodDecl(X10MethodDecl_c n){
		int line = n.position().line();
		int column = n.position().endColumn();
		String methodName = n.name().toString();
		CallTableMethodKey mk = new CallTableMethodKey(src_package,methodName,line,column);
		boolean f = calltable.keySet().contains(mk);
		if(f){
			System.out.println("find method "+n.toString());
		}
		else{
			System.out.println("miss method "+n.toString());
			System.out.println("\tgenerated key:"+mk.toString());
		}
	}
	private void visitEnterAsync(Async n){
		// in x10.compiler: /home/blshao/workspace/wala-bridge-1.0/test.x10/TrivialTest.x10
		// in wala: .home.blshao.workspace.wala-bridge-1.0.test.x10.TrivialTest.x10
		 
		int line = n.position().line();
		int column = n.position().column();
 		CallTableMethodKey m = new CallTableMethodKey(src_path,"activity",line,column);
		boolean f = calltable.keySet().contains(m);
		if(f){
			System.out.println("find async:"+n.toString());
		}
		else{
			System.out.println("miss async:"+n.toString());
			System.out.println("\tgenerated key:"+m.toString());
		}
		 
	}
	private Node visitExitAsync(Async n){
		
 		return n;
	}
	private void visitEnterFinish(Finish n){
		int line = n.body().position().line();
		int column = n.body().position().column();
		//System.out.println("line = " + line + ", column = "+column);
		CallTableFinishKey fs = new CallTableFinishKey(src_package,line,column,-1,true);
		boolean f = calltable.keySet().contains(fs);
		if(f){
			System.out.println("find finish:"+n.toString());
		}
		else{
			System.out.println("miss finish:"+n.toString());
			System.out.println("\tgenerated key:"+fs.toString());
		}
	}
	private void visitEnterAt(AtStmt n){
		int line = n.position().line();
		int column = n.position().column();
		CallTableFinishKey at; 
		at = new CallTableFinishKey(src_package,line,column,-1,false);
		//FIXME: if "at" is in a normal method, wala gives the package
		// this method belongs to as an "id"; However, if "at" is in an
		// async, which is treated as a wala-generated method, the "id"
		// becomes the full path of the file this async is in. So, here
		// we have to try both possibilities. Need to resolve this pecularity.
		
		boolean f = calltable.keySet().contains(at);
		if(f){
			System.out.println("find at:"+n.toString());
		}
		else{
			at = new CallTableFinishKey(src_path,line,column,-1,false);
			f = calltable.keySet().contains(at);
			if(f){
				System.out.println("find at:"+n.toString());
			}
			else{
				System.out.println("miss at:"+n.toString());
				System.out.println("\tgenerated key:"+at.toString());
			}
		}
	}
	private Node visitClosure(ClosureCall n){
		return n;
	}
	private Node visitCall(X10Call n){
		int line = n.methodInstance().position().line();
		int column = n.methodInstance().position().endColumn();
		String methodName = n.methodInstance().name().toString();
 		String pack = n.methodInstance().container().toString();
		CallTableMethodKey mk = new CallTableMethodKey(src_package,methodName,line,column);
		boolean f = calltable.keySet().contains(mk);
		if(f){
			System.out.println("find method "+n.toString());
		}
		else{
			System.out.println("miss method "+n.toString());
			System.out.println("\tgenerated key:"+mk.toString());
		}
		return n;
	}
	private Node visitAnnotation(AnnotationNode n){
		System.out.println(n.toString());
		return n;
	}
}
