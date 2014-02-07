/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Assign.Operator;
import polyglot.ast.Receiver;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;

import polyglot.types.Name;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.TypeSystem_c.MethodMatcher;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.ast.X10Binary_c;
import x10.ast.X10New_c;
import x10.ast.X10New_c.MatcherMaker;
import x10.ast.X10ProcedureCall;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;
import x10.errors.Warnings;
import x10.errors.Errors.CannotAssign;
import x10.errors.Errors.MethodOrStaticConstructorNotFound;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10LocalDef;
import polyglot.types.Context;

import x10.types.X10MemberDef;
import x10.types.MethodInstance;
import x10.types.X10ProcedureInstance;
import polyglot.types.TypeSystem;
import polyglot.types.NoMemberException;

import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CLocal;

import x10.types.matcher.Subst;
import x10.visit.X10TypeChecker;
import static polyglot.ast.Assign.*;

/**
 * A set of static methods used by AST nodes to check types.
 * @author vj
 *
 */
public class Checker {
	
	public static void checkOfferType(Position pos, 
			X10ProcedureInstance<? extends ProcedureDef> pi,ContextVisitor tc) throws SemanticException {
		Context cxt = (Context) tc.context();
		Type offerType = (Type) Types.get(pi.offerType());
		Type type = cxt.collectingFinishType();
		if (offerType != null) {
			if (type == null) 
				throw new Errors.CannotCallCodeThatOffers(pi, pos);
			if (! tc.typeSystem().isSubtype(offerType, type, cxt)) 
				throw new Errors.OfferTypeMismatch(offerType, type, pos);
		} else {
        }
		
	}

	public static Node typeCheckAssign(Assign_c a, ContextVisitor tc) {
	    Assign n = a;
	    
	    n = (Assign) a.typeCheckLeft(tc);

	    TypeSystem ts = tc.typeSystem();
	    Type t = n.leftType();

	    if (t == null)
	        t = ts.unknownType(n.position());

	    Expr right = n.right();
	    Assign.Operator op = n.operator();

	    Type s = right.type();

	    if (op == ASSIGN) {
	        Expr e = Converter.attemptCoercion(tc, right, t);
	        if (e != null) {
	            n = n.right(e);
	        } else {
	            // Don't try to extract the LHS expression, this is called by X10FieldAssign_c as well.
	            Errors.issue(tc.job(), Errors.CannotAssign.make(right, t, tc, n.position()));
	        }
	    }

	    if (op == ADD_ASSIGN || op == Assign.SUB_ASSIGN || op == Assign.MUL_ASSIGN ||
	        op == DIV_ASSIGN || op == MOD_ASSIGN || op == BIT_AND_ASSIGN || op == BIT_OR_ASSIGN ||
	        op == BIT_XOR_ASSIGN || op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN)
	    {
	        Binary.Operator bop = op.binaryOperator();
	        NodeFactory nf = tc.nodeFactory();
	        Binary bin = (Binary) nf.Binary(n.position(), n.left(), bop, right);
	        Call c = X10Binary_c.desugarBinaryOp(bin, tc);
	        if (c != null) {
	            MethodInstance mi = (MethodInstance) c.methodInstance();
                Warnings.checkErrorAndGuard(tc,mi,n);
	            t = c.type();
	        } else {
	            Errors.issue(tc.job(), Errors.CannotAssign.make(right, t, tc, n.position()));
	        }
	    }
	    return n.type(t);
	}
	
	/*public static void checkVariancesOfType(Position pos, Type t, ParameterType.Variance requiredVariance, 
	        String desc, Map<Name,ParameterType.Variance> vars, ContextVisitor tc) throws SemanticException {
	    if (t instanceof ParameterType) {
	        ParameterType pt = (ParameterType) t;
	        X10ClassDef cd = (X10ClassDef) tc.context().currentClassDef();
	        if (pt.def() != cd)
	            return;
	        ParameterType.Variance actualVariance = vars.get(pt.name());
	        if (actualVariance == null)
	            return;
	        switch (actualVariance) {
	        case INVARIANT:
	            break;
	        case COVARIANT:
	            switch (requiredVariance) {
	            case INVARIANT:
	                throw new SemanticException("Cannot use covariant parameter " + pt + " " + desc + "; must be invariant.", pos);
	            case COVARIANT:
	                break;
	            case CONTRAVARIANT:
	                throw new SemanticException("Cannot use covariant parameter " + pt + " " + desc + "; must be contravariant or invariant.", pos);
	            }
	            break;
	        case CONTRAVARIANT:
	            switch (requiredVariance) {
	            case INVARIANT:
	                throw new SemanticException("Cannot use contravariant parameter " + pt + " " + desc + "; must be invariant.", pos);
	            case COVARIANT:
	                throw new SemanticException("Cannot use contravariant parameter " + pt + " " + desc + "; must be covariant or invariant.", pos);
	            case CONTRAVARIANT:
	                break;
	            }
	            break;
	        }
	    }
	    if (t instanceof MacroType) {
	        MacroType mt = (MacroType) t;
	        checkVariancesOfType(pos, mt.definedType(), requiredVariance, desc, vars, tc);
	    }
	    if (t instanceof X10ClassType) {
	        X10ClassType ct = (X10ClassType) t;
	        X10ClassDef def = ct.x10Def();
	        if (ct.typeArguments() == null)
	            return;
	        for (int i = 0; i < ct.typeArguments().size(); i++) {
	            Type at = ct.typeArguments().get(i);
	            ParameterType pt = def.typeParameters().get(i);
	            ParameterType.Variance v = def.variances().get(i);
	            ParameterType.Variance newVariance;

	            switch (v) {
	            case INVARIANT:
	                checkVariancesOfType(pos, at, requiredVariance, desc, vars, tc);
	                break;
	            case COVARIANT:
	                checkVariancesOfType(pos, at, requiredVariance, desc, vars, tc);
	                break;
	            case CONTRAVARIANT:
	                switch (requiredVariance) {
	                case INVARIANT:
	                    checkVariancesOfType(pos, at, requiredVariance, desc, vars, tc);
	                    break;
	                case COVARIANT:
	                    checkVariancesOfType(pos, at, ParameterType.Variance.CONTRAVARIANT, desc, vars, tc);
	                    break;
	                case CONTRAVARIANT:
	                    checkVariancesOfType(pos, at, ParameterType.Variance.COVARIANT, desc, vars, tc);
	                    break;
	                }
	                break;
	            }
	        }
	    }
	    if (t instanceof ConstrainedType) {
	        ConstrainedType ct = (ConstrainedType) t;
	        Type at = Types.get(ct.baseType());
	        checkVariancesOfType(pos, at, requiredVariance, desc, vars, tc);
	    }
	}
*/
	/**
	 * Substitute the XTerm obtained by translating target (and an EQV if there is 
	 * no such term) for fi.thisVar() in t.
	 */
	public static Type rightType(Type t, X10MemberDef fi, Receiver target, Context c)  {
		//CConstraint x = Types.xclause(t);
		// Do this even if x.valid()
		if (fi.thisVar()==null || (! (target instanceof Expr)))
			return t;
		XVar receiver = null;

		TypeSystem ts = (TypeSystem) t.typeSystem();
		try {
			XTerm r=ts.xtypeTranslator().translate(ConstraintManager.getConstraintSystem().makeCConstraint(), target,  c);
			if (r instanceof XVar) {
				receiver = (XVar) r;
			}
		} catch (IllegalConstraint z) {
		}
		if (receiver == null)
			receiver = ConstraintManager.getConstraintSystem().makeEQV();
		try {
			t = Subst.subst(t, 
					(new XVar[] { receiver }), 
					(new XVar[] { fi.thisVar() }), 
					new Type[] { }, 
					new ParameterType[] { });
		} catch (SemanticException e) {
			throw new InternalCompilerError("Unexpected error while computing field type", e);
		}

		return t;
	}

    /**
     * Called from within XField_c and X10Call_c to return the adjusted
     * type for a call which is known to be a property call. The type is adjusted
     * with the contents of the property call.
     * @param type -- the current return type of the call 
     * @param t  -- the call
     * @param c -- the current context
     * @return type, adjusted with constraints clauses based on the body of the call
     * @throws IllegalConstraint
     */
	public static Type expandCall(Type type, Call t,  Context c) throws IllegalConstraint {
		CConstraint cs = ConstraintManager.getConstraintSystem().makeCConstraint();
		XTypeTranslator xt = ((TypeSystem) type.typeSystem()).xtypeTranslator();
		Receiver target = t.target();
		XTerm body = xt.translate(cs, t, c);
		CConstraint x = Types.xclause(type);
		X10MemberDef fi = (X10MemberDef) t.methodInstance().def();
		if (x == null || fi.thisVar() == null || !(target instanceof Expr))
			return type;
	
		x = x.copy();
		try {
			XVar receiver = Types.selfVarBinding(target.type());
			XVar root = null;
			if (receiver == null) {
				receiver = root = ConstraintManager.getConstraintSystem().makeUQV();
			}
			// Need to add the target's constraints in here because the target may not
			// be a variable. hence the type information wont be in the context.
			CConstraint ttc = Types.xclause(target.type());
			ttc = ttc == null ? ConstraintManager.getConstraintSystem().makeCConstraint() : ttc.copy();
			ttc = ttc.instantiateSelf(receiver);
			if (! Types.contextKnowsType(target))
				x.addIn(ttc);
			if (body != null)
				x.addSelfBinding(body);
			x=x.substitute(receiver, fi.thisVar());
			if (root != null) {
				x = x.project(root);
			}
			type = Types.addConstraint(Types.baseType(type), x);
		} catch (XFailure z) {
			Types.setInconsistent(type);
		}
		return type;
	}
	public static Type fieldRightType(Type type, X10MemberDef fi, Receiver target, Context c) {
		CConstraint x = Types.xclause(type);
		if (x == null || fi.thisVar() == null || !(target instanceof Expr))
			return type;
		// Need to add the target's constraints in here because the target may not
		// be a variable. hence the type information wont be in the context.
		CConstraint xc = Types.xclause(target.type());
		if (xc == null || xc.valid())
			return type;
		//xc = xc.copy();
		x = x.copy();
		try {
			XVar receiver = Types.selfVarBinding(target.type());
			XVar root = null;
			if (receiver == null) {
				receiver = root = ConstraintManager.getConstraintSystem().makeUQV();
			}
			xc = xc.instantiateSelf(receiver);
			if (! Types.contextKnowsType(target))
				x.addIn(xc);
			x=x.substitute(receiver, fi.thisVar());
			if (root != null) {
				x = x.project(root);
			}
			type = Types.addConstraint(Types.baseType(type), x);
		} catch (XFailure z) {
			Types.setInconsistent(type);
		}
		return type;
	}

	/**
	 * Find a method on the given targetType with the given name and list of args. If you cannot find
	 * one, create and return a fake method instance, recording the reason you could not find one.
	 * To facilitate further processing, if all the methods that you found have the same return type,
	 * record that return type for the fake method instance you are creating.
	 * @param tc
	 * @param targetType
	 * @param name
	 * @param typeArgs
	 * @param actualTypes
	 * @return
	 */
	public static MethodInstance findAppropriateMethod(ContextVisitor tc, Type targetType,
	        Name name, List<Type> typeArgs, List<Type> actualTypes)
	{
	    MethodInstance mi;
	    TypeSystem xts =  tc.typeSystem();
	    Context context = tc.context();
	    boolean haveUnknown = xts.hasUnknown(targetType);
	    for (Type t : actualTypes) {
	        if (xts.hasUnknown(t)) haveUnknown = true;
	    }
	    SemanticException error = null;
	    if (!haveUnknown) {
	        try {
	            return xts.findMethod(targetType, xts.MethodMatcher(targetType, name, typeArgs, actualTypes, context));
	        } catch (SemanticException e) {
	            error = e;
	        }
	    }
	    // If not returned yet, fake the method instance.
	    Collection<MethodInstance> mis = null;
	    try {
	        mis = xts.findMethods(targetType, xts.MethodMatcher(targetType, name, typeArgs, actualTypes, context));
	    } catch (SemanticException e) {
	        if (error == null) error = e;
	    }
	    // See if all matches have the same return type, and save that to avoid losing information.
	    Type rt = null;
	    if (mis != null) {
	        for (MethodInstance xmi : mis) {
	            if (rt == null) {
	                rt = xmi.returnType();
	            } else if (!xts.typeEquals(rt, xmi.returnType(), context)) {
	                if (xts.typeBaseEquals(rt, xmi.returnType(), context)) {
	                    rt = Types.baseType(rt);
	                } else {
	                    rt = null;
	                    break;
	                }
	            }
	        }
	    }
	    if (haveUnknown)
	        error = new SemanticException(); // null message
	    if (!targetType.isClass()) {
	        QName tName = targetType.fullName();
	        if (tName == null) {
	            tName = QName.make(null, targetType.toString());
	        }
	        targetType = xts.createFakeClass(tName, new SemanticException("Target type is not a class: "+targetType));
	    }
	    mi = xts.createFakeMethod(targetType.toClass(), Flags.PUBLIC, name, typeArgs, actualTypes, error);
	    if (rt == null) rt = mi.returnType();
	    rt = PlaceChecker.AddIsHereClause(rt, context);
	    mi = mi.returnType(rt);
	    return mi;
	}

	/**
	 * Find a match while trying implicit conversions.
	 * @param n
	 * @param tc
	 * @param targetType
	 * @param name
	 * @param typeArgs
	 * @param argTypes
	 * @return
	 * @throws SemanticException
	 */
	public static Pair<MethodInstance,List<Expr>> tryImplicitConversions(X10ProcedureCall n, ContextVisitor tc,
	        Type targetType, final Name name, List<Type> typeArgs, List<Type> argTypes) throws SemanticException {
	    final TypeSystem ts = (TypeSystem) tc.typeSystem();
	    final Context context = tc.context();
	
	    List<MethodInstance> methods = ts.findAcceptableMethods(targetType,
	            ts.MethodMatcher(targetType, name, typeArgs, argTypes, context,true));
	
	    Pair<MethodInstance,List<Expr>> p = Converter.<MethodDef,MethodInstance>tryImplicitConversions(n, tc,
	            targetType, methods, new X10New_c.MatcherMaker<MethodInstance>() {
	        public Matcher<MethodInstance> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes) {
	            return ts.MethodMatcher(ct, name, typeArgs, argTypes, context);
	        }
	    });
	
	    return p;
	}

	/**
	 * Looks up a method with given name and argument types.
	 */
	public static Pair<MethodInstance,List<Expr>> findMethod(ContextVisitor tc, X10ProcedureCall n,
	        Type targetType, Name name, List<Type> typeArgs, List<Type> actualTypes) {
	    MethodInstance mi;
	    TypeSystem xts =  tc.typeSystem();
	    Context context = (Context) tc.context();
	    boolean haveUnknown = xts.hasUnknown(targetType);
	    for (Type t : actualTypes) {
	        if (xts.hasUnknown(t)) haveUnknown = true;
	    }
	    SemanticException error = null;
	    try {
	        return findMethod(tc, context, n, targetType, name, typeArgs, actualTypes, context.inStaticContext());
	    } catch (SemanticException e) {
	        error = e;
	    }
	    // If not returned yet, fake the method instance.
	    Collection<MethodInstance> mis = null;
	    try {
	        mis = Checker.findMethods(tc, targetType, name, typeArgs, actualTypes);
	    } catch (SemanticException e) {
	        if (error == null) error = e;
	    }
	    // See if all matches have the same return type, and save that to avoid losing information.
	    Type rt = null;
	    if (mis != null) {
	        for (MethodInstance xmi : mis) {
	            if (rt == null) {
	                rt = xmi.returnType();
	            } else if (!xts.typeEquals(rt, xmi.returnType(), context)) {
	                if (xts.typeBaseEquals(rt, xmi.returnType(), context)) {
	                    rt = Types.baseType(rt);
	                } else {
	                    rt = null;
	                    break;
	                }
	            }
	        }
	    }
	    if (targetType == null)
	        targetType = context.currentClass();
	    if (haveUnknown)
	        error = new SemanticException(); // null message
	    if (!targetType.isClass()) {
	        QName tName = targetType.fullName(); 
	        if (tName == null) {
	        	tName = QName.make(null, targetType.toString());
	        }
	        targetType = xts.createFakeClass(tName, new SemanticException("Target type is not a class: "+targetType));
	    }
	    mi = xts.createFakeMethod(targetType.toClass(), Flags.PUBLIC.Static(), name, typeArgs, actualTypes, error);
	    if (rt != null) mi = mi.returnType(rt);
	    return new Pair<MethodInstance, List<Expr>>(mi, n.arguments());
	}

	private static Pair<MethodInstance,List<Expr>> findMethod(ContextVisitor tc, Context xc,
	        X10ProcedureCall n, Type targetType, Name name, List<Type> typeArgs,
			List<Type> argTypes, boolean requireStatic) throws SemanticException {
	
	    MethodInstance mi = null;
	    TypeSystem xts = (TypeSystem) tc.typeSystem();
	    if (targetType != null) {
	        mi = xts.findMethod(targetType, xts.MethodMatcher(targetType, name, typeArgs, argTypes, xc));
	        return new Pair<MethodInstance, List<Expr>>(mi, n.arguments());
	    }
	    if (xc.currentDepType() != null)
	        xc = (Context) xc.pop();
	    ClassType currentClass = xc.currentClass();
	    if (currentClass != null && xts.hasMethodNamed(currentClass, name)) {
	        // Override to change the type from C to C{self==this}.
	        Type t = currentClass;
	        XVar thisVar = null;
	        if (XTypeTranslator.THIS_VAR) {
	            CodeDef cd = xc.currentCode();
	            if (cd instanceof X10MemberDef) {
	                thisVar = ((X10MemberDef) cd).thisVar();
	            }
	        }
	        else {
	            //thisVar = xts.xtypeTranslator().transThis(currentClass);
	            thisVar = xts.xtypeTranslator().translateThisWithoutTypeConstraint();
	        }
	
	        if (thisVar != null)
	            t = Types.setSelfVar(t, thisVar);
	
	        // Found a class that has a method of the right name.
	        // Now need to check if the method is of the correct type.
	
	        // First try to find the method without implicit conversions.
	        try {
	            mi = xts.findMethod(t, xts.MethodMatcher(t, name, typeArgs, argTypes, xc));
	            if (requireStatic && !mi.flags().isStatic()) {
	                mi = mi.error(new Errors.CannotAccessNonStaticFromStaticContext(mi, n.position()));
	            }
	            return new Pair<MethodInstance, List<Expr>>(mi, n.arguments());
	        }
	        catch (SemanticException e) {
	        	// There is an ambiguity. Throw, don't try to use implicit conversions.
	        	if (e instanceof Errors.MultipleMethodDefsMatch) {
					throw e;
				}
                // only if we didn't find any methods, try coercions.
                if (!(e instanceof NoMemberException)) {
                    throw e;
                }
	            // Now, try to find the method with implicit conversions, making them explicit.
	            try {
	                Pair<MethodInstance,List<Expr>> p = tryImplicitConversions(n, tc, t, name, typeArgs, argTypes);
	                mi = p.fst();
	                if (requireStatic && !mi.flags().isStatic()) {
	                    mi = mi.error(new Errors.CannotAccessNonStaticFromStaticContext(mi, n.position()));
	                    p = new Pair<MethodInstance,List<Expr>>(mi, p.snd());
	                }
	                return p;
	            }
	            catch (SemanticException e2) {
	                throw e;
	            }
	        }
	    }

	    while (xc.pop() != null && xc.pop().currentClass() == currentClass)
	        xc = (Context) xc.pop();
	    if (xc.pop() != null) {
	        return findMethod(tc, (Context) xc.pop(), n, targetType, name, typeArgs, argTypes, currentClass.flags().isStatic());
	    }
	
	    TypeSystem_c.MethodMatcher matcher = xts.MethodMatcher(targetType, name, typeArgs, argTypes, xc);
	    throw new Errors.MethodOrStaticConstructorNotFound(matcher, n.position());
	}

	public static Collection<MethodInstance> findMethods(ContextVisitor tc, Type targetType, Name name, List<Type> typeArgs,
	        List<Type> actualTypes) throws SemanticException {
	    TypeSystem xts = tc.typeSystem();
	    Context context = (Context) tc.context();
	    if (targetType == null) {
	        // TODO
	        return Collections.emptyList();
	    }
	    return xts.findMethods(targetType, xts.MethodMatcher(targetType, name, typeArgs, actualTypes, context));
	}
}
