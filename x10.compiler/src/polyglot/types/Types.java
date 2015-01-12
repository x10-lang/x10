/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Binary.Operator;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FloatLit;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ast.Variable;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.visit.ContextVisitor;
import x10.ast.HasZeroTest;
import x10.ast.Here;
import x10.ast.Here_c;
import x10.ast.IsRefTest;
import x10.ast.ParExpr;

import x10.ast.SubtypeTest;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Errors.TypeIsMissingParameters;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.MacroType;
import x10.types.MacroType_c;
import x10.types.ParameterType;
import x10.types.ParameterType.Variance;
import x10.types.ThisDef;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassDef_c;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Def;
import x10.types.X10FieldDef;
import x10.types.X10FieldInstance;
import x10.types.MethodInstance;
import x10.types.X10LocalDef;
import x10.types.X10ParsedClassType;
import x10.types.X10ParsedClassType_c;
import x10.types.X10ProcedureDef;
import x10.types.X10ProcedureInstance;
import x10.types.X10ThisVar;
import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CLit;
import x10.types.constraints.CLocal;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.types.constraints.xnative.QualifiedVar;
import x10.types.matcher.Matcher;
import x10.types.matcher.Subst;
import x10.types.matcher.X10FieldMatcher;
import x10.X10CompilerOptions;


public class Types {

    public static <T> T get(Ref<T> ref) {
        return ref != null ? ref.get() : null;
    }

    public static <T> T getCached(Ref<T> ref) {
        return ref != null ? ref.getCached() : null;
    }

    @SuppressWarnings("unchecked") // Special-casing TypeObject
    public static <T> Ref<T> ref(T v) {
	    if (v instanceof TypeObject)
		    return (Ref<T>) new Ref_c((TypeObject) v);
	    else if (v == null)
		    return null;
	    else {
		    Ref<T> ref = lazyRef(v, new Runnable() {
			    public void run() { }
		    });
		    ref.update(v);
		    return ref;
	    }
    }

    /** Create a lazy reference to a type object, with an initial value.
     * @param defaultValue initial value
     * @param resolver goal used to bring the reference up-to-date
     * 
     * ### resolver should be a map
     */
    public static <T> LazyRef<T> lazyRef(T defaultValue) {
        return new LazyRef_c<T>(defaultValue);
    }

    public static <T> LazyRef<T> lazyRef(T defaultValue, Runnable resolver) {
        return new LazyRef_c<T>(defaultValue, resolver);
    }

	public static Type addBinding(Type t, XTerm t1, XTerm t2) {
		//assert (! (t instanceof UnknownType));
	    CConstraint c = Types.xclause(t);
	    c = c == null ? ConstraintManager.getConstraintSystem().makeCConstraint() :c.copy();
	    c.addBinding(t1, t2);
	    return Types.xclause(Types.baseType(t), c);
	}

	public static Type addBinding(Type t, XTerm t1, XConstrainedTerm t2) {
	 	assert (! (t instanceof UnknownType));
	 	CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
	 	c.addBinding(t1, t2);
	 	return Types.xclause(t, c);
	}

	public static Type addSelfBinding(Type t, XTerm t1) {
	    assert (! (t instanceof UnknownType));
	    CConstraint c = Types.xclause(t);
	    c = c == null ? ConstraintManager.getConstraintSystem().makeCConstraint() :c.copy();
	    c.addSelfBinding(t1);
	    return Types.xclause(Types.baseType(t), c); 
	}

	/**
	 * Add t1 != t2 to the type t.
	 * The type returned may be inconsistent.
	 * @param t
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static Type addDisBinding(Type t, XTerm t1, XTerm t2) {
	 	assert (! (t instanceof UnknownType));
	 	CConstraint c = Types.xclause(t);
	 	c = c == null ? ConstraintManager.getConstraintSystem().makeCConstraint() :c.copy();
	 	c.addDisBinding(t1, t2);
	 	return Types.xclause(Types.baseType(t), c);
	}

	/**
     * Add c to t. Note: The type returned may have an inconsistent
     * constraint.
     * @param t
     * @param t1
     * @param t2
     * @return
     */
	public static Type addConstraint(Type t, CConstraint xc) {
	    CConstraint c = Types.tryAddingConstraint(t, xc);
	    return Types.xclause(Types.baseType(t), c);
	}

	public static Type addTerm(Type t, XTerm term) {
	    try {
	        CConstraint c = Types.xclause(t);
	        c = c == null ? ConstraintManager.getConstraintSystem().makeCConstraint() :c.copy();
	        c.addTerm(term);
	        return Types.xclause(Types.baseType(t), c);
	    }
	    catch (XFailure f) {
	        throw new InternalCompilerError("Cannot add term " + term + " to " + t + ".", f);
	    }
	}

	public static void checkVariance(TypeNode t, ParameterType.Variance variance, Job errs) {
	    checkVariance(t.type(),variance,errs,t.position());
	}

	public static void checkVariance(Type t, ParameterType.Variance variance, Job errs, Position pos) {
	    Type base = null;
	    if (t instanceof ParameterType) {
	        ParameterType pt = (ParameterType) t;
	        ParameterType.Variance var = pt.getVariance();
	        if (var==variance || var==ParameterType.Variance.INVARIANT) {
	            // ok
	        } else {
	            Errors.issue(errs, 
	            		new Errors.IllegalVarianceParameter(var, variance, pos)); // todo: t.position() is incorrect (see XTENLANG-1439)
	        }
	
	    } else if (t instanceof X10ParsedClassType_c) {
	        X10ParsedClassType_c pt = (X10ParsedClassType_c) t;
	        List<Type> args = pt.typeArguments();
	        if (args == null)
	            args = Collections.<Type>emptyList();
	        X10ClassDef def = (X10ClassDef) pt.def();
	        final List<ParameterType.Variance> variances = def.variances();
	        for (int i=0; i<Math.min(args.size(), variances.size()); i++) {
	            Type arg = args.get(i);
	            ParameterType.Variance var = variances.get(i);
	            checkVariance(arg, variance.mult(var), errs, pos);
	        }
	    } else if (t instanceof ConstrainedType) {
	        ConstrainedType ct = (ConstrainedType) t;
	        base = get(ct.baseType());
	    } /*else if (t instanceof AnnotatedType_c) {
	        AnnotatedType_c at = (AnnotatedType_c) t;
	        base = at.baseType();
	    }*/ else if (t instanceof MacroType_c) {
	        MacroType mt = (MacroType) t;
	        base = mt.definedType();
	    }
	    if (base!=null)
	        checkVariance(base,variance,errs,pos);
	
	}

	public static Type baseType(Type t) {
		while (true) {
			if (t instanceof MacroType) {
				t = ((MacroType) t).definedType();
				continue;
			}
			if (t instanceof ConstrainedType) {
				t = get(((ConstrainedType) t).baseType());
				continue;
			}
			break;
	    }
	    return t;
	}
	
	public static List<FunctionType> functionTypes(Type t) {
	    List<FunctionType> ans = new ArrayList<FunctionType>();
	    Type bt = Types.baseType(t);
	    if (bt instanceof FunctionType) {
	        ans.add((FunctionType)bt);
	    } else {
	        TypeSystem ts = t.typeSystem();
	        if (ts.isFunctionType(bt)) {
	            X10ClassType ct = (X10ClassType)bt;
	            for (Type i : ct.interfaces()) {
	                if (i instanceof FunctionType)
	                    ans.add((FunctionType)i);
	            }
	        }	        
	    }
	    return ans;
	}

	public static ConstrainedType constrainedType(Type base, CConstraint c) {
		return new ConstrainedType((TypeSystem) base.typeSystem(), base.position(), base.position(), ref(base),
				ref(c));
	}

	public static boolean consistent(Type t) {
	    if (t instanceof MacroType) {
	        MacroType mt = (MacroType) t;
	        return consistent(mt.definedType());
	    }
	    if (t instanceof ConstrainedType) {
	        ConstrainedType ct = (ConstrainedType) t;
	        return ct.xclause().consistent();
	    }
	    if (t instanceof X10ParsedClassType) {
	        X10ParsedClassType ct = (X10ParsedClassType) t;
	        return ct.getXClause().consistent();
	    }
	    return true; // clause is null.
	}

	public static boolean eitherIsDependent(Type t1, Type t2) {
		return Types.isDependentOrDependentPath(t1) || Types.isDependentOrDependentPath(t2);
	}

	public static boolean entails(Type t, XTerm t1, XTerm t2) {
		 CConstraint c = Types.realX(t);
		 if (c==null) 
			 c = ConstraintManager.getConstraintSystem().makeCConstraint();
		 return c.entails(t1, t2);
	  }

	public static boolean disEntails(Type t, XTerm t1, XTerm t2) {
		 CConstraint c = Types.realX(t);
		 if (c==null) 
			 c = ConstraintManager.getConstraintSystem().makeCConstraint();
		 return c.disEntails(t1, t2);
	  }

	public static boolean disEntailsSelf(Type t, XTerm t2) {
		 CConstraint c = Types.realX(t);
		 if (c==null) 
			 c = ConstraintManager.getConstraintSystem().makeCConstraint();
		 return c.disEntails(c.self(), t2);
	  }


	public static Type arrayBaseType(Type t) {
		t = baseType(t);
		if (t instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) t;
			TypeSystem ts = (TypeSystem) t.typeSystem();
			ClassType a = (ClassType) ts.RegionArray();
			ClassType da = (ClassType) ts.RegionArray();
			if (ct.def() == a.def() || ct.def() == da.def())
				return ct.typeArguments().get(0);
			else
				arrayBaseType(ct.superClass());
		}
		return null;
	}

	public static SemanticException error(Type t) {
	    t = baseType(t);
	    if (t instanceof X10ClassType) {
	        X10ClassType ct = (X10ClassType) t;
	        return ct.error();
	    }
	    return null;
	}

	/**
	 * Returns a new constraint that allows null.
	 * E.g., given "{self.home==here, self!=null}" it returns "{self.home==here}"
	 * @param c a constraint "c" that doesn't allow null
	 * @return a new constraint with all the constraints in "c" except {self!=null}
	 * 
	 * TODO: this implementation is wrong, see XTENLANG-1380
	 */
//	public static CConstraint allowNull(CConstraint c) {
//	    final XVar self = c.self();
//	    CConstraint res = ConstraintManager.getConstraintSystem().makeCConstraint(self);
//	    assert !res.disEntails(self,ConstraintManager.getConstraintSystem().NULL);
//	    for (XTerm term : c.constraints()) {
//	        CConstraint copy = res.copy();
//	        try {
//	            copy.addTerm(term);
//	        } catch (XFailure xFailure) {
//	            assert false : xFailure;
//	        }
//	        if (!copy.disEntails(self,ConstraintManager.getConstraintSystem().NULL))
//	            res = copy;
//	    }
//	    return res;
//	}

	public static void checkMissingParameters(Receiver receiver) throws SemanticException {
	    Type xt = receiver.type();
	    checkMissingParameters(xt,receiver.position());
	}

	public static void checkMissingParameters(Type xt, Position pos) throws SemanticException {
		if (xt == null) return;
		xt = baseType(xt);
		
		if (xt instanceof X10ParsedClassType) {
			X10ParsedClassType xt1 = (X10ParsedClassType) xt;
	        final X10ClassDef classDef = (X10ClassDef) xt1.def();
			
			if (xt1.isMissingTypeArguments()) {
	            List<ParameterType> expectedArgs = classDef.typeParameters();
				throw new Errors.TypeIsMissingParameters(xt, expectedArgs, pos);
			} else {
	            // todo check the TypeConstraint of the class invariant is satisfied
	            
	        }
		}
	}

	public static Type arrayElementType(Type t) {
		t = baseType(t);
		TypeSystem xt = (TypeSystem) t.typeSystem();
		if (xt.isX10RegionArray(t) || xt.isX10RegionDistArray(t)) {
			if (t instanceof X10ParsedClassType) {
				Type result = ((X10ParsedClassType) t).typeArguments().get(0);
				return result;
			}
		}
		return null;
	}

	public static boolean contextKnowsType(Receiver r) {
		if (r instanceof Variable)
			return ((Variable) r).flags().isFinal();
		if (r instanceof Field)
			return contextKnowsType( ((Field) r).target());
		if (r instanceof Special || r instanceof Here_c || r instanceof Lit)
			return true;
		if (r instanceof ParExpr) 
			return contextKnowsType(((ParExpr) r).expr());
		if (r instanceof Cast) 
			return contextKnowsType(((Cast) r).expr());
		return false;
			
		
	}

	public static boolean areConsistent(Type t1, Type t2) {
	    if ( Types.isConstrained(t1) &&  Types.isConstrained(t2))
	        return Types.tryAddingConstraint(t1, Types.xclause(t2)).consistent();
		return true;
	}


	public static X10ParsedClassType instantiate(Type t, Type... typeArg) {
	if (t instanceof X10ParsedClassType) {
	    X10ParsedClassType ct = (X10ParsedClassType) t;
	    return ct.typeArguments(Arrays.asList(typeArg));
	}
	else {
	    throw new InternalCompilerError("Cannot instantiate non-class " + t);
	}
	}

	public static X10ParsedClassType instantiate(Type t, Ref<? extends Type> typeArg) {
	// TODO: should not deref now, since could be called by class loader
	return instantiate(t, get(typeArg));
	}

	public static X10ClassDef_c getDef(Type t) {
	    if (t==null) return null;
	    // GOLDEN
	    if (t.typeSystem().hasUnknown(t)) {
	    	return null;
	    }
	    return (X10ClassDef_c) ((X10ParsedClassType_c)baseType(t)).def();
	}

	public static boolean isConstrained(Type t) {
		
	        /*if (t instanceof AnnotatedType) {
	            AnnotatedType at = (AnnotatedType) t;
	            return isConstrained(at.baseType());
	        }*/
	        if (t instanceof MacroType) {
	                MacroType mt = (MacroType) t;
	                return isConstrained(mt.definedType());
	        }
		if (t instanceof ConstrainedType) {
			return true;
		}
		return false;
	}

	public static boolean isClass(Type t) {
	    return (t instanceof X10ClassType);
	}
	public static boolean isConstrainedType(Type t) {
        return (t instanceof ConstrainedType);
    }

	public static Type instantiateSelf(XTerm t, Type type) {
	 	//assert (! (t instanceof UnknownType));
		 CConstraint c = Types.xclause(type);
	        if (! ((c==null) || c.valid())) {
	        	CConstraint env = c = c.instantiateSelf(t);
	        	if (false && ! c.consistent()) {
	        		throw new InternalCompilerError("X10TypeMixin: Instantiating self on " + type + " with " + t + " is inconsistent.");
	        	}
	        	return Types.xclause(baseType(type), c);
	        }
	        return type;
	}

	public static boolean isDependentOrDependentPath(Type t) {
		return isConstrained(t);
	}

	public static Type getParameterType(Type theType, int i) {
	    Type b = baseType(theType);
	    if (b instanceof X10ClassType) {
		X10ClassType ct = (X10ClassType) b;
		if (ct.typeArguments() != null && i < ct.typeArguments().size()) {
		    return ct.typeArguments().get(i);
		}
	    }
	    return null;
	}

	/**
	 * Returns the var that is thisvar of all the terms in {t1,t2} that have a thisvar.
	 * If none do, return null. Else throw a SemanticError.
	 * @param t1
	 * @param t2
	 * @return
	 * @throws SemanticError
	 */
	public static XVar getThisVar(Type t1, Type t2) throws XFailure {
		XVar thisVar = t1 == null ? null : ((X10ThisVar) t1).thisVar();
		if (thisVar == null)
			return t2==null ? null : ((X10ThisVar) t2).thisVar();
		if (t2 != null && ! thisVar.equals(((X10ThisVar) t2).thisVar()))
			throw new XFailure("Inconsistent this vars " + thisVar + " and "
					+ ((X10ThisVar) t2).thisVar());
		return thisVar;
	}

	public static XVar getThisVar(CConstraint t1, CConstraint t2) throws XFailure {
		XVar thisVar = t1 == null ? null : t1.thisVar();
		if (thisVar == null)
			return t2==null ? null : t2.thisVar();
		if (t2 != null && ! thisVar.equals( t2.thisVar()))
			throw new XFailure("Inconsistent this vars " + thisVar + " and "
					+ ((X10ThisVar) t2).thisVar());
		return thisVar;
	}

	public static XVar getThisVar(List<Type> typeArgs) throws XFailure {
		XVar thisVar = null;
		if (typeArgs != null)
			for (Type type : typeArgs) {
				if (type instanceof X10ThisVar) {
					X10ThisVar xtype = (X10ThisVar)type;
					XVar o = xtype.thisVar();
					if (thisVar == null) {
						thisVar = o;
					} else {
						if (o != null && !thisVar.equals(o))
							throw new XFailure("Inconsistent thisVars in " + typeArgs
									+ "; cannot instantiate ");
					}
				}
			}
		return thisVar;
	}

	public static XTerm getRegionLowerBound(Type type) {
		return null;
	}

	public static XTerm getRegionUpperBound(Type type) {
		return null;
	}

	public static boolean hasVar(Type t, XVar x) {
	    if (t instanceof ConstrainedType) {
		ConstrainedType ct = (ConstrainedType) t;
		Type b = baseType(t);
		CConstraint c = Types.xclause(t);
		if ( hasVar(b, x)) return true;
		for (XTerm term : c.constraints()) {
		    if (term.hasVar(x))
			return true;
		}
	    }
	    if (t instanceof MacroType) {
		MacroType pt = (MacroType) t;
		return hasVar(pt.definedType(), x);
	    }
	    return false;
	}

	

	/**
	 * Does t imply {self!=null}?
	 */
	public static boolean isNonNull(Type t) {
	    if (isX10Struct(t)) return true;
		return disEntails(t, Types.self(t), ConstraintManager.getConstraintSystem().xnull());
	}

	public static boolean isNoThisAccess(X10Def def,TypeSystem ts) {
	    return isDefAnnotated(def,ts,"x10.compiler.NoThisAccess");
	}

	public static boolean isNonEscaping(X10Def def,TypeSystem ts) {
	    return isDefAnnotated(def,ts,"x10.compiler.NonEscaping");
	}

	public static boolean isDefAnnotated(X10Def def,TypeSystem ts, String name) {
	    try {
	        Type at = ts.systemResolver().findOne(QName.make(name));
	        return !def.annotationsMatching(at).isEmpty();
	    } catch (SemanticException e) {
	        return false;
	    }
	}

	// this is an under-approximation (it is always safe to return false, i.e., the user will just get more errors). In the future we will improve the precision so more types will have zero.
	public static boolean isHaszero(Type t, Context xc) {
	    TypeSystem ts = xc.typeSystem();
	    XLit zeroLit = null;
	    if (ts.isLongOrLess(t) || ts.isFloat(t) || ts.isDouble(t) || ts.isBoolean(t) || ts.isChar(t)) {
	        zeroLit = ConstraintManager.getConstraintSystem().makeZero(t);  // see Lit_c.constantValue() in its descendants
	    } else if (ts.isObjectOrInterfaceType(t, xc)) {
	        if (Types.permitsNull(xc, t)) return true;
	        //zeroLit = ConstraintManager.getConstraintSystem().NULL;
	    } else if (ts.isParameterType(t)) {
	        // we have some type "T" which is a type parameter. Does it have a zero value?
	        // So, type bounds (e.g., T<:Int) do not help, because  Int{self!=0}<:Int
	        // Similarly, Int<:T doesn't help, because  Int<:Any{self!=null}
	        // However, T==Int does help, and so does an explicit T hasZero
	    	// [DC] shouldn't we be using some kind of entailment check here?
	        TypeConstraint typeConst = xc.currentTypeConstraint();
	        List<SubtypeConstraint> env =  typeConst.terms();
	        for (SubtypeConstraint sc : env) {
	            if (sc.isEqualityConstraint()) {
	                Type other = null;
	                final Type sub = sc.subtype();
	                final Type sup = sc.supertype();
	                if (ts.typeEquals(t, sub,xc)) {
	                    if (!ts.isParameterType(sub)) other = sub;
	                    if (!ts.isParameterType(sup)) other = sup;
	                }
	                if (other!=null &&                                 
	                        isHaszero(other,xc)) { // careful of infinite recursion when calling isHaszero
	                                    // We cannot have infinite recursion because other is not a ParameterType
	                                    // (we can have that T==U, U==Int. but then typeEquals(T,Int,xc) should return true)
	        	        //zeroLit = ConstraintManager.getConstraintSystem().makeZero(t);
	                    return true; // T is equal to another type that has zero
	                }
	            } else if (sc.isSubtypeConstraint()) {
	                // doesn't help
	            } else {
	                assert sc.isHaszero() || sc.isIsRef();
	                if (ts.typeEquals(t,sc.subtype(),xc)) {
	        	        //zeroLit = ConstraintManager.getConstraintSystem().makeZero(t);
	                    return true;
	                }
	            }
	        }
	    } else if (Types.isX10Struct(t)) {
	        if (!(t instanceof ContainerType)) return false;
	        ContainerType structType = (ContainerType) t;
	        // user-defined structs (such as Complex) can have zero iff
	        // 1) They do not have a class invariant
	        // 2) all their fields have zero
	
	        // todo: When ConstrainedType_c.fields() is fixed (it should add the constraint to the fields), then I can remove this "if"
	        if (isConstrained(t)) return false; // currently I don't handle constrained user-defined types, i.e., Complex{re!=3.0} doesn't haszero
	        // far to do: if t is constrained, then a constraint with all fields=zero entails t's constraint
	        // e.g., Complex and Complex{re!=3.0} haszero,
	        // Complex{re!=0.0} and Complex{re==3.0} doesn't haszero
	
	        final Type base = baseType(t);
	        if (!(base instanceof X10ParsedClassType_c)) return false;
	        X10ParsedClassType_c xlass = (X10ParsedClassType_c) base;
	        final ClassDef def = xlass.def();
	        if (!(def instanceof X10ClassDef_c)) return false;
	        X10ClassDef_c x10ClassDef = (X10ClassDef_c) def;
	
	        // do we have an classInvariant? 
	        // todo: class invariant are not treated correctly: 
	        // X10ClassDecl_c.classInvariant is fine, 
	        // but  X10ClassDef_c.classInvariant is wrong
	        final Ref<CConstraint> ref = x10ClassDef.classInvariant();
	        if (ref!=null && ref.get().constraints().size()>0) return false; // the struct has a class invariant (so the zero value might not satisfy it)
	
	        // We use ts.structHaszero to prevent infinite recursion such as in the case of:
	        // struct U(u:U) {}
	        final Boolean res = ts.structHaszero(x10ClassDef);
	        if (res!=null) return res;
	        // it is true for type-checking: S[S[Int]]
	        // struct S[T] {T haszero} {val t:T = Zero.get[T](); }
	        ts.structHaszero().put(x10ClassDef,Boolean.TRUE);
	
	        // make sure all the fields and properties haszero
	        for (FieldInstance field : structType.fields()) {
	            if (field.flags().isStatic()) {
	                continue;
	            }
	            if (!isHaszero(field.type(),xc)) {
	                ts.structHaszero().put(x10ClassDef,Boolean.FALSE);
	                return false;
	            }
	        }
	        return true;
	    }
	    /*
	    if (ts.isParameterType(t)) { // FIXME: why is this code duplicated?
	        // we have some type "T" which is a type parameter. Does it have a zero value?
	        // So, type bounds (e.g., T<:Int) do not help, because  Int{self!=0}<:Int
	        // Similarly, Int<:T doesn't help, because  Int<:Any{self!=null}
	        // However, T==Int does help, and so does an explicit T hasZero
	        TypeConstraint typeConst = xc.currentTypeConstraint();
	        List<SubtypeConstraint> env =  typeConst.terms();
	        for (SubtypeConstraint sc : env) {
	            if (sc.isEqualityConstraint()) {
	                Type other = null;
	                final Type sub = sc.subtype();
	                final Type sup = sc.supertype();
	                if (ts.typeEquals(t, sub,xc)) {
	                    if (!ts.isParameterType(sub)) other = sub;
	                    if (!ts.isParameterType(sup)) other = sup;
	                }
	                if (other!=null)
	                	return isHaszero(other,xc);
	            } else if (sc.isSubtypeConstraint()) {
	                // doesn't help
	            } else {
	                assert sc.isHaszero();
	                if (ts.typeEquals(t,sc.subtype(),xc)) {
	                    return true;
	                }
	            }
	        }
	    }
	    */
	    if (zeroLit == null) return false;
	    if (!isConstrained(t)) return true;
	    final CConstraint constraint = Types.xclause(t);
	    final CConstraint zeroCons = ConstraintManager.getConstraintSystem().makeCConstraint(constraint.self());
	    // make sure the zeroLit is not in the constraint
	    zeroCons.addSelfBinding(zeroLit);
	    return zeroCons.entails(constraint);
	}

	public static boolean isIsRef(Type t, Context xc) {
	    TypeSystem ts = xc.typeSystem();
	    if (ts.isObjectOrInterfaceType(t, xc)) {
	    	return true;
	    } else if (ts.isParameterType(t)) {
	        // we have some type "T" which is a type parameter. Is it a reference?
	    	// Anything <: another type that is isref ought to do it. 
	        TypeConstraint typeConst = xc.currentTypeConstraint();
	        List<SubtypeConstraint> env =  typeConst.terms();
	        for (SubtypeConstraint sc : env) {
	            if (sc.isEqualityConstraint() || sc.isSubtypeConstraint()) {
	                Type other = null;
	                final Type sub = sc.subtype();
	                final Type sup = sc.supertype();
	                if (ts.typeEquals(t, sub,xc)) { // if this guy is T <: sup or T == sup
	                    if (!ts.isParameterType(sup)) other = sup;
	                }
	                if (other!=null &&                                 
	                		isIsRef(other,xc)) // careful of infinite recursion when calling isIsref
	                                    // We cannot have infinite recursion because other is not a ParameterType
	                                    // (we can have that T==U, U==Int. but then typeEquals(T,Int,xc) should return true)
	                    return true; // T is under some other type that is a ref
	            } else if (sc.isIsRef()) {
	                if (ts.typeEquals(t,sc.subtype(),xc)) {
			        	//System.out.println("ASDASD isIsRef "+t+" using "+sc);
	                    return true;
	                }
	            }
	        }
	    }
	    return false;
	}

	public static Expr getZeroVal(TypeNode typeNode, Position p, ContextVisitor tc) { // see X10FieldDecl_c.typeCheck
	    try {
	        Type t = typeNode.type();
	        TypeSystem ts = tc.typeSystem();
	        NodeFactory nf = tc.nodeFactory();
	    	Context context = tc.context();
	        if (!isHaszero(t,context)) return null;
	
	        Expr e = null;
	        if (t.isBoolean()) {
	            e = nf.BooleanLit(p, false);
	        } else if (ts.isLongOrLess(t)) {
	            e = nf.IntLit(p, ConstraintManager.getConstraintSystem().getIntLitKind(t), 0L);
	        } else if (ts.isChar(t)) {
	            e = nf.CharLit(p, '\0');
	        } else if (ts.isFloat(t)) {
	            e = nf.FloatLit(p, FloatLit.FLOAT, 0.0);
	        } else if (ts.isDouble(t)) {
	            e = nf.FloatLit(p, FloatLit.DOUBLE, 0.0);
	        } else if (ts.isObjectOrInterfaceType(t, context)) {
	            e = nf.NullLit(p);
	        } else if (ts.isParameterType(t) || Types.isX10Struct(t)) {
	            TypeNode receiver = nf.CanonicalTypeNode(p, ts.systemResolver().findOne(QName.make("x10.lang.Zero")));
	            //receiver = (TypeNode) receiver.del().typeCheck(tc).checkConstants(tc);
	            e = nf.X10Call(p,receiver, nf.Id(p,"get"),Collections.singletonList(typeNode), Collections.<Expr>emptyList());
	        }
	
	        if (e != null) {
	            e = (Expr) e.del().typeCheck(tc).checkConstants(tc);
	            if (ts.isSubtype(e.type(), t, context)) { // suppose the field is "var i:Int{self!=0}", then you cannot create an initializer which is 0!
	                return e;
	            }
	        }
	        return null;
	    } catch (Throwable e1) {
	        throw new InternalCompilerError(e1);
	    }
	}

	public static List<Type> expandTypes(List<Type> formals, TypeSystem xts) {
		List<Type> result = new ArrayList<Type>();
		for (Type f : formals) {
		    result.add(xts.expandMacros(f));
		}
		return result;
	}

	public static <PI extends X10ProcedureInstance<?>>  boolean isStatic(PI me) {
		if (me instanceof ConstructorInstance) 
			return true;
		if (me instanceof MethodInstance) {
			MethodInstance mi = (MethodInstance) me;
			return mi.flags().isStatic();
		}
		if (me instanceof MacroType) {
			MacroType mt = (MacroType) me;
			return mt.container()==null || mt.flags().isStatic();
		}
		return false;
	}

	public static ProcedureInstance<?> getOrigMI(ProcedureInstance<?> xp) {
		if (xp instanceof MethodInstance)
			return ((MethodInstance) xp).origMI();
		if (xp instanceof ConstructorInstance)
			return ((ConstructorInstance) xp).origMI();
		return xp;
	}

	public static Type instantiateTypeParametersExplicitly(Type t) {
		/*if (t instanceof AnnotatedType) {
			AnnotatedType at = (AnnotatedType) t;
			Type bt = at.baseType();
			Type ibt = instantiateTypeParametersExplicitly(bt);
			if (ibt != bt)
			    return at.baseType(ibt);
			return at;
		} else*/
		if (t instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) t;
			Type bt = get(ct.baseType());
			Type ibt = instantiateTypeParametersExplicitly(bt);
			if (ibt != bt)
			    ct = ct.baseType(ref(ibt));
			return ct;
		} else
		if (t instanceof X10ParsedClassType) {
			X10ParsedClassType pct = (X10ParsedClassType) t;
			pct = pct.instantiateTypeParametersExplicitly();
			List<Type> typeArguments = pct.typeArguments();
			List<Type> newTypeArguments = typeArguments;
			if (typeArguments != null) {
			    List<Type> res = new ArrayList<Type>();
			    for (Type a : typeArguments) {
			        Type ia = instantiateTypeParametersExplicitly(a);
			        if (ia != a)
			            newTypeArguments = res;
			        res.add(ia);
			    }
			}
			pct = pct.typeArguments(newTypeArguments);
			return pct;
		} else {
			return t;
		}
	}

    /**
     * Return the type Rail[type]{self.size==size}.
     * @param type
     * @param pos
     * @return
     */
    public static Type makeRailOf(Type type, long size, Position pos) {
        TypeSystem ts = type.typeSystem();
        X10ClassType t = ts.Rail(type);
        CConstraint c = Types.xclause(t);
        FieldInstance sizeField = t.toClass().fieldNamed(Name.make("size"));
        if (sizeField == null)
            throw new InternalCompilerError("Could not find size field of " + t, pos);
        try {
            XTerm selfSize = ts.xtypeTranslator().translate(c.self(), sizeField);
            XLit sizeLiteral = ConstraintManager.getConstraintSystem().makeLit(size, ts.Long());
            c.addBinding(selfSize, sizeLiteral);
            Type result = Types.xclause(t, c);
            return result;
        } catch (InternalCompilerError z) {
            throw new InternalCompilerError("Could not create Rail[T]{self.size==size}");
        }
    }

	/**
	 * Return the type Array[type]{self.rail==true,self.size==size}.
	 * @param type
	 * @param pos
	 * @return
	 */
	public static Type makeArrayRailOf(Type type, int size, Position pos) {
	    Type t = makeArrayRailOf(type, pos);
	    assert (t.isClass());
	    TypeSystem ts = type.typeSystem();
	    CConstraint c = Types.xclause(t);
	    FieldInstance sizeField = t.toClass().fieldNamed(Name.make("size"));
	    if (sizeField == null)
	        throw new InternalCompilerError("Could not find size field of " + t, pos);
	    try {
	        XTerm selfSize = ts.xtypeTranslator().translate(c.self(), sizeField);
	        XLit sizeLiteral = ConstraintManager.getConstraintSystem().makeLit(size, ts.Int());
	        c.addBinding(selfSize, sizeLiteral);
	        Type result = Types.xclause(t, c);
	        return result;
	    } catch (InternalCompilerError z) {
	        throw new InternalCompilerError("Could not create Array[T]{self.rail==true,self.size==size}");
	    }
	}

	/**
	 * Return the type Array[type]{self.rank==1,self.rect==true,self.zeroBased==true,self.rail==true}.
	 * @param type
	 * @param pos
	 * @return
	 */
	public static Type makeArrayRailOf(Type type, Position pos) {
	    TypeSystem ts = type.typeSystem();
	    X10ClassType t = ts.RegionArray(type);
	    CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
	    FieldInstance regionField = t.fieldNamed(Name.make("region"));
	    if (regionField == null)
	        throw new InternalCompilerError("Could not find region field of " + t, pos);
	    FieldInstance rankField = t.fieldNamed(Name.make("rank"));
	    if (rankField == null)
	        throw new InternalCompilerError("Could not find rank field of " + t, pos);
	    FieldInstance rectField = t.fieldNamed(Name.make("rect"));
	    if (rectField == null)
	        throw new InternalCompilerError("Could not find rect field of " + t, pos);
	    FieldInstance zeroBasedField = t.fieldNamed(Name.make("zeroBased"));
	    if (zeroBasedField == null)
	        throw new InternalCompilerError("Could not find zeroBased field of " + t, pos);
	    FieldInstance railField = t.fieldNamed(Name.make("rail"));
	    if (railField == null)
	        throw new InternalCompilerError("Could not find rail field of " + t, pos);

	    XTypeTranslator xt = ts.xtypeTranslator();
	    XVar self = c.self();
	    XTerm selfRank = xt.translate(self, rankField);
	    XTerm selfRect = xt.translate(self, rectField);
	    XTerm selfZeroBased = xt.translate(self, zeroBasedField);
	    XTerm selfRail = xt.translate(self, railField);

	    XLit rankLiteral = ConstraintManager.getConstraintSystem().makeLit(1L, ts.Long());
	    c.addBinding(selfRank, rankLiteral);
	    c.addBinding(selfRect, ConstraintManager.getConstraintSystem().xtrue());
	    c.addBinding(selfZeroBased, ConstraintManager.getConstraintSystem().xtrue());
	    c.addBinding(selfRail, ConstraintManager.getConstraintSystem().xtrue());
	    return Types.xclause(t, c); 
	}

	public static TypeConstraint parameterBounds(Type t) {
	    if (t instanceof ParameterType) {
	    }
	    else if (t instanceof ConstrainedType) {
	        ConstrainedType ct = (ConstrainedType) t;
	        TypeConstraint bounds = parameterBounds(get(ct.baseType()));
	        if (bounds == null)
	            assert bounds != null;
	        return bounds;
	    }
	    else if (t instanceof X10ClassType) {
	        X10ClassType ct = (X10ClassType) t;
	        TypeConstraint c = get(ct.x10Def().typeBounds());
	        if (c != null)
	            return TypeParamSubst.reinstantiateTypeConstraint(ct, c);
	    }
	    else if (t instanceof MacroType) {
	        MacroType mt = (MacroType) t;
	        TypeConstraint c = parameterBounds(mt.definedType());
	        TypeConstraint w = mt.typeGuard();
	        if (w != null) {
	            c = (TypeConstraint) c.copy();
	            c.addIn(w);
	        }
	        return c;
	    }
	    
	    return new TypeConstraint();
	}

	/**
	 * Returns the real constraint for the type t -- the specified constraint 
	 * (if any), and the root clause of the base type. 
	 * 
	 * <p>If t has a constraint clause (is a ConstrainedType) 
	 * then the returned constraint will have the same 
	 * self var as t's clause. 
	 * @param t - the type whose real clause is needed
	 * @return -- always a non-null constraint. May be inconsistent.
	 */
	public static CConstraint realX(Type t) {
	if (t instanceof ParameterType) {
	    return ConstraintManager.getConstraintSystem().makeCConstraint();
	}
	else if (t instanceof ConstrainedType) {
	        return ((ConstrainedType) t).getRealXClause().copy();
		}
		else if (t instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) t;
			CConstraint c = ct.x10Def().getRealClause().copy();
			return TypeParamSubst.reinstantiateConstraint(ct, c);
		}
		else if (t instanceof MacroType) {
		    MacroType mt = (MacroType) t;
		    CConstraint c = realX(mt.definedType());
		    CConstraint w = mt.guard();
		    if (w != null && ! w.valid()) {
              // c = c.copy();
               c.addIn(w); // c may have become inconsistent.
		    }
		    return c;
		}
	
		return ConstraintManager.getConstraintSystem().makeCConstraint();
	}

	/**
	 * Is t an X10 struct?
	 * @param t
	 * @return
	 */
	public static boolean isX10Struct(Type t) {
		t = baseType(t);
		if (t instanceof X10ClassType) {
			return ((X10ClassType) t).isX10Struct();
		}
		return false;
	}

	/**
	 * If x is a class type, return struct x. Else return x.
	 * @param x
	 * @return
	 */
	public static Type makeX10Struct(Type t) {
		if (t instanceof X10ClassType) 
			return ((X10ClassType) t).makeX10Struct();
		return t;
	}

	public static Type processFlags(Flags f, Type x) {
	    if (f==null)
	        return x;
	    if (f.isStruct()) {
	        x = makeX10Struct(x);
	    }
	    return x;
	}

	public static X10ParsedClassType_c myBaseType(Type t) {
	    Type basetype = baseType(t);
	    // it can be a ParameterType
	    if (basetype instanceof X10ParsedClassType_c) return (X10ParsedClassType_c) basetype;
	    return null;
	}

	public static void setInconsistent(Type t) {
		if (t instanceof MacroType) {
			MacroType mt = (MacroType) t;
			setInconsistent(mt.definedType());
		}
		if (t instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) t;
			CConstraint c = get(ct.constraint());
			c.setInconsistent();
			return;
		}
	}

	public static XVar selfVar(ConstrainedType thisType) {
		return selfVar(thisType.constraint().get());
	}

	public static XVar selfVar(CConstraint c) {
	    if (c == null) return null;
	    return c.self();
	}

	public static XVar selfVarBinding(Type thisType) {
	    CConstraint c = Types.xclause(thisType); // Should this be realX(thisType) ???  - Bowen
	    return selfVarBinding(c);
	}

	public static XVar selfVarBinding(CConstraint c) {
	    if (c == null) return null;
	    return c.bindingForVar(c.self());
	}

	public static XTerm selfBinding(Type thisType) {
	    CConstraint c = realX(thisType);
	    return selfBinding(c);
	}

	public static XTerm selfBinding(CConstraint c) {
	    if (c == null) return null;
	    return c.bindingForVar(c.self());
	}

	public static Type setSelfVar(Type t, XVar v) throws SemanticException {
		CConstraint c = Types.xclause(t);
		if (c == null) {
			c = ConstraintManager.getConstraintSystem().makeCConstraint();
		}
		else {
			c = c.copy();
		}
		c.addSelfBinding(v);
		return Types.xclause(baseType(t), c);
	}

	public static Type setThisVar(Type t, XVar v) throws SemanticException {
	    CConstraint c = Types.xclause(t);
	    if (c == null) {
	        c = ConstraintManager.getConstraintSystem().makeCConstraint();
	    }
	    else {
	        c = c.copy();
	    }
	    
		c.setThisVar(v);
	    return Types.xclause(baseType(t), c);
	}

	/**
	 * If the type constrains the given property to
	 * a particular value, then return that value, otherwise 
	 * return null
	 * @param name -- the name of the property.
	 * @return null if there is no value associated with the property in the type.
	 */
	public static XTerm propVal(Type t, Name name) {
	    CConstraint c = Types.xclause(t);
	    if (c == null) return null;
		return c.bindingForSelfField(Types.getProperty(t, name).def());
	}

	public static Type promote(Unary.Operator op, JavaPrimitiveType t) throws SemanticException {
	    TypeSystem ts = (TypeSystem) t.typeSystem();
	    Type pt =  ts.promote(t);
	    return  Types.xclause(baseType(pt), 
	    		promoteClause(ts, op, Types.xclause(t)));
	}

	public static CConstraint promoteClause(TypeSystem ts, polyglot.ast.Unary.Operator op, CConstraint c) {
	    if (c == null)
	        return null;
	
	    return ts.xtypeTranslator().unaryOp(op, c);
	}

	public static Type promote(Binary.Operator op, JavaPrimitiveType t1, JavaPrimitiveType t2) throws SemanticException {
	    TypeSystem ts = (TypeSystem) t1.typeSystem();
	    Type pt =  ts.promote(t1, t2);
	    return  Types.xclause(baseType(pt), 
	    		promoteClause(ts, op, Types.xclause(t1), Types.xclause(t2)));
	}

	public static CConstraint promoteClause(TypeSystem ts, Operator op, CConstraint c1, CConstraint c2) {
	    if (c1 == null || c2 == null)
	        return null;
	    return ts.xtypeTranslator().binaryOp(op, c1, c2);
	}

	public static List<FieldInstance> properties(Type t) {
	    t = baseType(t);
	    if (t instanceof X10ClassType) {
	        X10ClassType ct = (X10ClassType) t;
	        return ct.properties();
	    }
	    return Collections.<FieldInstance>emptyList();
	}

	public static boolean isX10RegionArray(Type t) {
	    TypeSystem ts = (TypeSystem) t.typeSystem();
	    Type tt = baseType(t);
	    Type at = baseType(ts.RegionArray());
	    if (tt instanceof ClassType && at instanceof ClassType) {
	        ClassDef tdef = ((ClassType) tt).def();
	        ClassDef adef = ((ClassType) at).def();
	        return ts.descendsFrom(tdef, adef);
	    }
	    return false;
	}

    public static boolean isX10Rail(Type t) {
        TypeSystem ts = (TypeSystem) t.typeSystem();
        Type tt = baseType(t);
        Type at = baseType(ts.Rail());
        if (tt instanceof ClassType && at instanceof ClassType) {
            ClassDef tdef = ((ClassType) tt).def();
            ClassDef adef = ((ClassType) at).def();
            return ts.descendsFrom(tdef, adef);
        }
        return false;
    }

	public static XVar self(Type t) {
	    CConstraint c = realX(t);
	    if (c == null)
		    return null;
	    return selfVar(c);
	}
	/**
	 * Are instances of this type accessible from anywhere?
	 * @param t
	 * @return
	
	public static boolean isGlobalType(Type t) {
		if (isX10Struct(t))
			return true;
		return false;
		
	}
	*/

	/**
	 * We need to ensure that there is a symbolic name for this type. i.e. self is bound to some variable.
	 * So if it is not, please create a new EQV and bind self to it. 
	 * 
	 * This is done  in particular before getting field instances of this type. This ensures
	 * that the field instance can be computed accurately, that is the constraint
	 * self = t.f can be added to it, where t is the selfBinding for the container (i.e. this).
	 * 
	 */
	
	/*public static Type ensureSelfBound(Type t) {
		if (t instanceof ConstrainedType) {
			((ConstrainedType) t).ensureSelfBound();
			return t;
		}
		XVar v = selfVarBinding(t);
		if (v !=null) 
			return t;
		try {
			t = setSelfVar(t,ConstraintManager.getConstraintSystem().makeUQV());
		} catch (SemanticException z) {
			
		}
		if (selfVarBinding(t) == null)
		assert selfVarBinding(t) != null;
		return t;
	}
	*/
	
	public static boolean isUninitializedField(X10FieldDef def,TypeSystem ts) {
	    return isDefAnnotated(def,ts,"x10.compiler.Uninitialized");
	}

	public static boolean isSuppressTransientErrorField(X10FieldDef def,TypeSystem ts) {
	    return isDefAnnotated(def,ts,"x10.compiler.SuppressTransientError");
	}

	public static boolean permitsNull(Context cxt, Type t) {
		if (isX10Struct(t))
			return false;
		CConstraint c = Types.xclause(t);
		if (c!= null && ! c.valid()) {
			c.addIn(cxt.constraintProjection(c));
			if (c.disEntails(c.self(), ConstraintManager.getConstraintSystem().xnull()))
				return false;
		}
		TypeSystem ts = t.typeSystem();
		if (ts.isParameterType(t)) {			
			TypeConstraint tc = cxt.currentTypeConstraint();
			boolean haszero = false;
			boolean isref = false;
			for (SubtypeConstraint term : tc.terms()) {
				if (term.subtype().typeEquals(t, cxt)) {
					if (term.isHaszero()) haszero = true;
					if (term.isIsRef()) isref = true;
				}
			}
			return isref && haszero;
		}
		return true;
	}

	public static Type meetTypes(TypeSystem xts, Type t1, Type t2, Context context) {
	    if (xts.isSubtype(t1, t2, context))
	        return t1;
	    if (xts.isSubtype(t2, t1, context))
	        return t2;
	    return null;
	}

	public static boolean moreSpecificImpl(Type ct, ProcedureInstance<?> xp1, ProcedureInstance<?> xp2, Context context) {
	    TypeSystem ts = (TypeSystem) xp1.typeSystem();
	    Type ct1 = xp2 instanceof MemberInstance<?> ? ((MemberInstance<?>) xp1).container() : null;
	    Type ct2 = xp2 instanceof MemberInstance<?> ? ((MemberInstance<?>) xp2).container() : null;
	
	    Type t1 = ct1;
	    Type t2 = ct2;
	    if (t1 != null && t2 != null) {
	        t1 = baseType(t1);
	        t2 = baseType(t2);
	    }
	
	    boolean descends = t1 != null && t2 != null && ts.descendsFrom(ts.classDefOf(t1), ts.classDefOf(t2));
	
	    Flags flags1 = xp1 instanceof MemberInstance<?> ? ((MemberInstance<?>) xp1).flags() : Flags.NONE;
	    Flags flags2 = xp2 instanceof MemberInstance<?> ? ((MemberInstance<?>) xp2).flags() : Flags.NONE;
	
	    // A static method in a subclass is always more specific.
	    // Note: this rule differs from Java but avoids an anomaly with conversion methods.
	    if (descends && ! ts.hasSameClassDef(t1, t2) && flags1.isStatic() && flags2.isStatic()) {
	        return true;
	    }
	    Reporter reporter = ts.extensionInfo().getOptions().reporter;
	    boolean java = javaStyleMoreSpecificMethod(xp1, xp2, (Context) context, ct1, t1, t2,descends);
	    if (reporter.should_report(Reporter.specificity, 1)) {
	        boolean old = oldStyleMoreSpecificMethod(xp1, xp2, (Context) context, ts, ct1, t1, t2, descends);
	        if (java != old) {
	            String msg = Types.MORE_SPECIFIC_WARNING +
	            ((java && ! old) ? "p1 is now more specific than p2; it was not in 2.0.6."
	                    : "p1 is now not more specific than p2; it was in 2.0.6.")
	                    + "\n\t: p1: " + getOrigMI(xp1)
	                    + "\n\t: at " + xp1.position()
	                    + "\n\t: p2: " + getOrigMI(xp2)
	                    + "\n\t: at " + xp2.position()
	                    + "\n\t: t1 is  " + t1
	                    + "\n\t: t2 is " + t2;
	            //new Error().printStackTrace();
	            reporter.report(1, "Warning: "+msg);
	        }
	    }
	    // Change this to return old to re-enable 2.0.6 style computation.
	    return  java; 
	}

	// This is taken from the 2.0.6 implementation.
	// This contains logic for pre-generic Java. One determines
	// that a method MI1 is more specific than MI2 if each argument of
	// MI1 is a subtype of the corresponding argument of MI2. That is,
	// MI2 is taken as the instance of the method definition for the given
	// call. Hence no type inference is done. 
	private static boolean oldStyleMoreSpecificMethod(
			ProcedureInstance<?> xp1, ProcedureInstance<?> xp2,
			Context context, TypeSystem ts, Type ct1, Type t1, Type t2,
			boolean descends) {
	    XVar[] ys = toVarArray(toLocalDefList(xp2.formalNames()), getPlaceTerm(xp2));
	    XVar[] xs = toVarArray(toLocalDefList(xp1.formalNames()), getPlaceTerm(xp1));
	    // if the formal params of p1 can be used to call p2, p1 is more specific
	    if (xp1.formalTypes().size() == xp2.formalTypes().size() ) {
	        for (int i = 0; i < xp1.formalTypes().size(); i++) {
	            Type f1 = xp1.formalTypes().get(i);
	            try {
	                f1 = Subst.subst(f1, ys, xs, new Type[]{}, new ParameterType[]{});
	            } catch (SemanticException e) {
	                throw new InternalCompilerError("Unexpected exception while comparing methods", e);
	            }
	            Type f2 = xp2.formalTypes().get(i);
	            // Ignore constraints.  This avoids an anomaly with the translation with erased constraints
	            // having inverting the result of the most-specific test.  Fixes XTENLANG-455.
	            Type b1 = baseType(f1);
	            Type b2 = baseType(f2);
	            if (! ts.isImplicitCastValid(b1, b2, context)) {
	                return false;
	            }
	        }
	    }
	
	    // If the formal types are all equal, check the containers; otherwise p1 is more specific.
	    for (int i = 0; i < xp1.formalTypes().size(); i++) {
	        Type f1 = xp1.formalTypes().get(i);
	        Type f2 = xp2.formalTypes().get(i);
	        if (! ts.typeEquals(f1, f2, context)) {
	            return true;
	        }
	    }
	
	    if (t1 != null && t2 != null) {
	        // If p1 overrides p2 or if p1 is in an inner class of p2, pick p1.
	        if (descends) {
	            return true;
	        }
	        if (t1.isClass() && t2.isClass()) {
	            if (t1.toClass().isEnclosed(t2.toClass())) {
	                return true;
	            }
	        }
	        return false;
	    }
	
	    return true;
	}

	static ProcedureInstance<?> orig(ProcedureInstance<?> xp) {
		if (xp instanceof MethodInstance) {
		return ((MethodInstance) xp).origMI();
		}
		if (xp instanceof X10ConstructorInstance) {
			return ((X10ConstructorInstance) xp).origMI();
		}
		assert false;
		return null;
	}
	/**
	 * 
	 * @param xp1 -- the first procedure instance
	 * @param xp2 -- the second procedure instance
	 * @param context -- the context for the original call
	 * @param ts
	 * @param ct1
	 * @param t1 -- base type of ct1
	 * @param t2 -- base type of the container of xp2.
	 * @param descends -- does t1 descend from t2?
	 * @return
	 */
	private static boolean javaStyleMoreSpecificMethod(
			ProcedureInstance<?> xp1, ProcedureInstance<?> xp2,
			Context context, Type ct1, Type t1, Type t2,
			boolean descends) {
		assert xp1 != null;
		assert xp2 != null;
		assert context != null;
		TypeSystem ts = (TypeSystem) context.typeSystem();
	    
	    	try {
	    		if (xp2 instanceof MethodInstance) {
	    			// Both xp1 and xp2 should be X10MethodInstance's 
	    			MethodInstance xmi2 = (MethodInstance) xp2;
	    			MethodInstance origMI2 = (MethodInstance) xmi2.origMI();
	    			assert origMI2 != null;
	    			
	    			if (! (xp1 instanceof MethodInstance))
	    				return false;
	    			MethodInstance xmi1 = (MethodInstance) xp1;
	    			MethodInstance origMI1 = (MethodInstance)xmi1.origMI();
	    			assert origMI1 != null;
	    			
	    			// Now determine that a call can be made to thisMI2 using the
	    			// argument list obtained from thisMI1. If not, return false.
	    			List<Type> argTypes = new ArrayList<Type>(origMI1.formalTypes());
	    			XVar[] ys = toVarArray(toLocalDefList(origMI2.formalNames()), getPlaceTerm(origMI2));
	    			XVar[] xs = toVarArray(toLocalDefList(origMI1.formalNames()), getPlaceTerm(origMI1));
	    			try {
	    			    argTypes = Subst.subst(argTypes, ys, xs);
	    			} catch (SemanticException e) {
	    			    throw new InternalCompilerError("Unexpected exception while comparing methods", e);
	    			}
	    			if (xp2.formalTypes().size() != argTypes.size())
	        			return false;
	    			// For xp1 to be more specific than xp2, it must have the same number of type parameters
	    			//if (xmi1.typeParameters().size() != 0 && (xmi2.typeParameters().size() != xmi1.typeParameters().size()))
	    			//	return false;
	    			// TODO: Establish that the current context is aware of the method
	    			// guard for xmi1.
	    			List<Type> typeArgs = origMI1.typeParameters(); // pass in the type parameters, no need to infer
	    			MethodInstance r = null;
	    			try { 
	    				r=Matcher.inferAndCheckAndInstantiate(context, origMI2, ct1, typeArgs, argTypes, xp2.position());
	    			} catch (SemanticException z) {
	    				
	    			}
	    					
	    			if (r == null){
	    				r = Matcher.inferAndCheckAndInstantiate(context, 
		    					origMI2, ct1, Collections.<Type>emptyList(), argTypes, xp2.position());
	    				if (r == null){
		    				return false;
		    			}
	    				
	    			}
	    			// fall through, we know that xp1 can be used to make a call to xp2
	    			
	    		} else  if (xp2 instanceof X10ConstructorInstance) {
	    			// Both xp1 and xp2 should be X10ConstructorInstance's 
	                X10ConstructorInstance xmi2 = (X10ConstructorInstance) xp2;
	                X10ConstructorInstance origMI2 = (X10ConstructorInstance) xmi2.origMI();
	                assert origMI2 != null;
	                
	            	if (! (xp1 instanceof X10ConstructorInstance))
	    				return false;
	            	X10ConstructorInstance xmi1 = (X10ConstructorInstance) xp1;
	            	X10ConstructorInstance origMI1 = (X10ConstructorInstance) xmi1.origMI();
	            	assert origMI1 != null;
	            	List<Type> argTypes = new ArrayList<Type>(origMI1.formalTypes());
	            	XVar[] ys = toVarArray(toLocalDefList(origMI2.formalNames()));
	            	XVar[] xs = toVarArray(toLocalDefList(origMI1.formalNames()));
	            	try {
	            	    argTypes = Subst.subst(argTypes, ys, xs);
	            	} catch (SemanticException e) {
	            	    throw new InternalCompilerError("Unexpected exception while comparing constructors", e);
	            	}
	    			if (xp2.formalTypes().size() != argTypes.size())
	        			return false;
	    			// TODO: Figure out how to do type inference.
	    			List<Type> typeArgs = xmi2.typeParameters();
	                X10ConstructorInstance r=null;
	                try {
	                	r= Matcher.inferAndCheckAndInstantiate( context,  origMI2, ct1, typeArgs, argTypes, xp2.position());
	                } catch (SemanticException z) {
	                	
	                }
	                if (r == null) {
	                	r = Matcher.inferAndCheckAndInstantiate(context, 
		    					origMI2, ct1, Collections.<Type>emptyList(), argTypes, xp2.position());
	                	if (r == null)
	                	return false;
	                }    
	            
	             // fall through, we know that xp1 can be used to make a call to xp2
	            }	else {
	            	// Should not happen.
	            	// System.out.println("Diagnostic. Unhandled MoreSpecificMatcher case: " + xp2 + " class " + xp2.getClass());
	            	assert false;	
	            }
	    	} catch (SemanticException z) {  		
	    		return false;
	    	}
	// I have kept the logic below from 2.0.6 for now.
	// [DC] I have changed it post 2.3 to handle generics properly
	// TODO: Determine whether this should stay or not.
	    // If the formal types are all equal, check the containers; otherwise p1 is more specific.
	    XVar[] ys = toVarArray(toLocalDefList(xp2.formalNames()));
	    XVar[] xs = toVarArray(toLocalDefList(xp1.formalNames()));
	    // Need to substitute the same variable for the thisVars of the two methods
	    // since they could be different. e.g. if pi is defined on Ci, then the
	    // thisVars will be C1#this and C2#this.Neeed 
	    XVar thisVar = ConstraintManager.getConstraintSystem().makeUQV();
	    xp1 = orig(xp1);
	    xp2 = orig(xp2);
	    boolean ascends = t1 != null && t2 != null && ts.descendsFrom(ts.classDefOf(t2), ts.classDefOf(t1));
	    boolean stacked = descends || ascends;
	    TypeParamSubst tps = null;
	    
	    List<ParameterType> tps1 = xp1.def().typeParameters();
	    List<ParameterType> tps2 = xp2.def().typeParameters();
	    if (tps1.size() == tps2.size()) {
		    if (ascends) {
		    	tps = new TypeParamSubst(ts, tps2, tps1);
		    } else if (descends) {
		    	tps = new TypeParamSubst(ts, tps1, tps2);
		    }
	    }
	    
	    XVar p1This = stacked ? xp1.def().thisVar(): null;
	    XVar p2This = stacked ? xp2.def().thisVar() : null;
	    for (int i = 0; i < xp1.formalTypes().size(); i++) {
	    	// Need to use original type information. The current type
	    	// information may have call specific uqv's in the constraints
	    	// rather than the declared formals.

	    	Type f1 = xp1.formalTypes().get(i);
	        Type f2 = xp2.formalTypes().get(i);
	        
	        if (tps != null) {
		        f1 = tps.reinstantiate(f1);
		        f2 = tps.reinstantiate(f2);
	        }
	       
	    	// [DC] in that case need to do the type substitution
	        try {
	            f1 = Subst.subst(f1, ys, xs); // , new Type[]{}, new ParameterType[]{});
	            if (stacked) {
	                // Substitute for both p1This and p2This, since only one of them
	                // might occur in both.
	                f1 = Subst.subst(f1, thisVar, p1This);
	                f1 = Subst.subst(f1, thisVar, p2This);
	            }
	        } catch (SemanticException e) {
	            throw new InternalCompilerError("Unexpected exception while comparing methods", e);
	        }
	        if (stacked)try { 
	            
	                // Substitute for both p1This and p2This, since only one of them
                    // might occur in both.
	                f2 = Subst.subst(f2, thisVar, p2This);
	                f2 = Subst.subst(f2, thisVar, p1This);
	            
	        } catch (SemanticException e) {
	            throw new InternalCompilerError("Unexpected exception while comparing methods", e);
	        }
	        if (! ts.typeEquals(f1, f2, context)) {
	        	return true;
	        }
	    }
	// the types are all equal, check the containers
	    if (t1 != null && t2 != null) {
	        // If p1 overrides p2 or if p1 is in an inner class of p2, pick p1.
	        if (descends) {
	            return true;
	        }
	        if (t1.isClass() && t2.isClass()) {
	            if (t1.toClass().isEnclosed(t2.toClass())) {
	                return true;
	            }
	        }
	      // p1 may be intfc method, p2 the implementing method
	        return false;
	    }
	
	    return true;
	}

	public static XVar getPlaceTerm(ProcedureInstance<?> mi) {
	    XConstrainedTerm pt = ((ProcedureDef) mi.def()).placeTerm();
	    return pt != null && pt.term() instanceof XVar ? (XVar) pt.term() : ConstraintManager.getConstraintSystem().makeUQV();
	}

	public static boolean isTypeConstraintExpression(Expr e) {
	    if (e instanceof ParExpr) 
	        return isTypeConstraintExpression(((ParExpr) e).expr());
	    else if (e instanceof Unary_c)
	        return isTypeConstraintExpression(((Unary) e).expr());
	    else if (e instanceof SubtypeTest)
	        return true;
	    else if (e instanceof HasZeroTest)
	        return true;
	    else if (e instanceof IsRefTest)
	        return true;
	    return false;
	}

	/**
	 * Return T if type implements Reducer[T];
	 * @param type
	 * @return
	 */
	public static Type reducerType(Type type) {
		TypeSystem ts = (TypeSystem) type.typeSystem();
			Type base = baseType(type);
	
			if (base instanceof X10ClassType) {
				if (ts.hasSameClassDef(base, ts.Reducible())) {
					return getParameterType(base, 0);
				}
				else {
					Type sup = ts.superClass(type);
					if (sup != null) {
						Type t = reducerType(sup);
						if (t != null) return t;
					}
					for (Type ti : ts.interfaces(type)) {
						Type t = reducerType(ti);
						if (t != null) {
							return t;
						}
					}
				}
			}
			return null;
		}

	public static Type typeArg(Type t, int i) {
	    if (t instanceof X10ParsedClassType) {
	        X10ParsedClassType ct = (X10ParsedClassType) t;
	        return ct.typeArguments().get(i);
	    } 
	    return typeArg(baseType(t), i);
	}

    ////////////////////////////////////////////////////////////////
    // For better error reporting, we remove the constraints if we ran with DYNAMIC_CALLS.
    public static Type stripConstraintsIfDynamicCalls(Type t) {
        if (t==null) return null;
	    if (((X10CompilerOptions)t.typeSystem().extensionInfo().getOptions()).x10_config.STATIC_CHECKS)
            return t;
        return stripConstraints(t);
    }
	public static Collection<Type> stripConstraintsIfDynamicCalls(Collection<Type> t) {
        if (t==null) return null;
        if (t.size()==0) return t;
	    if (((X10CompilerOptions)t.iterator().next().typeSystem().extensionInfo().getOptions()).x10_config.STATIC_CHECKS)
            return t;
        ArrayList<Type> res = new ArrayList<Type>(t.size());
        for (Type tt : t)
            res.add(stripConstraints(tt));
        return res;
    }
    // completely erases all constraints (from type arguments as well)
	public static Type stripConstraints(Type t) {
	    TypeSystem ts = (TypeSystem) t.typeSystem();
	    t = ts.expandMacros(t);
	    t = baseType(t);
	    if (t instanceof X10ClassType) {
	        X10ClassType ct = (X10ClassType) t;
	        if (ct.typeArguments() == null)
	            return ct;
	        List<Type> types = new ArrayList<Type>(ct.typeArguments().size());
	        for (Type ti : ct.typeArguments()) {
	            Type ti2 = stripConstraints(ti);
	            types.add(ti2);
	        }
	        return ct.typeArguments(types);
	    }
	    return t;
	}

	public static Type superClass(Type t) {
		t = baseType(t);
		assert t instanceof ClassType;
		return ((ClassType) t).superClass();
	}

	public static CConstraint tryAddingConstraint(Type t, CConstraint xc)  {
		 CConstraint c = Types.xclause(t);
	     c = c == null ? ConstraintManager.getConstraintSystem().makeCConstraint() :c.copy();
	     c.addIn(xc);
	     return c;
	}

	public static ConstrainedType toConstrainedType(Type t) {
		ConstrainedType result;
		if (t instanceof ConstrainedType) {
			result=(ConstrainedType) t;
		} else {
			result = constrainedType(t, ConstraintManager.getConstraintSystem().makeCConstraint());
		}
		return result;
	}

	public static XVar thisVar(XVar xthis, Type thisType) {
	    Type base = baseType(thisType);
	    if (base instanceof X10ClassType) {
	        XVar supVar = ((X10ClassType) base).x10Def().thisVar();
	        return supVar;
	    }
	    return xthis;
	}

	/**
	 * Return the constraint c entailed by the assertion v is of type t.
	 * @param v
	 * @param t
	 * @return
	 */
	public static CConstraint xclause(XVar v, Type t) {
		CConstraint c = xclause(t);
		return c.instantiateSelf(v);
	}

	/**
	 * Returns a copy of t's constraint, if it has one, null otherwise.
	 * @param t
	 * @return
	 */
	public static CConstraint xclause(Type t) {
	        if (t instanceof MacroType) {
	                MacroType mt = (MacroType) t;
	                return xclause(mt.definedType());
	        }
		if (t instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) t;
			return ct.xclause();
		}
		if (t instanceof X10ParsedClassType) {
			X10ParsedClassType ct = (X10ParsedClassType) t;
			return ct.getXClause().copy();
		}
		return null;
	}
	


	public static Type xclause(Type t, CConstraint c) {
		if (t == null)
			return null;
		if (c == null /*|| c.valid()*/) {
			return t;
		}
		return ConstrainedType.xclause(ref(t), ref(c));
	}
	public static X10FieldInstance getProperty( Type t, Name propName) {
	    TypeSystem xts = t.typeSystem();
	    try {
	        Context c = xts.emptyContext();
	        X10FieldInstance fi = xts.findField(t, t, propName, c);
	        if (fi != null && fi.isProperty()) {
	            return fi;
	        }
	    }
	    catch (SemanticException e) {
	        // ignore
	    }
	    return null;
	}
	
	public static MethodInstance getPropertyMethod(Type t, Name propName) {
	    TypeSystem xts = t.typeSystem();
	    try {
	        Context c = xts.emptyContext();
	        MethodInstance mi = xts.findMethod(t, xts.MethodMatcher(t, propName, Collections.<Type>emptyList(), c));
	        if (mi != null && mi.flags().isProperty()) {
	            return mi;
	        }
	    }
	    catch (SemanticException e) {
	        // ignore
	    }
	    return null;
	}


	/**
	 * Determine if xp1 is more specific than xp2 given some (unknown) current call c to a method m or a constructor
	 * for a class or interface Q (in the given context). (Note that xp1 and xp2 may not be function definitions since
	 * no method resolution is not necessary for function definitions.)
	 * 
	 * <p> We may assume that xp1 and xp2 are instantiations of underlying (possibly generic) procedure definitions, 
	 * pd1 and pd2 (respectively) that lie in the applicable and available method call set for c. 
	 * 
	 * <p> The determination is done as follows. First, if xp1 is an instance of a static method on a class C1, and xp2
	 * is an instance of a static method on a class C2, and C1 is distinct from C2 but descends from it,
	 * Otherwise we examine pd1 and pd2 -- the underlying possibly generic method definitions. Now pd1 is more 
	 * specific than pd2 if a call can be made to pd2 with the information available about pd1's arguments. As usual,
	 * type parameters of pd2 (if any) are permitted to be instantiated during this process.
	 * @param ct -- represents the container on which both xp1 and xp2 are available. Ignored now. TODO: Remove the machinery
	 * introduced to permit ct to be available in this call to moreSpecificImpl.
	 * @param xp1 -- the instantiated procedure definition.
	 * @param xp2
	 * @param context
	 * @return
	 */
	public static String MORE_SPECIFIC_WARNING = "Please check definitions p1 and p2.  ";



    private static Type instantiateThis(X10ParsedClassType_c classType, Type t, Type superType) {
        try {
            return X10FieldMatcher.instantiateAccess(t,superType,classType.x10Def().thisVar(),false);
        } catch (SemanticException e) {
            throw new InternalCompilerError(e);
        }
    }
    //abstract class A implements Iterable<A> {}
    //abstract class B extends A implements Iterable<B> {} // ERR in Java, but ok in X10
    // There can be multiple Iterable[T] due to type parameter upper bounds: X<:Dist && X<:Iterable[...]
    public static HashSet<Type> getIterableIndex(Type t, Context context) {
        HashSet<Type> res = new HashSet<Type>();
        final TypeSystem ts = t.typeSystem();
        Type base = Types.baseType(t);
        if (ts.isParameterType(base)) {
            // Now get the upper bound.
            List<Type> upperBounds = ts.env(context).upperBounds(t, false); // should return non-parameter types
            for (Type upper : upperBounds)
                res.addAll(getIterableIndex(upper, context));
        }
        if (t instanceof ObjectType && base instanceof X10ParsedClassType_c) {
            X10ParsedClassType_c classType_c = (X10ParsedClassType_c) base;
            ObjectType ot = (ObjectType) t;
            final Type superType = ot.superClass();
            if (superType!=null) res.addAll(getIterableIndex(instantiateThis(classType_c,t,superType),context));
            final List<Type> interfaces = ot.interfaces();
            for (Type tt : interfaces)
                res.addAll(getIterableIndex(instantiateThis(classType_c,t,tt),context));

            if (base instanceof X10ParsedClassType) {
                X10ParsedClassType classType = (X10ParsedClassType) base;
                final ClassDef iterable = ts.Iterable().def();
                if (classType.def()==iterable && classType.typeArguments().size()==1) {
                    Type arg = classType.typeArguments().get(0);
                    CConstraint xclause = Types.xclause(t);
			        final XVar tt = ConstraintManager.getConstraintSystem().makeEQV();
                    try {
                        xclause = Subst.subst(xclause, tt, xclause.self());
                    } catch (SemanticException e) {
                        assert false;
                    }
                    res.add(Types.xclause(arg, xclause));
                } else if (ts.typeIsJLIterable(classType)) {
                    Type arg = ts.Any();
                    CConstraint xclause = Types.xclause(t);
			        final XVar tt = ConstraintManager.getConstraintSystem().makeEQV();
                    try {
                        xclause = Subst.subst(xclause, tt, xclause.self());
                    } catch (SemanticException e) {
                        assert false;
                    }
                    res.add(Types.xclause(arg, xclause));
                }
            }
        }
        return res;
    }

	public static Type removeLocals(Context ctx, Type t) {
		t = t.typeSystem().expandMacros(t);
	
	    if (t instanceof X10ClassType) {
	        X10ClassType ct = (X10ClassType) t;
	        if (ct.typeArguments() == null)
	            return ct;
	        List<Type> types = new ArrayList<Type>(ct.typeArguments().size());
	        for (Type ti : ct.typeArguments()) {
	            Type ti2 = removeLocals(ctx, ti);
	            types.add(ti2);
	        }
	        return ct.typeArguments(types);
	    }
	    Type b = baseType(t);
	    if (b != t)
	        b = removeLocals(ctx, b);
	    CConstraint c = xclause(t);
	    if (c == null)
	        return b;
	    c = removeLocals(ctx, c);
	    return xclause(b, c);
	}

	/** Remove all local variables that are not formals from this constraint.
	 * 
	 * @param cxt
	 * @param c0
	 * @return
	 */
	public static CConstraint removeLocals(Context cxt, CConstraint c0) {
	    CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
	    c.addIn(c0); // ensure that this has a different selfVar.
	    Set<? extends XTerm> roots = c.getTerms();
	    if (roots.size() > 0) {
	        for (XTerm term : roots) {
	            if (term instanceof XVar) {
	                XVar[] vars = ((XVar) term).vars();
	                if (vars.length > 0) { // lits can have length 0
	                    XVar root = vars[0];
	                    if (root instanceof CLocal) {
	                        CLocal rootL = (CLocal) root;
	                        X10LocalDef name = rootL.localDef();
	                        if (! cxt.isFormalParameter(name) && cxt.isLocalExcludingAsyncAt(name.name())) {
	                            // This is a local variable. Eliminate!
	                            c = c.project(rootL);
	                        }
	                    }
	                }
	            }
	        }
	    }
	   return c;
	}

	public static XVar[] toVarArray(List<? extends VarDef> formalNames) {
	    int size = formalNames.size();
	    XVar[] oldFNs = new XVar[size];
	    toVarArray(oldFNs, 0, formalNames);
	    return oldFNs;
	}

	public static XVar[] toVarArray(List<? extends VarDef> formalNames, XVar v) {
	    int size = formalNames.size();
	    XVar[] oldFNs = new XVar[size+1];
	    toVarArray(oldFNs, 0, formalNames);
	    oldFNs[size] = v;
	    return oldFNs;
	}

	public static XVar[] toVarArray(List<? extends VarDef> formalNames, XVar v, XVar w) {
	    int size = formalNames.size();
	    XVar[] oldFNs = new XVar[size+2];
	    toVarArray(oldFNs, 0, formalNames);
	    oldFNs[size] = v;
	    oldFNs[size+1] = w;
	    return oldFNs;
	}

	private static void toVarArray(XVar[] oldFNs, int offset, List<? extends VarDef> formalNames) {
	    int sz = formalNames.size();
	    for (int i = 0; i < sz; i++) {
	        VarDef f = formalNames.get(i);
	        if (f instanceof X10LocalDef) {
	            oldFNs[offset+i] = ConstraintManager.getConstraintSystem().makeLocal((X10LocalDef) f);
	        } else if (f instanceof ThisDef) {
	            oldFNs[offset+i] = ((ThisDef) f).thisVar();
	        }
	    }
	}

	public static List<LocalDef> toLocalDefList(List<LocalInstance> lis) {
	    return new TransformingList<LocalInstance, LocalDef>(lis,
	            new Transformation<LocalInstance,LocalDef>() {
	                public LocalDef transform(LocalInstance o) {
	                    return o.def();
	                }
	            });
    }

    public static ClassType getClassType(Type t, TypeSystem ts, Context context) {
        Type baseType = Types.baseType(t);
        if (baseType instanceof ParameterType) {
            final List<Type> upperBounds = ts.env(context).upperBounds(baseType, false);
            baseType = null;
            for (Type up : upperBounds) {
                if (!(Types.baseType(up) instanceof ParameterType))
                    baseType = up;
            }
        }
        return baseType==null ? null : baseType.toClass();
    }
    public static ClassType getClassType(Type t, TypeSystem ts) {
        return getClassType(t, ts, null);
    }
    
    public static Type projectOutLocalVariables(Context cxt, Type t) {
    	CConstraint c0 = xclause(t);
    	if (c0==null)
    		return t;
    	CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
    	c.addIn(c0); // ensure that this has a different selfVar.
    	Set<? extends XTerm> roots = c.getTerms();
    	if (roots.size() > 0) {
    		CodeDef def = cxt.currentCode();
    		List<LocalDef> formals = null;
    		if (def instanceof ProcedureDef) {
    			formals = ((ProcedureDef) def).formalNames();
    		}
    		for (XTerm term : roots) {
    			if (term instanceof XVar) {
    				XVar[] vars = ((XVar) term).vars();
    				XVar root = vars[0];
    				if (root instanceof CLocal) {
    					CLocal rootL = (CLocal) root;
    					X10LocalDef name = rootL.localDef();
    					if (formals != null && ! formals.contains(name)) {
    						// This is a local variable. Eliminate!
    						c = c.project(rootL);
    					}
    				}
    			}
    		}
    	}
    	return constrainedType(baseType(t), c);
    }
    public static Type addInOuterClauses(Type type, Type outer) {
        CConstraint xclause = Types.xclause(outer);
        if (xclause != null && ! xclause.valid()) {
            // there is some information to transfer.
            CConstraint result = ConstraintManager.getConstraintSystem().makeCConstraint();
            XVar qvar = new QualifiedVar(Types.baseType(outer), result.self());
            xclause = xclause.instantiateSelf(qvar);
            result.addIn(xclause);
            type = Types.addConstraint(type, result);
        }
        return type;
    }
    public static Type container(Type type, ClassType newContainer) {
        if (type instanceof ClassType) 
            return ((ClassType) type).container(newContainer);
        if (type instanceof ConstrainedType) {
            ConstrainedType ct = ((ConstrainedType) type).copy();
            Type newBase = container(ct.baseType().get(), newContainer);
            ((Ref<Type>) ct.baseType()).update(newBase);
            return ct;
        }
       //return type;
        assert false;
        return null;
    }
    /** The list of outer types for a type. That is if a type is A.B.C.D, 
     * return {A.B.C.D, A.B.C, A.B, A}.
     * 
     * @param type
     * @return
     */
    public static List<X10ClassDef> outerTypes(Type type) {
        List<X10ClassDef> result = new ArrayList<X10ClassDef>();
        Type base = Types.baseType(type);
        if (base instanceof ClassType) {
            ClassType outer = (ClassType) base;
            while (outer != null) {
                result.add(outer.def());
                outer = outer.outer();
            }
        }
        return result;
    }
}
