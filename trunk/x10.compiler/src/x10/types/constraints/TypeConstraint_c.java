package x10.types.constraints;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.ParameterType_c;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10Context_c;
import x10.types.X10ProcedureDef;
import x10.types.X10ProcedureInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import polyglot.types.Name;
import polyglot.types.PrimitiveType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;

/**
 * Todo: This needs to be fixed. The constraints in this have to be used to figure
 * out whether c is entailed. This needs a proper constraint representation, e.g.
 * X <: Y, Y <: Z |- X <: Z
 * 
 * @author njnystrom
 * @author vj
 *
 */
public class TypeConstraint_c implements TypeConstraint {

    List<SubtypeConstraint> terms;
    boolean consistent;

    public TypeConstraint_c() {
        terms = new ArrayList<SubtypeConstraint>();
        consistent = true;
    }
  
    private  void addTypeParameterBindings(X10ClassDef xcd, X10ClassType xct, Type ytype) throws XFailure {
        if (ytype == null)
            return;

        if (ytype instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) ytype;
            addTypeParameterBindings(xcd, xct, ct.baseType().get());
        }

        if (ytype instanceof MacroType) {
            MacroType mt = (MacroType) ytype;
            addTypeParameterBindings(xcd, xct, mt.definedType());
        }

        if (ytype instanceof X10ClassType) {
            X10ClassType yct = (X10ClassType) ytype;
            X10ClassDef ycd = yct.x10Def();
            if (ycd == xcd) {
                for (int i = 0; i < yct.typeArguments().size(); i++) {
                    Type xt = xct.typeArguments().get(i);
                    Type yt = yct.typeArguments().get(i);
                    ParameterType.Variance v = xcd.variances().get(i);
                    X10TypeSystem xts = (X10TypeSystem) xcd.typeSystem();
                    switch (v) {
                    case INVARIANT: {
                        addTerm(new SubtypeConstraint_c(xt, yt, true));
                        break;
                    }
                    case CONTRAVARIANT: {
                        addTerm(new SubtypeConstraint_c(xt, yt, false));
                        break;
                    }
                    case COVARIANT: {
                        addTerm(new SubtypeConstraint_c(yt, xt, false));
                        break;
                    }
                    }
                }
            }
            else {
                addTypeParameterBindings(xcd, xct, yct.superClass());
                for (Type t: yct.interfaces()) {
                    addTypeParameterBindings(xcd, xct, t);
                }
            }
        }
    }

   
    public void addTypeParameterBindings(Type xtype, Type ytype) throws XFailure {
    	if (xtype instanceof ParameterType) {
    		X10TypeSystem xts = (X10TypeSystem) xtype.typeSystem();
    		//	    XRoot Xi = xts.xtypeTranslator().transTypeParam((ParameterType) xtype);
    		//	    XTerm Yi = xts.xtypeTranslator().trans(ytype);
    		//	    env.addBinding(Xi, Yi);
    		addTerm(new SubtypeConstraint_c(ytype, xtype, false));
    	}
    	if (xtype instanceof X10ClassType) {
    		X10ClassType xct = (X10ClassType) xtype;
    		X10ClassDef xcd = xct.x10Def();
    		addTypeParameterBindings(xcd, xct, ytype);
    	}
    	if (xtype instanceof ConstrainedType) {
    		ConstrainedType ct = (ConstrainedType) xtype;
    		addTypeParameterBindings(ct.baseType().get(), ytype);
    	}
    	if (xtype instanceof MacroType) {
    		MacroType mt = (MacroType) xtype;
    		addTypeParameterBindings(mt.definedType(), ytype);
    	}
    	if (xtype instanceof PrimitiveType) {
    		// Nothing to do
    	}
    }

    public TypeConstraint unify(Type t1, Type t2, X10TypeSystem xts) {
    	TypeConstraint result = this;
       final X10Context emptyContext = new X10Context_c(xts);
    	t1 = X10TypeMixin.stripConstraints(t1);
    	t2 = X10TypeMixin.stripConstraints(t2);   	
    	if (xts.typeEquals(t1, t2, emptyContext /*dummy*/))
    			return this;
    	if ((t1 instanceof ParameterType) || (t2 instanceof ParameterType)) {
    		result.addTerm(new SubtypeConstraint_c(t1, t2, SubtypeConstraint.EQUAL_KIND));
    		if (! (result.consistent(emptyContext)))
    			return result;
    	}
    	if ((t1 instanceof X10ClassType) && (t2 instanceof X10ClassType)) {
    		X10ClassType xt1 = (X10ClassType) t1;
    		X10ClassType xt2 = (X10ClassType) t2;
    		Type bt1 = xt1.x10Def().asType();
    		Type bt2 = xt2.x10Def().asType();
    		if (!xts.typeEquals(bt1,bt2, emptyContext)) {
    			result.setInconsistent();
    			return result;
    		}
    		List<Type> args1 = xt1.typeArguments();
    		List<Type> args2 = xt2.typeArguments();
    		if (args1.size() != args2.size()) {
    			result.setInconsistent();
    			return result;
    		}
    
    		for (int i=0; i < args1.size(); ++i) {
    			Type p1 = args1.get(i);
    			Type p2 = args2.get(i);
    			result = unify(p1,p2,xts);
    			if (! result.consistent(emptyContext)) {
    				return result;
    			}
    		}
    	}
    	return result;   			
    }
    public boolean entails(TypeConstraint c, X10Context xc) {
        X10TypeSystem xts = (X10TypeSystem) xc.typeSystem();
        for (SubtypeConstraint t : c.terms()) {
            if (t.isEqualityConstraint()) {
                if (!xts.typeEquals(t.subtype(), t.supertype(), xc)) {
                    return false;
                }
            }
            else if (t.isSubtypeConstraint()) {
                if (!xts.isSubtype(t.subtype(), t.supertype(), xc)) {
                    return false;
                }
            }
            
        }
        return true;
    }

    public List<SubtypeConstraint> terms() {
        return terms;
    }

    public TypeConstraint_c copy() {
        try {
            return (TypeConstraint_c) super.clone();
        }
        catch (CloneNotSupportedException e) {
            assert false;
            return this;
        }
    }
    
    public TypeConstraint addIn(TypeConstraint c) {
        terms.addAll(c.terms());
        return this;
    }
    
    public void addTerm(SubtypeConstraint c) {
        terms.add(c);
    }

    public void addTerms(List<SubtypeConstraint> terms) {
        this.terms.addAll(terms);
    }
    
    public boolean consistent(X10Context context) {
        if (consistent) {
            X10Context xc = (X10Context) context;
            X10TypeSystem ts = (X10TypeSystem) context.typeSystem();
            for (SubtypeConstraint t : terms()) {
                if (t.isEqualityConstraint()) {
                    if (! ts.typeEquals(t.subtype(), t.supertype(), xc)) {
                        consistent = false;
                        return false;
                    }
                }
                else if (t.isSubtypeConstraint()) {
                    if (! ts.isSubtype(t.subtype(), t.supertype(), xc)) {
                        consistent = false;
                        return false;
                    }
                }
            }
        }
        return consistent;
    }

    public void setInconsistent() {
        this.consistent = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.types.TypeConstraint#subst(x10.constraint.XTerm,
     * x10.constraint.XRoot, boolean)
     */
    public TypeConstraint subst(XTerm y, XRoot x) {
        TypeConstraint_c c = new TypeConstraint_c();
        List<SubtypeConstraint> l = c.terms;
        for (SubtypeConstraint s : terms) {
            l.add(s.subst(y, x));
        }
        return c;
    }

    @Override
    public String toString() {
        return terms.toString();
    }

	public void checkTypeQuery( TypeConstraint query, XVar ythis, XRoot xthis, XVar[] y, XRoot[] x, 
			 X10Context context) throws SemanticException {
		 if (! consistent(context)) {
	         throw new SemanticException("Call invalid; type environment is inconsistent.");
	     }
	    if (query != null) {
	    	 if ( ! ((X10TypeSystem) context.typeSystem()).consistent(query, context)) {
	             throw new SemanticException("Type guard " + query + " cannot be established; inconsistent in calling context.");
	         }
	        TypeConstraint query2 = xthis==null ? query : query.subst(ythis, xthis);
	        for (int i = 0; i < y.length; i++)
	            query2 = query2.subst(y[i], x[i]);
	        if (! entails(query2, context)) {
	            throw new SemanticException("Call invalid; calling environment does not entail the method guard.");
	        }
	    }
		
	}

	public static <PI extends X10ProcedureInstance<?>> Type[] inferTypeArguments(PI me, Type thisType, List<Type> actuals, List<Type> formals, List<Type> typeFormals, X10Context context) throws SemanticException {
	    X10TypeSystem xts = (X10TypeSystem) thisType.typeSystem();
	
	    TypeConstraint tenv = new TypeConstraint_c();
	    CConstraint env = new CConstraint_c();
	
	    XVar ythis = X10TypeMixin.selfVar(thisType);
	
	    if (ythis == null) {
	        CConstraint c = X10TypeMixin.xclause(thisType);
	        c = (c == null) ? new CConstraint_c() : c.copy();
	
	        try {
	            ythis = XTerms.makeUQV(); // xts.xtypeTranslator().genEQV(thisType, false);
	            c.addSelfBinding(ythis);
	            c.setThisVar(ythis);
	        }
	        catch (XFailure e) {
	            throw new SemanticException(e.getMessage(), me.position());
	        }
	
	        thisType = X10TypeMixin.xclause(X10TypeMixin.baseType(thisType), c);
	    }
	
	    assert actuals.size() == formals.size();
	
	    ParameterType[] X = new ParameterType[typeFormals.size()];
	    Type[] Y = new Type[typeFormals.size()];
	    Type[] Z = new Type[typeFormals.size()];
	    XRoot[] x = new XRoot[formals.size()];
	    XVar[] y = new XVar[formals.size()];
	
	    for (int i = 0; i < typeFormals.size(); i++) {
	        Type xtype = typeFormals.get(i);
	        xtype = xts.expandMacros(xtype);
	        Type ytype = new ParameterType_c(xts, me.position(), Name.makeFresh(), Types.ref((X10ProcedureDef) me.def()));
	
	        // TODO: should enforce this statically
	        assert xtype instanceof ParameterType : xtype + " is not a ParameterType, is a " + (xtype != null ? xtype.getClass().getName() : "null");
	
	        tenv.addTerm(new SubtypeConstraint_c(xtype, ytype, true));
	
	        X[i] = (ParameterType) xtype;
	        Y[i] = ytype;
	        Z[i] = ytype;
	    }
	
	    for (int i = 0; i < formals.size(); i++) {
	        Type xtype = formals.get(i);
	        Type ytype = actuals.get(i);
	
	        xtype = xts.expandMacros(xtype);
	        ytype = xts.expandMacros(ytype);
	
	        // Be sure to copy the constraints since we use the self vars
	        // in other constraints and don't want to conflate them if
	        // realX returns the same constraint twice.
	        final CConstraint yc = X10TypeMixin.realX(ytype).copy();
	
	        XRoot xi;
	        XVar yi;
	
	        yi = X10TypeMixin.selfVar(yc);
	
	        if (yi == null) {
	            // This must mean that yi was not final, hence it cannot occur in 
	            // the dependent clauses of downstream yi's.
	            yi = XTerms.makeUQV(); // xts.xtypeTranslator().genEQV(ytype, false);
	        }
	
	        try {
	            tenv.addTypeParameterBindings(xtype, ytype);
	        }
	        catch (XFailure f) {
	        }
	
	        CConstraint xc = X10TypeMixin.realX(xtype).copy();
	        xi = xts.xtypeTranslator().trans(me.formalNames().get(i), xtype);
	
	        x[i] = xi;
	        y[i] = yi;
	    }
	
	    // We'll subst selfVar for THIS.
	    XRoot xthis = null; // xts.xtypeTranslator().transThis(thisType);
	
	    if (me.def() instanceof X10ProcedureDef)
	        xthis = (XRoot) ((X10ProcedureDef) me.def()).thisVar();
	
	    if (xthis == null)
	        xthis = XTerms.makeLocal(XTerms.makeFreshName("this"));
	
	    // Create a big query for inferring type parameters.
	    // LIMITATION: can only infer types when actuals are subtypes of formals.
	    // This updates Y with new actual type arguments.
	    inferTypeArguments(context, me, tenv, X, Y, x, y, ythis, xthis);
	
	    for (int i = 0; i < Z.length; i++) {
	        if (Y[i] == Z[i])
	            throw new SemanticException("Cannot infer type for type parameter " + X[i] + ".", me.position());
	    }
	
	    return Y;
	}

	public static <PI extends X10ProcedureInstance<?>> void inferTypeArguments(X10Context context, PI me, TypeConstraint tenv,
	        ParameterType[] X, Type[] Y, XRoot[] x, XVar[] y, XVar ythis, XRoot xthis)
	throws SemanticException {
	
	    X10TypeSystem xts = (X10TypeSystem) me.typeSystem();
	
	    for (int i = 0; i < Y.length; i++) {
	        Type Yi = Y[i];
	
	        List<Type> upper = new ArrayList<Type>();
	        List<Type> lower = new ArrayList<Type>();
	
	        List<Type> worklist = new ArrayList<Type>();
	        worklist.add(Yi);
	
	        for (int j = 0; j < worklist.size(); j++) {
	            Type m = worklist.get(j);
	            for (SubtypeConstraint term : tenv.terms()) {
	                SubtypeConstraint eq = term;
	                if (term.isEqualityConstraint()) {
	                    if (m.typeEquals(eq.subtype(), context)) {
	                        if (! upper.contains(eq.supertype()))
	                            upper.add(eq.supertype());
	                        if (! lower.contains(eq.supertype()))
	                            lower.add(eq.supertype());
	                        if (! worklist.contains(eq.supertype()))
	                            worklist.add(eq.supertype());
	                    }
	                    if (m.typeEquals(eq.supertype(), context)) {
	                        if (! upper.contains(eq.subtype()))
	                            upper.add(eq.subtype());
	                        if (! lower.contains(eq.subtype()))
	                            lower.add(eq.subtype());
	                        if (! worklist.contains(eq.subtype()))
	                            worklist.add(eq.subtype());
	                    }
	                }
	                else {
	                    if (m.typeEquals(eq.subtype(), context)) {
	                        if (! upper.contains(eq.supertype()))
	                            upper.add(eq.supertype());
	                        if (! worklist.contains(eq.supertype()))
	                            worklist.add(eq.supertype());
	                    }
	                    if (m.typeEquals(eq.supertype(), context)) {
	                        if (! lower.contains(eq.subtype()))
	                            lower.add(eq.subtype());
	                        if (! worklist.contains(eq.subtype()))
	                            worklist.add(eq.subtype());
	                    }
	                }
	            }
	        }
	
	        for (Type Xi : X) {
	            upper.remove(Xi);
	            lower.remove(Xi);
	        }
	        for (Type Xi : Y) {
	            upper.remove(Xi);
	            lower.remove(Xi);
	        }
	
	        Type upperBound = null;
	        Type lowerBound = null;
	
	        for (Type t : upper) {
	            if (t != null) {
	                if (upperBound == null)
	                    upperBound = t;
	                else
	                    upperBound = X10TypeMixin.meetTypes(xts, upperBound, t, context);
	            }
	        }
	
	        for (Type t : lower) {
	            if (t != null) {
	                if (lowerBound == null)
	                    lowerBound = t;
	                else
	                    lowerBound = xts.leastCommonAncestor(lowerBound, t, context);
	            }
	        }
	
	        if (upperBound != null)
	            Y[i] = upperBound;
	        else if (lowerBound != null)
	            Y[i] = lowerBound;
	        else
	            throw new SemanticException("Could not infer type for type parameter " + X[i] + ".", me.position());
	    }
	}

    
}
