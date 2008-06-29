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
	
	    protected static Type setProperty(Type t, String propName) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	        return setProperty(t, propName, xts.TRUE());
	    }

	    protected static Type addBinding(Type t, XVar v1, XVar v2) {
	        return X10TypeMixin.addBinding(t, v1, v2);
	    }
	    
	    protected static Type setProperty(Type t, String propName, XTerm val) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	    	if (val == null) return t;
	        FieldInstance fi = getProperty(t, propName);
	        if (fi != null) {
	            try {
	        	    XTerm var = xts.xtypeTranslator().trans(XSelf.Self, fi);
	        	    XConstraint c = X10TypeMixin.xclause(t);
	        	    if (c == null) c = new XConstraint_c();
	        	    else c = c.copy();
	        	    c.addTerm(XTerms.makeEquals(var, val));
	        	    return X10TypeMixin.xclause(t, c);
	            }
	            catch (XFailure f) {
	        	    // Fail silently.
	        	    // FIXME: should be reported to caller, which can then choose whether to suppress the error
	        	    // but we're only called from code that would suppress the error anyway
	            }
	            catch (SemanticException e) {
	        	    // Fail silently.
	        	    // FIXME: should be reported to caller, which can then choose whether to suppress the error
	        	    // but we're only called from code that would suppress the error anyway
	            }
	        }
	        else {
	            assert false : "Could not find property " + propName + " in " + t;
	        }
	        return t;
	    }
	    
	    public static X10FieldInstance getProperty(Type t, String propName) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    if (t instanceof StructType) {
			    try {
				    X10FieldInstance fi = (X10FieldInstance) xts.findField((StructType) t, propName);
				    if (fi != null && fi.isProperty()) {
					    return fi;
				    }
			    }
			    catch (SemanticException e) {
				    // ignore
			    }
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

	    public static Type setRect(Type oldType) {
	       Type t = setProperty(oldType, "rect");
	        if (isRankOne(t) && isZeroBased(t) && ! isRail(t))
	            return setRail(t);
	        return t;
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
	    
	    public static Type setOnePlace(Type t, XTerm onePlace) {
	        return setProperty(t, "onePlace", onePlace);
	    }

	    public static boolean hasLocalProperty(Type t) {
		    XTerm onePlace = onePlace(t);
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    return onePlace != null && onePlace.equals(xts.here());
	    }
	    
	    public static boolean isZeroBased(Type t) {
	            if (isRail(t)) return true;
	            return amIProperty(t, "zeroBased");
	    }

	    public static Type setZeroBased(Type ot) {
		Type t = setProperty(ot, "zeroBased");
		if (isRect(t) && isRankOne(t) && !isRail(t))
			return setRail(t);
		return t;
	}
	    
	    public static boolean isRail(Type t) {
	        return amIProperty(t, "rail");
	    }

	    public static Type setRail(Type ot) {
		    Type t = setProperty(ot, "rail");
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    t = setRank(t, xts.ONE());
		    if (! isZeroBased(t)) t = setZeroBased(t);
		    if (! isRect(t)) t = setRect(t);
		    return t;
	    }
	    
	    public static XTerm rank(Type t) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	        if (isRail(t))
	           return xts.ONE();
	        return findOrSythesize(t, "rank");
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
	    
	    public static Type setRank(Type ot, XTerm rank) {
	            assert(rank !=null);
	            Type t = setProperty(ot, "rank", rank);
	            if (isRankOne(t) && isZeroBased(t) && isRect(t) && ! isRail(t))
	                    return setRail(t);
	            return t;
	    }
	    
	    public static boolean isRankOne(Type t) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	            return isRail(t) || xts.ONE().equals(rank(t));
	    }
	    public static boolean isRankTwo(Type t) {
	            X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	            return isRail(t) || xts.TWO().equals(rank(t));
	    }
	    public static boolean isRankThree(Type t) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    return isRail(t) || xts.THREE().equals(rank(t));
	    }

	    public static XTerm region(Type t) {
	        return findOrSythesize(t, "region");
	    }
	    public static Type setRegion(Type t, XTerm region) {
	           return setProperty(t, "region", region);
	    }
	    
	    public static XTerm distribution(Type t) {
	        return findOrSythesize(t, "distribution");
	    }
	    public static Type setDistribution(Type t, XTerm dist) {
	           return setProperty(t, "distribution", dist);
	    }

	    public static XVar self(Type t) {
		    XConstraint c = X10TypeMixin.realX(t);
		    if (c == null)
			    return null;
		    return X10TypeMixin.selfVar(c);
	    }
	    
	    public static boolean isConstantDist(Type t) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    return xts.isDistribution(t) && amIProperty(t, "constant");
	    }
	    
	    public static Type setConstantDist(Type t) {
	            return setProperty(t, "constant");
	    }
	    
	    public static  boolean isUniqueDist(Type t) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
		    return xts.isDistribution(t) && amIProperty(t, "unique");
	    }

	    public static Type setUniqueDist(Type t) {
	           return setProperty(t, "unique");
	    }
	    
	    /**
	     * The arg must be a region type. Set the properties of this type (rank, isZeroBased, isRect)
	     * from arg.
	     * @param arg
	     */
	    public static Type transferRegionProperties(Type t, Type arg) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	            XTerm rank = rank(arg);
	            XTerm region = region(arg);
	            if (region == null && xts.isRegion(arg))
	                    region = self(arg);
	            return acceptRegionProperties(t, region, rank, isZeroBased(t), isRect(t), isRail(t));
	    }
	    
	    public static Type acceptRegionProperties(Type t, XTerm region, XTerm rank, boolean isZeroBased, boolean isRect, boolean isRail) {
	        if (region != null) t = setRegion(t, region);
	        if (rank != null) t = setRank(t, rank);
	        if (isZeroBased) t = setZeroBased(t);
	        if (isRect) t = setRect(t);
	        if (isRail) t = setRail(t);
	        return t;
	    }
	    public static Type setZeroBasedRectRankOne(Type t) {
		    X10TypeSystem xts = (X10TypeSystem) t.typeSystem();
	        return acceptRegionProperties(t, region(t), xts.ONE(), true, true, true);
	    }

}
