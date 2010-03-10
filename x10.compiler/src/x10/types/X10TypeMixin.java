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
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ast.Binary.Operator;
import polyglot.frontend.Globals;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef_c;
import polyglot.types.LocalInstance;
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
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.ast.ParExpr;
import x10.ast.SemanticError;
import x10.ast.SubtypeTest;
import x10.ast.X10ClassDecl_c;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XNameWrapper;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.TypeConstraint_c;
import x10.types.constraints.XConstrainedTerm;

/** 
 * Utilities for dealing with X10 dependent types.
 * @author nystrom
 */
public class X10TypeMixin {
    

	public static X10FieldInstance getProperty(Type t, Name propName) {
	    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    try {
		        Context c = xts.emptyContext();
			    X10FieldInstance fi = (X10FieldInstance) xts.findField(t, xts.FieldMatcher(t, propName, c));
			    if (fi != null && fi.isProperty()) {
				    return fi;
			    }
		    }
		    catch (SemanticException e) {
			    // ignore
		    }
        return null;
    }
    
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
    public static CConstraint realX(Type t) {
	if (t instanceof ParameterType) {
	    return new CConstraint_c();
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

		return new CConstraint_c();
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
    		CConstraint c1 = new CConstraint_c();
    		c1.setInconsistent();
    		return c1;
    	}
    }
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
    	if (xf.isProto()) 
    		x =  ((Proto) x).makeProto();
    	if (xf.isStruct()) {
    		x = ((X10Struct) x).makeX10Struct();
    	}
    	return x;
    	
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
	public static Type xclause(Type t, CConstraint c) {
		if (t == null)
			return null;
		if (c == null || c.valid()) {
			return baseType(t);
		}
		return xclause(Types.ref(t), Types.ref(c));
	}
	public static Type constrainedType(Type base, CConstraint c) {
		return new ConstrainedType_c((X10TypeSystem) base.typeSystem(), base.position(), Types.ref(base),
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
	        X10TypeSystem ts = (X10TypeSystem) tx.typeSystem();
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
	    return new ConstrainedType_c((X10TypeSystem) tx.typeSystem(), tx.position(), t.known()? t: tref, cref);
	}

    public static boolean isConstrained(Type t) {
	    return t instanceof ConstrainedType;
    }
    public static boolean isX10Struct(Type t) {
    	if (! (t instanceof X10Struct))
    		return false;
    	return ((X10Struct) t).isX10Struct();
    }
    public static boolean isClass(Type t) {
	    return ! isX10Struct(t);
    }
    
    public static boolean isProto(Type t) {
    	return (t instanceof Proto) && ((Proto) t).isProto();
    }
    public static Type baseOfProto(Type t) {
    	if (! (t instanceof Proto))
    		return t;
    	Proto type = (Proto) t; 
    	return type.baseOfProto();
    	
    }
    public static Type superClass(Type t) {
    	t = baseType(t);
    	assert t instanceof ClassType;
    	return ((ClassType) t).superClass();
    }

    public static Type makeProto(Type t) {
    	if (! (t instanceof Proto)) 
    		return t;
    	Proto type = (Proto) t; 
    	return type.makeProto();
    	
    }
    public static Type addBinding(Type t, XTerm t1, XTerm t2) {
    	assert (! (t instanceof UnknownType));
        try {
            CConstraint c = xclause(t);
            c = c == null ? new CConstraint_c() :c.copy();
            c.addBinding(t1, t2);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
        }
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
            c = c == null ? new CConstraint_c() :c.copy();
            c.addBinding(t1, t2);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
        }
    }
    
    public static Type addDisBinding(Type t, XTerm t1, XTerm t2) {
     	assert (! (t instanceof UnknownType));
        try {
            CConstraint c = xclause(t);
            c = c == null ? new CConstraint_c() :c.copy();
            c.addDisBinding(t1, t2);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
        }
    }
    public static Type addConstraint(Type t, CConstraint xc) {
    	assert (! (t instanceof UnknownType));
        try {
            CConstraint c = xclause(t);
            c = c == null ? new CConstraint_c() :c.copy();
            c.addIn(xc);
            return xclause(X10TypeMixin.baseType(t), c);
        }
        catch (XFailure f) {
            throw new InternalCompilerError("X10TypeMixin_c: Cannot add " + xc + "to " + t + ".", f);
        }
    }
    
    public static Type addTerm(Type t, XTerm term) {
        try {
            CConstraint c = xclause(t);
            c = c == null ? new CConstraint_c() :c.copy();
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

    public static XVar selfVar(Type thisType) {
	    CConstraint c = xclause(thisType);
	    return selfVar(c);
    }

    public static XVar selfVar(CConstraint c) {
	    if (c == null) return null;
	    return c.self();
    }

    public static XVar selfVarBinding(Type thisType) {
	    CConstraint c = xclause(thisType);
	    return selfVarBinding(c);
    }

    public static XVar selfVarBinding(CConstraint c) {
	    if (c == null) return null;
	    return c.bindingForVar(c.self());
    }

    public static Type setSelfVar(Type t, XVar v) throws SemanticException {
    	CConstraint c = xclause(t);
    	if (c == null) {
    		c = new CConstraint_c();
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
            c = new CConstraint_c();
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
        TypeSystem ts = t.typeSystem();
        X10PrimitiveType pt = (X10PrimitiveType) ts.promote(t);
        return (X10PrimitiveType) xclause(X10TypeMixin.baseType(pt), promoteClause(op, xclause(t)));
    }

    public static CConstraint promoteClause(polyglot.ast.Unary.Operator op, CConstraint c) {
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

    public static CConstraint promoteClause(Operator op, CConstraint c1, CConstraint c2) {
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
	
	  
	  public static void protoTypeCheck(List<Formal> formals, Type retType, Position pos,
			  boolean isMethod) throws SemanticException {
	    	for (Formal f : formals) {
	    		if ( X10TypeMixin.isProto(f.type().type())) {
	    			if (! retType.isVoid() && ! X10TypeMixin.isProto(retType)) {
	    				throw new SemanticException("The argument " + f 
	    						+ " has a proto type; hence the return type must be "
	    						+ (isMethod ? "void or proto" : "proto")+", not "
	    						+ retType+".", pos);
	    						
	    			}
	    		}
	    	}
	    }
	  
	  public static boolean entails(Type t, XTerm t1, XTerm t2) {
		 CConstraint c = realX(t);
		 if (c==null) 
			 c = new CConstraint_c();
		 return c.entails(t1, t2);
	  }
	  
	  public static boolean disEntails(Type t, XTerm t1, XTerm t2) {
		 CConstraint c = realX(t);
		 if (c==null) 
			 c = new CConstraint_c();
		 return c.disEntails(t1, t2);
	  }
	  public static boolean disEntailsSelf(Type t, XTerm t2) {
			 CConstraint c = realX(t);
			 if (c==null) 
				 c = new CConstraint_c();
			 return c.disEntails(c.self(), t2);
		  }

	 
	protected static boolean amIProperty(Type t, Name propName, X10Context context) {
	    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	    CConstraint r = realX(t);
	
	    // first try self.p
	    X10FieldInstance fi = getProperty(t, propName);
	    if (fi != null) {
		    try {
			    CConstraint c = new CConstraint_c();
			    XVar term = xts.xtypeTranslator().trans(c, c.self(), fi);
			    c.addBinding(term, xts.xtypeTranslator().trans(true));
	            return r.entails(c, context.constraintProjection(r, c));
		    }
		    catch (XFailure f) {
			    return false;
		    }
		    catch (SemanticException f) {
			    return false;
		    }
	    }
	    else {
	        // try self.p()
	            try {
	                X10MethodInstance mi = xts.findMethod(t, xts.MethodMatcher(t, propName, Collections.EMPTY_LIST, xts.emptyContext()));
	                XTerm body = mi.body();
	                CConstraint c = new CConstraint_c();
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

	public static boolean isRect(Type t, X10Context context) {
	    return amIProperty(t, Name.make("rect"), context);
	}

	public static XTerm onePlace(Type t) {
	    return find(t, Name.make("onePlace"));
	}

	public static boolean isZeroBased(Type t, X10Context context) {
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
	 
	public static XTerm makeProperty(Type t, String propStr) {
		Name propName = Name.make(propStr);
		  CConstraint c = realX(t);
		    if (c != null) {
			    // build the synthetic term.
			    XTerm var = selfVar(c);
			    if (var !=null) {
				    X10FieldInstance fi = getProperty(t, propName);
				    if (fi != null) {
					    
						    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
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
		    CConstraint c = realX(t);
		    if (c != null) {
			    // build the synthetic term.
			    XTerm var = selfVar(c);
			    if (var !=null) {
				    X10FieldInstance fi = getProperty(t, propName);
				    if (fi != null) {
					    
						    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
						    val = xts.xtypeTranslator().trans(c, var, fi);
					    
				    }
			    }
		    }
	    }
	    return val;
	}

	public static boolean isRankOne(Type t, X10Context context) {
	    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	    return xts.ONE().equals(X10TypeMixin.rank(t, context));
	}

	public static boolean isRankTwo(Type t, X10Context context) {
	        X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	        return xts.TWO().equals(X10TypeMixin.rank(t, context));
	}

	public static boolean isRankThree(Type t, X10Context context) {
	    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	    return xts.THREE().equals(X10TypeMixin.rank(t, context));
	}
	
	public static boolean isNonNull(Type t) {
		return disEntails(t, selfVar(t),XTerms.NULL);
	}

	static XTerm findProperty(Type t, Name propName) {
		CConstraint c = realX(t);
		if (c == null) return null;

		// TODO: check dist.region.p and region.p

		FieldInstance fi = getProperty(t, propName);
		if (fi != null)
			return c.bindingForSelfField(XTerms.makeName(fi.def()));

		return null;
	}

	public static XTerm rank(Type t, X10Context context) {
	    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	    return findOrSythesize(t, Name.make("rank"));
	}

	public static Type railBaseType(Type t) {
	    t = baseType(t);
	    if (t instanceof X10ClassType) {
		X10ClassType ct = (X10ClassType) t;
		X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
		ClassType a = (ClassType) ts.Rail();
		ClassType v = (ClassType) ts.ValRail();
		if (ct.def() == a.def() || ct.def() == v.def())
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
		X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
		ClassType a = (ClassType) ts.Array();
		if (ct.def() == a.def())
		    return ct.typeArguments().get(0);
		else
		    arrayBaseType(ct.superClass());
	    }
	    return null;
	}

	public static boolean isVarArray(Type t) {
	    X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
	    Type tt = baseType(t);
	    Type at = baseType(ts.Array());
	    if (tt instanceof ClassType && at instanceof ClassType) {
	        ClassDef tdef = ((ClassType) tt).def();
	        ClassDef adef = ((ClassType) at).def();
	        return ts.descendsFrom(tdef, adef);
	    }
	    return false;
	}

	public static boolean isX10Array(Type t) {
	    return isVarArray(t);
	}

	static XTerm findOrSythesize(Type t, Name propName) {
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
	 */
	public static boolean isGlobalType(Type t) {
		if (isX10Struct(t))
			return true;
		return false;
		
	}

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
	
	public static boolean permitsNull(Type t) {
		if (isX10Struct(t))
			return false;
		if (X10TypeMixin.disEntailsSelf(t, XTerms.NULL))
			return false;
		if (((X10TypeSystem) t.typeSystem()).isParameterType(t))
			return false;
		return true;
		
	}

	public static XRoot thisVar(XRoot xthis, Type thisType) {
	    Type base = baseType(thisType);
	    if (base instanceof X10ClassType) {
	        XRoot supVar = ((X10ClassType) base).x10Def().thisVar();
	        return supVar;
	    }
	    return xthis;
	}

	public static void expandTypes(List<Type> formals, X10TypeSystem xts) {
		 for (int i = 0; i < formals.size(); ++i) {
	         formals.set(i, xts.expandMacros(formals.get(i)));
		 }
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

	public static Type meetTypes(X10TypeSystem xts, Type t1, Type t2, Context context) {
	    if (xts.isSubtype(t1, t2, context))
	        return t1;
	    if (xts.isSubtype(t2, t1, context))
	        return t2;
	    return null;
	}

	public static boolean moreSpecificImpl(ProcedureInstance<?> p1, ProcedureInstance<?> p2, Context context) {
	    X10TypeSystem ts = (X10TypeSystem) p1.typeSystem();
	
	    Type t1 = p1 instanceof MemberInstance ? ((MemberInstance) p1).container() : null;
	    Type t2 = p2 instanceof MemberInstance ? ((MemberInstance) p2).container() : null;
	
	    if (t1 != null && t2 != null) {
	        t1 = baseType(t1);
	        t2 = baseType(t2);
	    }
	
	    boolean descends = t1 != null && t2 != null && ts.descendsFrom(ts.classDefOf(t1), ts.classDefOf(t2));
	
	    Flags flags1 = p1 instanceof MemberInstance ? ((MemberInstance) p1).flags() : Flags.NONE;
	    Flags flags2 = p2 instanceof MemberInstance ? ((MemberInstance) p2).flags() : Flags.NONE;
	
	    // A static method in a subclass is always more specific.
	    // Note: this rule differs from Java but avoids an anomaly with conversion methods.
	    if (descends && ! ts.hasSameClassDef(t1, t2) && flags1.isStatic() && flags2.isStatic()) {
	        return true;
	    }
	    
	    
	
	    // if the formal params of p1 can be used to call p2, p1 is more specific
	    if (p1.formalTypes().size() == p2.formalTypes().size() ) {
	        for (int i = 0; i < p1.formalTypes().size(); i++) {
	            Type f1 = p1.formalTypes().get(i);
	            Type f2 = p2.formalTypes().get(i);
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
	    for (int i = 0; i < p1.formalTypes().size(); i++) {
	        Type f1 = p1.formalTypes().get(i);
	        Type f2 = p2.formalTypes().get(i);
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
	
	public static void checkMissingParameters(Type xt) throws SemanticException {
		if (xt == null) return;
		xt = baseType(xt);
		
		if (xt instanceof X10ParsedClassType) {
			X10ParsedClassType xt1 = (X10ParsedClassType) xt;
			
			if (xt1.subst() == null || xt1.subst().isMissingParameters()){
			List<ParameterType> expectedArgs = ((X10ClassDef) xt1.def()).typeParameters();
				throw new Errors.TypeIsMissingParameters(xt, expectedArgs, xt.position());
			}
		}
	}
	public static Type arrayElementType(Type t) {
		t = baseType(t);
		X10TypeSystem xt = (X10TypeSystem) t.typeSystem();
		if (xt.isX10Array(t) || xt.isRail(t)) {
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
	    return false;
	}
}
