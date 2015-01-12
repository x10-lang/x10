/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Call_c;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.ConstructorDecl_c;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.FieldDecl;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Field_c;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Import_c;
import polyglot.ast.Initializer_c;
import polyglot.ast.LocalDecl;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Local_c;
import polyglot.ast.MethodDecl;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.New_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode_c;
import polyglot.ast.SourceFile;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import x10.ExtensionInfo;
import x10.ast.DepParameterExpr;
import x10.extension.X10Ext;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10Type;
import x10.types.X10TypeObject;
import polyglot.frontend.Job;
import polyglot.frontend.MissingDependencyException;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import polyglot.main.Report;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.MemberInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.reflect.ClassFileLazyClassInitializer;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PruningVisitor;

public class PropagateDependentAnnotationsVisitor extends NodeVisitor {
	Job job;
	TypeSystem ts;
	NodeFactory nf;
	
	public PropagateDependentAnnotationsVisitor(Job job, TypeSystem ts, NodeFactory nf) {
		super();
		this.job = job;
		this.ts = ts;
		this.nf = nf;
	}
	
	public Job job() { return job; }
	
	public Node leave(Node old, Node n, NodeVisitor v) {
		n = super.leave(old, n, v);
		
		X10DelegatingVisitor d = new X10DelegatingVisitor() {
//			public void visit(Node n) {
//				if (n.ext() instanceof X10Ext) {
//					for (Iterator<AnnotationNode> i = ((X10Ext) n.ext()).annotations().iterator(); i.hasNext(); ) {
//						AnnotationNode an = i.next();
//					}
//				}
//				super.visit(n);
//			}

			protected void force(List l) {
				for (Iterator i = l.iterator(); i.hasNext(); ) {
					TypeObject o = (TypeObject) i.next();
					force(o);
				}
			}
			
			protected void force(TypeObject o) {
				if (o instanceof X10TypeObject) {
					X10TypeObject xo = (X10TypeObject) o;
					
					if (xo instanceof FieldInstance) {
						FieldInstance fi = (FieldInstance) xo;
						fi.constantValue();
						force(fi.type());
					}
					else if (xo instanceof LocalInstance) {
						LocalInstance li = (LocalInstance) xo;
						li.constantValue();
						force(li.type());
					}
					else if (xo instanceof MethodInstance) {
						MethodInstance mi = (MethodInstance) xo;
						force(mi.returnType());
						force(mi.formalTypes());
						force(mi.throwTypes());
					}
					else if (xo instanceof X10ConstructorInstance) {
						X10ConstructorInstance ci = (X10ConstructorInstance) xo;
						force(ci.returnType());
						force(ci.formalTypes());
						force(ci.throwTypes());
					}
					else if (xo instanceof ArrayType) {
						ArrayType at = (ArrayType) xo;
						force(at.base());
					}
					else if (xo instanceof X10ClassType) {
						X10ClassType ct = (X10ClassType) xo;
						DepParameterExpr dep = ct.dep();
						if (dep != null) {
							dep.visit(PropagateDependentAnnotationsVisitor.this);
						}
						if (ct instanceof X10ParsedClassType) {
							force(((X10ParsedClassType) ct).classAnnotations());
						}
//						force(ct.superType());
//						force(ct.interfaces());
//						force(ct.members());
					}
					else if (xo instanceof ClassType) {
						ClassType ct = (ClassType) xo;
//						force(ct.superType());
//						force(ct.interfaces());
//						force(ct.members());
					}
					
					if (xo instanceof X10ParsedClassType) {
						X10ParsedClassType ct = (X10ParsedClassType) xo;
						if (ct.job() == PropagateDependentAnnotationsVisitor.this.job()) {
							ct.setDefAnnotations(Collections.EMPTY_LIST);
							if (ct.isRootType())
								ct.setClassAnnotations(Collections.EMPTY_LIST);
							return;
						}
						if (ct.job() == null) {
							ct.setDefAnnotations(Collections.EMPTY_LIST);
							if (ct.isRootType())
								ct.setClassAnnotations(Collections.EMPTY_LIST);
							return;
						}
					}
					else if (xo instanceof MemberInstance) {
						MemberInstance mi = (MemberInstance) xo;
						boolean isRoot = false;
						if (mi instanceof X10FieldInstance && ((X10FieldInstance) mi).orig() == mi) isRoot = true;
						if (mi instanceof X10LocalInstance && ((X10LocalInstance) mi).orig() == mi) isRoot = true;
						if (mi instanceof X10MethodInstance && ((X10MethodInstance) mi).orig() == mi) isRoot = true;
						if (mi instanceof X10ConstructorInstance && ((X10ConstructorInstance) mi).orig() == mi) isRoot = true;
						if (mi.container() instanceof ParsedClassType) {
							ParsedClassType ct = (ParsedClassType) mi.container();
							if (ct.job() == PropagateDependentAnnotationsVisitor.this.job()) {
								if (isRoot) {
									xo.setDefAnnotations(Collections.EMPTY_LIST);
								}
								return;
							}
							if (ct.job() == null) {
								if (isRoot) {
									xo.setDefAnnotations(Collections.EMPTY_LIST);
								}
								return;
							}
						}
					}
					else {
						return;
					}
					
					force(xo.defAnnotations());
				}
			}

			@Override
			public void visit(Call_c n) {
				force(n.methodInstance());
				super.visit(n);
			}
			
			@Override
			public void visit(ClassDecl_c n) {
				force(n.type());
				super.visit(n);
			}
			
			@Override
			public void visit(ConstructorCall_c n) {
				force(n.constructorInstance());
				super.visit(n);
			}
			
			@Override
			public void visit(ConstructorDecl_c n) {
				force(n.constructorInstance());
				super.visit(n);
			}
			
			@Override
			public void visit(Expr_c n) {
				force(n.type());
				super.visit(n);
			}

			@Override
			public void visit(Field_c n) {
				force(n.fieldInstance());
				super.visit(n);
			}

			@Override
			public void visit(FieldDecl_c n) {
				force(n.fieldInstance());
				super.visit(n);
			}

			@Override
			public void visit(Formal_c n) {
				force(n.localDef());
				super.visit(n);
			}

			// @Override
			// public void visit(Import_c n) {
			// super.visit(n);
			// }

			@Override
			public void visit(Initializer_c n) {
				force(n.initializerInstance());
				super.visit(n);
			}
			
			@Override
			public void visit(Local_c n) {
				force(n.localInstance());
				super.visit(n);
			}

			@Override
			public void visit(LocalDecl_c n) {
				force(n.localDef());
				super.visit(n);
			}

			@Override
			public void visit(MethodDecl_c n) {
				force(n.methodInstance());
				super.visit(n);
			}

			@Override
			public void visit(New_c n) {
				force(n.constructorInstance());
				super.visit(n);
			}

			@Override
			public void visit(PackageNode_c n) {
				force(n.package_());
				super.visit(n);
			}

			@Override
			public void visit(TypeNode_c n) {
				force(n.type());
				super.visit(n);
			}
		};
		
		try {
			d.visitAppropriate(n);
		}
		catch (MissingDependencyException e) {
			if (Report.should_report(Report.frontend, 3))
				e.printStackTrace();
			Scheduler scheduler = job.extensionInfo().scheduler();
			Goal g = scheduler.currentGoal();
			scheduler.addDependencyAndEnqueue(g, e.goal(), e.prerequisite());
			g.setUnreachableThisRun();
		}
		
		return n;
	}
}
