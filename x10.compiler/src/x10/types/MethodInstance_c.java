/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.types.FunctionInstance_c;
import polyglot.types.JavaArrayType;
import polyglot.types.Context;
import polyglot.types.DerefTransform;
import polyglot.types.ErrorRef_c;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;

import polyglot.types.Name;
import polyglot.types.JavaPrimitiveType;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UpcastTransform;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.matcher.Matcher;
import x10.types.MethodInstance;

/**
 * A representation of a MethodInstance.  
 * 
 * @author vj
 *
 */
public class MethodInstance_c extends FunctionInstance_c<MethodDef> implements MethodInstance {
    private static final long serialVersionUID = 3883485772306553465L;

    public MethodInstance_c(TypeSystem ts, Position pos, Position errorPos, Ref<? extends X10MethodDef> def) {
        super(ts, pos, errorPos, def);
    }

    protected Name name;
    protected Flags flags;
    protected ContainerType container;
    
    public MethodInstance container(ContainerType container) {
        if (container == this.container)
            return this;
        MethodInstance_c p = (MethodInstance_c) copy();
        p.container = container;
        return p;
    }

    public ContainerType container() {
        if (this.container == null) {
            return Types.get(def().container());
        }
        return this.container;
    }
    
    public MethodInstance flags(Flags flags) {
        MethodInstance_c p = (MethodInstance_c) copy();
        p.flags = flags;
        return p;
    }
    
    public Flags flags() {
        if (this.flags == null) { 
            return def().flags();
        }
        return this.flags;
    }
    
    public MethodInstance name(Name name) {
        MethodInstance_c p = (MethodInstance_c) copy();
        p.name = name;
        return p;
    }

    public Name name() {
        if (this.name == null) { 
            return def().name();
        }
        return this.name;
    }
    
    @Override
    public boolean moreSpecific(Type container, ProcedureInstance<MethodDef> p, Context context) {
        return Types.moreSpecificImpl(container, this, p, context);
    }

    public static class NoClauseVariant implements Transformation<Type, Type> {
        public Type transform(Type o) {
            if (o instanceof JavaArrayType) {
                JavaArrayType at = (JavaArrayType) o;
                return at.base(Types.ref(transform(at.base())));
            }
            if (o instanceof ConstrainedType) {
                ConstrainedType ct = (ConstrainedType) o;
                return transform(Types.get(ct.baseType()));
            }
            return  o;
        }
    }

    @Override
    public MethodInstance returnType(Type returnType) {
        return (MethodInstance) super.returnType(returnType);
    }
    @Override
    public MethodInstance returnTypeRef(Ref<? extends Type> returnType) {
        if (returnType == this.returnType) 
            return this;
        return (MethodInstance) super.returnTypeRef(returnType);
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

    public MethodInstance body(XTerm body) {
        if (body == this.body) return this;
        MethodInstance_c n = (MethodInstance_c) copy();
        n.body = body;
        return n;
    }

    public Ref <? extends Type> offerType() {
    	return x10Def().offerType();
    }
  
   

    

    public List<Type> typeParameters;

    public List<Type> typeParameters() {
        if (this.typeParameters == null) {
            return new TransformingList<ParameterType, Type>(x10Def().typeParameters(), new UpcastTransform<Type, ParameterType>());
        }

        return typeParameters;
    }

    public MethodInstance typeParameters(List<Type> typeParameters) {
        if (typeParameters == this.typeParameters) return this;
        MethodInstance_c n = (MethodInstance_c) copy();
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

    public MethodInstance formalNames(List<LocalInstance> formalNames) {
        if (formalNames == this.formalNames) return this;
        MethodInstance_c n = (MethodInstance_c) copy();
        n.formalNames = formalNames;
        return n;
    }

    public MethodInstance formalTypes(List<Type> formalTypes) {
        if (formalTypes == this.formalTypes) 
            return this;
        return (MethodInstance) super.formalTypes(formalTypes);
    }

    private SemanticException error;

    public SemanticException error() {
        return error;
    }

    public MethodInstance error(SemanticException e) {
        if (e == this.error) return this;
        MethodInstance_c n = (MethodInstance_c) copy();
        n.error = e;
        return n;
    }

    public static void buildSubst(MethodInstance mi, List<XVar> ys, List<XVar> xs, XVar thisVar) {
    	XVar mdThisVar = mi.x10Def().thisVar();
        if (mdThisVar != null && mdThisVar != thisVar && ! xs.contains(mdThisVar)) {
            ys.add(thisVar);
            xs.add(mdThisVar);
        }

        buildSubst(mi.container(), ys, xs, thisVar);
    }

    public static void buildSubst(Type t, List<XVar> ys, List<XVar> xs, XVar thisVar) {
        Type container = Types.baseType(t);
        if (container instanceof X10ClassType) {
            X10ClassDef cd = ((X10ClassType) container).x10Def();
            XVar cdThisVar = cd.thisVar();
            if (cdThisVar != null && cdThisVar != thisVar && ! xs.contains(cdThisVar) ) {
                ys.add(thisVar);
                xs.add(cdThisVar);
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

    public boolean isPropertyGetter() {
        ContainerType container = this.container();
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
   /* public boolean isSafe() {
        StructType container = this.container();
        assert container instanceof X10ParsedClassType : container + " for " + this;
        boolean result = ((X10ParsedClassType) container).isSafe();
        if (result) return true;
        X10Flags f = X10Flags.toX10Flags(flags());
        result = f.isSafe();
        return result;
    }*/


    public X10MethodDef x10Def() {
        return (X10MethodDef) def();
    }

    public String containerString() {
        Type container = container();
        container = Types.baseType(container);
        if (container instanceof FunctionType) {
            return "(" + container.toString() + ")";
        }
        return container.fullName().toString();
    }

    public String toString() {
        String s = designator() + " " + flags().prettyPrint() + containerString() + "." + signature();


        if (! throwTypes().isEmpty()) {
            s += " throws " + CollectionUtil.listToString(throwTypes());
        }


        if (body != null)
            s += " = " + body;

        return s;
    }

    public String signature() {
        StringBuilder sb = new StringBuilder();
        Name name = this.name();
        sb.append(name != null ? name : def().name());
        List<String> params = new ArrayList<String>();
        List<Type> typeParameters = this.typeParameters();
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
        List<Type> formalTypes = this.formalTypes();
        if (formalTypes != null) {
            List<LocalInstance> formalNames = this.formalNames();
            for (int i = 0; i < formalTypes.size(); i++) {
                String s = "";
                String t = formalTypes.get(i).toString();
                if (formalNames != null && i < formalNames.size()) {
                    X10LocalInstance a = (X10LocalInstance) formalNames.get(i);
                    if (a != null && ! a.x10Def().isUnnamed())
                        s = a.name() + ":" + t;
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
        CConstraint guard = this.guard();
        if (guard != null) {
            if (!guard.atoms().isEmpty()) sb.append(guard);
        }
        else if (x10Def().guard() != null) {
            guard = x10Def().guard().get();
            if (!guard.atoms().isEmpty()) sb.append(guard);
        }
        TypeConstraint typeGuard = this.typeGuard();
        if (typeGuard != null)
            sb.append(typeGuard);
        else if (x10Def().typeGuard() != null)
            sb.append(x10Def().typeGuard());
        Ref<? extends Type> returnType = this.returnTypeRef();
        if (returnType != null && returnType.known()) {
            sb.append(":");
            sb.append(returnType);
        }
        return sb.toString();
    }
    
    public MethodInstance throwTypes(List<Type> throwTypes) {
        return (MethodInstance) super.throwTypes(throwTypes);
    }
    
    /** Returns true iff <this> is the same method as <m> */
    public final boolean isSameMethod(MethodInstance m, Context context) {
    return ts.isSameMethod((MethodInstance) this, m, context);
    }

    public final List<MethodInstance> overrides(Context context) {
    return ts.overrides((MethodInstance) this, context);
    }
    
  

    Type rightType;

    /**
     * Iff this is a zero-ary property method invocation, add the clause
     * self=term to the return type, where term is body if the method def has 
     * a body, else, term is this.m().
     * 
     * Thus the resulting type may have occurrences of this.
     */
    public Type rightType() {
        TypeSystem xts = (TypeSystem) ts;

        if (rightType == null) {
            Type t = returnType();

            // If a property method, replace T with T{self==this}.
            Flags flags = flags();

            if (flags.isProperty() && formalTypes.size() == 0) {
                if (xts.isUnknown(t)) {
                    rightType = t;
                }
                else {
                    CConstraint rc = Types.xclause(t);
                    if (rc == null)
                        rc = ConstraintManager.getConstraintSystem().makeCConstraint();

                    XTerm receiver;

                    if (flags.isStatic()) {
                        receiver = xts.xtypeTranslator().translate(container());
                    }
                    else {
                        receiver = x10Def().thisVar();
                        assert receiver != null;
                    }

                    // ### pass in the type rather than letting XField call fi.type();
                    // otherwise, we'll get called recursively.
                    XTerm self = body();

                    CConstraint c = rc.copy();

                    // TODO: handle non-vars, like rail().body
                    if (self == null || ! (self instanceof XVar)) {
                        self = xts.xtypeTranslator().translate(receiver, this);
                    }

                    if (self != null) {
                        c.addSelfBinding(self);
                    }
                    if (! flags.isStatic()) {
                        c.setThisVar((XVar) receiver);
                    }
                    rightType = Types.xclause(Types.baseType(t), c);
                }
            }
            else {
                rightType = t;
            }

            assert rightType != null;
        }

        return rightType;
    }

    static private String toString( XVar[] ys) {
    	String s = "";
    		for (XVar x : ys) s += x.toString() + " ";
    		return s;
    }

    public boolean isValid() {
        return !(def instanceof ErrorRef_c<?>);
    }
    
    /**
     * Leave this method in for historic reasons, to make sure that extensions
     * modify their code correctly.
     */
    public final boolean canOverride(MethodInstance mj, Context context) {
    return ts.canOverride((MethodInstance) this, mj, context);
    }

    // nobody calls this.
    public final void checkOverride(MethodInstance mj, Context context) throws SemanticException {
    ts.checkOverride((MethodInstance) this, mj, context);
    }

    public final List<MethodInstance> implemented(Context context) {
        return ts.implemented((MethodInstance) this, context);
    }
    
    protected MethodInstance origMI;
    public MethodInstance origMI() { return origMI;}
    public void setOrigMI(MethodInstance origMI) {
        this.origMI = origMI;
    }
}
