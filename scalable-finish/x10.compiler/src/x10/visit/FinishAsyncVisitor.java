package x10.visit;

import java.util.HashMap;
import java.util.Stack;

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
import x10.finish.table.*;

public class FinishAsyncVisitor extends ContextVisitor {
	private static HashMap<CallTableKey, LinkedList<CallTableVal>> calltable =
		OutputUtil.loadCallTable("/Users/blshao/calltable.dat");
	private String src_package = null;
	private String method_key = "";
	private Stack<String> nestedAsyncs = new Stack<String>();
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
		
		return n;
	}
	
	private void visitMethodDecl(X10MethodDecl_c n){
		String allFormals = changeFormals(n.formals());
		String ret = changeReturn(n.returnType().toString());
		String methodKey = src_package + "." + n.name().toString() + 
							"("+ allFormals + ")" + ret;
		method_key = methodKey;
		// methodKey works to retrieve information from the calltable
		//System.out.println("wala format:"+ methodKey);
		CallTableMethodKey mk = new CallTableMethodKey(methodKey);
		boolean f = calltable.keySet().contains(mk);
		if(f){
			System.out.println("find method "+n.toString());
		}
		else{
			System.out.println("miss method "+n.toString());
			System.out.println("\tgenerated key:"+methodKey);
		}
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
	private void visitEnterAsync(Async n){
		// in x10.compiler: /home/blshao/workspace/wala-bridge-1.0/test.x10/TrivialTest.x10
		// in wala: .home.blshao.workspace.wala-bridge-1.0.test.x10.TrivialTest.x10
		 
		String filename = n.position().file();
		filename = filename.replace('/', '.');
		filename = "activity"+filename+":"+String.valueOf(n.position().line())
			+":"+String.valueOf(n.position().column());
		nestedAsyncs.push(filename);
		CallTableMethodKey m = new CallTableMethodKey(filename);
		boolean f = calltable.keySet().contains(m);
		if(f){
			System.out.println("find async:"+m.toString());
		}
		else{
			System.out.println("miss async:"+m.toString());
		}
		 
	}
	private Node visitExitAsync(Async n){
		
		nestedAsyncs.pop();
		return n;
	}
	private void visitEnterFinish(Finish n){
		int line = n.body().position().line();
		int column = n.body().position().column();
		//System.out.println("line = " + line + ", column = "+column);
		CallTableFinishKey fs = new CallTableFinishKey(method_key,line,column,-1,true);
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
		if(nestedAsyncs.isEmpty()){
			at = new CallTableFinishKey(method_key,line,column,-1,false);
		}
		else{
			at = new CallTableFinishKey(nestedAsyncs.peek(),line,column,-1,false);
		}
		boolean f = calltable.keySet().contains(at);
		if(f){
			System.out.println("find at:"+n.toString());
		}
		else{
			System.out.println("miss at:"+n.toString());
			System.out.println("\tgenerated key:"+at.toString());
		}
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
	private String toWalaFormat(String type){
		String tmp = type;
		int start = tmp.indexOf(':');
		int end = tmp.indexOf('[');
		if(end!=-1){
			tmp = tmp.substring(start+1,end); 
		}
		else{
			tmp = tmp.substring(start+1);
		}
		tmp = tmp.replace('.', '/');
		if(type.contains(": ")){
			// this types belongs to an argument in this case
			// to return value otherwise
			tmp = tmp.substring(1);
		}
		tmp = "L"+tmp+";";
		return tmp;

	}
}
