package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.ArrayInit_c;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FloatLit;
import polyglot.ast.Instanceof;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.New;
import polyglot.ast.NewArray;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.StringLit;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.X10Binary;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10CastInfo;
import polyglot.ext.x10.ast.X10DepCastInfo;
import polyglot.ext.x10.ast.X10Field_c;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;
import polyglot.parse.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/**
 * Visitor that inserts boxing and unboxing code into the AST.
 */
public class X10Caster extends AscriptionVisitor {
	X10TypeSystem xts;
	boolean castCheckClassNotLoaded = true;
	public X10Caster(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		xts = (X10TypeSystem) ts;
	}

	/**
	 * This method rewrites an AST node. We have to be careful also to provide
	 * type information with the newly created node, because the type checker
	 * ran before this pass and the node must hence be annotated. Just calling
	 * the node factory is not sufficient.
	 * 
	 * @throws SemanticException
	 */
	public Expr ascribe(Expr e, Type toType) throws SemanticException {
		Type fromType = e.type();
		Expr ret_notype = e;

		if (this.castCheckClassNotLoaded) {
			((Type) ts.systemResolver().find("x10.lang.RuntimeCastChecker")).toClass();
			castCheckClassNotLoaded = false;
		}
		
		if (toType == null) {
			return e;
		}
		
		Position p = e.position();
		if ((e instanceof Cast) || (e instanceof Instanceof)) {
			TypeBuilder tb = new TypeBuilder(job, ts, nf);
			AmbiguityRemover ar = new AmbiguityRemover(job, ts, nf);
			TypeChecker tc = new TypeChecker(job, ts, nf);
			ar = (AmbiguityRemover) ar.context(context());
			tc = (TypeChecker) tc.context(context());

			X10CastInfo cast = (X10CastInfo) ret_notype;

			// First some checks related to nullable, 
			// that may avoid to perform a runtime check
			if (cast.isToTypeNullable()) {
				// Check expression like litteral null to nullable type
				if (e instanceof Instanceof) {
					if (cast.expr().type().isNull()) {
						// null instanceof nullable<T> || T is always false
						return (Expr) nf.BooleanLit(p,false).visit(tc);
					}
				}

				if (e instanceof Cast) { // obviously it is cast
					if (cast.expr().type().isNull()) {
						// target type is nullable hence cast is always valid, we rewrite the node
						return (Expr) nf.NullLit(p).visit(tc);
					}
				}
			}

			if (cast.isDepTypeCheckingNeeded()) {
				// dynamic cast is needed which means toType may have constraint
				// we must check at runtime
				// may be replaced by some pattern
				MethodChecking mc = (e instanceof Cast) ? new MethodCastChecking(
						nf, ts, tb, ar, tc, p)
						: new MethodInstanceOfChecking(nf, ts, tb, ar, tc, p);

				return mc.getRuntimeCheckingExpr(ret_notype);
			} else {				
				if (cast.notNullRequired() && (e instanceof Cast)) {
					// Here type cast is T <-- nullable<T>
					// Hence we don't want the regulat java cast (T) NullType) works. 
					MethodChecking mc = new MethodCastChecking(nf, ts, tb, ar, tc, p);	
					return mc.getNonNullableCheckingExpr(ret_notype);
				}
			}
		}

		return e;
	}

	public Node leaveCall(Node old, Node n, NodeVisitor v)
			throws SemanticException {
		n = super.leaveCall(old, n, v);

		// RMF 11/3/2005 - Don't rewrite yet if this goal is already marked
		// unreachable;
		// the next time we try to run this pass, we'll have a half-rewritten
		// class, and
		// will end up with duplicate compiler-generated methods, or worse.
		//
		if (job.extensionInfo().scheduler().currentGoal().state() != Goal.UNREACHABLE_THIS_RUN) {
			if (n.ext() instanceof X10Ext) {
				return ((X10Ext) n.ext()).rewrite((X10TypeSystem) typeSystem(),
						nodeFactory(), job.extensionInfo());
			}
		}

		return n;
	}
	private class CastChecking {
		private NodeFactory nf;
		private TypeSystem ts;
		protected TypeBuilder tb;
		protected AmbiguityRemover ar;
		protected TypeChecker tc;
		protected Position p;
		
		public CastChecking(NodeFactory nf2, TypeSystem ts2, TypeBuilder tb,
				AmbiguityRemover ar, TypeChecker tc, Position p) {
			this.nf = nf2;
			this.ts = ts2;
			this.tb = tb;
			this.ar = ar;
			this.tc = tc;
			this.p = p;
		}

		protected Name getRuntimeCheckingClassName() {
			Name x10 = new Name(nf, ts, p, "x10");
			Name x10Lang = new Name(nf, ts, p, x10, "lang");
			Name x10LangRuntimeCastChecker = new Name(nf, ts, p, x10Lang,
					"RuntimeCastChecker");

			return x10LangRuntimeCastChecker;
		}

		protected Expr checkExpression(Expr e) {
			return (Expr) e.visit(tb).visit(ar).visit(tc);
		}
		
	}

	protected abstract class MethodChecking extends CastChecking {
		public MethodChecking(NodeFactory nf, TypeSystem ts, TypeBuilder tb,
				AmbiguityRemover ar, TypeChecker tc, Position p) {
			super(nf, ts, tb, ar, tc, p);
		}
		
		protected abstract Name runtimeCheckingToNonNullableMethodName() throws SemanticException;
		
		protected abstract Name runtimeConstrainedCheckingMethodName();

		protected abstract Name primitiveConstrainedCastCheckerMethodName() throws SemanticException;

		protected Name getRuntimeConstraintClassName() {
			return new Name(nf, ts, p,
					"x10.lang.RuntimeCastChecker.RuntimeConstraint");
		}

		protected Name getRuntimeConstraintOnSelfClassName() {
			return new Name(nf, ts, p,
					"x10.lang.RuntimeCastChecker.RuntimeConstraintOnSelf");
		}


		public Expr getNonNullableCheckingExpr(Expr checkingNode) 
			throws SemanticException {
			List methodArgs = new ArrayList();
			Expr exprToCheck = ((X10CastInfo) checkingNode).expr();

			methodArgs.add(exprToCheck.copy());
			methodArgs.add(nf.ClassLit(p, ((X10CastInfo) checkingNode)
					.getTypeNode()));

			Call checkNullableCall = nf.Call(p, this
					.getRuntimeCheckingClassName().toReceiver(), this
					.runtimeCheckingToNonNullableMethodName().name, methodArgs);

			checkNullableCall = (Call) checkExpression(checkNullableCall);

			return this.finalizeRuntimeCheckingExpr(checkingNode,
					checkNullableCall);
		}

		public Expr getRuntimeCheckingExpr(Expr checkingNode)
				throws SemanticException {

			Constraint declaredConstraints = this
					.getConstraintsToCheck(checkingNode);

			Expr constraintCheckingCall = this.buildConstraintCheckingCall(
					checkingNode, declaredConstraints);

			return this.finalizeRuntimeCheckingExpr(checkingNode,
					constraintCheckingCall);
		}

		protected abstract Constraint getConstraintsToCheck(Expr checkingNode);

		protected abstract Expr finalizeRuntimeCheckingExpr(Expr castNode,
				Expr runtimeCheckingExpr) throws SemanticException;

		protected Expr buildConstraintCheckingCall(Expr checkingExpr,
				Constraint declaredConstraints) throws SemanticException {

			// building constraint array that will be used at runtime
			Expr constraintArray = this.buildConstraintArray(this.checkConstraintList(this
					.buildConstraintList(((X10DepCastInfo) checkingExpr)
							.depParameterExpr(), declaredConstraints), checkingExpr));

			// building runtime cast checking method call
			Expr checkingCall = ((X10CastInfo) checkingExpr).isPrimitiveCast() ? this
					.buildPrimitiveCheckingMethodCall(constraintArray,
							checkingExpr)
					: this.buildCheckingMethodCall((X10CastInfo) checkingExpr,
							constraintArray, ((X10CastInfo) checkingExpr)
									.expr());
			checkingCall = (Call) checkExpression(checkingCall);

			return checkingCall;
		}

		private List checkConstraintList(List list, Expr checkingExpr) {
			X10CastInfo cast = (X10CastInfo) checkingExpr;
			List res = new LinkedList();
			
			if (((X10TypeSystem)ts).isBoxedType(cast.getTypeNode().type())){
				// we replace 'self' by the good accessor
				String methodToInvoke = ((X10TypeSystem)ts).getGetterName(cast.getTypeNode().type());
				for (Iterator iter = list.iterator(); iter.hasNext();) {
					New newConstraint = (New) iter.next();
					List newConstructorArgs = new LinkedList();
					assert(!newConstraint.arguments().isEmpty());
					// iterating constraint args
					for (Iterator iterator = newConstraint.arguments().iterator(); iterator.hasNext();) {
						Expr expr = (Expr) iterator.next();
						if ((expr instanceof StringLit) && ((StringLit) expr).value().equals("self")) {
							newConstructorArgs.add(((StringLit) expr).value(methodToInvoke));
						} else {
							newConstructorArgs.add(expr);
						}
					}

					res.add(this.checkExpression((Expr) newConstraint.arguments(newConstructorArgs)));
				}
			} else {
				res = list;
			}
			
			return res;
		}

		private Expr buildConstraintArray(List runtimeConstraints) {
			TypeNode arrayBaseType = this.getRuntimeConstraintClassName()
					.toType();

			// we are building an array
			NewArray constraintArray = nf.NewArray(p, arrayBaseType,
					Collections.EMPTY_LIST, 1, new ArrayInit_c(p,
							runtimeConstraints));

			constraintArray = (NewArray) checkExpression(constraintArray);

			return constraintArray;
		}

		private List buildConstraintList(DepParameterExpr expr, Constraint declaredConstraint) throws SemanticException {
			ConstraintBuilder constraintBuilder = new ConstraintBuilder(declaredConstraint);
			List constraint = constraintBuilder.visit(expr);
			return constraint;
		}

		protected Expr buildCheckingMethodCall(X10CastInfo checkingNode,
				Expr constraintArray, Expr exprToCheck) {
			// must build an argument list of expression
			List methodArgs = new ArrayList();
			methodArgs.add(constraintArray);
			methodArgs.add(nf.BooleanLit(p, ((X10CastInfo) checkingNode)
					.notNullRequired()));
			methodArgs.add(nf.BooleanLit(p, ((X10CastInfo) checkingNode)
					.isToTypeNullable()));
			methodArgs.add(exprToCheck.copy());
			methodArgs.add(nf.ClassLit(p, ((X10CastInfo) checkingNode)
					.getTypeNode()));

			Call checkCastCall = nf.Call(p, this.getRuntimeCheckingClassName()
					.toReceiver(),
					this.runtimeConstrainedCheckingMethodName().name,
					methodArgs);

			return checkCastCall;
		}

		protected Expr buildPrimitiveCheckingMethodCall(Expr constraintArray,
				Expr exprToCheck) throws SemanticException {
			// must build an argument list of expression
			List methodArgs = new ArrayList();
			methodArgs.add(constraintArray);
			methodArgs.add(exprToCheck.copy());

			// receiver should be x10.lang.RuntimeCastChecker class
			Call checkCastCall = nf.Call(p, this.getRuntimeCheckingClassName()
					.toReceiver(), this
					.primitiveConstrainedCastCheckerMethodName().name,
					methodArgs);

			return checkCastCall;
		}
		
		
		private class ConstraintBuilder {
			private Constraint declaredConstraint;
			public ConstraintBuilder(Constraint declaredConstraint) {
				this.declaredConstraint = declaredConstraint;
			}
			
			public List visit(DepParameterExpr expr) throws SemanticException {
				Expr constraintExpr = expr.condition();
				
				X10Binary b = (X10Binary) ((constraintExpr instanceof X10Binary) ? constraintExpr :
						makeBinary(constraintExpr));
				
				return visit(b);
			}
			
			private List visit(X10Binary binary) throws SemanticException {
				List result = new LinkedList();
				if (binary.operator() == X10Binary.EQ) {
					result.addAll(this.visitEqual(binary));
				}
				if (binary.operator() == X10Binary.COND_AND) {
					result.addAll(this.visitAnd(binary));
				}
				return result;
			}

			private List visit(Expr constraintExpr) throws SemanticException {
				if (constraintExpr instanceof X10Binary)
					return visit((X10Binary) constraintExpr);
				
				if(constraintExpr instanceof Field) {
					return visit((Field) constraintExpr);
				}

				if(constraintExpr instanceof Local) {
					return visit((Local) constraintExpr);
				}

				if(constraintExpr instanceof Lit) {
					return visit((Lit) constraintExpr);
				}

				if(constraintExpr instanceof X10Special) {
					return visit((X10Special) constraintExpr);
				}
				// default behavior, (no visit method has been found for constraintExpr)
				throw new SemanticException("Unhandled expression of type " + constraintExpr.getClass());			
			}

			private boolean isBinary(Expr expr) {
				return expr instanceof X10Binary;
			}
			
			private List visitAnd(X10Binary binary) throws SemanticException {
				X10Binary left, right;
				List constraints = new LinkedList();

				left = (X10Binary) (isBinary(binary.left()) ? binary.left() : makeBinary(binary.left())); 
				constraints.addAll(visitLeft(left));
				
				right = (X10Binary) (isBinary(binary.right()) ? binary.right() : makeBinary(binary.right())); 
				constraints.addAll(visitRight(right));
				
				return constraints;			
			}
			
			private X10Binary makeBinary(Field f) throws SemanticException {

				Expr constraintRightValue = 
					this.constraintToExpr(declaredConstraint.find(f.name()),f.position());
				
				return (X10Binary) ((X10NodeFactory)nf).Binary(f.position(),f, X10Binary.EQ, constraintRightValue);
			}
			
			private X10Binary makeBinary(Expr expr) throws SemanticException {
				if (expr instanceof Field) {
					return this.makeBinary((Field) expr);
				}
				 throw new SemanticException("Can't convert expr of type " + expr.type() + " to binary expression");
			}
			
			private List visitEqual(X10Binary binary) throws SemanticException {
				LinkedList res = new LinkedList();
				LinkedList constructorArgs = new LinkedList();
				constructorArgs.addAll(visitLeft(binary.left()));
				constructorArgs.addAll(visitRight(binary.right()));
				// flatten
				Expr newConstraint = checkExpression(nf.New(binary.position(), 
						X10Caster.MethodChecking.this.getRuntimeConstraintClassName().toType(),
						constructorArgs));
				res.add(newConstraint);
				return res;		
			}
			
			
			private List visitRight(Expr constraintExpr) throws SemanticException {
				// does not matter if we are on the right or left side of a binary expression
				if (constraintExpr instanceof Field) {
					return this.visitRight((Field) constraintExpr);
				}
				return this.visit(constraintExpr);			
			}

			private List visitLeft(Expr constraintExpr) throws SemanticException {
				// does not matter if we are on the right or left side of a binary expression
				if (constraintExpr instanceof Field) {
					return this.visitLeft((Field) constraintExpr);
				}
				return this.visit(constraintExpr);			
			}
			
			private List visitRight(Field constraintExpr) throws SemanticException {
				List constructorArgs = new LinkedList();
				constructorArgs.add(visitField(constraintExpr));
				constructorArgs.add(nf.BooleanLit(constraintExpr.position(), true));
				return constructorArgs;
			}

			private List visitLeft(Field constraintExpr) throws SemanticException {
				List constructorArgs = new LinkedList();
				constructorArgs.add(visitField(constraintExpr));
				return constructorArgs;			
			}
			
			private Expr visitField(Field field) throws SemanticException {
				if ((field.target() instanceof X10Special)
						&& (((X10Special) field.target()).kind() == X10Special.SELF)) {
					return nf.StringLit(field.position(), field.name());
					// add a tag if the field is a right value the
				} else if ((field.target() instanceof X10Special)
						&& (((X10Special) field.target()).kind() == X10Special.THIS)) {
					return field;
				} else {
					throw new SemanticException("Unknown constraint expression");
				}
			}
			
			private List visit(Local local) throws SemanticException {
				List res = new LinkedList();
				res.add(local);
				return res;
			}
			
			private List visit(Lit lit) throws SemanticException {
				List res = new LinkedList();
				res.add(lit);
				return res;
			}

			private List visit(X10Special special) throws SemanticException {
				List res = new LinkedList();
				res.add(nf.StringLit(special.position(),special.toString()));
				return res;
			}
			
			/**
			 * Method used to transform simple constraint such as litteral.
			 * This code is needed when constraint are declared using shortcuts.
			 * For example some array's properties like rect, can be declared simply
			 * using 'rect' in the clause and not the full expression 'rect==true'.
			 * As we do not have access to the expression representing rect value, we need
			 * to creates one.
			 * @param term The term to convert to an expression
			 * @param p Expression position
			 * @return
			 * @throws SemanticException
			 */
			private Expr constraintToExpr(C_Term term, Position p) throws SemanticException {
				Expr res = null;

				// LITTERALS
				if (term instanceof C_Lit) {
					C_Lit lit = (C_Lit) term;
					if (lit.type().isInt()) {
						return nf.IntLit(p, IntLit.INT, ((Number) lit.val())
								.intValue());
					}
					if (lit.type().isLong()) {
						return nf.IntLit(p, IntLit.LONG, ((Number) lit.val())
								.longValue());
					}
					if (lit.type().isDouble()) {
						return nf.FloatLit(p, FloatLit.DOUBLE, ((Number) lit.val())
								.doubleValue());
					}
					if (lit.type().isFloat()) {
						return nf.FloatLit(p, FloatLit.FLOAT, ((Number) lit.val())
								.floatValue());
					}
					if (lit.type().isBoolean()) {
						return nf.BooleanLit(p, ((Boolean) lit.val())
								.booleanValue());
					}
				}

				throw new SemanticException("Unsupported runtime constraint "
						+ term);
			}
		}
	}

	private class MethodInstanceOfChecking extends MethodChecking {
		public MethodInstanceOfChecking(NodeFactory nf, TypeSystem ts,
				TypeBuilder tb, AmbiguityRemover ar, TypeChecker tc, Position p) {
			super(nf, ts, tb, ar, tc, p);
		}

		protected Expr finalizeRuntimeCheckingExpr(Expr castNode,
				Expr runtimeCheckingExpr) throws SemanticException {
			return runtimeCheckingExpr;
		}

		@Override
		protected Name runtimeConstrainedCheckingMethodName() {
			Name castChecker = new Name(nf, ts, p, "isInstanceOf");
			return castChecker;
		}

		@Override
		protected Name primitiveConstrainedCastCheckerMethodName() throws SemanticException {
			throw new SemanticException("Compiler internal error, instanceof must be checked against a reference from a reference");
		}

		@Override
		protected Constraint getConstraintsToCheck(Expr checkingNode) {
			X10Type toType = (X10Type) ((Instanceof) checkingNode)
					.compareType().type();
			return (toType instanceof NullableType) ? ((NullableType) toType)
					.base().depClause() : toType.depClause();
		}

		@Override
		protected Name runtimeCheckingToNonNullableMethodName() throws SemanticException {
			throw new SemanticException("Compiler error: Runtime Checking to non nullable is " +
					"not implemented, and should be the regular java instanceof code");
		}
	}

	protected class MethodCastChecking extends MethodChecking {

		public MethodCastChecking(NodeFactory nf, TypeSystem ts,
				TypeBuilder tb, AmbiguityRemover ar, TypeChecker tc, Position p) {
			super(nf, ts, tb, ar, tc, p);
		}

		protected Expr finalizeRuntimeCheckingExpr(Expr castNode,
				Expr runtimeCheckingExpr) throws SemanticException {

			// surround method call checking with the targeted cast.
			Cast newCast = nf.Cast(p, ((X10CastInfo) castNode).getTypeNode(),
					runtimeCheckingExpr);

			// Building types, removing ambiguities and type checking all !
			newCast = (Cast) checkExpression(newCast);
			return newCast;
		}
		
		@Override
		protected Name runtimeCheckingToNonNullableMethodName() throws SemanticException {
			Name castChecker = new Name(nf, ts, p, "checkCastToNonNullable");
			return castChecker;
		}

		@Override
		protected Name runtimeConstrainedCheckingMethodName() {
			Name castChecker = new Name(nf, ts, p, "checkCast");
			return castChecker;
		}

		@Override
		protected Name primitiveConstrainedCastCheckerMethodName() {
			Name castChecker = new Name(nf, ts, p, "checkPrimitiveType");
			return castChecker;
		}

		@Override
		protected Constraint getConstraintsToCheck(Expr checkingNode) {
			X10Type toType = (X10Type) checkingNode.type();
			return (toType instanceof NullableType) ? ((NullableType) toType)
					.base().depClause() : toType.depClause();
		}
	}
	
}
