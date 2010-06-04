package x10.visit;

import java.util.HashMap;
import java.util.Iterator;


import java.util.LinkedList;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile_c;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.*;
import x10.finish.util.OutputUtil;
import x10.finish.util.CallTableUtil;
import x10.finish.table.CallTableKey;
import x10.finish.table.CallTableMethodKey;
import x10.finish.table.CallTableVal;

public class FinishAsyncVisitor extends ContextVisitor {
	private static HashMap<CallTableKey, LinkedList<CallTableVal>> calltable =
		OutputUtil.loadCallTable("/Users/blshao/calltable.dat");
	private String src_package = null;
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
			visitMethodDecl((X10MethodDecl_c)n);
		}
		return this;
		
	}
	public Node leaveCall(Node old, Node n, NodeVisitor v)
			throws SemanticException {
		
		if (n instanceof Async){
			return visitAsync((Async)n);
		}
		if (n instanceof Finish){
			return visitFinish((Finish)n);
		}
		if (n instanceof AtStmt){
			return visitAt((AtStmt)n);
		}
		if (n instanceof ClosureCall){    
			return visitClosure((ClosureCall)n);
		}
		if (n instanceof X10Call){
			return visitCall((X10Call)n);
		}
		
		return n;
	}
	
	private void visitMethodDecl(X10MethodDecl_c n){
		String allFormals = changeFormals(n.formals());
		String ret = changeReturn(n.returnType().toString());
		String methodKey = src_package + "." + n.name().toString() + 
							"("+ allFormals + ")" + ret;
		//System.out.println("wala format:"+ methodKey);
		CallTableMethodKey mk = new CallTableMethodKey(methodKey);
		//calltable.get(mk);
	}
	private void visitSourceFile(SourceFile_c n){
		src_package = n.package_().toString();
		String file = n.toString();
		//file = "<<<< name.x10 >>>>"
		int s = file.indexOf(" ");
		int e = file.indexOf(".x10");
		String name = file.substring(s+1, e);
		src_package = src_package + "." + name;
	}
	private Node visitAsync(Async n){
		// in x10.compiler: /home/blshao/workspace/wala-bridge-1.0/test.x10/TrivialTest.x10
		// in wala: .home.blshao.workspace.wala-bridge-1.0.test.x10.TrivialTest.x10
		String filename = n.position().file();
		filename = filename.replace('/', '.');
		filename = "activity"+filename+":"+String.valueOf(n.position().line())
			+":"+String.valueOf(n.position().column());
		CallTableMethodKey m = new CallTableMethodKey(filename);
		LinkedList<CallTableVal> l = calltable.get(m);
		if(l!=null){
			System.out.println("find async:"+m.toString());
		}
		else{
			System.out.println("miss async:"+m.toString());
		}
		return n;
	}
	
	private Node visitFinish(Finish n){
		return n;
	}
	private Node visitAt(AtStmt n){
		return n;
	}
	private Node visitClosure(ClosureCall n){
		return n;
	}
	private Node visitCall(X10Call n){
		return n;
	}
	/**
	 * for a list of types, x10.lang.Rail[...] and x10.lang.Rail[...],
	 * this method returns a string: Lx10/lang/Rail;Lx10/lang/Rail;
	 * @param fs
	 * @return
	 */
	private String changeFormals(java.util.List<Formal> fs){
		String f = "";
		Iterator<Formal> it = fs.iterator();
		while(it.hasNext()){
			X10Formal_c fl = (X10Formal_c)it.next();
			String tmp = fl.toString();
			tmp = toWalaFormat(tmp);
			f = f + tmp;
		}
		return f;
	}
	
	private String changeReturn(String ret){
		String new_ret;
		if(ret.contains("Void")){
			new_ret = "V";
		}
		else{
			new_ret = toWalaFormat(ret);
		}
		return new_ret;
	}
	private String toWalaFormat(String tmp){
		
			int start = tmp.indexOf(':');
			int end = tmp.indexOf('[');
			if(end!=-1){
				tmp = tmp.substring(start+1,end); 
			}
			else{
				tmp = tmp.substring(start+1);
			}
			tmp = tmp.replace('.', '/');
			tmp = tmp.substring(1);
			tmp = "L"+tmp+";";
			return tmp;

	}
}
