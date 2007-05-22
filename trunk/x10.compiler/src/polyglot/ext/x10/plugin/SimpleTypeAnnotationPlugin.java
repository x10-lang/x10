package polyglot.ext.x10.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.ext.x10.ast.AnnotationNode;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10Instanceof;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.VisitorGoal;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

public abstract class SimpleTypeAnnotationPlugin implements CompilerPlugin {
	public SimpleTypeAnnotationPlugin() {
		super();
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
			l.addAll(super.prerequisiteGoals(scheduler));
			return l;
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
	}

	public class CheckVisitor extends ContextVisitor {
		public CheckVisitor(Job job, TypeSystem ts, NodeFactory nf) {
			super(job, ts, nf);
		}

		public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
			if (n instanceof LocalDecl) {
				LocalDecl ld = (LocalDecl) n;
				if (ld.init() != null) {
					Expr newInit = checkImplicitCoercion((X10Type) ld.type().type(), ld.init(), (X10Context) context, (X10TypeSystem) ts, (X10NodeFactory) nf);
					return ld.init(newInit);
				}
			}
			if (n instanceof Assign) {
				Assign a = (Assign) n;
				Expr newRight = checkImplicitCoercion((X10Type) a.left().type(), a.right(), (X10Context) context, (X10TypeSystem) ts, (X10NodeFactory) nf);
				return a.right(newRight);
			}
			if (n instanceof Call) {
				Call c = (Call) n;
				List newActuals = new ArrayList(c.arguments().size());
				MethodInstance mi = c.methodInstance();
				for (int i = 0; i < c.arguments().size(); i++) {
					Expr e = (Expr) c.arguments().get(i);
					Type t = (Type) mi.formalTypes().get(i);
					Expr newE = checkImplicitCoercion((X10Type) t, e, (X10Context) context, (X10TypeSystem) ts, (X10NodeFactory) nf);
					newActuals.add(newE);
				}
				return c.arguments(newActuals);
			}
			if (n instanceof X10Cast) {
				X10Cast c = (X10Cast) n;
				Expr e = checkCast((X10Type) c.castType().type(), c.expr(), (X10Context) context, (X10TypeSystem) ts, (X10NodeFactory) nf);
				return c.expr(e);
			}
			
			return n;
		}
	}
	
	public Expr checkCast(X10Type castType, Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		if (checkCastCoercion(castType,(X10Type) e.type(), context, ts, nf)) return e;
		throw new SemanticException("Cannot cast to " + castType + ".", e.position());
	}

	public boolean checkCastCoercion(X10Type toType, X10Type fromType, X10Context context, X10TypeSystem ts, X10NodeFactory nf) {
		return ts.isCastValid(fromType, toType);
	}
	
	public Expr checkImplicitCoercion(X10Type toType, Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		if (checkImplicitCoercion((X10Type) e.type(), toType, context, ts, nf))
			return e;
		if (checkNumericCoercion(toType, e, context, ts, nf))
			return e;
		throw new SemanticException("Cannot coerce to " + toType + ".", e.position());
	}

	public boolean checkNumericCoercion(X10Type toType, Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) {
		return e.type().isLongOrLess() && e.isConstant() && ts.numericConversionValid(toType, (Number) e.constantValue());
	}
	
	public boolean checkImplicitCoercion(X10Type toType, X10Type fromType, X10Context context, X10TypeSystem ts, X10NodeFactory nf) {
		return ts.isImplicitCastValid(fromType, toType);
	}
	
	public boolean checkSubtype(X10Type toType, X10Type fromType, X10Context context, X10TypeSystem ts, X10NodeFactory nf) {
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
}
