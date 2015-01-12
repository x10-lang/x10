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

package x10.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.ArrayInit;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Unary;
import x10.ExtensionInfo;
import x10.ExtensionInfo.X10Scheduler;
import x10.ast.ParExpr;
import x10.ast.X10Cast;
import x10.ast.X10Instanceof;
import x10.ast.X10NodeFactory;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.X10MethodInstance;
import x10.types.X10Type;
import x10.types.X10TypeSystem;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.types.ArrayType;
import polyglot.types.ProcedureInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

public abstract class SimpleTypeAnnotationPlugin implements CompilerPlugin {
	public SimpleTypeAnnotationPlugin() {
		super();
		System.out.println("Registering " + this.getClass().getName());
	}
	
	public static class CheckerGoal extends VisitorGoal {
		public static Goal create(Scheduler scheduler, Job job, NodeVisitor v) {
			return scheduler.internGoal(new CheckerGoal(job, v));
		}
		
		Goal precheckGoal;
		
		private CheckerGoal(Job job, NodeVisitor v) {
			super(job, v);
		}
		
		public Collection prerequisiteGoals(Scheduler scheduler) {
			X10Scheduler x10Sched = (X10Scheduler) scheduler;
			List<Goal> l = new ArrayList<Goal>();
			l.add(x10Sched.TypeChecked(job));
			l.add(x10Sched.ConstantsChecked(job));
			l.add(x10Sched.PropagateAnnotations(job));
			l.addAll(super.prerequisiteGoals(scheduler));
			return l;
		}
		
		public int hashCode() {
			return super.hashCode() + v.hashCode();
		}
		
		public boolean equals(Object o) {
			return super.equals(o) && v.equals(((VisitorGoal) o).visitor());
		}
	}
	
	public static class CasterGoal extends VisitorGoal {
		public static Goal create(Scheduler scheduler, Job job, NodeVisitor v, Goal checkGoal) {
			return scheduler.internGoal(new CasterGoal(job, v, checkGoal));
		}
		
		Goal checkGoal;
		
		private CasterGoal(Job job, NodeVisitor v, Goal checkGoal) {
			super(job, v);
			this.checkGoal = checkGoal;
		}
		
		public Collection prerequisiteGoals(Scheduler scheduler) {
			X10Scheduler x10Sched = (X10Scheduler) scheduler;
			List<Goal> l = new ArrayList<Goal>();
			l.add(checkGoal);
			l.addAll(super.prerequisiteGoals(scheduler));
			return l;
		}
		
		public int hashCode() {
			return super.hashCode() + v.hashCode();
		}
		
		public boolean equals(Object o) {
			return super.equals(o) && v.equals(((VisitorGoal) o).visitor());
		}
	}

	public class CheckVisitor extends ContextVisitor {
		public CheckVisitor(Job job, TypeSystem ts, NodeFactory nf) {
			super(job, ts, nf);
		}
		
		@Override
		public NodeVisitor begin() {
//			System.out.println("Running " + plugin().getClass().getName());
			return super.begin();
		}

		public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
			X10Context context = (X10Context) this.context;
			X10TypeSystem ts = (X10TypeSystem) this.ts;
			X10NodeFactory nf = (X10NodeFactory) this.nf;
			
			if (n instanceof LocalDecl) {
				LocalDecl ld = (LocalDecl) n;
				X10Type type = (X10Type) ld.type().type();
				if (ld.init() != null) {
					Expr newInit = checkImplicitCoercion(type, ld.init(), context, ts, nf);
					return ld.init(newInit);
				}
				if (ld.init() instanceof ArrayInit && type instanceof ArrayType) {
					ArrayInit init = (ArrayInit) ld.init();
					List<Expr> newElements = new ArrayList<Expr>(init.elements().size());
					for (Iterator i = init.elements().iterator(); i.hasNext(); ) {
						Expr e = (Expr) i.next();
						Expr newE = checkImplicitCoercion((X10Type) ((ArrayType) type).base(), e, context, ts, nf);
						newElements.add(newE);
					}
					return ld.init(init.elements(newElements));
				}
			}
			if (n instanceof Assign) {
				Assign a = (Assign) n;
				Expr newRight = checkImplicitCoercion((X10Type) a.left().type(), a.right(), context, ts, nf);
				return annotationCast(a.right(newRight), context, ts, nf);
			}
			if (n instanceof ProcedureCall) {
				ProcedureCall c = (ProcedureCall) n;
				List<Expr> newActuals = new ArrayList<Expr>(c.arguments().size());
				ProcedureInstance pi = c.procedureInstance();
				for (int i = 0; i < c.arguments().size(); i++) {
					Expr e = (Expr) c.arguments().get(i);
					Type t = (Type) pi.formalTypes().get(i);
					Expr newE = checkImplicitCoercion((X10Type) t, e, context, ts, nf);
					newActuals.add(newE);
				}
				c = (ProcedureCall) c.arguments(newActuals);
				if (c instanceof Call) {
					return annotationCast(checkCall((Call) c, context, ts, nf), context, ts, nf);
				}
				if (c instanceof New) {
					return annotationCast(checkNew((New) c, context, ts, nf), context, ts, nf);
				}
				if (c instanceof Expr) {
					return annotationCast((Expr) c, context, ts, nf);
				}
				return c;
			}
			if (n instanceof ParExpr) {
				ParExpr p = (ParExpr) n;
				return annotationCast(p.type(p.expr().type()), context, ts, nf);
			}
			if (n instanceof Field) {
				Field f = (Field) n;
				return annotationCast(checkField(f, context, ts, nf), context, ts, nf);
			}
			if (n instanceof Local) {
				Local l = (Local) n;
				return annotationCast(checkLocal(l, context, ts, nf), context, ts, nf);
			}
			if (n instanceof X10Cast) {
				X10Cast c = (X10Cast) n;
				Expr e = checkCast((X10Type) c.castType().type(), c.expr(), context, ts, nf);
				return annotationCast(c.expr(e), context, ts, nf);
			}
			if (n instanceof Unary) {
				Unary b = (Unary) n;
				return annotationCast(b.type(unaryPromote(b, ts, nf)), context, ts, nf);
			}
			if (n instanceof Binary) {
				Binary b = (Binary) n;
				return annotationCast(b.type(binaryPromote(b, ts, nf)), context, ts, nf);
			}
			if (n instanceof Expr) {
				Expr e = (Expr) n;
				return annotationCast(e, context, ts, nf);
			}
			return n;
		}
		
		public SimpleTypeAnnotationPlugin plugin() {
			return SimpleTypeAnnotationPlugin.this;
		}
		
		public int hashCode() {
			return super.hashCode() + plugin().hashCode();
		}
		
		public boolean equals(Object o) {
			return super.equals(o) && plugin() == ((CheckVisitor) o).plugin();
		}
	}
	
	public Expr checkField(Field f, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10FieldInstance fi = (X10FieldInstance) f.fieldInstance();
		List<X10ClassType> memberAnnotations = ((X10FieldInstance) fi.orig()).annotations();
		X10Type type = (X10Type) fi.orig().type();
		List<X10ClassType> typeAnnotations = type.defAnnotations();
		return propagate(f, type, memberAnnotations, typeAnnotations);
	}
	
	public Expr checkLocal(Local l, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10LocalInstance li = (X10LocalInstance) l.localInstance();
		List<X10ClassType> memberAnnotations = ((X10LocalInstance) li.orig()).annotations();
		X10Type type = (X10Type) li.orig().type();
		List<X10ClassType> typeAnnotations = type.defAnnotations();
		return propagate(l, type, memberAnnotations, typeAnnotations);
	}
	
	public Expr checkCall(Call c, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
		List<X10ClassType> memberAnnotations = ((X10MethodInstance) mi.orig()).annotations();
		X10Type type = (X10Type) mi.orig().returnType();
		List<X10ClassType> typeAnnotations = type.defAnnotations();
		return propagate(c, type, memberAnnotations, typeAnnotations);
	}
	public Expr checkNew(New n, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10ConstructorInstance ci = (X10ConstructorInstance) n.constructorInstance();
		List<X10ClassType> memberAnnotations = ((X10ConstructorInstance) ci.orig()).annotations();
		X10Type type = (X10Type) ci.orig().container();
		List<X10ClassType> typeAnnotations = type.defAnnotations();
		return propagate(n, type, memberAnnotations, typeAnnotations);
	}
	
	public Expr propagate(Expr e, X10Type declType, List<X10ClassType> memberAnnotations, List<X10ClassType> typeAnnotations) {
		X10Type t = (X10Type) e.type();
		t = (X10Type) t.annotations(typeAnnotations);
		return e.type(t);
	}

	public X10Type binaryPromote(Binary b, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return (X10Type) b.type();
	}
	
	public X10Type unaryPromote(Unary u, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return (X10Type) u.type();
	}
	
	public Expr checkCast(X10Type castType, Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10Type etype = (X10Type) e.type();
		if (ts.isCastValid(etype, castType) && checkCastCoercion(castType, etype, context, ts, nf)) return e;
		throw new SemanticException("Cannot cast " + e + "(" + etype + ") to " + castType + ".", e.position());
	}

	public boolean checkCastCoercion(X10Type toType, X10Type fromType, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return true;
	}
	
	public Expr checkImplicitCoercion(X10Type toType, Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10Type etype = (X10Type) e.type();
		if (ts.isImplicitCastValid(etype, toType) && checkImplicitCoercion(toType, etype, context, ts, nf))
			return e;
		if (e.isConstant() && ts.numericConversionValid(toType, e.constantValue()) && checkNumericCoercion(toType, e, context, ts, nf))
			return e;
		throw new SemanticException("Cannot coerce " + e + " (" + etype + ") to " + toType + ".", e.position());
	}

	public boolean checkNumericCoercion(X10Type toType, Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return true;
	}
	
	public boolean checkImplicitCoercion(X10Type toType, X10Type fromType, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return true;
	}
	
	public class CastRewriteVisitor extends ContextVisitor {
		public CastRewriteVisitor(Job job, TypeSystem ts, NodeFactory nf) {
			super(job, ts, nf);
		}
		
		public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
			if (n instanceof X10Cast) {
				return rewriteCast((X10Cast) n, (X10Context) context, (X10TypeSystem) ts, (X10NodeFactory) nf);
			}
			if (n instanceof X10Instanceof) {
				return rewriteInstanceof((X10Instanceof) n, (X10Context) context, (X10TypeSystem) ts, (X10NodeFactory) nf);
			}
			return n;
		}
		
		public SimpleTypeAnnotationPlugin plugin() {
			return SimpleTypeAnnotationPlugin.this;
		}
		
		public int hashCode() {
			return super.hashCode() + plugin().hashCode();
		}
		
		public boolean equals(Object o) {
			return super.equals(o) && plugin() == ((CheckVisitor) o).plugin();
		}
	}
	
	protected Expr rewriteCast(X10Cast n, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return n;
	}
	
	protected Expr rewriteInstanceof(X10Instanceof e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return e;
	}
	
	protected CheckVisitor checkVisitor(ExtensionInfo extInfo, Job job) {
		return new CheckVisitor(job, extInfo.typeSystem(), extInfo.nodeFactory());
	}
	
	protected CastRewriteVisitor castRewriteVisitor(ExtensionInfo extInfo, Job job) {
		return new CastRewriteVisitor(job, extInfo.typeSystem(), extInfo.nodeFactory());
	}
	
	public Goal register(ExtensionInfo extInfo, Job job) {
		Goal check = CheckerGoal.create(extInfo.scheduler(), job,
				checkVisitor(extInfo, job));
		Goal cast = CasterGoal.create(extInfo.scheduler(), job,
				castRewriteVisitor(extInfo, job), check);
		
		X10Scheduler x10Sched = (X10Scheduler) extInfo.scheduler();
		x10Sched.addDependencyAndEnqueue(x10Sched.X10Boxed(job), cast, true);
		
		return cast;
	}

	protected Expr annotationCast(Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		return e;
	}
}
