/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package x10.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.types.ArrayType;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.DerefTransform;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LazyRef_c;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MemberDef;
import polyglot.types.MemberInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.MethodInstance_c;
import polyglot.types.Name;
import polyglot.types.Named;
import polyglot.types.PrimitiveType;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

/**
 * A representation of a MethodInstance. This implements the requirement that method
 * annotations such as sequential, local, nonblocking, safe are preserved on overriding.
 * @author vj
 *
 */
public class X10MethodInstance_c extends MethodInstance_c implements X10MethodInstance {

	
    public X10MethodInstance_c(TypeSystem ts, Position pos, Ref<? extends X10MethodDef> def) {
        super(ts, pos, def);
    }

    @Override
    public boolean moreSpecific(ProcedureInstance<MethodDef> p, Context context) {
        return moreSpecificImpl(this, p, context);
    }

    public static boolean moreSpecificImpl(ProcedureInstance<?> p1, ProcedureInstance<?> p2, Context context) {
        X10TypeSystem ts = (X10TypeSystem) p1.typeSystem();

        Type t1 = p1 instanceof MemberInstance ? ((MemberInstance) p1).container() : null;
        Type t2 = p2 instanceof MemberInstance ? ((MemberInstance) p2).container() : null;

        if (t1 != null && t2 != null) {
            t1 = X10TypeMixin.baseType(t1);
            t2 = X10TypeMixin.baseType(t2);
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
                Type b1 = X10TypeMixin.baseType(f1);
                Type b2 = X10TypeMixin.baseType(f2);
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


    public static class NoClauseVariant implements Transformation<Type, Type> {
        public Type transform(Type o) {
            if (o instanceof ArrayType) {
                ArrayType at = (ArrayType) o;
                return at.base(Types.ref(transform(at.base())));
            }
            if (o instanceof ConstrainedType) {
                ConstrainedType ct = (ConstrainedType) o;
                return transform(Types.get(ct.baseType()));
            }
            return ((X10Type) o);
        }
    }

    public Object copy() { 
        return super.copy();
    }

    @Override
    public X10MethodInstance returnType(Type returnType) {
        return (X10MethodInstance) super.returnType(returnType);
    }
    public X10MethodInstance returnTypeRef(Ref<? extends Type> returnType) {
        return (X10MethodInstance) super.returnTypeRef(returnType);
    }

    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }

    XTerm body;

    public XTerm body() {
        if (this.body == null)
            return Types.get(x10Def().body());
        return body;
    }

    public X10MethodInstance body(XTerm body) {
        X10MethodInstance_c n = (X10MethodInstance_c) copy();
        n.body = body;
        return n;
    }

    /** Constraint on formal parameters. */
    protected XConstraint guard;
    public XConstraint guard() {
        if (guard == null)
            return Types.get(x10Def().guard());
        return guard;
    }
    public X10MethodInstance guard(XConstraint s) { 
        X10MethodInstance_c n = (X10MethodInstance_c) copy();
        n.guard = s; 
        return n;
    }

    /** Constraint on type parameters. */
    protected TypeConstraint typeGuard;
    public TypeConstraint typeGuard() {
        if (typeGuard == null)
            return Types.get(x10Def().typeGuard());
        return typeGuard;
    }
    public X10MethodInstance typeGuard(TypeConstraint s) { 
        X10MethodInstance_c n = (X10MethodInstance_c) copy();
        n.typeGuard = s; 
        return n;
    }

    public List<Type> typeParameters;

    public List<Type> typeParameters() {
        if (this.typeParameters == null) {
            return new TransformingList<Ref<? extends Type>, Type>(x10Def().typeParameters(), new DerefTransform<Type>());
        }

        return typeParameters;
    }

    public X10MethodInstance typeParameters(List<Type> typeParameters) {
        X10MethodInstance_c n = (X10MethodInstance_c) copy();
        n.typeParameters = typeParameters;
        return n;
    }

    public List<LocalInstance> formalNames;

    public List<LocalInstance> formalNames() {
        if (this.formalNames == null) {
            return new TransformingList<LocalDef,LocalInstance>(x10Def().formalNames(),
                    new Transformation<LocalDef,LocalInstance>() {
                public LocalInstance transform(LocalDef o) {
                    return o.asInstance();
                }
            });
        }

        return formalNames;
    }

    public X10MethodInstance formalNames(List<LocalInstance> formalNames) {
        X10MethodInstance_c n = (X10MethodInstance_c) copy();
        n.formalNames = formalNames;
        return n;
    }

    public static void buildSubst(X10MethodInstance mi, List<XVar> ys, List<XRoot> xs, XRoot thisVar) {
        if (mi.x10Def().thisVar() != null && mi.x10Def().thisVar() != thisVar) {
            ys.add(thisVar);
            xs.add(mi.x10Def().thisVar());
        }

        buildSubst(mi.container(), ys, xs, thisVar);
    }

    public static void buildSubst(Type t, List<XVar> ys, List<XRoot> xs, XRoot thisVar) {
        Type container = X10TypeMixin.baseType(t);
        if (container instanceof X10ClassType) {
            X10ClassDef cd = ((X10ClassType) container).x10Def();
            if (cd.thisVar() != null && cd.thisVar() != thisVar) {
                ys.add(thisVar);
                xs.add(cd.thisVar());
            }

            Type superClass = ((X10ClassType) container).superClass();
            if (superClass != null) {
                buildSubst(superClass, ys, xs, thisVar);
            }

            for (Type ti : ((X10ClassType) container).interfaces()) {
                buildSubst(ti, ys, xs, thisVar);
            }
        }
    }

    public static X10MethodInstance fixThis(X10MethodInstance mi, final XVar[] y, final XRoot[] x) {
        X10MethodInstance mj = mi;

        X10TypeSystem ts = (X10TypeSystem) mi.typeSystem();

        final X10MethodInstance zmj = mj;
        final LazyRef<Type> tref = new LazyRef_c<Type>(null);
        tref.setResolver(new Runnable() { 
            public void run() {
                try {
                    Type subst = Subst.subst(zmj.returnType(), y, x, new Type[] { }, new ParameterType[] { });
                    tref.update(subst);
                }
                catch (SemanticException e) {
                    tref.update(zmj.returnType());
                }
            }
        });

        mj = (X10MethodInstance) ((MethodInstance) mj).returnTypeRef(tref);

        List<Type> newFormals = new ArrayList<Type>();

        for (Type t : mj.formalTypes()) {
            try {
                Type newT;
                newT = Subst.subst(t, y, x, new Type[] { }, new ParameterType[] { });
                newFormals.add(newT);
            }
            catch (SemanticException e) {
                newFormals.add(t);
            }
        }

        mj = (X10MethodInstance) mj.formalTypes(newFormals);

        if (mj.guard() != null) {
            try {
                XConstraint newGuard = mj.guard().substitute(y, x);
                mj = (X10MethodInstance) mj.guard(newGuard);
            }
            catch (XFailure e) {
            }
        }
        if (mj.typeGuard() != null) {
            TypeConstraint newGuard = mj.typeGuard();
            for (int i = 0; i < x.length; i++)
                newGuard = newGuard.subst(y[i], x[i]);
            mj = (X10MethodInstance) mj.typeGuard(newGuard);
        }

        return mj;
    }


    public boolean isPropertyGetter() {
        StructType container = this.container();
        assert container instanceof X10ParsedClassType;
        if (!formalTypes.isEmpty()) return false;
        for (FieldInstance fi : container.fields()) {
            FieldDef fd = fi.def();
            if (fd instanceof X10FieldDef) {
                X10FieldDef xfd = (X10FieldDef) fd;
                if (xfd.isProperty()) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isSafe() {
        StructType container = this.container();
        assert container instanceof X10ParsedClassType : container + " for " + this;
        boolean result = ((X10ParsedClassType) container).safe();
        if (result) return true;
        X10Flags f = X10Flags.toX10Flags(flags());
        result = f.isSafe();
        return result;
    }
    protected static String myListToString(List l) {
        StringBuffer sb = new StringBuffer();

        for (Iterator i = l.iterator(); i.hasNext(); ) {
            Object o = i.next();
            sb.append(o.toString());

            if (i.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public static boolean callValidImpl(X10ProcedureInstance<?> me, Type thisType, final List<Type> args, Context context) {
        // me should have been instantiated correctly; if so, the call is valid
        if (true) return true;
        try {
            assert false : "need to rewrite";
        inferAndCheckAndInstantiate((X10Context) context, me, thisType, Collections.EMPTY_LIST, args);
        return true;
        }
        catch (SemanticException e) {
            return false;
        }
    }

    public X10MethodDef x10Def() {
        return (X10MethodDef) def();
    }

    public String containerString() {
        Type container = container();
        container = X10TypeMixin.baseType(container);
        if (container instanceof FunctionType) {
            return "(" + container.toString() + ")";
        }
        if (container instanceof Named) {
            Named n = (Named) container;
            return n.fullName().toString();
        }
        return container.toString();
    }

    public String toString() {
        String s = designator() + " " + X10Flags.toX10Flags(flags()).prettyPrint() + containerString() + "." + signature();

        if (! throwTypes().isEmpty()) {
            s += " throws " + CollectionUtil.listToString(throwTypes());
        }

        if (body != null)
            s += " = " + body;

        return s;
    }

    public String signature() {
        StringBuilder sb = new StringBuilder();
        sb.append(name != null ? name : def().name());
        List<String> params = new ArrayList<String>();
        if (typeParameters != null) {
            for (int i = 0; i < typeParameters.size(); i++) {
                params.add(typeParameters.get(i).toString());
            }
        }
        else {
            for (int i = 0; i < x10Def().typeParameters().size(); i++) {
                params.add(x10Def().typeParameters().get(i).toString());
            }
        }
        if (params.size() > 0) {
            sb.append("[");
            sb.append(CollectionUtil.listToString(params));
            sb.append("]");
        }
        List<String> formals = new ArrayList<String>();
        if (formalTypes != null) {
            for (int i = 0; i < formalTypes.size(); i++) {
                String s = "";
                String t = formalTypes.get(i).toString();
                if (formalNames != null && i < formalNames.size()) {
                    LocalInstance a = formalNames.get(i);
                    if (a != null && ! a.name().toString().equals(""))
                        s = a.name() + ": " + t; 
                    else
                        s = t;
                }
                else {
                    s = t;
                }
                formals.add(s);
            }
        }
        else {
            for (int i = 0; i < def().formalTypes().size(); i++) {
                formals.add(def().formalTypes().get(i).toString());
            }
        }
        sb.append("(");
        sb.append(CollectionUtil.listToString(formals));
        sb.append(")");
        if (guard != null)
            sb.append(guard);
        else if (x10Def().guard() != null)
            sb.append(x10Def().guard());
        if (typeGuard != null)
            sb.append(typeGuard);
        else if (x10Def().typeGuard() != null)
            sb.append(x10Def().typeGuard());
        if (returnType != null && returnType.known()) {
            sb.append(": ");
            sb.append(returnType);
        }
        return sb.toString();
    }


    private static void addTypeParameterBindings(X10ClassDef xcd, X10ClassType xct, Type ytype, TypeConstraint env) throws XFailure {
        if (ytype == null)
            return;

        if (ytype instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) ytype;
            addTypeParameterBindings(xcd, xct, ct.baseType().get(), env);
        }

        if (ytype instanceof MacroType) {
            MacroType mt = (MacroType) ytype;
            addTypeParameterBindings(xcd, xct, mt.definedType(), env);
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
                        env.addTerm(new SubtypeConstraint_c(xt, yt, true));
                        break;
                    }
                    case CONTRAVARIANT: {
                        env.addTerm(new SubtypeConstraint_c(xt, yt, false));
                        break;
                    }
                    case COVARIANT: {
                        env.addTerm(new SubtypeConstraint_c(yt, xt, false));
                        break;
                    }
                    }
                }
            }
            else {
                addTypeParameterBindings(xcd, xct, yct.superClass(), env);
                for (Type t: yct.interfaces()) {
                    addTypeParameterBindings(xcd, xct, t, env);
                }
            }
        }
    }

    private static void addTypeParameterBindings(Type xtype, Type ytype, TypeConstraint env) throws XFailure {
        if (xtype instanceof ParameterType) {
            X10TypeSystem xts = (X10TypeSystem) xtype.typeSystem();
            //	    XRoot Xi = xts.xtypeTranslator().transTypeParam((ParameterType) xtype);
            //	    XTerm Yi = xts.xtypeTranslator().trans(ytype);
            //	    env.addBinding(Xi, Yi);
            env.addTerm(new SubtypeConstraint_c(ytype, xtype, false));
        }
        if (xtype instanceof X10ClassType) {
            X10ClassType xct = (X10ClassType) xtype;
            X10ClassDef xcd = xct.x10Def();
            addTypeParameterBindings(xcd, xct, ytype, env);
        }
        if (xtype instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) xtype;
            addTypeParameterBindings(ct.baseType().get(), ytype, env);
        }
        if (xtype instanceof MacroType) {
            MacroType mt = (MacroType) xtype;
            addTypeParameterBindings(mt.definedType(), ytype, env);
        }
        if (xtype instanceof PrimitiveType) {
            // Nothing to do
        }
    }

    public static <PI extends X10ProcedureInstance<?>> Type[] inferTypeArguments(PI me, Type thisType, List<Type> actuals, List<Type> formals, List<Type> typeFormals, X10Context context) throws SemanticException {
        X10TypeSystem xts = (X10TypeSystem) thisType.typeSystem();

        TypeConstraint tenv = new TypeConstraint_c();
        XConstraint env = new XConstraint_c();

        XVar ythis = X10TypeMixin.selfVar(thisType);

        if (ythis == null) {
            XConstraint c = X10TypeMixin.xclause(thisType);
            c = (c == null) ? new XConstraint_c() : c.copy();

            try {
                ythis = xts.xtypeTranslator().genEQV(thisType, false);
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
            final XConstraint yc = X10TypeMixin.realX(ytype).copy();

            XRoot xi;
            XVar yi;

            yi = X10TypeMixin.selfVar(yc);

            if (yi == null) {
                // This must mean that yi was not final, hence it cannot occur in 
                // the dependent clauses of downstream yi's.
                yi = xts.xtypeTranslator().genEQV(ytype, false);
            }

            try {
                addTypeParameterBindings(xtype, ytype, tenv);
            }
            catch (XFailure f) {
            }

            XConstraint xc = X10TypeMixin.realX(xtype).copy();
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

    public static <PI extends X10ProcedureInstance<?>> PI inferAndCheckAndInstantiate(X10Context context, PI me, 
    		Type thisType, 
    		List<Type> typeActuals, 
    		final List<Type> actuals) throws SemanticException {
        final List<Type> typeFormals = me.typeParameters();
        final List<Type> formals = me.formalTypes();

        if (typeActuals.isEmpty() && ! typeFormals.isEmpty()) {
            Type[] Y = inferTypeArguments(me, thisType, actuals, formals, typeFormals, context);
            return inferAndCheckAndInstantiate(context, me, thisType, Arrays.asList(Y), actuals);
        }

      
        Type[] thisTypes = new Type[] {thisType};
        XVar[] ys = new XVar[actuals.size()+1];
        
        // this call sets up the return type. In the return type the symbolic variables (if any) 
        // generated to represent the actual arguments to the call should be treated as EQVs.
        PI me3 = instantiate2(context, me, thisTypes, typeActuals, actuals, true,  ys);
        
        // ensure that a fresh ys and thisTypes are sent in to the instantiate2 call whose
        // results are going to be used to check that the call is valid.
        // In this call, the symbolic variables (if any) generated to represent the actual
        // arguments to the call must be UQVs ... the types must be satisfied for *any* value
        // of the called types.
        ys = new XVar[actuals.size()+1];
        thisTypes = new Type[] {thisType};
        PI me2 = instantiate2(context, me, thisTypes, typeActuals, actuals, false, ys);
        checkCall(context, me2, thisTypes[0], typeActuals, actuals, ys);
        
        // Return the procedure instance used to generate the right return type.
        return me3;
    }
    
    private static void checkQuery(XConstraint query, XVar ythis, XRoot xthis, XVar[] y, XRoot[] x, 
    		XConstraint env, X10Context context) throws SemanticException {
    	 // Check that the guard is entailed.
        try {
            if (query != null) { 
                if (! ((X10TypeSystem) context.typeSystem()).consistent(query)) {
                    throw new SemanticException("Guard " + query + " cannot be established; inconsistent in calling context.");
                }
                XConstraint query2 = xthis==null ? query : query.substitute(ythis, xthis);
                query2.setThisVar(ythis);
                //	                XConstraint query3 = query2.substitute(Y, X);
                XConstraint query3 = query2;
                XConstraint query4 = query3.substitute(y, x);
                
                if (! env.entails(query4, context.constraintProjection(env, query4))) {
                    throw new SemanticException("Call invalid; calling environment does not entail the method guard.");
                }
            }
        }
        catch (XFailure f) {
            // Substitution introduces inconsistency.
            throw new SemanticException("Call invalid; calling environment is inconsistent.");
        }
    }
    private static void checkTypeQuery( TypeConstraint query, XVar ythis, XRoot xthis, XVar[] y, XRoot[] x, 
    		TypeConstraint tenv, X10Context context) throws SemanticException {
    	 if (! tenv.consistent(context)) {
             throw new SemanticException("Call invalid; type environment is inconsistent.");
         }
        if (query != null) {
        	 if ( ! ((X10TypeSystem) context.typeSystem()).consistent(query, context)) {
                 throw new SemanticException("Type guard " + query + " cannot be established; inconsistent in calling context.");
             }
            TypeConstraint query2 = xthis==null ? query : query.subst(ythis, xthis);
            for (int i = 0; i < y.length; i++)
                query2 = query2.subst(y[i], x[i]);
            if (! tenv.entails(query2, context)) {
                throw new SemanticException("Call invalid; calling environment does not entail the method guard.");
            }
        }
    	
    }
    
    /**
     * Update the types in formals by subsituting ythis/xthis and y/x.
     * @param formals
     * @param ythis
     * @param xthis
     * @param y
     * @param x
     */
    private static void updateFormalTypes(List<Type> formals, XVar ythis, XRoot xthis, XVar[] y, XRoot[] x, 
    		boolean isStatic)
    		throws SemanticException {
        for (int i=0; i < formals.size(); ++i) {
        	 XConstraint formalC = X10TypeMixin.xclause(formals.get(i));
             if (formalC != null) {
             	try {
             	formalC = formalC.copy().substitute(y, x);
             	formalC = formalC.instantiateSelf(y[i]);
             	if ((! isStatic) && xthis != null)
             		formalC = formalC.substitute(ythis, xthis);
             	formals.set(i, X10TypeMixin.constrainedType(X10TypeMixin.baseType(formals.get(i)), 
             			formalC));
             	} catch (XFailure z) {
             		 throw new SemanticException("Call invalid; calling environment is inconsistent.");
             	}
             }
        }
    }
    private static void expandTypes(List<Type> formals, X10TypeSystem xts) {
    	 for (int i = 0; i < formals.size(); ++i) {
             formals.set(i, xts.expandMacros(formals.get(i)));
    	 }
    }
   
    private static XConstraint computeNewSigma(Type thisType, List<Type> actuals, XVar[] y, X10TypeSystem xts) 
    throws SemanticException {
    	return computeNewSigma(thisType, actuals, null, y, xts);
    }
    
    private static XConstraint computeNewSigma(Type thisType, List<Type> actuals, 
    		XVar ythis, XVar[] y, X10TypeSystem xts) 
    throws SemanticException {

    	XConstraint env = X10TypeMixin.xclause(thisType);
    	if (ythis != null) {
    		if (! ((env == null) || env.valid())) {
    			env = env.instantiateSelf(ythis);
    		}
    	}
    	
    	env = env == null ? new XConstraint_c() : env.copy();
       
        for (int i = 0; i < actuals.size(); i++) { // update Gamma
            Type ytype = actuals.get(i);
            final XConstraint yc = X10TypeMixin.realX(ytype);
            try {
                if (! ((yc == null) || yc.valid())){
                    env.addIn(y[i], yc);
                }
            } catch (XFailure f) {
                throw new SemanticException("Call invalid; calling environment is inconsistent.");
            }
        }
        return env;
    }
    private static XVar getSymbol(Type type, boolean eqv, X10TypeSystem xts) {
    	return getSymbol(type, "arg", eqv, xts);
    }
    private static XVar getSymbol(Type type, String prefix, boolean eqv, X10TypeSystem xts) {
    	  XVar symbol = X10TypeMixin.selfVarBinding(type);
          if (symbol == null) {
              symbol = xts.xtypeTranslator().genEQV(XTerms.makeFreshName(prefix),  type, eqv);
          }
          return symbol;
    }
    private static XVar[] getSymbolicNames(List<Type> actuals, boolean eqv, X10TypeSystem xts) {
    	  XVar[] ySymbols = new XVar[actuals.size()];
          for (int i = 0; i < actuals.size(); i++) {
               ySymbols[i] = getSymbol(actuals.get(i), eqv, xts); 
          }
          return ySymbols;
    }
    private static XRoot[] getSymbolicNames(List<Type> formals, List<LocalInstance> formalNames, X10TypeSystem xts) 
    throws SemanticException {
    	 XRoot[] x = new XRoot[formals.size()];
         for (int i = 0; i < formals.size(); i++) {
             x[i]=xts.xtypeTranslator().trans(formalNames.get(i), formals.get(i));
             assert x[i] != null;
         }
         return x;
    }
  /**
   * thisType has had ys[0] substituted in for its self, and for this.
   * 
   * @param <PI>
   * @param context
   * @param me
   * @param thisType
   * @param typeActuals
   * @param actuals
   * @param ys
   * @throws SemanticException
   */
    private static <PI extends X10ProcedureInstance<?>> void checkCall(X10Context context, PI me, 
    		Type thisType, 
    		List<Type> typeActuals, 
    		final List<Type> actuals, 
    		XVar[] ys) throws SemanticException {

    	final X10TypeSystem xts = (X10TypeSystem) me.typeSystem();
        final List<Type> formals =  new ArrayList<Type>(me.formalTypes());
        final List<LocalInstance> formalNames = me.formalNames();
        final List<Type> typeFormals = me.typeParameters();
        boolean isStatic = isStatic(me);

        expandTypes(formals, xts);
        final XRoot[] x = getSymbolicNames(formals, me.formalNames(), xts);
        final XVar[] y = new XVar[formals.size()];
        final XVar ythis = ys[0];
        System.arraycopy(ys, 1, y, 0, formals.size());
        
        final XConstraint env = computeNewSigma(thisType, actuals, y, xts);
        try {
        	env.addBinding(XTerms.HERE, context.currentPlaceTerm().term());
        } catch (XFailure z) {
        	throw new SemanticException("Inconsistent place constraints");
        }
        context = context.pushAdditionalConstraint(env);
        
        // We'll subst ythis for THIS unless we are in a static context
        XRoot xthis = null; 
        if (me.def() instanceof X10ProcedureDef)
            xthis = (XRoot) ((X10ProcedureDef) me.def()).thisVar();

        updateFormalTypes(formals, ythis, xthis, y, x, isStatic);
        checkQuery(me.guard(), ythis, xthis, y, x, env, context);        
        
     // Establish the type env, tenv and set up X and Y.
        
        TypeConstraint tenv = new TypeConstraint_c();
        Type[] X = new Type[typeFormals.size()];
        Type[] Y = new Type[typeFormals.size()];
        for (int i = 0; i < typeFormals.size(); i++) {
            X[i] = xts.expandMacros(typeFormals.get(i));
            Y[i] = xts.expandMacros(typeActuals.get(i));
            tenv.addTerm(new SubtypeConstraint_c(X[i], Y[i], true));
        }
        checkTypeQuery(me.typeGuard(), ythis, xthis, y, x, tenv, context);

        // After inferring the types, check that the assignment is allowed.
        for (int i = 0; i < formals.size(); i++) {
        	Type ytype = actuals.get(i);
        	Type xtype = formals.get(i);

        	if (! xts.consistent(xtype, context)) {
                throw new SemanticException("Parameter type " + xtype + " of call is inconsistent in calling context.");
            }
        	if (! xts.isSubtypeWithValueInterfaces(ytype, xtype, context)) {
        		throw new SemanticException("Call invalid; actual parameter of type |" + 
        				ytype  + "|\n cannot be assigned to formal parameter type |" + xtype + "|.");
        	}
        } 
    }
    
    
    private static <PI extends X10ProcedureInstance<?>>  boolean isStatic(PI me) {
    	if (me instanceof ConstructorInstance) 
    		return true;
    	if (me instanceof MethodInstance) {
    		MethodInstance mi = (MethodInstance) me;
    		return mi.flags().isStatic();
    	}
    	return false;
    }
    public static <PI extends X10ProcedureInstance<?>> PI instantiate2(X10Context context, PI me, 
    		Type[] thisTypeArray, 
    		List<Type> typeActuals, 
    		List<Type> actuals, 
    	    boolean eqv, 
    		XVar[] ys) throws SemanticException {
        final X10TypeSystem xts = (X10TypeSystem) me.typeSystem();
        final List<Type> formals = new ArrayList<Type>(me.formalTypes());
        final List<LocalInstance> formalNames = me.formalNames();
        final List<Type> typeFormals = me.typeParameters();
        final boolean isStatic = isStatic(me);

        if (actuals.size() != formals.size()) 
            throw new SemanticException("Call not valid; incorrect number of actual arguments.", me.position());
  
        if (typeActuals.size() != typeFormals.size()) 
            throw new SemanticException("Call not valid; incorrect number of actual type arguments.", me.position());
        
        
        expandTypes(formals, xts);
        actuals = new ArrayList<Type>(actuals);
        expandTypes(actuals, xts);
        
        Type thisType = thisTypeArray[0];
        XVar ythiseqv =  ys[0] = getSymbol(thisType, eqv, xts);
        thisTypeArray[0] = thisType = X10TypeMixin.instantiateSelf(ythiseqv, thisType);
      

        XRoot[] x = getSymbolicNames(formals, me.formalNames(), xts); 
        XVar[] ySymbols = getSymbolicNames(actuals, eqv, xts);
        System.arraycopy(ySymbols, 0, ys, 1, actuals.size());
       
        XConstraint returnEnv = computeNewSigma(thisType, actuals, ythiseqv, ySymbols, xts);

        // We'll subst selfVar for THIS.
        XRoot xthis = null; // xts.xtypeTranslator().transThis(thisType);

        if (me.def() instanceof X10ProcedureDef)
            xthis = (XRoot) ((X10ProcedureDef) me.def()).thisVar();

        if (xthis == null)
            xthis = XTerms.makeLocal(XTerms.makeFreshName("this"));
        
        updateFormalTypes(formals, ythiseqv, xthis, ySymbols, x, isStatic);
        
//        me = unifyThis(me, xthis, thisType);

        XRoot[] x2 = x;
        XTerm[] y2eqv = ySymbols;
        if (! isStatic) {
        	 x2 = new XRoot[x.length+2];
             y2eqv = new XTerm[ySymbols.length+2];
             x2[0] = xthis;
             y2eqv[0] = ythiseqv;
             x2[1] = thisVar(xthis, thisType);
             y2eqv[1] = ythiseqv;

             System.arraycopy(x, 0, x2, 2, x.length);
             System.arraycopy(ySymbols, 0, y2eqv, 2, ySymbols.length);
        }
        
       

        List<Type> newFormals = new ArrayList<Type>();

        ParameterType[] X = new ParameterType[typeFormals.size()];
        Type[] Y = new Type[typeFormals.size()];
        for (int i = 0; i < typeFormals.size(); i++) {
            Type xtype = xts.expandMacros(typeFormals.get(i));
            Y[i] = xts.expandMacros(typeActuals.get(i));
           
            // TODO: should enforce this statically
            assert xtype instanceof ParameterType : xtype + " is not a ParameterType, is a " 
            + (xtype != null ? xtype.getClass().getName() : "null");
            X[i] = (ParameterType) xtype;
        }
        
        for (Type t : formals) {
            Type newT = Subst.subst(t, y2eqv, x2, Y, X); 
            newFormals.add(newT);
        }
        
        
        // BUG: we subst in an existential, then check that existential
        // should subst in a fresh var, not an existential

        XConstraint newWhere = Subst.subst(me.guard(), y2eqv, x2, Y, X); 
        TypeConstraint newTWhere = Subst.subst(me.typeGuard(), y2eqv, x2, Y, X);

        final PI zme = me;
        final XTerm[] zy2 = y2eqv;
        final XRoot[] zx2 = x2;
        final Type[] zY = Y;
        final ParameterType[] zX = X;
        final XConstraint zenv = returnEnv;
        final X10Context zcontext = context;
        
        final LazyRef_c<Type> newReturnTypeRef = new LazyRef_c<Type>(null);
        newReturnTypeRef.setResolver(new Runnable() {
            public void run() {
                try {
                    PI zz = zme;
                    Type rt = zz.returnType();
                    Type newReturnType = Subst.subst(rt, zy2, zx2, zY, zX);
                    if (! newReturnType.isVoid() && ! (newReturnType instanceof UnknownType)) {
                        XConstraint c = X10TypeMixin.xclause(newReturnType);
                        c = c == null ? new XConstraint_c() : c.copy();
                        try {
                            // Add the terms in the environment for any vars actually appearing in the constraint.
                            // Complicated by the fact that when we add a term, we need to add all other terms with the same vars.
                            Set<XTerm> added = new HashSet<XTerm>();
                            Map<XEQV,List<XTerm>> m = new HashMap<XEQV, List<XTerm>>();
                            
                            for (XTerm t : zenv.constraints()) {
                                for (XEQV v: t.eqvs()) {
                                    List<XTerm> l = m.get(v);
                                    if (l == null) {
                                        l = new ArrayList<XTerm>(1);
                                        m.put(v, l);
                                    }
                                    l.add(t);
                                }
                            }

                            List<XVar> w = new ArrayList<XVar>();
                            w.addAll(c.eqvs());
                            for (int i = 0; i < w.size(); i++) {
                                XVar v = w.get(i);
                                List<XTerm> l = m.get(v);
                                if (l != null) {
                                    for (XTerm t : l) {
                                        if (! added.contains(t)) {
                                            c.addTerm(t);
                                            added.add(t);
                                            w.addAll(t.eqvs());
                                        }
                                    }
                                }
                            }
                            newReturnType = X10TypeMixin.xclause(newReturnType, c);
                        }
                        catch (XFailure e) {
                        }
                    }
                    if (! xts.consistent(newReturnType, zcontext)) {
                        throw new SemanticException("Result type " + newReturnType + " of call is inconsistent in calling context.");
                    }
                    newReturnTypeRef.update(newReturnType);
                }
                catch (SemanticException e) {
                    newReturnTypeRef.update(xts.unknownType(zme.position()));
                }
            } 
        });

        me = (PI) me.copy();
        me = (PI) me.typeParameters(Arrays.asList(Y));
        me = (PI) me.returnTypeRef(newReturnTypeRef);
        me = (PI) me.formalTypes(newFormals);
        me = (PI) me.guard(newWhere);
        me = (PI) me.typeGuard(newTWhere);

        return me;
    }

    private static <PI extends X10ProcedureInstance<?>> void inferTypeArguments(X10Context context, PI me, TypeConstraint tenv,
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
                        upperBound = meetTypes(xts, upperBound, t, context);
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

    public static Type meetTypes(X10TypeSystem xts, Type t1, Type t2, Context context) {
        if (xts.isSubtype(t1, t2, context))
            return t1;
        if (xts.isSubtype(t2, t1, context))
            return t2;
        return null;
    }

    Type rightType;

    public Type rightType() {
        X10TypeSystem xts = (X10TypeSystem) ts;

        if (rightType == null) {
            Type t = returnType();

            // If a property method, replace T with T{self==this}.
            X10Flags flags = X10Flags.toX10Flags(flags());

            if (flags.isProperty() && formalTypes.size() == 0) {
                if (t instanceof UnknownType) {
                    rightType = t;
                }
                else {
                    XConstraint rc = X10TypeMixin.xclause(t);
                    if (rc == null)
                        rc = new XConstraint_c();

                    XTerm receiver;

                    if (flags.isStatic()) {
                        receiver = xts.xtypeTranslator().trans(container());
                    }
                    else {
                        receiver = x10Def().thisVar();
                        assert receiver != null;
                    }

                    try {
                        // ### pass in the type rather than letting XField call fi.type();
                        // otherwise, we'll get called recursively.
                        XTerm self = body();

                        XConstraint c = rc.copy();

                        // TODO: handle non-vars, like rail().body
                        if (self == null || ! (self instanceof XVar)) {
                            self = xts.xtypeTranslator().trans(c, receiver, this, t);
                        }

                        c.addSelfBinding(self);
                        if (! flags.isStatic()) {
                        	c.setThisVar((XVar) receiver);
                        }
                        rightType = X10TypeMixin.xclause(X10TypeMixin.baseType(t), c);
                    }
                    catch (XFailure f) {
                        throw new InternalCompilerError("Could not add self binding: " + f.getMessage(), f);
                    }
                    catch (SemanticException f) {
                        throw new InternalCompilerError(f);
                    }
                }
            }
            else {
                rightType = t;
            }

            assert rightType != null;
        }

        return rightType;
    }

    public static <PI extends X10ProcedureInstance<?>> PI instantiate(X10Context context, PI me, 
    		Type thisType, 
    		List<Type> typeActuals, 
    		final List<Type> actuals) throws SemanticException {
        return instantiate2(context, me, new Type[] {thisType}, typeActuals, actuals, false, new XVar[actuals.size()+1]);
    }
    
    
    
//    static <PI extends X10ProcedureInstance<?>> PI unifyThis(PI me, XRoot xthis, Type thisType) {
//        Type base = X10TypeMixin.baseType(thisType);
//        if (base instanceof X10ClassType) {
//            XRoot supVar = ((X10ClassType) base).x10Def().thisVar();
//            try {
//                if (me instanceof MemberInstance) {
//                    StructType container = ((MemberInstance<?>) me).container();
//                    StructType newContainer = (StructType) Subst.subst(container, xthis, supVar);
//                    me = (PI) ((MemberInstance<?>) me).container((StructType) newContainer);
//                }
//                me = me.formalTypes(newFormals);
//            }
//            catch (SemanticException e) {
//            }
//        }
//        return me;
//    }
    
    static XRoot thisVar(XRoot xthis, Type thisType) {
        Type base = X10TypeMixin.baseType(thisType);
        if (base instanceof X10ClassType) {
            XRoot supVar = ((X10ClassType) base).x10Def().thisVar();
            return supVar;
        }
        return xthis;
    }

    static private String toString( XVar[] ys) {
    	String s = "";
    		for (XVar x : ys) s += x.toString() + " ";
    		return s;
    }

}
