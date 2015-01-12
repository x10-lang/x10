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

package x10.types.checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.Matcher;
import polyglot.types.MemberInstance;

import polyglot.types.Flags;
import polyglot.types.LazyRef_c;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.NoMemberException;
import polyglot.types.ObjectType;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.ContextVisitor;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.X10CompilerOptions;
import x10.ast.OperatorNames;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10Cast;
import x10.ast.X10Cast_c;
import x10.ast.X10New_c;
import x10.ast.X10ProcedureCall;
import x10.ast.X10New_c.MatcherMaker;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassType;
import x10.types.MethodInstance;
import x10.types.X10LocalDef;
import x10.types.X10LocalDef_c;
import x10.types.X10LocalInstance;
import x10.types.X10LocalInstance_c;
import polyglot.types.Context;
import x10.types.X10ParsedClassType_c;
import x10.types.X10ProcedureDef;
import x10.types.X10ProcedureInstance;
import polyglot.types.TypeSystem;

import x10.types.constants.ConstantValue;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CLocal;
import x10.types.constraints.CNativeRequirement;
import x10.types.constraints.TypeConstraint;

import x10.types.matcher.Subst;
import x10.util.Synthesizer;

/**
 * A set of static methods used to convert an AST representing an X10 expressions of a given type
 * into an AST representing an expressions of another type.
 * 
 * @author vj 2/6/2010
 *
 */
public class Converter {

	public static enum ConversionType {
		UNKNOWN_CONVERSION,
		UNKNOWN_IMPLICIT_CONVERSION,
		CALL_CONVERSION, // vj: Introduced 3/28/10 to implement cast-as-needed call semantics
        DESUGAR_LATER,
		PRIMITIVE,
		CHECKED,
		SUBTYPE,
		UNBOXING,
		BOXING,
		UNCHECKED;

        public boolean isChecked() { return this==CHECKED || this==DESUGAR_LATER; }
	}

	/**
	 * Return the expression, obtained from e through a sequence of operations, that is of type toType
	 * @param tc -- Visitor to use during construction
	 * @param e -- the subject expression
	 * @param toType -- the target type
	 * @return -- the expression constructed from e of type toType
	 * @throws SemanticException If this is not possible
	 */
	public static Expr attemptCoercion(ContextVisitor tc, Expr e, Type toType) {
		TypeSystem ts = (TypeSystem) tc.typeSystem();
		Type t1 = e.type();
		t1 = PlaceChecker.ReplaceHereByPlaceTerm(t1, (Context) tc.context());
		if (ts.isSubtype(t1, toType, tc.context())) 
			return e;
		
		ConversionType ct = ts.numericConversionValid(toType, e.type(), ConstantValue.toJavaObject(e.constantValue()), tc.context()) 
		? ConversionType.UNKNOWN_CONVERSION
		: ConversionType.CALL_CONVERSION; // ConversionType.UNKNOWN_IMPLICIT_CONVERSION;
		
		NodeFactory nf = (NodeFactory) tc.nodeFactory();

		X10CanonicalTypeNode tn = (X10CanonicalTypeNode) nf.CanonicalTypeNode(e.position(), toType);
		Expr result = typeCheckCast(nf.X10Cast(e.position(), tn, e, ct), tc);
		
		if (result instanceof X10Cast && ((X10Cast) result).conversionType()==ConversionType.CHECKED) {
			// OK that succeeded. Now ensure that there is a depexpr created for the check.

			CConstraint cn = Types.xclause(toType);
			if (cn.hasPlaceTerm()) {
				// Failed to translate the constraint
				// For now the only possibility is the constraint refers
				// to a variable synthesized by the compiler.
				//throw new Errors.CannotGenerateCast(e, e.position());
				return null;
			}

			tn = new Synthesizer(nf, ts).makeCanonicalTypeNodeWithDepExpr(e.position(), toType, tc);
			if (tn.type() != toType) {
				// alright, now we actually synthesized a new depexpr. 
				// lets splice it in.
				result = typeCheckCast(nf.X10Cast(e.position(), tn, e, ct), tc);
			}
		}

		return result;
	}

	private static Expr typeCheckCast(X10Cast cast, ContextVisitor tc) {
	    if (cast.castType() != null) {
	        try {
	            Types.checkMissingParameters(cast.castType());
	        } catch (SemanticException e) {
	            return null;
	        }
	    }
	    try {
	        Expr e = Converter.converterChain((X10Cast_c) cast, tc);
	        assert e.type() != null;
	        assert ! (e instanceof X10Cast_c) || ((X10Cast_c) e).conversionType() != Converter.ConversionType.UNKNOWN_CONVERSION;
	        assert ! (e instanceof X10Cast_c) || ((X10Cast_c) e).conversionType() != Converter.ConversionType.UNKNOWN_IMPLICIT_CONVERSION;
	        return e;
	    } catch (SemanticException e) {
	        return null;
	    }
	    //return cast;
	}

	/**
	 * 
	 * @param <PD>
	 * @param <PI>
	 * @param n
	 * @param tc
	 * @param targetType
	 * @param methods
	 *            Unsubstituted, uninstantiated methods. Need to go through
	 *            MethodMatcher.instantiate to use.
	 * @param maker
	 * @return
	 * @throws SemanticException
	 */
	public static <PD extends ProcedureDef, PI extends ProcedureInstance<PD>> Pair<PI, List<Expr>> 
	tryImplicitConversions(X10ProcedureCall n,
			ContextVisitor tc, Type targetType, List<PI> methods, X10New_c.MatcherMaker<PI> maker) throws SemanticException {
		NodeFactory nf = (NodeFactory) tc.nodeFactory();
		TypeSystem ts = (TypeSystem) tc.typeSystem();
		Reporter reporter = ts.extensionInfo().getOptions().reporter;
		Context xc = (Context) tc.context();
		ClassDef currentClassDef = xc.currentClassDef();

		List<PI> acceptable = new ArrayList<PI>();
		Map<Def, List<Expr>> newArgs = CollectionFactory.newHashMap();

		List<Type> typeArgs = new ArrayList<Type>(n.typeArguments().size());

		for (TypeNode tn : n.typeArguments()) {
			typeArgs.add(tn.type());
		}

		METHOD: for (PI smi :  methods) {
			X10ProcedureInstance<?> xmi = (X10ProcedureInstance<?>) smi;

			if (reporter.should_report(Reporter.types, 3))
				reporter.report(3, "Trying " + smi);

			List<ParameterType> typeParameters = ((X10ProcedureDef) xmi.def()).typeParameters();
			if (typeParameters.size() != typeArgs.size()) {
			    if (!typeArgs.isEmpty()) continue METHOD; // Number of arguments doesn't match
			    Type container = (smi instanceof MemberInstance<?>) ? ((MemberInstance<?>) smi).container() : null;
			    List<Type> actuals = new ArrayList<Type>();
			    for (Expr e : n.arguments()) actuals.add(e.type());
			    List<Type> typeFormals = new ArrayList<Type>(typeParameters);
			    Type[] ta = TypeConstraint.inferTypeArguments(xmi, container, actuals, smi.formalTypes(), typeFormals, xc);
			    typeArgs = Arrays.asList(ta);
			    smi = new TypeParamSubst(ts, typeArgs, typeParameters).reinstantiate(smi);
			}
			List<Expr> transformedArgs = new ArrayList<Expr>();
			List<Type> transformedArgTypes = new ArrayList<Type>();
			List<XVar> transformedYs = new ArrayList<XVar>();
			
			List<Type> formals = smi.formalTypes();
			ContextVisitor argtc = tc.context(xc.pushBlock());

			boolean checkAtRuntime = false;
			for (int j = 0; j < n.arguments().size(); j++) {
				Expr e = n.arguments().get(j);
				Type toType = formals.get(j);
				// toType may have occurrences of CLoc's corresponding to the args
				// k in 0..j-1. These must be treated as of type transformedArgTypes.get(k).
				// Therefore substitute transformedYs.get(k) for the original CLoc. 
				for (int k=0; k < j; k++) {
				    toType = Subst.subst(toType, transformedYs.get(k),
				            ConstraintManager.getConstraintSystem().makeLocal((X10LocalDef) smi.formalNames().get(k).def()));
				}

                // In DYNAMIC_CHECKS we can't just insert a cast for each argument due to dependencies between arguments, e.g.,
                //def m(a:Int, b:Int{self==a}) {}
                //def test(x:Int, y:Int) {
                //  m(x+1,y);
                //}
                //will be desugared into:
                //def m(a:Int, b:Int{self==a}) {}
                //def test(x:Int, y:Int) {
                // ( (a:Int, b:Int) => if (!(b==a)) throw new ...;  m(a,b)) (x+1,y);
                //}
			
				Expr e2 = attemptCoercion(argtc, e, toType); 
				// attemptCoercion is used in many places (for loops, local&field 
				// init expressions, etc), so we special handle it for method calls                
                if (e2 instanceof X10Cast) {
                    X10Cast e2Cast = (X10Cast) e2;
                    if (e2Cast.conversionType()==ConversionType.DESUGAR_LATER)
                        e2 = e2Cast
                        .conversionType(ConversionType.SUBTYPE)
                        .type(Types.baseType(e2Cast.type())); 
                    // because in instantiate we will flag the method call as 
                    // checkGuardAtRuntime and create a closure for it
                }

				if (e2 == null)
					continue METHOD; // this method def is not applicable for this call
				Type e2Type = e2.type();
				if (e2Type instanceof UnknownType)
					continue METHOD;
				Type nType = e2Type;
				for (int k = 0; k < j; k++) {
				    nType = Subst.subst(nType, ConstraintManager.getConstraintSystem().makeEQV(), transformedYs.get(k));
				}
				if (!nType.typeEquals(e2Type, argtc.context())) {
				    // Do not add e2. This may contain some of the new variables
				    // and they won't be in scope for the Desugarer.
				    // Let the Desugarer again generate e2.
				    transformedArgs.add(e);
				    transformedArgTypes.add(toType);
				    checkAtRuntime = true;
				} else {
				    transformedArgs.add(e2);
				    transformedArgTypes.add(e2Type);
				}
				{
					// Construct the new transformedY. 
					Ref<Type> ref = new LazyRef_c<Type>(e2Type);
					X10LocalDef def = X10LocalDef_c.makeHidden(ts, Position.COMPILER_GENERATED, Flags.FINAL, ref,
							Name.makeFresh("arg"));
					XVar y = ConstraintManager.getConstraintSystem().makeLocal(def);
					ref.update(Types.addSelfBinding(e2Type, y));
					transformedYs.add(y);
					argtc.context().addVariable(def.asInstance());
				}
				
			}

			try {
				Matcher<PI> matcher = maker.matcher(targetType, typeArgs, transformedArgTypes);
				// ((X10ProcedureInstance) smi).returnType();
				// X10MethodInstance_c.checkCall(xc, (X10ProcedureInstance) smi,
				// targetType, typeArgs, transformedArgTypes);
				// // smi = (PI) matcher.instantiate(smi);

				// Reinstantiate using the new argument types.
				// Be careful to re-subst in the type arguments of the container type.
				// This should be cleaner!
				PI raw = (PI) smi.def().asInstance();
				if (smi instanceof MemberInstance<?>) {
					Type container = ((MemberInstance<?>) smi).container();
					Type base = Types.baseType(container);
					if (base instanceof X10ClassType) {
						X10ParsedClassType_c ct = (X10ParsedClassType_c) base;
						raw = ct.subst().reinstantiate(raw);
					}
				}
				PI smi2 = (PI) matcher.instantiate(raw);
				if (smi2 instanceof MethodInstance) {
				    ((MethodInstance) smi2).setOrigMI((MethodInstance) raw);
				    smi2 = (PI) smi2.checkConstraintsAtRuntime(checkAtRuntime);
				} else {
					if (smi2 instanceof ConstructorInstance) {
						((ConstructorInstance) smi2).setOrigMI((ConstructorInstance) raw);
					}
				}
				// ((X10ProcedureInstance) smi2).returnType();
				acceptable.add(smi2);
				newArgs.put(smi2.def(), transformedArgs);
			}
			catch (SemanticException e) {
				int q = 3;
			}
		}

		if (acceptable.size() == 0) {
			if (n instanceof New || n instanceof ConstructorCall)
				throw new NoMemberException(NoMemberException.CONSTRUCTOR, 
						"Could not find matching constructor in " + targetType + ".", 
						n.position());
			else
				throw new NoMemberException(NoMemberException.METHOD,
						"Could not find matching method in " + targetType + ".", 
						n.position());
		}

		Collection<PI> maximal = ts.<PD, PI> findMostSpecificProcedures(acceptable, (Matcher<PI>) null, xc);

		if (maximal.size() > 1) {
			StringBuffer sb = new StringBuffer();
			for (Iterator<PI> i = maximal.iterator(); i.hasNext();) {
				PI ma = (PI) i.next();
				if (ma instanceof MemberInstance<?>) {
					sb.append(((MemberInstance<?>) ma).container());
					sb.append(".");
				}
				sb.append(ma.signature());
				if (i.hasNext()) {
					if (maximal.size() == 2) {
						sb.append(" and ");
					}
					else {
						sb.append(", ");
					}
				}
			}

			if (n instanceof New || n instanceof ConstructorCall)
				throw new NoMemberException(NoMemberException.CONSTRUCTOR, "Reference to " 
						+ targetType + " is ambiguous, multiple " + "constructors match: "
						+ sb.toString(), n.position());
			else
				throw new NoMemberException(NoMemberException.METHOD, "Reference to " 
						+ targetType + " is ambiguous, multiple " + "methods match: "
						+ sb.toString(), n.position());
		}

		PI mi;
		mi = (PI) maximal.iterator().next();

		List<Expr> args = newArgs.get(mi.def());
		assert args != null;

		return new Pair<PI, List<Expr>>(mi, args);
	}

	/** Return list of conversion functions needed to convert from fromType to toType */
	public static Expr converterChain(final X10Cast cast, final ContextVisitor tc) throws SemanticException {
		try {
			return Converter.checkCast(cast, tc);
		}
		catch (SemanticException e) {
		}

		TypeSystem ts = (TypeSystem) tc.typeSystem();
		final NodeFactory nf = (NodeFactory) tc.nodeFactory();
		final Context context = tc.context();

		class Helper {
			Expr attempt(X10ClassType ct, int i, List<Type>[] alternatives, 
					Type fromType, List<Type> accum, Type toType, boolean changed) 
			throws SemanticException {
				assert alternatives.length == accum.size();

				if (i < alternatives.length) {
					try {
						accum.set(i, ct.typeArguments().get(i));
						return attempt(ct, i+1, alternatives, fromType, accum, toType, changed);
					}
					catch (SemanticException e) {
					}
					for (Type ti : alternatives[i]) {
						try {
							accum.set(i, ti);
							return attempt(ct, i+1, alternatives, fromType, accum, toType, true);
						}
						catch (SemanticException e) {
						}
					}
				}
				else if (changed) {
					X10ClassType ct2 = ct.typeArguments(accum);
					Type newFrom = Types.xclause(Types.baseType(ct2), Types.xclause(fromType));
					if (fromType.typeEquals(newFrom, context)) {
						assert false;
					}
					if (newFrom.isSubtype(toType, context))
						return cast.expr();
					X10Cast newCast = nf.X10Cast(cast.position(), 
							nf.CanonicalTypeNode(cast.position(), newFrom), cast.expr(), 
							Converter.ConversionType.UNKNOWN_IMPLICIT_CONVERSION);
					Expr newE = converterChain(newCast, tc);
					assert newE.type() != null;
					X10Cast newC = (X10Cast) cast.expr(newE);
					return Converter.checkCast(newC, tc); // FIXME
				}

				throw new Errors.CannotConvertToType(fromType, toType, cast.position());
			
			}

			void addSuperTypes(List<Type> l, Type t) {
				Type b = Types.baseType(t);
				if (! b.typeSystem().typeEquals(b, t, context)) {
					l.add(b);
				}
				else
					if (t instanceof ObjectType) {
						ObjectType o = (ObjectType) t;
						if (o.superClass() != null) {
							l.add(o.superClass());
						}
						for (Type ti : o.interfaces()) {
							l.add(ti);
						}
					}
			}
		}

		Type fromType = cast.expr().type();
		Type toType = cast.castType().type();

		// If the fromType has a covariant parameter,
		// try supertypes of the corresponding argument type.
		Type baseFrom = Types.baseType(fromType);

		if (baseFrom instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) baseFrom;
			if (ct.typeArguments() != null && ct.typeArguments().size() > 0) {
				List<Type>[] alternatives = new List[ct.typeArguments().size()];
				List<Type> newArgs = new ArrayList<Type>(ct.typeArguments().size());
				if (ct.x10Def().variances().size() != ct.typeArguments().size()) {
				    // an error would have been reported already
				    throw new Errors.CannotConvertExprToType(cast.expr(), cast.conversionType(), toType, cast.position());
				}
				for (int i = 0; i < ct.typeArguments().size(); i++) {
					ParameterType.Variance v = ct.x10Def().variances().get(i);
					Type ti = ct.typeArguments().get(i);
					alternatives[i] = new ArrayList<Type>();
					switch (v) {
					case COVARIANT:
						new Helper().addSuperTypes(alternatives[i], ti);
						break;
					default:
						break;                              
					}
				}

				// Now, try all possible combinations of the alternative type arguments.
				try {
					return new Helper().attempt(ct, 0, alternatives, fromType, new ArrayList<Type>(ct.typeArguments()), toType, false);
				}
				catch (SemanticException e) {
					// Fall through.
				}
			}
		}

		throw new Errors.CannotConvertExprToType(cast.expr(), cast.conversionType(), toType, cast.position());
	}

	public static Expr checkCast(X10Cast cast, ContextVisitor tc) throws SemanticException {
		X10CompilerOptions opts = (X10CompilerOptions) tc.job().extensionInfo().getOptions();
		TypeSystem ts =  tc.typeSystem();
		Type toType = cast.castType().type();
		Type fromType = cast.expr().type();
		Context context =  tc.context();

		if (ts.isUnknown(toType)) {
		    if (opts.x10_config.CHECK_INVARIANTS)
			Errors.issue(tc.job(), new Errors.UnknownType(cast.position()));
		    return cast;
		}

		if (ts.isVoid(toType) || ts.isVoid(fromType))
			throw new Errors.CannotConvertToType(fromType, toType, cast.position());


        Expr withoutCoercion = checkCastWithoutCoercions(cast, tc);
        // even if withoutCoercion!=null, I still want to check coercions because I need to produce a warning
        //  if we favor a system-as over a user-defined-as (both implicit and explicit)
        Expr withCoercion = checkCastWithCoercions(cast, tc);
        if (withoutCoercion!=null && withCoercion!=null) {
            // produce a warning
            // todo: add a verbose flag
            if (opts.x10_config.VERBOSE)
                Warnings.issue(tc.job(), "The casting can be done both by a user-defined coercion and system cast, and the compiler always favors a system cast over a user-defined cast. Make sure this is the desired behavior.", cast.position());
        }
        if (withoutCoercion!=null) return withoutCoercion;
        if (withCoercion!=null) return withCoercion;
		throw new Errors.CannotConvertExprToType(cast.expr(), cast.conversionType(), toType, cast.position());
    }
    private static Expr checkCastWithoutCoercions(X10Cast cast, ContextVisitor tc) throws SemanticException {
		X10CompilerOptions opts = (X10CompilerOptions) tc.job().extensionInfo().getOptions();
		TypeSystem ts =  tc.typeSystem();
		Type toType = cast.castType().type();
		Type fromType = cast.expr().type();
		Context context = tc.context();

        // Is it an upcast?
		if (ts.isSubtype(fromType, toType, context)) {
		    // Add the clause self==x if the fromType's self binding is x,
		    // since for these casts we know the result is identical to expr.
		    //XTerm sv = Types.selfBinding(fromType);
		    //if (sv != null)
		    //    toType = Types.addSelfBinding((Type) toType.copy(), sv);
		    X10Cast n =  cast.conversionType(ConversionType.SUBTYPE);
		    return n.type(toType);
		}

        // is it a downcast?
		if (cast.conversionType() != ConversionType.UNKNOWN_IMPLICIT_CONVERSION
				&& cast.conversionType() != ConversionType.CALL_CONVERSION) {
			if (! ts.isParameterType(fromType)
					&& ! ts.isParameterType(toType)
					&& ts.isCastValid(fromType, toType, context)) {
				X10Cast n = cast.conversionType(ConversionType.CHECKED);
				XTerm sv = Types.selfBinding(fromType);
				if (sv != null)
				    toType = Types.addSelfBinding((Type) toType.copy(), sv);
				return n.type(toType);
			}
		}


	    l:  if (cast.conversionType() != ConversionType.UNKNOWN_IMPLICIT_CONVERSION
	    		&& cast.conversionType() != ConversionType.CALL_CONVERSION) {
        	if (ts.isParameterType(toType)) {
        		// Now get the upper bound.
        		List<Type> upper = ts.env(context).upperBounds(toType, false);
        		if (upper.isEmpty()) {
        			// No upper bound. Now a checked conversion is permitted only
        			// if fromType is not Null.
        			if (! fromType.isNull())
        				return checkedConversionForTypeParameter(cast, fromType, toType);
        		} else {
        			for (Type t : upper)
        				if (ts.isSubtype(fromType, t))
        					return checkedConversionForTypeParameter(cast, fromType, toType);
        		}
        	} else 	if (ts.isParameterType(fromType)) {
        		// Now get the upper bound.
        		List<Type> upper = ts.env(context).upperBounds(fromType, false);
        		for (Type t : upper)
        			if (! ts.isSubtype(t, toType))
        				break l;
        		return checkedConversionForTypeParameter(cast, fromType, toType);
        	}
	    }

		// Added 03/28/10 to support new call conversion semantics.
		Type baseFrom = Types.baseType(fromType);
		Type baseTo = Types.baseType(toType);

		boolean inferGuard = false;
		ProcedureDef procDef = null;
		if (opts.x10_config.CONSTRAINT_INFERENCE) {
			assert (context.currentCode() instanceof ProcedureDef);
			procDef = (ProcedureDef) context.currentCode();
			inferGuard = procDef.inferGuard();
		}

		if (ts.isSubtype(baseFrom, baseTo, context)) {
			if (inferGuard) {
				try {
					procDef.requirements().add(fromType, toType, context);
				} catch (XFailure e) {
					return null;
				}
			}
			if (!opts.x10_config.STATIC_CHECKS || inferGuard)
				// TODO we do not have to check constraints at runtime with inferGuard
				if (( cast.conversionType() == ConversionType.CALL_CONVERSION)
						&& ts.isCastValid(fromType, toType, context)) {
					//return cast.conversionType(ConversionType.DESUGAR_LATER).type(baseTo);
					X10Cast n = cast.conversionType(ConversionType.DESUGAR_LATER);
					XVar sv = Types.selfVarBinding(fromType); // FIXME: Vijay, can this be an XTerm?  -Bowen
					if (sv != null)
					    toType = Types.addSelfBinding((Type) toType.copy(), sv);
					return n.type(toType);
				}
		}

        return null;
    }
    private static Expr checkCastWithCoercions(X10Cast cast, ContextVisitor tc) throws SemanticException {
		TypeSystem ts =  tc.typeSystem();
		Type toType = cast.castType().type();
		Type fromType = cast.expr().type();
		NodeFactory nf = tc.nodeFactory();
		Context context = tc.context();

		Type baseTo = Types.baseType(toType);

		{
			MethodInstance converter = null;
			Call c = null;
			MethodInstance mi = converter;
			Position p = cast.position();
			Expr e = cast.expr();

			// Can convert if there is a static method toType.$convert(fromType)
			if (converter == null && cast.conversionType() != ConversionType.UNKNOWN_IMPLICIT_CONVERSION
					&& cast.conversionType() != ConversionType.CALL_CONVERSION) {
				try {
					mi = ts.findMethod(toType, ts.MethodMatcher(toType, Converter.operator_as, 
							Collections.singletonList(fromType), context));
					Type miType = mi.returnType();
					Type baseMiType = Types.baseType(miType);
					if (mi.flags().isStatic() && baseMiType.isSubtype(baseTo, context)
							&& Types.areConsistent(miType, toType)) {
						converter = mi;
						// Do the conversion.
						c = nf.Call(p, nf.CanonicalTypeNode(p, toType), nf.Id(p, mi.name()), e);
						c = c.methodInstance(mi);
						c = (Call) c.type(mi.returnType());
					}
				}
				catch (SemanticException z1) {
				    
				}
			}
			// or  can convert if there is a static implict cast operator defined on toType: static operator (f:fromType)

			if (converter == null) {
				try {
					mi = ts.findMethod(toType, ts.MethodMatcher(toType, Converter.implicit_operator_as, 
							Collections.singletonList(fromType), context));
					
					Type miType = mi.returnType();
					Type baseMiType = Types.baseType(miType);
					if (mi.flags().isStatic() && baseMiType.isSubtype(baseTo, context)
							&& Types.areConsistent(miType, toType)) {
						converter = mi;
						// Do the conversion.
						c = nf.Call(p, nf.CanonicalTypeNode(p, toType), nf.Id(p, mi.name()), e);
						c = c.methodInstance(mi);
						c = (Call) c.type(mi.returnType());
					}
				}
				catch (SemanticException z2) {
					try {
						mi = ts.findMethod(fromType, ts.MethodMatcher(fromType, Converter.implicit_operator_as, 
								Collections.singletonList(fromType), context));
						Type miType = mi.returnType();
						Type baseMiType = Types.baseType(miType);
						if (mi.flags().isStatic() && baseMiType.isSubtype(baseTo, context)
								&& Types.areConsistent(miType, toType)) {
							converter = mi;
							c = nf.Call(p, nf.CanonicalTypeNode(p, fromType), nf.Id(p, mi.name()), e);
							c = c.methodInstance(mi);
							c = (Call) c.type(mi.returnType());
						}
					} catch (SemanticException z) {
					}
                    // yoav todo: we check 3 conditions:
                    // toType.operator_as(fromType)
                    // toType.implicit_operator_as(fromType)
                    // fromType.implicit_operator_as(fromType)
                    // but we should also check:
                    // fromType.operator_as(fromType)
                    // IMPORTANT: we currently disable defining coercions in the fromType (only in the toType), see X10MethodDecl_c: static boolean SEARCH_CASTS_ONLY_IN_TARGET = true; // see XTENLANG_2667
				}
			}

			if (converter != null) {

				// Now, do a coercion if needed to check any additional constraints on the type.
				if (! ts.isParameterType(fromType) && ! mi.returnType().isSubtype(toType, context)) {
					X10Cast n = cast.exprAndConversionType(c, ConversionType.CHECKED); 
					return n.type(toType);
				}
				else {
					return c;
				}
			}
		}
        return null;
	}
		   
	static Expr checkedConversionForTypeParameter(X10Cast cast, Type fromType, Type toType) {
	    return cast.conversionType(ConversionType.CHECKED).type(toType);
	}
	public static <T extends Node> T check(T n, ContextVisitor tc) throws SemanticException {
		return (T) n.del().disambiguate(tc).del().typeCheck(tc).del().checkConstants(tc);
	}
	
	public static final Name operator_as = OperatorNames.AS;
	public static final Name implicit_operator_as = OperatorNames.IMPLICIT_AS;
}
