/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Field_c;
import polyglot.ast.FloatLit;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Instanceof;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Lit_c;
import polyglot.ast.Local;
import polyglot.ast.Local_c;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.DepCast;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.ParExpr_c;
import polyglot.ext.x10.ast.X10Binary;
import polyglot.ext.x10.ast.X10CastInfo;
import polyglot.ext.x10.ast.X10DepCastInfo;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.ast.X10Unary_c;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10PrimitiveType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/**
 * Visitor that inserts boxing and unboxing code into the AST.
 */
public class X10Caster extends AscriptionVisitor {
	X10TypeSystem xts;
	boolean castCheckClassNotLoaded = true;
	private static String RUNTIME_CAST_CHECKER_CLASSNAME = "x10.runtime.RuntimeCastChecker";
	private static String RUNTIME_CAST_CHECKER_CONSTRAINT_CLASSNAME = "x10.runtime.RuntimeConstraint";
	
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
			((Type) ts.systemResolver().find(RUNTIME_CAST_CHECKER_CLASSNAME)).toClass();
			castCheckClassNotLoaded = false;
		}
		
		if (toType == null) {
			return e;
		}

		Position p = e.position();
		if ((e instanceof Cast) || (e instanceof Instanceof)) {
			TypeBuilder tb = new TypeBuilder(job, ts, nf);
			TypeChecker tc = new TypeChecker(job, ts, nf);
			tb = tb.pushClass(context().currentClassScope());
			tb = tb.pushCode(context().currentCode(), Globals.currentGoal());
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
				// dynamic cast is needed which means toType may 
				// have constraint we must check at runtime
				X10CastInfo castOrInstanceof = (X10CastInfo) e;
				AbstractRuntimeChecking mc;

				boolean codeIsInlinable = 
					X10CastHelper.isSideEffectFree(castOrInstanceof.expr());
				
				if (e instanceof DepCast) {
					if (codeIsInlinable) {
						mc = new InlineCastChecking(tb, tc, p);
					} else {						
						mc = new InnerClassCastChecking(tb, tc, p);
					}
				} else {
					if (codeIsInlinable) {
						mc = new InlineInstanceofChecking(tb, tc, p);
					} else {						
						mc = new InnerClassInstanceofChecking(tb, tc, p);
					}
				}

				// runtime checking expression generated
				Expr runtimeCheckingExpr = (Expr) mc.getRuntimeCheckingExpression((X10CastInfo)e);

				return runtimeCheckingExpr;

			} else {
				if (cast.notNullRequired() && (e instanceof Cast)) {
					// Here type cast is T <-- nullable<T>
					// Hence we don't want the regulat java cast (T) NullType) works. 
					AbstractRuntimeChecking mc = new InlineCastChecking(tb, tc, p);	
					return mc.checking.getNonNullableCheckingExpr(ret_notype);
				}
			}
		}

		return e;
	}
		 
	private abstract class AbstractRuntimeChecking {
		protected TypeBuilder tb;
		protected TypeChecker tc;
		protected Position p;
		protected Checking checking;
		
		protected static final String CHECK_CAST_MTH_NAME = "checkCast";
		protected static final String X10_RUNTIME_NESTED_CONSTRAINT = "x10.runtime.RuntimeCastChecker.DepTypeRuntimeChecking";


		public AbstractRuntimeChecking(TypeBuilder tb,
				TypeChecker tc, Position p, Checking checking) {
			this.tb = tb;
			this.tc = tc;
			this.p = p;
			this.checking = checking;
		}
		
		/**
		 * Build type, disambigaute and type check a node.
		 * @param n The node to check.
		 * @return A checked node.
		 */
		protected Node checkExpression(Node n) {
			return n.visit(tb).visit(tc);
		}

		protected TypeNode getRuntimeCheckingClassName() {
			return nf.TypeNodeFromQualifiedName(p, RUNTIME_CAST_CHECKER_CLASSNAME);
		}

		/**
		 * Extract constraint from a type, that could be embedded in a nullable.
		 * @param toTypeNode type node to extract constraint from.
		 * @return The dependent type constraint extrated from the type.
		 */
		protected Constraint getConstraintsToCheck(TypeNode toTypeNode) {
			X10Type toType = (X10Type) toTypeNode.type();
			return (toType instanceof NullableType) ? ((NullableType) toType)
					.base().depClause() : toType.depClause();
		}

		/**
		 * Generate some code to check a   
		 * @param castOrInstanceof
		 * @return
		 * @throws SemanticException
		 */
		public abstract Node getRuntimeCheckingExpression(X10CastInfo castOrInstanceof) throws SemanticException;
	}
	

	protected abstract class InnerClassChecking extends AbstractRuntimeChecking {
		private static final String OBJ_TO_CAST = "x10_generated_objToCast";
		protected ConstraintBuilder constraintBuilder;
		protected NullableCheckBuilder nullableCheckBuilder;

		public InnerClassChecking(TypeBuilder tb, TypeChecker tc, Position p, Checking checking) {
			super(tb, tc, p, checking);
			this.constraintBuilder = this.checking.getConstraintBuilder();
			this.nullableCheckBuilder = this.checking.getNullableCheckBuilder();
		}
		
		@Override
		public Node getRuntimeCheckingExpression(X10CastInfo castOrInstanceof) throws SemanticException {
			Node runtimeCheckingNode;

			// ***************************************************************
			// Variable initialization
			// ***************************************************************

			TypeNode checkingType;
			Expr exprToCheck;
			Expr incomingExpr = castOrInstanceof.expr();
			Expr objToCast = nf.Local(p,nf.Id(p,OBJ_TO_CAST));
			TypeNode destType = this.checking.getToTypeRootType(castOrInstanceof);
			Expr castedExpr = nf.Cast(p, destType, objToCast);			
			TypeNode checkingConstrainedType = this.checking.getToType(castOrInstanceof);
			
			boolean notNullRequired = castOrInstanceof.notNullRequired();
			boolean isToTypeNullable = castOrInstanceof.isToTypeNullable();

			boolean isBoxedCode = 
				((X10TypeSystem) ts).isBoxedType(this.checking.getToType(castOrInstanceof).type()); 

			if (isBoxedCode) {
				// this code handle either boxed integer runtime checking
				// this code is needed when expr to cast's type is a primitive nullable.
				X10Type incomingExprType = ((X10Type)incomingExpr.type()); 
				if (xts.isNullable(incomingExprType)) {
					NullableType nullType = (NullableType) incomingExprType;
					if (nullType.base().isPrimitive()) {
						nullType = nullType.base(Types.ref(xts.boxedType((X10PrimitiveType)nullType.base())));
						// we change incomingExpr type from nullable<Primitive> to nullable<BoxedPrimitive>
						incomingExpr =  incomingExpr.type(nullType);
					}
				}
				// constraint checking is done using the primitive type, not the boxed one
				checkingType = nf.CanonicalTypeNode(p, ((X10TypeSystem) ts).boxedTypeToPrimitiveType(destType.type()));

				// Expression to check against deptype constraint is the unboxed primitive.
				exprToCheck = nf.Call(p,castedExpr,nf.Id(p,((X10TypeSystem) ts).getGetterName(destType.type())));
			} else {
				// Constraint checking is done using cast's target type
				checkingType = destType;
				// Expression to check against deptype constraint is the casted expr
				exprToCheck = castedExpr;
			}
			
			// expr to return is method's parameter (objToCast) with the incoming expr's type.
			// Note: We need to set the type otherwise the newly created objToCast doesn't have one, 
			//       and we want to use it before typechecking occurs.
			Expr exprToReturn = objToCast.type(incomingExpr.type()); // this avoid passing extra parameters later
			
			// ***************************************************************
			// Generating the expression that checks dependent type constraint
			// ***************************************************************
			// exprToCheck has already been casted to cast's target type.
			Expr constraintCheckExpr = this.constraintBuilder.buildConstraint(
					((X10NodeFactory) nf).ParExpr(p,exprToCheck),
					this.getConstraintsToCheck(checkingConstrainedType),
					((X10DepCastInfo) castOrInstanceof).depParameterExpr());			

			// now that we have the deptype constraint checking, we may want 
			// additional checks as regard to nullable.

			Expr nullableCheckExpr;
			
			// First we check if cast's targeted type is primitive; really a primitive not even a Boxed type.
			// If so, we DO NOT want to check about nullable as there is no null value for a primitive type.
			if(destType.type().isPrimitive()) {
				notNullRequired = false;
				isToTypeNullable = false;
			}
			
			// ***************************************************************
			// Generating the expression that checks nullable properties
			// ***************************************************************
			// this could return the constraintCheckExpr unchanged if no additional checking is needed			
			nullableCheckExpr = 
				this.nullableCheckBuilder.buildNullableChecking(
						exprToReturn, // This will be casted to the right cast's target type
						notNullRequired, // check if expr to cast is not null
						isToTypeNullable, // if to type is nullable we allows null value
						constraintCheckExpr, // the deptype constraint checking expression, previously generated
						destType); // Cast's targeted type
			

			// Cast checking method is only composed of previously generated expression.
			// We return results of this expression evaluation.
			List<Stmt> statements = new LinkedList();
			statements.add(nf.Return(p, nullableCheckExpr));
			Block methodBody = nf.Block(p,statements);
			
			// ***************************************************************
			// Begining inner class code generation 
			// ***************************************************************
			
			// we have generated runtime cast checking method body, now we generate the method.			
			TypeNode paramType; 
			
			if(destType.type().isPrimitive()) {
				// If cast's target type is a primitive then, expression to cast is also.
				// This is because when a cast implies unboxing, it is systematically done 
				// before (in cast checking method's parameter, objToCast is the unboxing code)
				paramType = nf.CanonicalTypeNode(p,destType.type());
			} else {
				// Method's parameter is the actual expression to cast type
				paramType = nf.CanonicalTypeNode(p,incomingExpr.type());
			}
			
			// building cast checking method formal parameters
			List<Formal> paramFormalList = new LinkedList();
			Formal mthParamFormal = nf.Formal(p, Flags.FINAL, paramType, nf.Id(p, OBJ_TO_CAST));
			paramFormalList.add(mthParamFormal);

			// building cast checking method declaration (will be inside the inner class)
			// returnType may be either cast's toType or a simple boolean for instanceof.
			// method return type is not cast target type as if we are dealing with
			// an instanceof checking we have to return a boolean. 
			MethodDecl checkCastMethod =   
				nf.MethodDecl(p, Flags.PUBLIC, this.checking.getExprReturnType(castOrInstanceof), 
						nf.Id(p, CHECK_CAST_MTH_NAME), paramFormalList, Collections.EMPTY_LIST, methodBody);

			// This method is part of an inner class body.
			List<ClassMember> classBodyMembers = new LinkedList();
			classBodyMembers.add(checkCastMethod);
			
			// Getting inner class body responsible to perform cast checking
			ClassBody classBody = nf.ClassBody(p,classBodyMembers);			

			// Getting inner class interface name
			TypeNode anonClassName = nf.TypeNodeFromQualifiedName(p, X10_RUNTIME_NESTED_CONSTRAINT);		
			
			// Which is instanciated using a given Constructor.
			New newAnonClass = 
				nf.New(p, anonClassName, Collections.EMPTY_LIST, classBody);
			
			// Now we only have to call perviously generated method on the newly created
			// inner class

			Call checkingCall;
			if (isBoxedCode) {
				// this is a hack to get nullable<int> compliant with nullable<BoxedInteger>.
				incomingExpr = nf.Cast(p, destType, incomingExpr);
			}
			
			checkingCall = nf.Call(p, newAnonClass, nf.Id(p, CHECK_CAST_MTH_NAME), incomingExpr);

			// needed to set type builder's flag before visiting the anonymous class.
			tb = tb.pushCode(context().currentCode(), Globals.currentGoal()); // ###

			// we build types, disambiguate and typecheck.
			runtimeCheckingNode = 
				this.checkExpression(checkingCall);
			
			return runtimeCheckingNode;
		}
	}

	private abstract class Checking {
		protected TypeBuilder tb;
		protected TypeChecker tc;
		protected Position p;
		
		public Checking(TypeBuilder tb, TypeChecker tc, 
				Position p) {
			this.tb = tb;
			this.tc = tc;
			this.p = p;
		}

		public abstract TypeNode getExprReturnType(X10CastInfo castOrInstanceof);

		protected abstract Id runtimeCheckingToNonNullableMethodName() throws SemanticException;
		
		public abstract TypeNode getToType(X10CastInfo castOrInstanceof);

		public abstract Expr getExprToCast(X10CastInfo castOrInstanceof);

		protected abstract Expr finalizeRuntimeCheckingExpr(Expr castNode,
				Expr runtimeCheckingExpr) throws SemanticException;

		public abstract NullableCheckBuilder getNullableCheckBuilder();
		

		public TypeNode getToTypeRootType(X10CastInfo castOrInstanceof) {
			Type baseType = (((X10Type) castOrInstanceof.getTypeNode().type()).rootType());
			if (((X10TypeSystem) ts).isNullable(baseType)) {
				baseType = ((NullableType) baseType).base();
			}
			return nf.CanonicalTypeNode(p,baseType);	
		}
		
		protected Cast getAsCast(X10CastInfo castOrInstanceof) {
			return nf.Cast(p, getToType(castOrInstanceof), getExprToCast(castOrInstanceof));
		}

		protected Node checkExpression(Node n) {
			return n.visit(tb).visit(tc);
		}

		// This one is only used when there is no constraint checking involved
		public Expr getNonNullableCheckingExpr(Expr checkingNode) 
			throws SemanticException {
			List<Expr> methodArgs = new ArrayList<Expr>();
			Expr exprToCheck = ((X10CastInfo) checkingNode).expr();

			methodArgs.add((Expr) exprToCheck.copy());
			methodArgs.add(nf.ClassLit(p, ((X10CastInfo) checkingNode)
					.getTypeNode()));

			Call checkNullableCall = nf.Call(p, this
					.getRuntimeCheckingClassName(), 
					this.runtimeCheckingToNonNullableMethodName(), methodArgs);

			checkNullableCall = (Call) checkExpression(checkNullableCall);

			return this.finalizeRuntimeCheckingExpr(checkingNode,
					checkNullableCall);
		}

		protected TypeNode getRuntimeCheckingClassName() {
			return nf.TypeNodeFromQualifiedName(p, RUNTIME_CAST_CHECKER_CLASSNAME);
		}

		public ConstraintBuilder getConstraintBuilder() {
			return new ConstraintBuilder();
		}
	}
	
	private class InstanceofChecking extends Checking{

		public InstanceofChecking(TypeBuilder tb, TypeChecker tc, 
				Position p) {
			super(tb,tc,p);
		}

		public TypeNode getToType(X10CastInfo castOrInstanceof) {
			return ((Instanceof) castOrInstanceof).compareType();
		}

		public Expr getExprToCast(X10CastInfo castOrInstanceof) {
			// we return only expr from instanceof node 'expr instanceof T'
			return castOrInstanceof.expr();			
		}
		
		protected Expr finalizeRuntimeCheckingExpr(Expr castNode,
				Expr runtimeCheckingExpr) throws SemanticException {
			return runtimeCheckingExpr;
		}

		@Override
		protected Id runtimeCheckingToNonNullableMethodName() throws SemanticException {
			throw new SemanticException("Compiler error: Runtime Checking to non nullable is " +
					"not implemented, and should be the regular java instanceof code");
		}

		@Override
		public NullableCheckBuilder getNullableCheckBuilder() {
			// TODO point to the actual Instanceof NullableCheckBuilder
			return new NullableCheckInstanceofBuilder(tb,tc,p);
		}
				
		@Override
		public TypeNode getExprReturnType(X10CastInfo castOrInstanceof) {
			// TODO Auto-generated method stub
			return nf.CanonicalTypeNode(p,ts.Boolean());
		}
	}

	private class CastChecking extends Checking {

		public CastChecking(TypeBuilder tb, TypeChecker tc, 
				Position p) {
			super(tb,tc,p);
		}

		@Override
		public TypeNode getToType(X10CastInfo castOrInstanceof) {
			return castOrInstanceof.getTypeNode();
		}

		@Override
		public Expr getExprToCast(X10CastInfo castOrInstanceof) {
			return castOrInstanceof.expr();
		}
		
		@Override
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
		protected Id runtimeCheckingToNonNullableMethodName() throws SemanticException {
                        return nf.Id(p, "checkCastToNonNullable");
		}

		@Override
		public NullableCheckBuilder getNullableCheckBuilder() {
			// TODO point to the actual Cast NullableCheckBuilder
			return new NullableCheckCastBuilder(tb,tc,p);
		}
		
		@Override
		public TypeNode getExprReturnType(X10CastInfo castOrInstanceof) {
			return this.getToTypeRootType(castOrInstanceof);
		}
	}

	private class InnerClassInstanceofChecking extends InnerClassChecking {
		public InnerClassInstanceofChecking(
				TypeBuilder tb, TypeChecker tc, Position p) {
			super(tb, tc, p, new InstanceofChecking(tb,tc,p));
		}
	}

	private class InnerClassCastChecking extends InnerClassChecking {

		public InnerClassCastChecking(TypeBuilder tb, TypeChecker tc, Position p) {
			super(tb, tc, p, new CastChecking(tb,tc,p));
		}
	}

	/**
	 * Abstract class responsible to generate all inlinable code. 
	 * It is parametrized by its subclass whether it should deal 
	 * with cast or instanceof expression.
	 * @author vcave
	 *
	 */
	protected abstract class InlineChecking extends AbstractRuntimeChecking {
		protected ConstraintBuilder constraintBuilder;
		protected NullableCheckBuilder nullableCheckBuilder;
		
		public InlineChecking(TypeBuilder tb, TypeChecker tc, Position p, Checking checking) {
			super(tb, tc, p, checking);
			
			this.constraintBuilder = this.checking.getConstraintBuilder();
			this.nullableCheckBuilder = this.checking.getNullableCheckBuilder();
		}

		public Node getRuntimeCheckingExpression(X10CastInfo castOrInstanceof) throws SemanticException {
			
		Node runtimeCheckingNode;

		/** Begin runtime cast checking code generation **/
		
		
		Expr exprToCheck;
		Expr objToCast = castOrInstanceof.expr();
		Expr incomingExpr = castOrInstanceof.expr();
		TypeNode destType = this.checking.getToTypeRootType(castOrInstanceof);
		Expr castedExpr = nf.Cast(p, destType, objToCast);
		TypeNode checkingConstrainedType = this.checking.getToType(castOrInstanceof);
		
		boolean notNullRequired = castOrInstanceof.notNullRequired();
		boolean isToTypeNullable = castOrInstanceof.isToTypeNullable();

		boolean isBoxedCode = 
			((X10TypeSystem) ts).isBoxedType(this.checking.getToType(castOrInstanceof).type()); 

		if (isBoxedCode) {
			// this code handle either boxed integer runtime checking
			// this code is needed when expr to cast's type is a primitive nullable.
			X10Type incomingExprType = ((X10Type)incomingExpr.type()); 
			if (xts.isNullable(incomingExprType)) {
				NullableType nullType = (NullableType) incomingExprType;
				if (nullType.base().isPrimitive()) {
					nullType = nullType.base(Types.ref(xts.boxedType((X10PrimitiveType)nullType.base())));
					// we change incomingExpr type from nullable<Primitive> to nullable<BoxedPrimitive>
					incomingExpr =  incomingExpr.type(nullType);
				}
			}
			// Expression to check against deptype constraint is the unboxed primitive.
			exprToCheck = nf.Call(p,castedExpr,nf.Id(p, ((X10TypeSystem) ts).getGetterName(destType.type())));
		} else {
			// Expression to check against deptype constraint is the casted expr
			exprToCheck = castedExpr;
		}
		
		// expr to return is method's parameter (objToCast) with the incoming expr's type.
		// Note: We need to set the type otherwise the newly created objToCast doesn't have one, 
		//       and we want to use it before typechecking occurs.
		Expr exprToReturn = 
			objToCast.type(incomingExpr.type()); // this avoid passing extra parameters later
		
		// ***************************************************************
		// Generating the expression that checks dependent type constraint
		// ***************************************************************
		// exprToCheck has already been casted to cast's target type.
		Expr constraintCheckExpr = this.constraintBuilder.buildConstraint(
				((X10NodeFactory) nf).ParExpr(p,exprToCheck),
				this.getConstraintsToCheck(checkingConstrainedType),
				((X10DepCastInfo) castOrInstanceof).depParameterExpr());			

		// now that we have the deptype constraint checking, we may want 
		// additional checks as regard to nullable.
		
		// First we check if cast's targeted type is primitive; really a primitive not even a Boxed type.
		// If so, we DO NOT want to check about nullable as there is no null value for a primitive type.
		if(destType.type().isPrimitive()) {
			notNullRequired = false;
			isToTypeNullable = false;
		}
		
		// ***************************************************************
		// Generating the expression that checks nullable properties
		// ***************************************************************
		// this could return the constraintCheckExpr unchanged if no additional checking is needed			
		Expr nullableCheckExpr = 
			this.nullableCheckBuilder.buildNullableChecking(
					exprToReturn, // This will be casted to the right cast's target type
					notNullRequired, // check if expr to cast is not null
					isToTypeNullable, // if to type is nullable we allows null value
					constraintCheckExpr, // the deptype constraint checking expression, previously generated
					destType); // Cast's targeted type
		
		// needed to set type builder's flag before visiting the anonymous class.
		tb = tb.pushCode(context().currentCode(), Globals.currentGoal());

		runtimeCheckingNode = 
			this.checkExpression(nullableCheckExpr);
		
		return runtimeCheckingNode;
	}
	} 

	/**
	 * This class handles runtime checking code generation
	 * for inlinable instanceof expression.
	 * @author vcave
	 *
	 */
	private class InlineInstanceofChecking extends InlineChecking {
		public InlineInstanceofChecking(
				TypeBuilder tb, TypeChecker tc, Position p) {
			super(tb, tc, p, new InstanceofChecking(tb,tc,p));
		}
	}

	/**
	 * This class handles runtime checking code generation
	 * for inlinable cast expression.
	 * @author vcave
	 *
	 */
	private class InlineCastChecking extends InlineChecking {
		public InlineCastChecking(TypeBuilder tb, TypeChecker tc, Position p) {
			super(tb, tc, p, new CastChecking(tb,tc,p));
		}
	}

	/**
	 * Root Class for all runtime checking code builders.
	 * @author vcave
	 *
	 */
	private class Builder {
		protected TypeBuilder tb;
		protected TypeChecker tc;
		protected Position p;

		public Builder(TypeBuilder tb,
				TypeChecker tc, Position p) {
			this.tb = tb;
			this.tc = tc;
			this.p = p;
		}	
	}
		
	/**
	 * NullableCheckCastBuilder is responsible to add all checks related to nullable
	 * before deptype constraint is checked. 
	 * @author vcave
	 *
	 */
	private abstract class NullableCheckBuilder extends Builder{
		
		public NullableCheckBuilder(TypeBuilder tb, TypeChecker tc, Position p) {
			super(tb, tc, p);
		}

		/**
		 * Produce code to 
		 * @param exprToCheck Expression to check
		 * @param mustBeNotNull If expr must be not null we generates code to check that.
		 * @param toTypeIsNullable If targeted type is nullable we generates code to be compliant will null instances.
		 * @param constraintExpr The deptype's constraint checking expression already generated.
		 * @param toType Cast's targeted type
		 * @return
		 */
		public abstract Expr buildNullableChecking(Expr exprToCheck, boolean mustBeNotNull, 
				boolean toTypeIsNullable, Expr constraintExpr, TypeNode toType);
		
		protected TypeNode getRuntimeCheckingClassName(Position p) {
			return nf.TypeNodeFromQualifiedName(p, RUNTIME_CAST_CHECKER_CLASSNAME);
		}
	}
	
	/**
	 * NullableCheckCastBuilder is responsible to add all checks related to nullable
	 * before deptype constraint is checked. Otherwise resulting in NullPointerException.
	 * Typicaly it includes, checking cast from a nullable to a non-nullable fails if
	 * the nullable is actually null. Also if cast's target type is a constrainted nullable
	 * it ensures a null instance pass the checking. 
	 * @author vcave
	 *
	 */
	private class NullableCheckCastBuilder extends NullableCheckBuilder{
		
		public NullableCheckCastBuilder(TypeBuilder tb, TypeChecker tc, Position p) {
			super(tb, tc, p);
		}
		
		@Override
		public Expr buildNullableChecking(Expr exprToReturn, boolean mustBeNotNull, 
				boolean toTypeIsNullable, Expr constraintExpr, TypeNode toType) {
			X10NodeFactory xnf = (X10NodeFactory) nf;
			Expr retExpr;
			constraintExpr = xnf.ParExpr(p, constraintExpr);
			Expr castExprToReturn = nf.Cast(p,toType,exprToReturn);
			
			retExpr = this.constraintCheckingExpr(castExprToReturn,constraintExpr, toType);
			// (constraintExpr ? object : RuntimeCastChecker.throwClassCastException(object, "Constraint not meet"))

			if (mustBeNotNull) {
				Expr nestedCondition = retExpr;
				// (constraintExpr ? object : RuntimeCastChecker.throwClassCastException(object, "Constraint not meet"))
				
				Expr firstCondition = xnf.ParExpr(p,
						nf.Binary(p, exprToReturn, Binary.NE, nf.Cast(p,xnf.Nullable(p,nf.CanonicalTypeNode(p,exprToReturn.type())),nf.NullLit(p)))); 
				Expr firstAlternative = nf.Cast(p, toType, this.throwClassCastExceptionExpr(p, exprToReturn, "Cannot cast a null instance to a non nullable type"));
				return retExpr = xnf.ParExpr(p, nf.Conditional(p, firstCondition, nestedCondition, firstAlternative));
				// (objToCast != null) ? nestedCondition : RuntimeCastChecker.throwClassCastException(object, "Target type is not nullable");
				
				// (objToCast != null) ? 
				//		(constraintExpr ? object : RuntimeCastChecker.throwClassCastException(object, "Constraint not meet"))
				//			: RuntimeCastChecker.throwClassCastException(object, "Target type is not nullable");
			}
			
			if (toTypeIsNullable) {
				Expr conditionLeft = xnf.ParExpr(p, nf.Binary(p, exprToReturn, Binary.EQ, 
						nf.Cast(p, xnf.Nullable(p,nf.CanonicalTypeNode(p,exprToReturn.type())), nf.NullLit(p))));
				Expr condition = xnf.ParExpr(p, nf.Binary(p,conditionLeft, Binary.COND_OR, constraintExpr)); 
				// ((objToCast == null) || constraintExpr)
				Expr alternative = xnf.ParExpr(p, nf.Cast(p, toType, this.throwClassCastExceptionExpr(p, exprToReturn, "Constraint not meet")));
				retExpr = nf.Conditional(p, condition, castExprToReturn, alternative);
				// ((objToCast == null) || constraint) ? object : throw new ClassCastException("Constraint not meet");
			}
			
			return retExpr = xnf.ParExpr(p, retExpr);
		}
		
		private Expr constraintCheckingExpr(Expr castExpr, Expr constraintExpr, TypeNode toType) {
			// regular checking
			Expr nestedConsequence = castExpr;
			Expr nestedAlternative = nf.Cast(p, toType, this.throwClassCastExceptionExpr(p, castExpr, "Constraint is not meet"));
			return nf.Conditional(p, constraintExpr, nestedConsequence, nestedAlternative);			
		}
		
		private Expr throwClassCastExceptionExpr(Position p, Expr exprToCast, String errorMsg) {
			return nf.Call(p, this.getRuntimeCheckingClassName(p), nf.Id(p, "throwClassCastException"), exprToCast, nf.StringLit(p, errorMsg));
		}		
	}
	
	private class NullableCheckInstanceofBuilder extends NullableCheckBuilder {
		
		public NullableCheckInstanceofBuilder(TypeBuilder tb, TypeChecker tc, Position p) {
			super(tb, tc, p);
		}

		@Override
		public Expr buildNullableChecking(Expr exprToReturn, boolean mustBeNotNull, boolean toTypeIsNullable, Expr constraintExpr, TypeNode toType) {
			Expr retExpr;
			X10NodeFactory xnf = (X10NodeFactory) nf; 
			Expr isAssignable = 
				xnf.ParExpr(p, nf.Call(p, nf.ClassLit(p, toType), nf.Id(p, "isAssignableFrom"), nf.Call(p,exprToReturn, nf.Id(p, "getClass"))));
			// toType.class.isAssignableFrom(o.getClass())
			
			retExpr = nf.Binary(p, isAssignable, Binary.COND_AND, constraintExpr);
			// (isAssignable) && (constraintCheck)
			
			// null is never an instance of anything
			if ((mustBeNotNull) || (toTypeIsNullable)) {
				// prepend not null check
				
				Expr isExprNotNull = xnf.ParExpr(p, nf.Binary(p,exprToReturn,Binary.NE, 
						nf.Cast(p, xnf.Nullable(p,nf.CanonicalTypeNode(p,exprToReturn.type())), nf.NullLit(p))));
				retExpr = nf.Binary(p, isExprNotNull, Binary.COND_AND, retExpr);
				//  (objToCast != null) && (isAssignable) && (constraintCheck)
			}

			// TODO Always performing the null checking should be better
			// TODO There should be always some weird case where a non-nullable could be null
			// TODO for example using uninitialized java array.
			retExpr = ((X10NodeFactory) nf).ParExpr(p, retExpr);
			// 	((objToCast != null) && (isAssignable) && (constraintCheck))
			// OR
			// 	((isAssignable) && (constraintCheck))
			
			return retExpr;
		}
	}

	/**
	 * Utility class to build from deptype's constraint an expression able 
	 * to check at runtime an certain instance is valid against a type.  
	 * @author vcave
	 *
	 */
	private class ConstraintBuilder {
		private Constraint declaredConstraint;
		private Expr self;

		public ConstraintBuilder() {			
		}

		/**
		 * Builds the constraint runtime checking expression. 
		 * Constraints have no representation at runtime. Therefore we must create one
		 * to perform constraint checking while casting. 
		 * @param self The expression used to translate 'self'.
		 * @param declaredConstraint The constraint declared
		 * @param expr The expression representing the constraint.
		 * @return A clause checking 'self' meet constraint requirements.
		 * @throws SemanticException
		 */
		public Expr buildConstraint(Expr self, Constraint declaredConstraint, DepParameterExpr expr) throws SemanticException {
			this.declaredConstraint = declaredConstraint;
			Expr constraintExpr = expr.condition();
			this.self = self;
			assert self != null;
			
			X10Binary b = (X10Binary) ((constraintExpr instanceof X10Binary) ? constraintExpr :
					makeBinary(constraintExpr));
			
			return visit(b);
		}

		/**
		 * Visit an expression.
		 * @param constraintExpr expression to visit.
		 * @return translated expression.
		 * @throws SemanticException
		 */
		private Expr visit(Expr constraintExpr) throws SemanticException {
			if (constraintExpr instanceof X10Binary)
				return visit((X10Binary) constraintExpr);
			
			if(constraintExpr instanceof Field) {
				return visit((Field) constraintExpr);
			}

			if ((constraintExpr instanceof Lit) || 
					(constraintExpr instanceof X10Unary_c) ||  
					(constraintExpr instanceof Local)){
				return visitWithoutChanging(constraintExpr);
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
		
		/**
		 * Handle special case such as shortcut properties like 'rect' or 'zeroBased'
		 * which does not have an explicit value as an expression but have one 
		 * as a constraint. Hence we get the value from the constraint and inject it to get
		 * a binary expression.
		 * @param f The no-value properties (i.e. a field).
		 * @return A binary expression with the actual property value.
		 * @throws SemanticException
		 */
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

		/**
		 * Visit a binary.
		 * @param binary expression to visit.
		 * @return translated expression.
		 * @throws SemanticException
		 */
		private Expr visit(X10Binary binary) throws SemanticException {
			binary = (X10Binary) binary.left(visit(binary.left()));
			binary = (X10Binary) binary.right(visit(binary.right()));
			
			// TODO: update me with all constraint operator
			// TODO: why don't we juste return ParEx for all ?
			
			if (binary.operator() == Binary.EQ) {
				return new ParExpr_c(binary.position(), binary);
			}

			return binary;		
		}
	
		
		/**
		 * Visit a local
		 * @param local expression to visit.
		 * @return translated expression.
		 * @throws SemanticException
		 */
		private Expr visitWithoutChanging(Expr expr) throws SemanticException {
			return expr;
		}

		/**
		 * Visit a field.
		 * Here we rewrite 'self' and properties referring to this
		 * The expression to use as a replacement for 'self' has been provided
		 * at ConstraintBuilder instanciation time.
		 * Also we rewrite calls to this, using the fully qualified class name.
		 * this.p -> CurrentClass.this.p
		 * @param field expression to visit
		 * @return translated expression.
		 * @throws SemanticException
		 */
		private Expr visit(Field field) throws SemanticException {
			if ((field.target() instanceof X10Special)
					&& (((X10Special) field.target()).kind() == X10Special.SELF)) {
				return nf.Call(field.position(),this.self, field.id());
				// add a tag if the field is a right value the
			} else if ((field.target() instanceof X10Special)
					&& (((X10Special) field.target()).kind() == X10Special.THIS)) {
				// Getting the fully qualified reference to 'this'
				// ex: this.p -> CurrentClass.this.p
				Special thisTarget = nf.This(field.position(), 
						nf.TypeNodeFromQualifiedName(field.position(),
								((X10ParsedClassType)field.fieldInstance().container()).name()));
				return nf.Field(field.position(), thisTarget, field.id());
			} else {
				return field;
			}
		}

		/**
		 * X10Special is translated to 'self'
		 * @param special expression to visit
		 * @return expression representing 'self'.
		 * @throws SemanticException
		 */
		private Expr visit(X10Special special) throws SemanticException {
			return this.self;
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
	
    /**
     * Regroup some method that can be used either by X10Cast_c 
     * or X10Instanceof_c to avoid duplication.
     * @author vcave
     */

	public static class X10CastHelper {
        /**
         * In order to sharpen cast code generated, we try to 
         * identify the true nature of the expression to cast
         * if the expression is surronded by parenthesis, we get the nested one. 
         * @param source Expression to analyze.
         * @return Source's expression nested expression enclosed between parenthesis or source.
         */
        public static Expr getNestedExpression(Expr source) {
    		if (source instanceof ParExpr_c) {
    			Expr nestedExpr = source;
    			while(nestedExpr instanceof ParExpr_c) {
    				nestedExpr = ((ParExpr_c) nestedExpr).expr();
    			}
    			
    			return nestedExpr;
    		}
    		return source;
        }
        
		/**
         * Checks whether the expression may produce side effect (rudimentary).
         * @param expression Expression to check.
         * @return true if expression is side effect free.
         */
    	public static boolean isSideEffectFree(Expr exprToCast) {
    		Expr nestedExpression = X10CastHelper.getNestedExpression(exprToCast);
    		if (nestedExpression instanceof Field_c) {
    			// resolve how the field is access
    			// this.a; meth().a, etc..
    			return isSideEffectFree((Expr) ((Field_c) nestedExpression).target());
    		}
    		if ((nestedExpression instanceof Local_c) || 
    				(nestedExpression instanceof Lit_c))
    			return true;
    		
    		return false;
    	}
	}
}
