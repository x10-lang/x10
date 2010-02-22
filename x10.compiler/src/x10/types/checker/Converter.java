/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.Matcher;
import polyglot.types.MemberInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.NoMemberException;
import polyglot.types.ObjectType;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.ast.X10Cast;
import x10.ast.X10Cast_c;
import x10.ast.X10New_c;
import x10.ast.X10NodeFactory;
import x10.ast.X10ProcedureCall;
import x10.ast.X10New_c.MatcherMaker;
import x10.constraint.XConstraint;
import x10.errors.Errors;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10ParsedClassType_c;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;

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
		PRIMITIVE,
		CHECKED,
		SUBTYPE,
		UNBOXING,
		BOXING,
		UNCHECKED
	}

	/**
	 * Return the expression, obtained from e through a sequence of operations, that is of type toType
	 * @param tc -- Visitor to use during construction
	 * @param e -- the subject expression
	 * @param toType -- the target type
	 * @return -- the expression constructed from e of type toType
	 * @throws SemanticException If this is not possible
	 */
	public static Expr attemptCoercion(ContextVisitor tc, Expr e, Type toType) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

		if (ts.isSubtype(e.type(), toType, tc.context())) 
			return e;
		
		ConversionType ct = ts.numericConversionValid(toType, e.type(), e.constantValue(), tc.context()) 
		? ConversionType.UNKNOWN_CONVERSION
		: ConversionType.UNKNOWN_IMPLICIT_CONVERSION;
		
		X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();

		Expr result = check(nf.X10Cast(e.position(), nf.CanonicalTypeNode(e.position(), toType), e, ct),tc);
		return result;
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
		X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10Context xc = (X10Context) tc.context();
		ClassDef currentClassDef = xc.currentClassDef();

		List<PI> acceptable = new ArrayList<PI>();
		Map<Def, List<Expr>> newArgs = new HashMap<Def, List<Expr>>();

		List<Type> typeArgs = new ArrayList<Type>(n.typeArguments().size());

		for (TypeNode tn : n.typeArguments()) {
			typeArgs.add(tn.type());
		}

		METHOD: for (Iterator<PI> i = methods.iterator(); i.hasNext();) {
			PI smi = (PI) i.next();

			if (Report.should_report(Report.types, 3))
				Report.report(3, "Trying " + smi);

			List<Expr> transformedArgs = new ArrayList<Expr>();
			List<Type> transformedArgTypes = new ArrayList<Type>();

			List<Type> formals = smi.formalTypes();

			for (int j = 0; j < n.arguments().size(); j++) {
				Expr e = n.arguments().get(j);
				Type toType = formals.get(j);

				try {
					Expr e2 = attemptCoercion(tc, e, toType);
					transformedArgs.add(e2);
					transformedArgTypes.add(e2.type());
				}
				catch (SemanticException ex) {
					// Implicit cast not allowed here.
					continue METHOD;
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
				if (smi instanceof MemberInstance) {
					Type container = ((MemberInstance) smi).container();
					Type base = X10TypeMixin.baseType(container);
					if (base instanceof X10ClassType) {
						X10ParsedClassType_c ct = (X10ParsedClassType_c) base;
						raw = ct.subst().reinstantiate(raw);
					}
				}
				PI smi2 = (PI) matcher.instantiate(raw);
				// ((X10ProcedureInstance) smi2).returnType();
				acceptable.add(smi2);
				newArgs.put(smi2.def(), transformedArgs);
			}
			catch (SemanticException e) {
				System.out.print("");
			}
		}

		if (acceptable.size() == 0) {
			if (n instanceof New || n instanceof ConstructorCall)
				throw new NoMemberException(NoMemberException.CONSTRUCTOR, "Could not find matching constructor in " + targetType + ".", n.position());
			else
				throw new NoMemberException(NoMemberException.METHOD, "Could not find matching method in " + targetType + ".", n.position());
		}

		Collection<PI> maximal = ts.<PD, PI> findMostSpecificProcedures(acceptable, (Matcher<PI>) null, xc);

		if (maximal.size() > 1) {
			StringBuffer sb = new StringBuffer();
			for (Iterator<PI> i = maximal.iterator(); i.hasNext();) {
				PI ma = (PI) i.next();
				if (ma instanceof MemberInstance) {
					sb.append(((MemberInstance) ma).container());
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
				throw new NoMemberException(NoMemberException.CONSTRUCTOR, "Reference to " + targetType + " is ambiguous, multiple " + "constructors match: "
						+ sb.toString(), n.position());
			else
				throw new NoMemberException(NoMemberException.METHOD, "Reference to " + targetType + " is ambiguous, multiple " + "methods match: "
						+ sb.toString(), n.position());
		}

		PI mi;
		mi = (PI) maximal.iterator().next();

		List<Expr> args = newArgs.get(mi.def());
		assert args != null;

		return new Pair<PI, List<Expr>>(mi, args);
	}



	/** Return list of conversion functions needed to convert from fromType to toType */
	public static Expr converterChain(final X10Cast_c cast, final ContextVisitor tc) throws SemanticException {
		try {
			return Converter.checkCast(cast, tc);
		}
		catch (SemanticException e) {
		}

		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		final X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
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
					Type newFrom = X10TypeMixin.xclause(X10TypeMixin.baseType(ct2), X10TypeMixin.xclause(fromType));
					if (fromType.typeEquals(newFrom, context)) {
						assert false;
					}
					if (newFrom.isSubtype(toType, context))
						return cast.expr();
					X10Cast_c newCast = (X10Cast_c) nf.X10Cast(cast.position(), 
							nf.CanonicalTypeNode(cast.position(), newFrom), cast.expr(), 
							Converter.ConversionType.UNKNOWN_IMPLICIT_CONVERSION);
					Expr newE = converterChain(newCast, tc);
					assert newE.type() != null;
					X10Cast_c newC = (X10Cast_c) cast.expr(newE);
					return Converter.checkCast(newC, tc);
				}

				throw new Errors.CannotConvertToType(fromType, toType, cast.position());
			
			}

			void addSuperTypes(List<Type> l, Type t) {
				Type b = X10TypeMixin.baseType(t);
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
		Type baseFrom = X10TypeMixin.baseType(fromType);

		if (baseFrom instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) baseFrom;
			if (ct.typeArguments().size() > 0) {
				List<Type>[] alternatives = new List[ct.typeArguments().size()];
				List<Type> newArgs = new ArrayList<Type>(ct.typeArguments().size());
				for (int i = 0; i < ct.typeArguments().size(); i++) {
					ParameterType.Variance v = ct.x10Def().variances().get(i);
					Type ti = ct.typeArguments().get(i);
					switch (v) {
					case COVARIANT:
						alternatives[i] = new ArrayList<Type>();
						new Helper().addSuperTypes(alternatives[i], ti);
						break;
					default:
						alternatives[i] = Collections.EMPTY_LIST;
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

	public  static Expr checkCast(X10Cast_c cast, ContextVisitor tc) throws SemanticException {
		Type toType = cast.castType().type();
		Type fromType = cast.expr().type();
		X10TypeSystem_c ts = (X10TypeSystem_c) tc.typeSystem();
		X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
		X10Context context = (X10Context) tc.context();

		if (ts.isVoid(toType) || ts.isVoid(fromType))
			throw new Errors.CannotConvertToType(fromType, toType, cast.position());

		if (ts.isSubtype(fromType, toType, context)) {
			X10Cast n =  cast.conversionType(ConversionType.SUBTYPE);
			return n.type(toType);
		}

		Type baseFrom = X10TypeMixin.baseType(fromType);
		Type baseTo = X10TypeMixin.baseType(toType);
		XConstraint cFrom = X10TypeMixin.xclause(fromType);
		XConstraint cTo = X10TypeMixin.xclause(toType);

		if (cast.conversionType() != ConversionType.UNKNOWN_IMPLICIT_CONVERSION) {
			if (! ts.isParameterType(fromType) 
					&& ! ts.isParameterType(toType) 
					&& ts.isCastValid(fromType, toType, context)) {
				X10Cast n = cast.conversionType(ConversionType.CHECKED); 
				return n.type(toType);
			}
		}

		{
			MethodInstance converter = null;
			Call c = null;
			MethodInstance mi = converter;
			Position p = cast.position();
			Expr e = cast.expr();

			// Can convert if there is a static method toType.$convert(fromType)
			if (converter == null && cast.conversionType() != ConversionType.UNKNOWN_IMPLICIT_CONVERSION) {
				try {
					mi = ts.findMethod(toType, ts.MethodMatcher(toType, Converter.operator_as, 
							Collections.singletonList(fromType), context));
					Type baseMiType = X10TypeMixin.baseType(mi.returnType());
					if (mi.flags().isStatic() && baseMiType.isSubtype(baseTo, context)) {
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
			// or  can convert if there is a static method fromType.$convert(ToType)

			if (converter == null) {
				try {
					mi = ts.findMethod(toType, ts.MethodMatcher(toType, Converter.implicit_operator_as, 
							Collections.singletonList(fromType), context));
					Type baseMiType = X10TypeMixin.baseType(mi.returnType());
					if (mi.flags().isStatic() && baseMiType.isSubtype(baseTo, context)) {
						converter = mi;
						// Do the conversion.
						c = nf.Call(p, nf.CanonicalTypeNode(p, toType), nf.Id(p, mi.name()), e);
						c = c.methodInstance(mi);
						c = (Call) c.type(mi.returnType());
					}
				}
				catch (SemanticException z2) {
					try {
						mi = ts.findMethod(fromType, ts.MethodMatcher(toType, Converter.implicit_operator_as, 
								Collections.singletonList(fromType), context));
						Type baseMiType = X10TypeMixin.baseType(mi.returnType());
						if (mi.flags().isStatic() && baseMiType.isSubtype(baseTo, context)) {
							converter = mi;
							c = nf.Call(p, nf.CanonicalTypeNode(p, fromType), nf.Id(p, mi.name()), e);
							c = c.methodInstance(mi);
							c = (Call) c.type(mi.returnType());
						}
					} catch (SemanticException z) {
					}
				}
			}

			if (converter != null) {

				// Now, do a coercion if needed to check any additional constraints on the type.
				if (! ts.isParameterType(fromType) && ! mi.returnType().isSubtype(toType, context)) {
					X10Cast n = cast.conversionType(c, ConversionType.CHECKED); 
					return n.type(toType);
				}
				else {
					return c;
				}
			}
		}

	    l:  if (cast.conversionType() != ConversionType.UNKNOWN_IMPLICIT_CONVERSION) {
        	if (ts.isParameterType(toType)) {
        		// Now get the upper bound.
        		List<Type> upper = ts.env(context).upperBounds(toType, false);
        		if (upper.isEmpty()) {
        			// No upper bound. Now a hecked conversion is permitted only
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

		throw new Errors.CannotConvertExprToType(cast.expr(), cast.conversionType(), toType, cast.position());
	}
		
		   
	    static Expr checkedConversionForTypeParameter(X10Cast_c cast, Type fromType, Type toType) {
	        return cast.conversionType(ConversionType.CHECKED).type(toType);
	    }
	public static <T extends Node> T check(T n, ContextVisitor tc) throws SemanticException {
		return (T) n.del().disambiguate(tc).del().typeCheck(tc).del().checkConstants(tc);
	}
	public static final Name operator_as = Name.make("operator_as");
	public static final Name implicit_operator_as = Name.make("implicit_operator_as");

}
