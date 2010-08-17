package x10.visit;

import java.util.HashMap;

import java.util.List;
import java.util.Stack;

import java.util.Iterator;

import java.util.LinkedList;

import polyglot.ast.AmbTypeNode;
import polyglot.ast.BooleanLit;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NodeList_c;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile_c;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.IntLit.Kind;
import polyglot.frontend.Job;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Ref_c;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.*;
import x10.extension.X10Ext;
import x10.extension.X10Ext_c;
import x10.finish.table.*;
import x10.types.X10ClassDef;
import x10.types.X10ParsedClassType_c;
import x10.types.X10TypeSystem;

public class FinishAsyncVisitor extends ContextVisitor {
	private static HashMap<CallTableKey, Integer> finishTable = null;
	private static HashMap<CallTableKey, LinkedList<CallTableVal>> callTable = null;
	private String src_package = null;
	private String src_path = null;
	private String src_method = null;
	final String theLanguage;
	final X10TypeSystem xts;
	final X10NodeFactory xnf;

	public FinishAsyncVisitor(Job job, TypeSystem ts, NodeFactory nf,
			String theLanguage,
			HashMap<CallTableKey, LinkedList<CallTableVal>> ct) {
		super(job, job.extensionInfo().typeSystem(), job.extensionInfo()
				.nodeFactory());
		xts = (X10TypeSystem) ts;
		xnf = (X10NodeFactory) nf;
		this.theLanguage = theLanguage;
		if(finishTable == null && callTable == null){
			callTable = ct;
			finishTable = new HashMap<CallTableKey, Integer>();
			Iterator<CallTableKey> it = ct.keySet().iterator();
			while(it.hasNext()){
				CallTableKey k = it.next();
				if(k instanceof CallTableScopeKey){
					finishTable.put(k, new Integer(((CallTableScopeKey) k).pattern));
				}
			}
		}
	}

	public NodeVisitor enterCall(Node n) {
		if (n instanceof SourceFile_c) {
			visitSourceFile((SourceFile_c) n);
		}
		if (n instanceof X10MethodDecl_c) {
			// visitMethodDecl((X10MethodDecl_c)n);
		}
		if (n instanceof Async) {
			// visitEnterAsync((Async) n);
		}
		if (n instanceof Finish) {
			// visitEnterFinish((Finish)n);
		}
		if (n instanceof AtStmt) {
			// visitEnterAt((AtStmt)n);
		}
		return this;

	}

	public Node leaveCall(Node old, Node n, NodeVisitor v)
			throws SemanticException {

		if (n instanceof Finish) {
			return visitExitFinish((Finish) n);
		}
		if (n instanceof AtStmt) {
			// return visitExitAt((AtStmt) n);
		}
		if (n instanceof ClosureCall) {
			// return visitClosure((ClosureCall) n);
		}
		if (n instanceof X10Call) {
			// return visitCall((X10Call) n);
		}

		return n;
	}

	private void visitSourceFile(SourceFile_c n) {
		PackageNode pn = n.package_(); 
		String file = n.toString();
		// file = "<<<< name.x10 >>>>"
		int s = file.indexOf(" ");
		int e = file.indexOf(".x10");
		String name = file.substring(s + 1, e);
		if(pn == null){
			src_package = name;
		}else{
			src_package = pn.toString() + "." + name;
		}
		src_path = n.source().path();
		src_path = src_path.substring(1, src_path.length() - 4);
		src_path = src_path.replace('/', '.');
	}

	private void visitMethodDecl(X10MethodDecl_c n) {
		int line = n.position().line();
		int column = n.position().endColumn();
		String methodName = n.name().toString();
		src_method = methodName;
		CallTableMethodKey mk = new CallTableMethodKey(src_package, methodName,
				line, column);
		boolean f = callTable.keySet().contains(mk);
		if (f) {
			System.out.println("find method " + n.toString());

		} else {
			System.out.println("miss method " + n.toString());
			System.out.println("\tgenerated key:" + mk.toString());
		}
	}

	private Node visitExitAt(AtStmt n) {
		int line = n.position().line();
		int column = n.position().column();
		CallTableScopeKey at;
		at = new CallTableScopeKey(src_package, src_method, line, column, -1,
				false);
		// FIXME: if "at" is in a normal method, wala gives the package
		// this method belongs to as an "id"; However, if "at" is in an
		// async, which is treated as a wala-generated method, the "id"
		// becomes the full path of the file this async is in. So, here
		// we have to try both possibilities. Need to resolve this pecularity.

		boolean f = callTable.keySet().contains(at);
		if (f) {
			System.out.println("find at:" + n.toString());
		} else {
			at = new CallTableScopeKey(src_path, src_method, line, column, -1,
					false);
			f = callTable.keySet().contains(at);
			if (f) {
				System.out.println("find at:" + n.toString());
			} else {
				System.out.println("miss at:" + n.toString());
				System.out.println("\tgenerated key:" + at.toString());
			}
		}
		return n;
	}

	private Node visitCall(X10Call n) {
		int line = n.methodInstance().position().line();
		int column = n.methodInstance().position().endColumn();
		String methodName = n.methodInstance().name().toString();
		String pack = n.methodInstance().container().toString();
		CallTableMethodKey mk = new CallTableMethodKey(src_package, methodName,
				line, column);
		boolean f = callTable.keySet().contains(mk);
		if (f) {
			System.out.println("find method " + n.toString());
		} else {
			System.out.println("miss method " + n.toString());
			System.out.println("\tgenerated key:" + mk.toString());
		}
		return n;
	}

	private Node visitExitFinish(Finish n) throws SemanticException {
		//System.out.println("annotating "+job.source().name());
		int line = n.body().position().line();
		int column = n.body().position().column();
		CallTableScopeKey fs1 = new CallTableScopeKey(src_package, src_method,line, column, -1, true);
		CallTableScopeKey fs2 = new CallTableScopeKey(src_path, src_method,line, column, -1, true);
		boolean f1 = finishTable.containsKey(fs1);
		boolean f2 = finishTable.containsKey(fs2);
		if(f1){
			return addAnnotation2Finish(n,fs1);
		}
		if(f2){
			return addAnnotation2Finish(n,fs2);
		}
		System.out.println("finish "+fs1+" not found!");
		return n;
		
	}
	
	private Finish addAnnotation2Finish(Finish f, CallTableScopeKey fs) throws SemanticException{
		int pattern = finishTable.get(fs).intValue();
		//System.out.println("find finish:" + fs);
		X10Ext_c xext = (X10Ext_c) f.ext();
		// old is unmodifiedlist
		List<AnnotationNode> old = (List<AnnotationNode>) (xext.annotations());
		LinkedList<AnnotationNode> newannote = new LinkedList<AnnotationNode>();
		// retains all existing annotations
		newannote.addAll(old);
		// annotations added by compiler always have lower priority than those by programmers
		newannote.addLast(makeFinishAnnotation(f,fs.line,fs.column,false,pattern));
		return (Finish) ((X10Ext) f.ext()).annotations(newannote);
	}
	
	
	private void visitEnterAsync(Async n) {
		int line = n.position().line();
		int column = n.position().column();
		CallTableMethodKey m = new CallTableMethodKey(src_path, "activity",
				line, column);
		boolean f = callTable.keySet().contains(m);
		if (f) {
			System.out.println("find async:" + n.toString());
		} else {
			System.out.println("miss async:" + n.toString());
			System.out.println("\tgenerated key:" + m.toString());
		}

	}

	private Node visitClosure(ClosureCall n) {
		return n;
	}

	private AnnotationNode makeFinishAnnotation(Finish n, int arity, int place,
			boolean islast, int pattern) throws SemanticException {
		// find the type of this annotation
		Type t = (Type) xts.systemResolver().find(
				QName.make("x10.compiler.FinishAsync"));
		// create a node (type node) for this type
		CanonicalTypeNode tn = xnf.CanonicalTypeNode(n.position(), t);
		// create a annotation (ast node) based on the type node
		AnnotationNode an = ((X10NodeFactory_c) nf).AnnotationNode(
				n.position(), tn);
		// create parameters for this annotation
		IntLit x10arity = nf.IntLit(n.position(), IntLit.INT, arity);
		IntLit x10place = nf.IntLit(n.position(), IntLit.INT, place);
		BooleanLit x10islast = nf.BooleanLit(n.position(), islast);
		IntLit x10pattern = nf.IntLit(n.position(), IntLit.INT, pattern);
		List<Expr> initproperties = new LinkedList<Expr>();
		initproperties.add(x10arity);
		initproperties.add(x10place);
		initproperties.add(x10islast);
		initproperties.add(x10pattern);
		// patch this annotation with parameters
		Ref r = an.annotationType().typeRef();
		X10ParsedClassType_c xpct = (X10ParsedClassType_c) r.getCached();
		xpct = (X10ParsedClassType_c) xpct.propertyInitializers(initproperties);
		r.update(xpct);
		// return the annotation
		//System.out.println("newly created annotation:" + an.toString());
		return an;
	}

}
