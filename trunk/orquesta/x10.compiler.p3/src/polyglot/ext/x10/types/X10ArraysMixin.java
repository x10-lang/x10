package polyglot.ext.x10.types;

import polyglot.types.FieldInstance;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class X10ArraysMixin {
	
            public static Type arrayBaseType(Type t) {
        	X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
        	return xts.arrayBaseType(t);
            }
            
	    protected static Type addBinding(Type t, XVar v1, XVar v2) {
	        return X10TypeMixin.addBinding(t, v1, v2);
	    }
	    
	    public static X10FieldInstance getProperty(Type t, String propName) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
			    try {
				    X10FieldInstance fi = (X10FieldInstance) xts.findField(t, xts.FieldMatcher(t, propName));
				    if (fi != null && fi.isProperty()) {
					    return fi;
				    }
			    }
			    catch (SemanticException e) {
				    // ignore
			    }
	        return null;
	    }
	    
	    protected static boolean amIProperty(Type t, String propName) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    XConstraint r = X10TypeMixin.realX(t);

		    X10FieldInstance fi = getProperty(t, propName);
		    if (fi != null) {
			    try {
				    XVar term = xts.xtypeTranslator().trans(XSelf.Self, fi);
				    XConstraint c = new XConstraint_c();
				    c.addBinding(term, xts.xtypeTranslator().trans(true));
				    return r.entails(c);
			    }
			    catch (XFailure f) {
				    return false;
			    }
			    catch (SemanticException f) {
				    return false;
			    }
		    }
		    return false;
	    }

	    public static boolean isRect(Type t) {
	        return amIProperty(t, "rect");
	    }

	    public static XTerm onePlace(Type t) {
	        return find(t, "onePlace");
	    }

	    private static XTerm findProperty(Type t, String propName) {
	        XConstraint c = X10TypeMixin.realX(t);
	        if (c == null) return null;
	        try {
	        	FieldInstance fi = getProperty(t, propName);
	        	if (fi != null)
	        		return c.find(XTerms.makeName(fi.def()));
		}
		catch (XFailure e) {
		}
		return null;
	    }
	    
	    public static boolean isZeroBased(Type t) {
	            if (isRail(t)) return true;
	            return amIProperty(t, "zeroBased");
	    }
	    
	    public static boolean isRail(Type t) {
	        return amIProperty(t, "rail");
	    }

	    public static XTerm distribution(Type t) {
		return findProperty(t, "dist");
	    }
	    
	    public static XTerm region(Type t) {
		return findProperty(t, "region");
	    }

	    public static XTerm rank(Type t) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	        if (isRail(t))
	           return xts.ONE();
	        return findOrSythesize(t, X10TypeSystem.RANK_FIELD);
	    }

	    private static XTerm findOrSythesize(Type t, String propName) {
		    return find(t, propName);
	    }

	    public static XTerm find(Type t, String propName) {
		    XTerm val = findProperty(t, propName);
		    
		    if (val == null) {
			    XConstraint c = X10TypeMixin.realX(t);
			    if (c != null) {
				    // build the synthetic term.
				    XTerm var = X10TypeMixin.selfVar(c);
				    if (var !=null) {
					    X10FieldInstance fi = getProperty(t, propName);
					    if (fi != null) {
						    try {
							    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
							    val = xts.xtypeTranslator().trans(var, fi);
						    }
						    catch (SemanticException e) {
							    throw new InternalCompilerError(e.getMessage(), e);
						    }
					    }
				    }
			    }
		    }
		    return val;
	    }
	    
	    public static boolean isRankOne(Type t) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	            return isRail(t) || xts.ONE().equals(rank(t));
	    }
	    public static boolean isRankTwo(Type t) {
	            X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	            return xts.TWO().equals(rank(t));
	    }
	    public static boolean isRankThree(Type t) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    return xts.THREE().equals(rank(t));
	    }

	    public static XVar self(Type t) {
		    XConstraint c = X10TypeMixin.realX(t);
		    if (c == null)
			    return null;
		    return X10TypeMixin.selfVar(c);
	    }
	    
}
