/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.Collections;

import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class X10ArraysMixin {
	
	
	public static boolean isX10Array(Type t) {
	    return isVarArray(t);
	}
	
	public static boolean isVarArray(Type t) {
	    X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
	    Type tt = X10TypeMixin.baseType(t);
	    Type at = X10TypeMixin.baseType(ts.Array());
	    if (tt instanceof ClassType && at instanceof ClassType) {
	        ClassDef tdef = ((ClassType) tt).def();
	        ClassDef adef = ((ClassType) at).def();
	        return ts.descendsFrom(tdef, adef);
	    }
	    return false;
	}

	public static Type arrayBaseType(Type t) {
	    t = X10TypeMixin.baseType(t);
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
            
	public static Type railBaseType(Type t) {
	    t = X10TypeMixin.baseType(t);
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
	
	    protected static Type addBinding(Type t, XVar v1, XVar v2) {
	        return X10TypeMixin.addBinding(t, v1, v2);
	    }
	    
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
	    
	    protected static boolean amIProperty(Type t, Name propName, X10Context context) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    XConstraint r = X10TypeMixin.realX(t);

		    // first try self.p
		    X10FieldInstance fi = getProperty(t, propName);
		    if (fi != null) {
			    try {
				    XConstraint c = new XConstraint_c();
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
		                XConstraint c = new XConstraint_c();
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

	    private static XTerm findProperty(Type t, Name propName) {
	        XConstraint c = X10TypeMixin.realX(t);
	        if (c == null) return null;
	        
	        // TODO: check dist.region.p and region.p
	        try {
	        	FieldInstance fi = getProperty(t, propName);
	        	if (fi != null)
	        		return c.find(XTerms.makeName(fi.def()));
		}
		catch (XFailure e) {
		}
		return null;
	    }
	    
	    public static boolean isZeroBased(Type t, X10Context context) {
	            if (isRail(t, context)) return true;
	            return amIProperty(t, Name.make("zeroBased"), context);
	    }
	    
	    public static boolean isRail(Type t, X10Context context) {
	        return amIProperty(t, Name.make("rail"), context);
	    }

	    public static XTerm distribution(Type t) {
		return findProperty(t, Name.make("dist"));
	    }
	    
	    public static XTerm region(Type t) {
		return findProperty(t, Name.make("region"));
	    }

	    public static XTerm rank(Type t, X10Context context) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	        if (isRail(t, context))
	           return xts.ONE();
	        return findOrSythesize(t, Name.make("rank"));
	    }

	    private static XTerm findOrSythesize(Type t, Name propName) {
		    return find(t, propName);
	    }

	    public static XTerm find(Type t, Name propName) {
		    XTerm val = findProperty(t, propName);
		    
		    if (val == null) {
			    XConstraint c = X10TypeMixin.realX(t);
			    if (c != null) {
				    // build the synthetic term.
				    XTerm var = X10TypeMixin.selfVar(c);
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
	            return isRail(t, context) || xts.ONE().equals(rank(t, context));
	    }
	    public static boolean isRankTwo(Type t, X10Context context) {
	            X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	            return xts.TWO().equals(rank(t, context));
	    }
	    public static boolean isRankThree(Type t, X10Context context) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    return xts.THREE().equals(rank(t, context));
	    }

	    public static XVar self(Type t) {
		    XConstraint c = X10TypeMixin.realX(t);
		    if (c == null)
			    return null;
		    return X10TypeMixin.selfVar(c);
	    }
	    
}
