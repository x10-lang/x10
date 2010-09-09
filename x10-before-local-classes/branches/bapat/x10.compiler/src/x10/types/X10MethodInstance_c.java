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
        if (container instanceof ClosureType) {
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
                ythis = xts.xtypeTranslator().genEQV(c, thisType, false);
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
                yi = xts.xtypeTranslator().genEQV(env, ytype, false);
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

    public static <PI extends X10ProcedureInstance<?>> PI inferAndCheckAndInstantiate(X10Context context, PI me, Type thisType, List<Type> typeActuals, final List<Type> actuals) throws SemanticException {
        final List<Type> typeFormals = me.typeParameters();
        final List<Type> formals = me.formalTypes();

        if (typeActuals.isEmpty() && ! typeFormals.isEmpty()) {
            Type[] Y = inferTypeArguments(me, thisType, actuals, formals, typeFormals, context);
            return inferAndCheckAndInstantiate(context, me, thisType, Arrays.asList(Y), actuals);
        }

        PI me3 = instantiate2(context, me, thisType, typeActuals, actuals, true, new XVar[actuals.size()+1]);
        XVar[] ys = new XVar[actuals.size()+1];
        PI me2 = instantiate2(context, me, thisType, typeActuals, actuals, false, ys);
        checkCall(context, me2, thisType, typeActuals, actuals, ys);
        return me3;
    }
    
    public static <PI extends X10ProcedureInstance<?>> void checkCall(X10Context context, PI me, Type thisType, List<Type> typeActuals, final List<Type> actuals, XVar[] ys) throws SemanticException {
        final X10TypeSystem xts = (X10TypeSystem) me.typeSystem();
        final List<Type> formals = me.formalTypes();
        final List<LocalInstance> formalNames = me.formalNames();
        final List<Type> typeFormals = me.typeParameters();
        
        if (actuals.size() != formals.size()) {
            throw new SemanticException("Call not valid; incorrect number of actual arguments.", me.position());
        }
        
        if (typeActuals.size() != typeFormals.size()) {
            throw new SemanticException("Call not valid; incorrect number of actual type arguments.", me.position());
        }
        
        XVar ythis = ys[0];
        
        if (ythis != null) {
            XConstraint c = X10TypeMixin.xclause(thisType);
            c = (c == null) ? new XConstraint_c() : c.copy();
            
            try {
                c.addSelfBinding(ythis);
            }
            catch (XFailure e) {
                throw new SemanticException(e.getMessage(), me.position());
            }
            
            thisType = X10TypeMixin.xclause(X10TypeMixin.baseType(thisType), c);
        }
        
        TypeConstraint tenv = new TypeConstraint_c();
        
        XConstraint env = new XConstraint_c();
        
        try {
            XConstraint yc = X10TypeMixin.xclause(thisType);
            if (yc != null) {
                XConstraint yc2 = yc.substitute(ythis, yc.self());
                yc2.setThisVar(ythis);
                env.addIn(yc2);
            }
        }
        catch (XFailure e) {
            throw new SemanticException(e.getMessage(), me.position());
        }
        
        
        // Given call e.m[T1,...,Tk](e1,...,en)
        // and method T.m[X1,...,Xk](x1: S1,...,xn: Sn){c}
        // We build the following environment:
        // env = {X1==T1,...,Xk==Tk,x1==e1,...,xn==en}
        // and check
        // {e1.type <: S1, ..., en.type <: Sn, c}
        
        assert typeActuals.size() == typeFormals.size();
        assert actuals.size() == formals.size();
        
        Type[] X = new Type[typeFormals.size()];
        Type[] Y = new Type[typeFormals.size()];
        XRoot[] x = new XRoot[formals.size()];
        XVar[] y = new XVar[formals.size()];
        
        for (int i = 0; i < typeFormals.size(); i++) {
            Type xtype = typeFormals.get(i);
            xtype = xts.expandMacros(xtype);
            Type ytype = typeActuals.get(i);
            ytype = xts.expandMacros(ytype);
            
            tenv.addTerm(new SubtypeConstraint_c(xtype, ytype, true));
            
            X[i] = xtype;
            Y[i] = ytype;
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
            XVar yi = ys[i+1];
            
            XConstraint xc = X10TypeMixin.realX(xtype).copy();
            xi = xts.xtypeTranslator().trans(me.formalNames().get(i), xtype);

            XVar self = X10TypeMixin.selfVar(xc);
            
            try {
                if (self instanceof XRoot) {
                    env.addBinding(xi, self);
                    env.addBinding(xc.self(), self);
                }

                if (yc != null) {
                    XConstraint yc2 = yc.substitute(yi, yc.self());
                    env.addIn(yc2);
                }
//                env.addBinding(xi, yi);
            }
            catch (XFailure f) {
                // environment is inconsistent.
                throw new SemanticException("Call invalid; calling environment is inconsistent.");
            }
            
            x[i] = xi;
            y[i] = yi;
        }
        
        // We'll subst selfVar for THIS.
        XRoot xthis = null; // xts.xtypeTranslator().transThis(thisType);
        
        if (me.def() instanceof X10ProcedureDef)
            xthis = (XRoot) ((X10ProcedureDef) me.def()).thisVar();
        
        if (xthis == null)
            xthis = XTerms.makeLocal(XTerms.makeFreshName("this"));
        
        try {
            XConstraint query = me.guard();
            if (query != null) {
                XConstraint query2 = query.substitute(ythis, xthis);
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
        
        TypeConstraint query = me.typeGuard();
        if (query != null) {
            TypeConstraint query2 = query.subst(ythis, xthis);
            for (int i = 0; i < y.length; i++)
                query2 = query2.subst(y[i], x[i]);
            TypeConstraint query4 = query2;
            
            if (! tenv.entails(query4, context)) {
                throw new SemanticException("Call invalid; calling environment does not entail the method guard.");
            }
        }
        
        if (! tenv.consistent(context)) {
            throw new SemanticException("Call invalid; type environment is inconsistent.");
        }
        
        for (Type t : formals) {
            if (! xts.consistent(t, context)) {
                throw new SemanticException("Parameter type " + t + " of call is inconsistent in calling context.");
            }
        }
        
        X10Context xc = (X10Context) context.pushBlock();
        {
            XConstraint c = xc.currentConstraint();
            c = c == null ? new XConstraint_c() : c.copy();
            try {
                c.addIn(env);
//                c.addIn(xc.constraintProjection(c));
            }
            catch (XFailure e) {
            }
            xc.setCurrentConstraint(c);
//            xc.setCurrentTypeConstraint(tenv);
        }
        
        // After inferring the types, check that the assignment is allowed.
        for (int i = 0; i < formals.size(); i++) {
            Type ytype = actuals.get(i);
            Type xtype = formals.get(i);
            
            if (! xts.isSubtype(ytype, xtype, xc)) {
                throw new SemanticException("Call invalid; actual parameter of type " + ytype + " cannot be assigned to formal parameter type " + xtype + ".");
            }
        }
        
        if (me.guard() != null && ! xts.consistent(me.guard())) {
            throw new SemanticException("Guard " + me.guard() + " cannot be established; inconsistent in calling context.");
        }
        
        if (me.typeGuard() != null && ! xts.consistent(me.typeGuard(), context)) {
            throw new SemanticException("Type guard " + me.typeGuard() + " cannot be established; inconsistent in calling context.");
        }
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

    public static <PI extends X10ProcedureInstance<?>> PI instantiate(X10Context context, PI me, Type thisType, List<Type> typeActuals, final List<Type> actuals) throws SemanticException {
        return instantiate2(context, me, thisType, typeActuals, actuals, true, new XVar[actuals.size()+1]);
    }
    
    public static <PI extends X10ProcedureInstance<?>> PI instantiate2(X10Context context, PI me, Type thisType, List<Type> typeActuals, final List<Type> actuals, boolean eqv, XVar[] ys) throws SemanticException {
        final X10TypeSystem xts = (X10TypeSystem) me.typeSystem();
        final List<Type> formals = new ArrayList<Type>(me.formalTypes());
        final List<LocalInstance> formalNames = me.formalNames();
        final List<Type> typeFormals = me.typeParameters();

        if (actuals.size() != formals.size()) {
            throw new SemanticException("Call not valid; incorrect number of actual arguments.", me.position());
        }

        if (typeActuals.size() != typeFormals.size()) {
            throw new SemanticException("Call not valid; incorrect number of actual type arguments.", me.position());
        }

        XVar ythiseqv = X10TypeMixin.selfVar(thisType);
        
        if (ythiseqv == null) {
            ythiseqv = xts.xtypeTranslator().genEQV(new XConstraint_c(), thisType, eqv);
        }

        ys[0] = ythiseqv;
        
        XConstraint returnEnv = new XConstraint_c();

        try {
            XConstraint yc = X10TypeMixin.xclause(thisType);
            if (yc != null) {
                XConstraint ycSubst = yc.substitute(ythiseqv, yc.self());
                returnEnv.addIn(ycSubst);
            }
        }
        catch (XFailure e) {
            throw new SemanticException(e.getMessage(), me.position());
        }

        ParameterType[] X = new ParameterType[typeFormals.size()];
        Type[] Y = new Type[typeFormals.size()];
        XRoot[] x = new XRoot[formals.size()];
        XVar[] yeqv = new XVar[formals.size()];

        for (int i = 0; i < typeFormals.size(); i++) {
            Type xtype = typeFormals.get(i);
            xtype = xts.expandMacros(xtype);
            
            Type ytype = typeActuals.get(i);
            ytype = xts.expandMacros(ytype);

            // TODO: should enforce this statically
            assert xtype instanceof ParameterType : xtype + " is not a ParameterType, is a " + (xtype != null ? xtype.getClass().getName() : "null");

            X[i] = (ParameterType) xtype;
            Y[i] = ytype;
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
            XVar yieqv;

            yieqv = X10TypeMixin.selfVar(yc);
            
            if (yieqv == null) {
                yieqv = xts.xtypeTranslator().genEQV(new XConstraint_c(), ytype, eqv);
            }

            XConstraint xc = X10TypeMixin.realX(xtype).copy();
            xi = xts.xtypeTranslator().trans(me.formalNames().get(i), xtype);
            
            try {
                XConstraint ycSubst = yc.substitute(yieqv, yc.self());
                returnEnv.addIn(ycSubst);
            }
            catch (XFailure f) {
                // environment is inconsistent.
                throw new SemanticException("Call invalid; calling environment is inconsistent.");
            }

            xtype = Subst.subst(xtype, new XTerm[] { xi }, new XRoot[] { xc.self() });
            formals.set(i, xtype);

            x[i] = xi;
            yeqv[i] = yieqv;
            ys[i+1] = yieqv;
        }

        // We'll subst selfVar for THIS.
        XRoot xthis = null; // xts.xtypeTranslator().transThis(thisType);

        if (me.def() instanceof X10ProcedureDef)
            xthis = (XRoot) ((X10ProcedureDef) me.def()).thisVar();

        if (xthis == null)
            xthis = XTerms.makeLocal(XTerms.makeFreshName("this"));
      
//        me = unifyThis(me, xthis, thisType);

        XRoot[] x2 = new XRoot[x.length+2];
        XTerm[] y2eqv = new XTerm[yeqv.length+2];
        x2[0] = xthis;
        y2eqv[0] = ythiseqv;
        x2[1] = thisVar(xthis, thisType);
        y2eqv[1] = ythiseqv;

        System.arraycopy(x, 0, x2, 2, x.length);
        System.arraycopy(yeqv, 0, y2eqv, 2, yeqv.length);

        List<Type> newFormals = new ArrayList<Type>();

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

    

}
