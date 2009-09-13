/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Apr 18, 2005
 */
package x10.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Formal;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.frontend.Globals;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.LazyRef_c;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.ast.SemanticError;
import x10.ast.X10ClassDecl_c;
import x10.constraint.XConstrainedTerm;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XNameWrapper;
import x10.constraint.XPromise;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/** 
 * Utilities for dealing with X10 dependent types.
 * @author nystrom
 */
public class X10TypeMixin {
    

    public static Type instantiate(Type t, Type... typeArg) {
	if (t instanceof X10ParsedClassType) {
	    X10ParsedClassType ct = (X10ParsedClassType) t;
	    return ct.typeArguments(Arrays.asList(typeArg));
	}
	else {
	    throw new InternalCompilerError("Cannot instantiate non-class " + t);
	}
    }
    
    public static Type instantiate(Type t, Ref<? extends Type> typeArg) {
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
        
        return new TypeConstraint_c();
    }
    public static XConstraint realX(Type t) {
	if (t instanceof ParameterType) {
	    return new XConstraint_c();
	}
	else if (t instanceof ConstrainedType) {
            return ((ConstrainedType) t).getRealXClause();
		}
		else if (t instanceof X10ClassType) {
			X10ClassType ct = (X10ClassType) t;
			XConstraint c = ct.x10Def().getRootClause();
			return TypeParamSubst.reinstantiateConstraint(ct, c);
		}
		else if (t instanceof MacroType) {
		    MacroType mt = (MacroType) t;
		    XConstraint c = realX(mt.definedType());
		    XConstraint w = mt.guard();
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

		return new XConstraint_c();
	}
	
    /**
     * Return the constraint c entailed by the assertion v is of type t.
     * @param v
     * @param t
     * @return
     */
    public static XConstraint xclause(XVar v, Type t) {
    	XConstraint c = xclause(t);
    	try {
    	return c.substitute(v, c.self());
    	} catch (XFailure z) {
    		XConstraint c1 = new XConstraint_c();
    		c1.setInconsistent();
    		return c1;
    	}
    }
	public static XConstraint xclause(Type t) {
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
			return Types.get(ct.constraint());
		}
		if (t instanceof X10ParsedClassType) {
			X10ParsedClassType ct = (X10ParsedClassType) t;
			return ct.getXClause();
		}
		return null;
	}
	/**
     * If t is the type proto S, return S. Else return t.
     * @param t
     * @return
     */
	public static X10Type baseForProto(X10Type x) {
    	if (! x.isProto())
    		return x;
    	return x.clearFlags(X10Flags.PROTO);
    }
	
	/**
	 * If x is a class type, return struct x. Else return x.
	 * @param x
	 * @return
	 */
	public static X10Type makeStruct(X10Type x) {
		if (x.isX10Struct())
			return x;
		return x.setFlags(X10Flags.STRUCT);
	}
	
	/**
	 * If x is a class type, return x, else return struct x.
	 * @param x
	 * @return
	 */
	public static X10Type makeRef(X10Type x) {
		if (! x.isX10Struct())
			return x;
		return x.clearFlags(X10Flags.STRUCT);
	}
	public static Type baseTypeWithoutProto(Type t) {
		return baseForProto((X10Type) baseType(t));
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
    public static Type stripConstraints(Type t) {
        X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
        t = ts.expandMacros(t);
        t = X10TypeMixin.baseType(t);
        if (t instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) t;
            List<Type> types = new ArrayList<Type>(ct.typeArguments().size());
            for (Type ti : ct.typeArguments()) {
                Type ti2 = stripConstraints(ti);
                types.add(ti2);
            }
            return ct.typeArguments(types);
        }
        return t;
    }
	public static Type xclause(Type t, XConstraint c) {
		if (t == null)
			return null;
		if (c == null || c.valid()) {
			return baseType(t);
		}
		return xclause(Types.ref(t), Types.ref(c));
	}
	public static Type constrainedType(Type base, XConstraint c) {
		return new ConstrainedType_c((X10TypeSystem) base.typeSystem(), base.position(), Types.ref(base),
				Types.ref(c));
	}
	// vj: 08/11/09 -- have to recursively walk the 
	// type parameters and add the constraint to them.
	public static Type xclause(final Ref<? extends Type> t, final Ref<XConstraint> c) {
	    if (t == null) {
	        return null;
	    }

	    if (t.known() && c != null && c.known()) {
	        Type tx = Types.get(t);
	        X10TypeSystem ts = (X10TypeSystem) tx.typeSystem();
	        tx = ts.expandMacros(tx);

	        XConstraint oldc = X10TypeMixin.xclause(tx);
	        XConstraint newc = Types.get(c);

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
	    
	    final LazyRef_c<XConstraint> cref = new LazyRef_c<XConstraint>(null);
	    cref.setResolver(new Runnable() { 
	        public void run() {
	            XConstraint oldc = X10TypeMixin.xclause(Types.get(t));
	            if (oldc != null) {
	                XConstraint newc = Types.get(c);
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
	    return new ConstrainedType_c((X10TypeSystem) tx.typeSystem(), tx.position(), tref, cref);
	}

    public static boolean isConstrained(Type t) {
	    return t instanceof ConstrainedType;
    }
    public static boolean isStruct(Type t) {
	    return ((X10Type) t).isX10Struct();
    }
    public static boolean isClass(Type t) {
	    return ! isStruct(t);
    }
    
    public static Type addBinding(Type t, XTerm t1, XTerm t2) {
    	assert (! (t instanceof X10UnknownType));
        try {
            XConstraint c = xclause(t);
            c = c == null ? new XConstraint_c() :c.copy();
            c.addBinding(t1, t2);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
        }
    }
	public static Type instantiateSelf(XTerm t, Type type) throws SemanticException {
	 	assert (! (t instanceof X10UnknownType));
		 XConstraint c = xclause(type);
	        if (! ((c==null) || c.valid())) {
	        	XConstraint env = c = c.copy().instantiateSelf(t);
	        	if (! c.consistent()) {
	        		throw new InternalCompilerError("X10TypeMixin: Instantiating self on " + type + " with " + t + " is inconsistent.");
	        	}
	        	return xclause(X10TypeMixin.baseType(type), c);
	        }
	        return type;
	}
    public static Type addBinding(Type t, XTerm t1, XConstrainedTerm t2) {
     	assert (! (t instanceof X10UnknownType));
        try {
            XConstraint c = xclause(t);
            c = c == null ? new XConstraint_c() :c.copy();
            c.addBinding(t1, t2);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
        }
    }
    public static Type addConstraint(Type t, XConstraint xc) {
    	assert (! (t instanceof X10UnknownType));
        try {
            XConstraint c = xclause(t);
            c = c == null ? new XConstraint_c() :c.copy();
            c.addIn(xc);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("X10TypeMixin_c: Cannot add " + xc + "to " + t + ".", f);
        }
    }
    
    public static Type addTerm(Type t, XTerm term) {
        try {
            XConstraint c = xclause(t);
            c = c == null ? new XConstraint_c() :c.copy();
            c.addTerm(term);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot add term " + term + " to " + t + ".", f);
        }
    }

    public static boolean consistent(X10Type t) {
	    XConstraint c = xclause(t);
        if (c == null) return true;
        return c.consistent();
    }

    public static XVar selfVar(Type thisType) {
	    XConstraint c = xclause(thisType);
	    return selfVar(c);
    }

    public static XVar selfVar(XConstraint c) {
	    if (c == null) return null;
	    return c.self();
    }

    public static XVar selfVarBinding(Type thisType) {
	    XConstraint c = xclause(thisType);
	    return selfVarBinding(c);
    }

    public static XVar selfVarBinding(XConstraint c) {
	    if (c == null) return null;
	    return c.bindingForVar(c.self());
    }

    public static Type setSelfVar(Type t, XVar v) throws SemanticException {
        XConstraint c = xclause(t);
        if (c == null) {
            c = new XConstraint_c();
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
        XConstraint c = xclause(t);
        if (c == null) {
            c = new XConstraint_c();
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
    public static XTerm propVal(X10Type t, Name name) {
        XConstraint c = xclause(t);
        if (c == null) return null;
        try {
		return c.find(new XNameWrapper<Name>(name));
	}
	catch (XFailure e) {
		return null;
	}
    }
    
    
    // Helper functions for the various type tests
    // At the top of test foo(o), put:
    // if (Mixin.isConstrained(this) || Mixin.isParametric(this) || Mixin.isConstrained(o) || Mixin.isParametric(o))
    //     return Mixin.foo(this, o)
    // ...
    
    public static boolean eitherIsDependent(X10Type t1, X10Type t2) {
    	return isDependentOrDependentPath(t1) || isDependentOrDependentPath(t2);
    }
    
    public static boolean isDependentOrDependentPath(Type t) {
    	return isConstrained(t);
    }

    public static X10PrimitiveType promote(Unary.Operator op, X10PrimitiveType t) throws SemanticException {
        TypeSystem ts = t.typeSystem();
        X10PrimitiveType pt = (X10PrimitiveType) ts.promote(t);
        return (X10PrimitiveType) xclause(X10TypeMixin.baseType(pt), promoteClause(op, xclause(t)));
    }

    public static XConstraint promoteClause(polyglot.ast.Unary.Operator op, XConstraint c) {
        if (c == null)
            return null;
        X10TypeSystem ts = (X10TypeSystem) Globals.TS();
        return ts.xtypeTranslator().unaryOp(op, c);
    }

    public static X10PrimitiveType promote(Binary.Operator op, X10PrimitiveType t1, X10PrimitiveType t2) throws SemanticException {
        TypeSystem ts = t1.typeSystem();
        X10PrimitiveType pt = (X10PrimitiveType) ts.promote(t1, t2);
        return (X10PrimitiveType) xclause(X10TypeMixin.baseType(pt), promoteClause(op, xclause(t1), xclause(t2)));
    }

    public static XConstraint promoteClause(Operator op, XConstraint c1, XConstraint c2) {
        if (c1 == null || c2 == null)
            return null;
        X10TypeSystem ts = (X10TypeSystem) Globals.TS();
        return ts.xtypeTranslator().binaryOp(op, c1, c2);
    }

	public static Type getParameterType(Type theType, int i) {
	    Type b = baseType(theType);
	    if (b instanceof X10ClassType) {
		X10ClassType ct = (X10ClassType) b;
		if (i < ct.typeArguments().size()) {
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
	    return Collections.EMPTY_LIST;
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
	public static XVar getThisVar(XConstraint t1, XConstraint t2) throws XFailure {
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
						if (! thisVar.equals(o))
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
		XConstraint c = xclause(t);
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
	
	  public static X10Type makeProto(X10Type t) {
	    	X10Type r = (X10Type) t.copy();
	    	r=r.setFlags(X10Flags.toX10Flags(t.flags()).Proto());
	    	return r;
	    }
	  public static void protoTypeCheck(List<Formal> formals, X10Type retType, Position pos,
			  boolean isMethod) throws SemanticException {
	    	for (Formal f : formals) {
	    		if (((X10Type) f.type().type()).isProto()) {
	    			if (! retType.isVoid() && ! retType.isProto()) {
	    				throw new SemanticException("The argument " + f 
	    						+ " has a proto type; hence the return type must be "
	    						+ (isMethod ? "void or proto" : "proto")+", not "
	    						+ retType+".", pos);
	    						
	    			}
	    		}
	    	}
	    }
}
