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

package x10.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Lit;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ast.Variable;
import polyglot.ast.FloatLit;
import polyglot.ast.TypeNode;
import polyglot.ast.IntLit;
import polyglot.ast.Binary.Operator;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef_c;
import polyglot.types.MemberInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.types.QName;
import polyglot.types.FieldDef;
import polyglot.types.StructType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.ErrorInfo;
import polyglot.visit.ContextVisitor;
import polyglot.frontend.Job;
import x10.ast.Here;
import x10.ast.ParExpr;
import x10.ast.SemanticError;
import x10.ast.SubtypeTest;
import x10.ast.HasZeroTest;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XNameWrapper;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.errors.Errors;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.types.constraints.SubtypeConstraint;
import x10.types.matcher.Matcher;

/** 
 * Utilities for dealing with X10 dependent types.
 * @author nystrom
 */
public class X10TypeMixin {

    public static X10FieldInstance getProperty(Type t, Name propName) {
        TypeSystem xts = t.typeSystem();
        try {
            Context c = xts.emptyContext();
            X10FieldInstance fi = xts.findField(t, xts.FieldMatcher(t, propName, c));
            if (fi != null && fi.isProperty()) {
                return fi;
            }
        }
        catch (SemanticException e) {
            // ignore
        }
        return null;
    }

    public static X10MethodInstance getPropertyMethod(Type t, Name propName) {
        TypeSystem xts = t.typeSystem();
        try {
            Context c = xts.emptyContext();
            X10MethodInstance mi = xts.findMethod(t, xts.MethodMatcher(t, propName, Collections.<Type>emptyList(), c));
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
     * Return the type Array[type]{self.rail==true,self.size==size}.
     * @param type
     * @param pos
     * @return
     */
    public static Type makeArrayRailOf(Type type, int size, Position pos) {
        Type t = makeArrayRailOf(type, pos);
        assert (t.isClass());
        TypeSystem ts = type.typeSystem();
        CConstraint c = X10TypeMixin.xclause(t);
        FieldInstance sizeField = t.toClass().fieldNamed(Name.make("size"));
        if (sizeField == null)
            throw new InternalCompilerError("Could not find size field of " + t, pos);
        try {
            XVar selfSize = ts.xtypeTranslator().trans(c, c.self(), sizeField);
            XLit sizeLiteral = ts.xtypeTranslator().trans(size);
            c.addBinding(selfSize, sizeLiteral);
            return X10TypeMixin.xclause(t, c);
        } catch (XFailure z) {
            throw new InternalCompilerError("Could not create Array[T]{self.rail==true,self.size==size}");
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
        X10ClassType r = ts.Array();
        X10ClassType t = X10TypeMixin.instantiate(r, type);
        CConstraint c = new CConstraint();
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
        try {
            XVar selfRank = ts.xtypeTranslator().trans(c, c.self(), rankField);
            XVar selfRect = ts.xtypeTranslator().trans(c, c.self(), rectField);
            XVar selfZeroBased = ts.xtypeTranslator().trans(c, c.self(), zeroBasedField);
            XVar selfRail = ts.xtypeTranslator().trans(c, c.self(), railField);

            XLit rankLiteral = XTerms.makeLit(1);
            c.addBinding(selfRank, rankLiteral);
            c.addBinding(selfRect, XTerms.TRUE);
            c.addBinding(selfZeroBased, XTerms.TRUE);
            c.addBinding(selfRail, XTerms.TRUE);
            return X10TypeMixin.xclause(t, c);
        } catch (XFailure z) {
            throw new InternalCompilerError("Could not create Array[T]{self.rank==1,self.rect==true,self.zeroBased==true,self.rail==true}");
        }
    }
    public static Type typeArg(Type t, int i) {
        if (t instanceof X10ParsedClassType) {
            X10ParsedClassType ct = (X10ParsedClassType) t;
            return ct.typeArguments().get(i);
        } 
        return typeArg(X10TypeMixin.baseType(t), i);
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
	return instantiate(t, Types.get(typeArg));
    }
   
    public static TypeConstraint parameterBounds(Type t) {
        if (t instanceof ParameterType) {
        }
        else if (t instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) t;
            TypeConstraint bounds = parameterBounds(Types.get(ct.baseType()));
            if (bounds == null)
                assert bounds != null;
            return bounds;
        }
        else if (t instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) t;
            TypeConstraint c = Types.get(ct.x10Def().typeBounds());
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
    public static CConstraint realX(Type t) {
	if (t instanceof ParameterType) {
	    return new CConstraint();
	}
	else if (t instanceof ConstrainedType) {
            return ((ConstrainedType) t).getRealXClause();
		}
		else if (t instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) t;
			CConstraint c = ct.x10Def().getRootClause();
			return TypeParamSubst.reinstantiateConstraint(ct, c);
		}
		else if (t instanceof MacroType) {
		    MacroType mt = (MacroType) t;
		    CConstraint c = realX(mt.definedType());
		    CConstraint w = mt.guard();
		    if (w != null) {
			c = c.copy();
			try {
			    c.addIn(w);
			}
			catch (XFailure e) {
			    c.setInconsistent();
			}
		    }
		    return c;
		}

		return new CConstraint();
	}
	
    /**
     * Return the constraint c entailed by the assertion v is of type t.
     * @param v
     * @param t
     * @return
     */
    public static CConstraint xclause(XVar v, Type t) {
    	CConstraint c = xclause(t);
    	try {
    	return c.substitute(v, c.self());
    	} catch (XFailure z) {
    		CConstraint c1 = new CConstraint();
    		c1.setInconsistent();
    		return c1;
    	}
    }
    /**
     * Returns a copy of t's constraint, if it has one, null otherwise.
     * @param t
     * @return
     */
	public static CConstraint xclause(Type t) {
	        if (t instanceof AnnotatedType) {
	            AnnotatedType at = (AnnotatedType) t;
	            return xclause(at.baseType());
	        }
	        if (t instanceof MacroType) {
	                MacroType mt = (MacroType) t;
	                return xclause(mt.definedType());
	        }
		if (t instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) t;
			return Types.get(ct.constraint()).copy();
		}
		if (t instanceof X10ParsedClassType) {
			X10ParsedClassType ct = (X10ParsedClassType) t;
			return ct.getXClause().copy();
		}
		return null;
	}
	
	
	/**
	 * If x is a class type, return struct x. Else return x.
	 * @param x
	 * @return
	 */
	public static Type makeX10Struct(Type t) {
		if (! (t instanceof X10Struct))
			return t;
    	X10Struct type = (X10Struct) t; 
    	return type.makeX10Struct();
		
	}
	
	public static Type processFlags(Flags f, Type x) {
	    if (f==null || !(f instanceof X10Flags))
	        return x;
	    X10Flags xf = (X10Flags) f;
	    if (xf.isStruct()) {
	        x = ((X10Struct) x).makeX10Struct();
	    }
	    return x;
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
                Errors.issue(errs, new SemanticException("Illegal variance! Type parameter has variance "+var+" but it is used in a "+variance+" position.",pos)); // todo: t.position() is incorrect (see XTENLANG-1439)
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
        } else if (t instanceof ConstrainedType_c) {
	        ConstrainedType ct = (ConstrainedType) t;
            base = Types.get(ct.baseType());
        } else if (t instanceof AnnotatedType_c) {
	        AnnotatedType_c at = (AnnotatedType_c) t;
            base = at.baseType();
        } else if (t instanceof MacroType_c) {
	        MacroType mt = (MacroType) t;
            base = mt.definedType();
        }
        if (base!=null)
            checkVariance(base,variance,errs,pos);

    }
	public static Type baseType(Type t) {
	    if (t instanceof AnnotatedType) {
	        AnnotatedType at = (AnnotatedType) t;
	        return baseType(at.baseType());
	    }
	    if (t instanceof MacroType) {
	        MacroType mt = (MacroType) t;
	        return baseType(mt.definedType());
	    }
	    if (t instanceof ConstrainedType) {
	        ConstrainedType ct = (ConstrainedType) t;
	        return baseType(Types.get(ct.baseType()));
	    }
	    return t;
	}
	public static Type erasedType(Type t) {
	    if (t instanceof AnnotatedType) {
	        AnnotatedType at = (AnnotatedType) t;
	        return erasedType(at.baseType());
	    }
	    if (t instanceof MacroType) {
	        MacroType mt = (MacroType) t;
	        return erasedType(mt.definedType());
	    }
	    if (t instanceof ConstrainedType) {
	        ConstrainedType ct = (ConstrainedType) t;
	        return erasedType(baseType(Types.get(ct.baseType())));
	    }
	    return t;
	}
	
    public static Type stripConstraints(Type t) {
        TypeSystem ts = (TypeSystem) t.typeSystem();
        t = ts.expandMacros(t);
        t = X10TypeMixin.baseType(t);
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
	public static Type xclause(Type t, CConstraint c) {
		if (t == null)
			return null;
		if (c == null || c.valid()) {
			return baseType(t);
		}
		return xclause(Types.ref(t), Types.ref(c));
	}
	public static Type constrainedType(Type base, CConstraint c) {
		return new ConstrainedType_c((TypeSystem) base.typeSystem(), base.position(), Types.ref(base),
				Types.ref(c));
	}
	// vj: 08/11/09 -- have to recursively walk the 
	// type parameters and add the constraint to them.
	public static Type xclause(final Ref<? extends Type> t, final Ref<CConstraint> c) {
	    if (t == null) {
	        return null;
	    }

	    if (t.known() && c != null && c.known()) {
	        Type tx = Types.get(t);
	        TypeSystem ts = (TypeSystem) tx.typeSystem();
	        tx = ts.expandMacros(tx);

	        CConstraint oldc = X10TypeMixin.xclause(tx);
	        CConstraint newc = Types.get(c);

	        if (newc == null)
	            return tx;
	        
	        if (oldc == null) {
	            return new ConstrainedType_c(ts, tx.position(), t, c);
	        }
	        else {
	            newc = newc.copy();
	            try {
	                newc.addIn(oldc);
	            }
	            catch (XFailure e) {
	                newc.setInconsistent();
	            }
	            assert tx != null;
	            return new ConstrainedType_c(ts, tx.position(), Types.ref(X10TypeMixin.baseType(tx)), Types.ref(newc));
	        }
	    }
	    
	    final LazyRef_c<Type> tref = new LazyRef_c<Type>(null);
	    tref.setResolver(new Runnable() {
	        public void run() {
	            Type oldt = X10TypeMixin.baseType(Types.get(t));
	            tref.update(oldt);
	        }
	    });
	    
	    final LazyRef_c<CConstraint> cref = new LazyRef_c<CConstraint>(null);
	    cref.setResolver(new Runnable() { 
	        public void run() {
	            CConstraint oldc = X10TypeMixin.xclause(Types.get(t));
	            if (oldc != null) {
	                CConstraint newc = Types.get(c);
	                if (newc != null) {
	                    newc = newc.copy();
	                    try {
	                        newc.addIn(oldc);
	                    }
	                    catch (XFailure e) {
	                        newc.setInconsistent();
	                    }
	                    cref.update(newc);
	                }
	                else {
	                    cref.update(oldc);
	                }
	            }
	            else {
	                cref.update(oldc);
	            }		                
	        }
	    });

	    Type tx = t.getCached();
	    assert tx != null;
	    return new ConstrainedType_c((TypeSystem) tx.typeSystem(), tx.position(), t.known()? t: tref, cref);
	}

    public static boolean isConstrained(Type t) {
    	
	        if (t instanceof AnnotatedType) {
	            AnnotatedType at = (AnnotatedType) t;
	            return isConstrained(at.baseType());
	        }
	        if (t instanceof MacroType) {
	                MacroType mt = (MacroType) t;
	                return isConstrained(mt.definedType());
	        }
		if (t instanceof ConstrainedType) {
			return true;
		}
		return false;
    }
    public static boolean isX10Struct(Type t) {
    	if (! (t instanceof X10Struct))
    		return false;
    	return ((X10Struct) t).isX10Struct();
    }

    public static boolean isClass(Type t) {
	    return ! isX10Struct(t);
    }

    public static Type superClass(Type t) {
    	t = baseType(t);
    	assert t instanceof ClassType;
    	return ((ClassType) t).superClass();
    }

    public static Type addBinding(Type t, XTerm t1, XTerm t2) throws XFailure {
    	//assert (! (t instanceof UnknownType));
       
            CConstraint c = xclause(t);
            c = c == null ? new CConstraint() :c.copy();
            c.addBinding(t1, t2);
            return xclause(X10TypeMixin.baseType(t), c);
      
    }
	public static Type instantiateSelf(XTerm t, Type type) {
	 	assert (! (t instanceof UnknownType));
		 CConstraint c = xclause(type);
	        if (! ((c==null) || c.valid())) {
	        	CConstraint env = c = c.copy().instantiateSelf(t);
	        	if (! c.consistent()) {
	        		throw new InternalCompilerError("X10TypeMixin: Instantiating self on " + type + " with " + t + " is inconsistent.");
	        	}
	        	return xclause(X10TypeMixin.baseType(type), c);
	        }
	        return type;
	}
    public static Type addBinding(Type t, XTerm t1, XConstrainedTerm t2) {
     	assert (! (t instanceof UnknownType));
        try {
            CConstraint c = xclause(t);
            c = c == null ? new CConstraint() :c.copy();
            c.addBinding(t1, t2);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
        }
    }
    public static Type addSelfBinding(Type t, XTerm t1) throws XFailure {
        assert (! (t instanceof UnknownType));
            CConstraint c = xclause(t);
            c = c == null ? new CConstraint() :c.copy();
            c.addSelfBinding(t1);
            return xclause(X10TypeMixin.baseType(t), c); 
    }
    
    public static Type addDisBinding(Type t, XTerm t1, XTerm t2) {
     	assert (! (t instanceof UnknownType));
        try {
            CConstraint c = xclause(t);
            c = c == null ? new CConstraint() :c.copy();
            c.addDisBinding(t1, t2);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
        }
    }
    static CConstraint tryAddingConstraint(Type t, CConstraint xc) throws XFailure {
    	 CConstraint c = xclause(t);
         c = c == null ? new CConstraint() :c.copy();
         c.addIn(xc);
         return c;
    }
    public static Type addConstraint(Type t, CConstraint xc) {
        try {
            CConstraint c = tryAddingConstraint(t, xc);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("X10TypeMixin: Cannot add " + xc + "to " + t + ".", f);
        }
    }
    
    public static Type addTerm(Type t, XTerm term) {
        try {
            CConstraint c = xclause(t);
            c = c == null ? new CConstraint() :c.copy();
            c.addTerm(term);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot add term " + term + " to " + t + ".", f);
        }
    }

    public static boolean consistent(Type t) {
	    CConstraint c = xclause(t);
        if (c == null) return true;
        return c.consistent();
    }
    public static void setInconsistent(Type t) {
    	if (t instanceof AnnotatedType) {
    		AnnotatedType at = (AnnotatedType) t;
    		setInconsistent(at.baseType());
    	}
    	if (t instanceof MacroType) {
    		MacroType mt = (MacroType) t;
    		setInconsistent(mt.definedType());
    	}
    	if (t instanceof ConstrainedType) {
    		ConstrainedType ct = (ConstrainedType) t;
    		CConstraint c = Types.get(ct.constraint());
    		c.setInconsistent();
    		return;
    	}
    }

    public static XVar selfVar(Type thisType) {
	    CConstraint c = xclause(thisType); // Should this be realX(thisType) ???  - Bowen
	    return selfVar(c);
    }

    public static XVar selfVar(CConstraint c) {
	    if (c == null) return null;
	    return c.self();
    }

    public static XVar selfVarBinding(Type thisType) {
	    CConstraint c = xclause(thisType); // Should this be realX(thisType) ???  - Bowen
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
    	CConstraint c = xclause(t);
    	if (c == null) {
    		c = new CConstraint();
    	}
    	else {
    		c = c.copy();
    	}
    	try {
    		c.addSelfBinding(v);
    	}
    	catch (XFailure e) {
    		throw new SemanticException(e.getMessage(), t.position());
    	}
    	return xclause(X10TypeMixin.baseType(t), c);
    }
    
    public static Type setThisVar(Type t, XVar v) throws SemanticException {
        CConstraint c = xclause(t);
        if (c == null) {
            c = new CConstraint();
        }
        else {
            c = c.copy();
        }
        
		c.setThisVar(v);
        return xclause(X10TypeMixin.baseType(t), c);
    }

    /**
     * If the type constrains the given property to
     * a particular value, then return that value, otherwise 
     * return null
     * @param name -- the name of the property.
     * @return null if there is no value associated with the property in the type.
     */
    public static XTerm propVal(Type t, Name name) {
        CConstraint c = xclause(t);
        if (c == null) return null;
		return c.bindingForSelfField(new XNameWrapper<Name>(name));
    }
    
    
    // Helper functions for the various type tests
    // At the top of test foo(o), put:
    // if (Mixin.isConstrained(this) || Mixin.isParametric(this) || Mixin.isConstrained(o) || Mixin.isParametric(o))
    //     return Mixin.foo(this, o)
    // ...
    
    public static boolean eitherIsDependent(Type t1, Type t2) {
    	return isDependentOrDependentPath(t1) || isDependentOrDependentPath(t2);
    }
    
    public static boolean isDependentOrDependentPath(Type t) {
    	return isConstrained(t);
    }

    public static X10PrimitiveType promote(Unary.Operator op, X10PrimitiveType t) throws SemanticException {
        TypeSystem ts = (TypeSystem) t.typeSystem();
        X10PrimitiveType pt = (X10PrimitiveType) ts.promote(t);
        return (X10PrimitiveType) xclause(X10TypeMixin.baseType(pt), 
        		promoteClause(ts, op, xclause(t)));
    }

    public static CConstraint promoteClause(TypeSystem ts, polyglot.ast.Unary.Operator op, CConstraint c) {
        if (c == null)
            return null;
   
        return ts.xtypeTranslator().unaryOp(op, c);
    }

    public static X10PrimitiveType promote(Binary.Operator op, X10PrimitiveType t1, X10PrimitiveType t2) throws SemanticException {
        TypeSystem ts = (TypeSystem) t1.typeSystem();
        X10PrimitiveType pt = (X10PrimitiveType) ts.promote(t1, t2);
        return (X10PrimitiveType) xclause(X10TypeMixin.baseType(pt), 
        		promoteClause(ts, op, xclause(t1), xclause(t2)));
    }

    public static CConstraint promoteClause(TypeSystem ts, Operator op, CConstraint c1, CConstraint c2) {
        if (c1 == null || c2 == null)
            return null;
        return ts.xtypeTranslator().binaryOp(op, c1, c2);
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
	
	public static List<FieldInstance> properties(Type t) {
	    t = baseType(t);
	    if (t instanceof X10ClassType) {
	        X10ClassType ct = (X10ClassType) t;
	        return ct.properties();
	    }
	    return Collections.<FieldInstance>emptyList();
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
		CConstraint c = xclause(t);
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
	
	  
	  
	  public static boolean entails(Type t, XTerm t1, XTerm t2) {
		 CConstraint c = realX(t);
		 if (c==null) 
			 c = new CConstraint();
		 return c.entails(t1, t2);
	  }
	  
	  public static boolean disEntails(Type t, XTerm t1, XTerm t2) {
		 CConstraint c = realX(t);
		 if (c==null) 
			 c = new CConstraint();
		 return c.disEntails(t1, t2);
	  }
	  public static boolean disEntailsSelf(Type t, XTerm t2) {
			 CConstraint c = realX(t);
			 if (c==null) 
				 c = new CConstraint();
			 return c.disEntails(c.self(), t2);
		  }

	 
	protected static boolean amIProperty(Type t, Name propName, Context context) {
	    TypeSystem xts = (TypeSystem) t.typeSystem();
	    CConstraint r = realX(t);
	
	    // first try self.p
	    X10FieldInstance fi = getProperty(t, propName);
	    if (fi != null) {
		    try {
			    CConstraint c = new CConstraint();
			    XVar term = xts.xtypeTranslator().trans(c, c.self(), fi);
			    c.addBinding(term, xts.xtypeTranslator().trans(true));
	            return r.entails(c, context.constraintProjection(r, c));
		    }
		    catch (XFailure f) {
			    return false;
		    }
	    }
	    else {
	        // try self.p()
	            try {
	                X10MethodInstance mi = xts.findMethod(t, xts.MethodMatcher(t, propName, Collections.<Type>emptyList(), xts.emptyContext()));
	                XTerm body = mi.body();
	                CConstraint c = new CConstraint();
	                body = body.subst(c.self(), mi.x10Def().thisVar());
	                c.addTerm(body);
	                return r.entails(c, context.constraintProjection(r, c));
	            }
	            catch (XFailure f) {
	                return false;
	            }
	            catch (SemanticException f) {
	                return false;
	            }
	    }
	}

	public static boolean isRect(Type t, Context context) {
	    return amIProperty(t, Name.make("rect"), context);
	}

	public static XTerm onePlace(Type t) {
	    return find(t, Name.make("onePlace"));
	}

	public static boolean isZeroBased(Type t, Context context) {
	return amIProperty(t, Name.make("zeroBased"), context);
	}

	public static XTerm distribution(Type t) {
	return findProperty(t, Name.make("dist"));
	}

	public static XTerm region(Type t) {
	return findProperty(t, Name.make("region"));
	}
	public static XTerm zeroBased(Type t) {
		return findProperty(t, Name.make("zeroBased"));
	}
	public static XTerm makeZeroBased(Type t) {
		return makeProperty(t, "zeroBased");
	}
    public static XTerm makeRail(Type t) {
        return makeProperty(t, "rail");
    }
	 
	public static XTerm makeProperty(Type t, String propStr) {
		Name propName = Name.make(propStr);
		  CConstraint c = realX(t);
		    if (c != null) {
			    // build the synthetic term.
			    XTerm var = selfVar(t);
			    if (var !=null) {
				    X10FieldInstance fi = getProperty(t, propName);
				    if (fi != null) {
					    
						    TypeSystem xts = (TypeSystem) t.typeSystem();
						    XTerm val = xts.xtypeTranslator().trans(c, var, fi);
					    return val;
				    }
			    }
		    }
		return null;
		
	}
	public static XTerm find(Type t, Name propName) {
	    XTerm val = findProperty(t, propName);

	    if (val == null) {
	        TypeSystem xts = (TypeSystem) t.typeSystem();
	        CConstraint c = realX(t);
	        if (c != null) {
	            // build the synthetic term.
	            XTerm var = selfVar(c);
	            if (var !=null) {
	                X10FieldInstance fi = getProperty(t, propName);
	                if (fi != null) {
	                    val = xts.xtypeTranslator().trans(c, var, fi);
	                } else {
	                    X10MethodInstance mi = getPropertyMethod(t, propName);
	                    if (mi != null) {
	                        val = xts.xtypeTranslator().trans(c, var, mi, mi.rightType());
	                    }
	                }
	            }
	        }
	    }
	    return val;
	}

	
	public static boolean isRankOne(Type t, Context context) {
	    TypeSystem xts = (TypeSystem) t.typeSystem();
	    return xts.ONE().equals(X10TypeMixin.rank(t, context));
	}

	public static boolean isRankTwo(Type t, Context context) {
	        TypeSystem xts = (TypeSystem) t.typeSystem();
	        return xts.TWO().equals(X10TypeMixin.rank(t, context));
	}

	public static boolean isRankThree(Type t, Context context) {
	    TypeSystem xts = (TypeSystem) t.typeSystem();
	    return xts.THREE().equals(X10TypeMixin.rank(t, context));
	}
	
	public static boolean isNonNull(Type t) {
		return disEntails(t, self(t), XTerms.NULL);
	}

	static XTerm findProperty(Type t, Name propName) {
		CConstraint c = realX(t);
		if (c == null) return null;

		// TODO: check dist.region.p and region.p

		X10FieldInstance fi = getProperty(t, propName);
		if (fi != null)
			return c.bindingForSelfField(XTerms.makeName(fi.def()));

		X10MethodInstance mi = getPropertyMethod(t, propName);
		if (mi != null) {
		    return c.bindingForSelfField(XTerms.makeName(mi.def()));
		}
		
		return null;
	}

	public static XTerm rank(Type t, Context context) {
	    TypeSystem xts = (TypeSystem) t.typeSystem();
	    return findOrSynthesize(t, Name.make("rank"));
	}

	/**
	 * Add the constraint self.rank==x to t unless
	 * that causes an inconsistency.
	 * @param t
	 * @param x
	 * @return
	 */
	public static Type addRank(Type t, int x) {
	    return addRank(t, XTerms.makeLit(new Integer(x)));
	}

	/**
	 * Add the constraint self.rank==x to t unless
	 * that causes an inconsistency.
	 * @param t
	 * @param x
	 * @return
	 */
	public static Type addRank(Type t, XTerm x) {
	    TypeSystem xts = (TypeSystem) t.typeSystem();
	    XTerm xt = findOrSynthesize(t, Name.make("rank"));
	    try {
	        t = addBinding(t, xt, x);
	    } catch (XFailure f) {
	        // without the binding added.
	    }
	    return t;
	}

	public static Type addRect(Type t) {
	    TypeSystem xts = (TypeSystem) t.typeSystem();
	    XTerm xt = findOrSynthesize(t, Name.make("rect"));
	    try {
	        t = addBinding(t, xt, XTerms.TRUE);
	    } catch (XFailure f) {
	    	// without the binding added.
	    }
	    return t;
	}

	public static Type addZeroBased(Type t) {
	    TypeSystem xts = (TypeSystem) t.typeSystem();
	    XTerm xt = findOrSynthesize(t, Name.make("zeroBased"));
	    try {
	        t = addBinding(t, xt, XTerms.TRUE);
	    } catch (XFailure f) {
	    	// without the binding added.
	    }
	    return t;
	}

	public static Type railBaseType(Type t) {
	    t = baseType(t);
	    if (t instanceof X10ClassType) {
		X10ClassType ct = (X10ClassType) t;
		TypeSystem ts = (TypeSystem) t.typeSystem();
		ClassType a = (ClassType) ts.Rail();
		if (ct.def() == a.def())
		    return ct.typeArguments().get(0);
		else
		    arrayBaseType(ct.superClass());
	    }
	    return null;
	}

	public static Type arrayBaseType(Type t) {
	    t = baseType(t);
	    if (t instanceof X10ClassType) {
		X10ClassType ct = (X10ClassType) t;
		TypeSystem ts = (TypeSystem) t.typeSystem();
		ClassType a = (ClassType) ts.Array();
		ClassType da = (ClassType) ts.Array();
		if (ct.def() == a.def() || ct.def() == da.def())
		    return ct.typeArguments().get(0);
		else
		    arrayBaseType(ct.superClass());
	    }
	    return null;
	}

	public static boolean isX10Array(Type t) {
        TypeSystem ts = (TypeSystem) t.typeSystem();
        Type tt = baseType(t);
        Type at = baseType(ts.Array());
        if (tt instanceof ClassType && at instanceof ClassType) {
            ClassDef tdef = ((ClassType) tt).def();
            ClassDef adef = ((ClassType) at).def();
            return ts.descendsFrom(tdef, adef);
        }
        return false;
	}
	
	public static boolean isX10DistArray(Type t) {
        TypeSystem ts = (TypeSystem) t.typeSystem();
        Type tt = baseType(t);
        Type at = baseType(ts.DistArray());
        if (tt instanceof ClassType && at instanceof ClassType) {
            ClassDef tdef = ((ClassType) tt).def();
            ClassDef adef = ((ClassType) at).def();
            return ts.descendsFrom(tdef, adef);
        }
        return false;
	}


	public static XTerm findOrSynthesize(Type t, Name propName) {
	    return find(t, propName);
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
			t = setSelfVar(t,XTerms.makeUQV());
		} catch (SemanticException z) {
			
		}
		if (selfVarBinding(t) == null)
		assert selfVarBinding(t) != null;
		return t;
	}
	*/

    /**
     * Returns a new constraint that allows null.
     * E.g., given "{self.home==here, self!=null}" it returns "{self.home==here}"
     * @param c a constraint "c" that doesn't allow null
     * @return a new constraint with all the constraints in "c" except {self!=null}
     */
	public static CConstraint allowNull(CConstraint c) {
        final XVar self = c.self();
        CConstraint res = new CConstraint(self);
        assert !res.disEntails(self,XTerms.NULL);
        for (XTerm term : c.constraints()) {
            CConstraint copy = res.copy();
            try {
                copy.addTerm(term);
            } catch (XFailure xFailure) {
                assert false : xFailure;
            }
            if (!copy.disEntails(self,XTerms.NULL))
                res = copy;
        }
        return res;
    }
    public static boolean isUninitializedField(X10FieldDef def,TypeSystem ts) {
        return isDefAnnotated(def,ts,"x10.compiler.Uninitialized");
    }
    public static boolean isSuppressTransientErrorField(X10FieldDef def,TypeSystem ts) {
        return isDefAnnotated(def,ts,"x10.compiler.SuppressTransientError");
    }
    public static boolean isNoThisAccess(X10ProcedureDef def,TypeSystem ts) {
        return isDefAnnotated(def,ts,"x10.compiler.NoThisAccess");
    }
    public static boolean isNonEscaping(X10ProcedureDef def,TypeSystem ts) {
        return isDefAnnotated(def,ts,"x10.compiler.NonEscaping");
    }
    public static boolean isDefAnnotated(X10Def def,TypeSystem ts, String name) {
        try {
            Type at = (Type) ts.systemResolver().find(QName.make(name));
            return !def.annotationsMatching(at).isEmpty();
        } catch (SemanticException e) {
            return false;
        }
    }
    // this is an under-approximation (it is always safe to return false, i.e., the user will just get more errors). In the future we will improve the precision so more types will have zero.
    public static boolean isHaszero(Type t, Context xc) {
        X10TypeSystem_c ts = (X10TypeSystem_c) xc.typeSystem();
        XLit zeroLit = null;  // see Lit_c.constantValue() in its decendants
        if (t.isBoolean()) {
            zeroLit = XTerms.FALSE;
        } else if (ts.isChar(t)) {
            zeroLit = XTerms.ZERO_CHAR;
        } else if (ts.isInt(t) || ts.isByte(t) || ts.isUByte(t) || ts.isShort(t) || ts.isUShort(t)) {
            zeroLit = XTerms.ZERO_INT;
        } else if (ts.isUInt(t) || ts.isULong(t) || ts.isLong(t)) {
            zeroLit = XTerms.ZERO_LONG;
        } else if (ts.isFloat(t)) {
            zeroLit = XTerms.ZERO_FLOAT;
        } else if (ts.isDouble(t)) {
            zeroLit = XTerms.ZERO_DOUBLE;
        } else if (ts.isObjectOrInterfaceType(t, xc)) {
            if (permitsNull(t)) return true;
            //zeroLit = XTerms.NULL;
        } else if (ts.isParameterType(t)) {
            // we have some type "T" which is a type parameter. Does it have a zero value?
            // So, type bounds (e.g., T<:Int) do not help, because  Int{self!=0}<:Int
            // Similarly, Int<:T doesn't help, because  Int<:Any{self!=null}
            // However, T==Int does help
            if (isConstrained(t)) return false; // if we have constraints on the type parameter, e.g., T{self!=null}, then we give up and return false.
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
                            isHaszero(other,xc)) // careful of infinite recursion when calling isHaszero
                                        // We cannot have infinite recursion because other is not a ParameterType
                                        // (we can have that T==U, U==Int. but then typeEquals(T,Int,xc) should return true)
                        return true; // T is equal to another type that has zero
                } else if (sc.isSubtypeConstraint()) {
                    // doesn't help
                } else {
                    assert sc.isHaszero();
                    if (ts.typeEquals(t,sc.subtype(),xc)) {
                        return true;
                    }
                }
            }
        } else if (isX10Struct(t)) {
            if (!(t instanceof StructType)) return false;
            StructType structType = (StructType) t;
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

            final Boolean res = ts.structHaszero.get(x10ClassDef);
            if (res!=null) return res;
            ts.structHaszero.put(x10ClassDef,Boolean.FALSE);

            // do we have an classInvariant? todo: class invariant are not treated correctly: X10ClassDecl_c.classInvariant is fine, but  X10ClassDef_c.classInvariant is wrong
            final Ref<CConstraint> ref = x10ClassDef.classInvariant();
            if (ref!=null && ref.get().constraints().size()>0) return false; // the struct has a class invariant (so the zero value might not satisfy it)

            // We use ts.structHaszero to prevent infinite recursion such as in the case of:
            // struct U(u:U) {}

            // make sure all the fields and properties haszero
            for (FieldInstance field : structType.fields()) {
                if (field.flags().isStatic()) {
                    continue;
                }
                if (!isHaszero(field.type(),xc)) return false;
            }
            ts.structHaszero.put(x10ClassDef,Boolean.TRUE);
            return true;
        }
        if (zeroLit==null) return false;
        if (!isConstrained(t)) return true;
        final CConstraint constraint = xclause(t);
        final CConstraint zeroCons = new CConstraint(constraint.self());
        // make sure the zeroLit is not in the constraint
        try {
            zeroCons.addSelfBinding(zeroLit);
            return zeroCons.entails(constraint);
        } catch (XFailure xFailure) {
            return false;
        }

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

            } else if (ts.isShort(t)) {
                e = nf.IntLit(p, IntLit.SHORT, 0L);
            } else if (ts.isUShort(t)) {
                e = nf.IntLit(p, IntLit.USHORT, 0L);
            } else if (ts.isByte(t)) {
                e = nf.IntLit(p, IntLit.BYTE, 0L);
            } else if (ts.isUByte(t)) {
                e = nf.IntLit(p, IntLit.UBYTE, 0L);
                
            } else if (ts.isChar(t)) {
                e = nf.CharLit(p, '\0');
            } else if (ts.isInt(t)) {
                e = nf.IntLit(p, IntLit.INT, 0L);
            } else if (ts.isUInt(t)) {
                e = nf.IntLit(p, IntLit.UINT, 0L);
            } else if (ts.isLong(t)) {
                e = nf.IntLit(p, IntLit.LONG, 0L);
            } else if (ts.isULong(t)) {
                e = nf.IntLit(p, IntLit.ULONG, 0L);
            } else if (ts.isFloat(t)) {
                e = nf.FloatLit(p, FloatLit.FLOAT, 0.0);
            } else if (ts.isDouble(t)) {
                e = nf.FloatLit(p, FloatLit.DOUBLE, 0.0);
            } else if (ts.isObjectOrInterfaceType(t, context)) {
                e = nf.NullLit(p);
            } else if (ts.isParameterType(t) || isX10Struct(t)) {
                // call Zero.get[T]()  (e.g., "0 as T" doesn't work if T is String)
                TypeNode receiver = (TypeNode) nf.CanonicalTypeNode(p, (Type) ts.systemResolver().find(QName.make("x10.lang.Zero")));
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
    
	public static boolean permitsNull(Type t) {
		if (isX10Struct(t))
			return false;
		if (X10TypeMixin.disEntailsSelf(t, XTerms.NULL))
			return false;
		TypeSystem ts = ((TypeSystem) t.typeSystem());
		if (ts.isParameterType(t)) {			
			return false; // a parameter type might be instantiated with a struct that doesn't permit null.
		}
		return true;
	}

	public static XVar thisVar(XVar xthis, Type thisType) {
	    Type base = baseType(thisType);
	    if (base instanceof X10ClassType) {
	        XVar supVar = ((X10ClassType) base).x10Def().thisVar();
	        return supVar;
	    }
	    return xthis;
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
		return false;
	}

	public static Type meetTypes(TypeSystem xts, Type t1, Type t2, Context context) {
	    if (xts.isSubtype(t1, t2, context))
	        return t1;
	    if (xts.isSubtype(t2, t1, context))
	        return t2;
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
    public static String MORE_SEPCIFIC_WARNING = "Please check definitions p1 and p2.  ";
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
	    // For now (10/10/10) we check using both styles and mark the cases in which results are different
	    // as a diagnostic output for the compiler. 
	    boolean java = javaStyleMoreSpecificMethod(xp1, xp2, (Context) context, ct1, t1, t2,descends);
	    boolean old = oldStyleMoreSpecificMethod(xp1, xp2, (Context) context, ts, ct1, t1, t2, descends);
	    if (java != old) {
            String msg = MORE_SEPCIFIC_WARNING +
	    			((java && ! old) ? "p1 is now more specific than p2; it was not in 2.0.6."
	    					: "p1 is now not more specific than p2; it was in 2.0.6.")
	    			+ "\n\t: p1: " + getOrigMI(xp1)
	    			+ "\n\t: at " + xp1.position()
	    			+ "\n\t: p2: " + getOrigMI(xp2)
	    			+ "\n\t: at " + xp2.position();
            ts.extensionInfo().compiler().errorQueue().enqueue(ErrorInfo.WARNING,msg);
	    }
	    // Change this to return old to re-enable 2.0.6 style computation.
	    return  java; 
	}
	static ProcedureInstance<?> getOrigMI(ProcedureInstance<?> xp) {
		if (xp instanceof MethodInstance)
			return ((MethodInstance) xp).origMI();
		if (xp instanceof ConstructorInstance)
			return ((ConstructorInstance) xp).origMI();
		return xp;
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
	    // if the formal params of p1 can be used to call p2, p1 is more specific
	    if (xp1.formalTypes().size() == xp2.formalTypes().size() ) {
	        for (int i = 0; i < xp1.formalTypes().size(); i++) {
	            Type f1 = xp1.formalTypes().get(i);
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
	    	List<Type> typeArgs = Collections.<Type>emptyList();
	    	try {
	    		if (xp2 instanceof X10MethodInstance) {
	    			// Both xp1 and xp2 should be X10MethodInstance's 
	    			X10MethodInstance xmi2 = (X10MethodInstance) xp2;
	    			X10MethodInstance origMI2 = (X10MethodInstance) xmi2.origMI();
	    			assert origMI2 != null;
	    			
	    			if (! (xp1 instanceof X10MethodInstance))
	    				return false;
	    			X10MethodInstance xmi1 = (X10MethodInstance) xp1;
	    			X10MethodInstance origMI1 = (X10MethodInstance)xmi1.origMI();
	    			assert origMI1 != null;
	    			
	    			// Now determine that a call can be made to thisMI2 using the
	    			// argument list obtained from thisMI1. If not, return false.
	    			List<Type> argTypes = new ArrayList<Type>(origMI1.formalTypes());
	    			if (xp2.formalTypes().size() != argTypes.size())
	        			return false;
	    			// TODO: Establish that the current context is aware of the method
	    			// guard for xmi1.
	    			
	    			if (typeArgs.isEmpty() || typeArgs.size() == xmi2.typeParameters().size()) {
	    				MethodInstance r = Matcher.inferAndCheckAndInstantiate(context, 
	    						origMI2, ct1, typeArgs, argTypes, xp2.position());
	    				if (r == null)
	    					return false;
	    			}
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
	    			if (xp2.formalTypes().size() != argTypes.size())
	        			return false;
	    			// TODO: Figure out how to do type inference.
	                X10ConstructorInstance r = Matcher.inferAndCheckAndInstantiate( context, 
	                        origMI2, ct1, typeArgs, argTypes, xp2.position());
	                if (r == null)
	                	return false;
	            }	else {
	            	// Should not happen.
	            	// System.out.println("Diagnostic. Unhandled MoreSpecificMatcher case: " + xp2 + " class " + xp2.getClass());
	            	assert false;	
	            }
	    	} catch (SemanticException z) {  		
	    		return false;
	    	}
	// I have kept the logic below from 2.0.6 for now. 
	// TODO: Determine whether this should stay or not.
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
		if (xt.isX10Array(t) || xt.isX10DistArray(t) || xt.isRail(t)) {
			if (t instanceof X10ParsedClassType) {
				Type result = ((X10ParsedClassType) t).typeArguments().get(0);
				return result;
			}
		}
		return null;
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
	    return false;
	}
	public static boolean contextKnowsType(Receiver r) {
		if (r instanceof Variable)
			return ((Variable) r).flags().isFinal();
		if (r instanceof Field)
			return contextKnowsType( ((Field) r).target());
		if (r instanceof Special || r instanceof Here || r instanceof Lit)
			return true;
		if (r instanceof ParExpr) 
			return contextKnowsType(((ParExpr) r).expr());
		if (r instanceof Cast) 
			return contextKnowsType(((Cast) r).expr());
		return false;
			
		
	}
	/**
	 * Return T if type implements Reducer[T];
	 * @param type
	 * @return
	 */
	public static Type reducerType(Type type) {
		TypeSystem ts = (TypeSystem) type.typeSystem();
			Type base = X10TypeMixin.baseType(type);

			if (base instanceof X10ClassType) {
				if (ts.hasSameClassDef(base, ts.Reducible())) {
					return X10TypeMixin.getParameterType(base, 0);
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
	public static boolean areConsistent(Type t1, Type t2) {
		try {
			if ( isConstrained(t1) &&  isConstrained(t2))
				tryAddingConstraint(t1, xclause(t2));
			return true;
		} catch (XFailure z) {
			return false;
		}
	}

	public static Type instantiateTypeParametersExplicitly(Type t) {
		if (t instanceof AnnotatedType) {
			AnnotatedType at = (AnnotatedType) t;
			Type bt = at.baseType();
			Type ibt = instantiateTypeParametersExplicitly(bt);
			if (ibt != bt)
			    return at.baseType(ibt);
			return at;
		} else
		if (t instanceof ConstrainedType) {
			ConstrainedType ct = (ConstrainedType) t;
			Type bt = Types.get(ct.baseType());
			Type ibt = instantiateTypeParametersExplicitly(bt);
			if (ibt != bt)
			    ct = ct.baseType(Types.ref(ibt));
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
}
