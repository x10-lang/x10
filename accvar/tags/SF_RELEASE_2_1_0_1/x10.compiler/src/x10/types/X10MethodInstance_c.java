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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.types.ArrayType;
import polyglot.types.Context;
import polyglot.types.DerefTransform;
import polyglot.types.ErrorRef_c;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MemberDef;
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
import polyglot.types.UpcastTransform;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.matcher.Matcher;

/**
 * A representation of a MethodInstance.  
 * 
 * @author vj
 *
 */
public class X10MethodInstance_c extends MethodInstance_c implements X10MethodInstance {
    private static final long serialVersionUID = -2510860168293880632L;

    public X10MethodInstance_c(TypeSystem ts, Position pos, Ref<? extends X10MethodDef> def) {
        super(ts, pos, def);
    }

    @Override
    public boolean moreSpecific(Type container, ProcedureInstance<MethodDef> p, Context context) {
        return X10TypeMixin.moreSpecificImpl(container, this, p, context);
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
            return  o;
        }
    }

    public Object copy() { 
        return super.copy();
    }

    @Override
    public X10MethodInstance returnType(Type returnType) {
        return (X10MethodInstance) super.returnType(returnType);
    }
    @Override
    public X10MethodInstance returnTypeRef(Ref<? extends Type> returnType) {
        if (returnType == this.returnType) return this;
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
        if (body == this.body) return this;
        X10MethodInstance_c n = (X10MethodInstance_c) copy();
        n.body = body;
        return n;
    }

    public Ref <? extends Type> offerType() {
    	return x10Def().offerType();
    }
  
    @Override
    public X10MethodInstance container(StructType container) {
        if (container == this.container) return this;
        return (X10MethodInstance) super.container(container);
    }

    /** Constraint on formal parameters. */
    protected CConstraint guard;
    public CConstraint guard() {
        if (guard == null)
            return Types.get(x10Def().guard());
        return guard;
    }
    public X10MethodInstance guard(CConstraint s) {
        if (s == this.guard) return this;
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
        if (s == this.typeGuard) return this;
        X10MethodInstance_c n = (X10MethodInstance_c) copy();
        n.typeGuard = s; 
        return n;
    }

    public List<Type> typeParameters;

    public List<Type> typeParameters() {
        if (this.typeParameters == null) {
            return new TransformingList<ParameterType, Type>(x10Def().typeParameters(), new UpcastTransform<Type, ParameterType>());
        }

        return typeParameters;
    }

    public X10MethodInstance typeParameters(List<Type> typeParameters) {
        if (typeParameters == this.typeParameters) return this;
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
        if (formalNames == this.formalNames) return this;
        X10MethodInstance_c n = (X10MethodInstance_c) copy();
        n.formalNames = formalNames;
        return n;
    }

    public X10MethodInstance formalTypes(List<Type> formalTypes) {
        if (formalTypes == this.formalTypes) return this;
        return (X10MethodInstance) super.formalTypes(formalTypes);
    }

    private SemanticException error;

    public SemanticException error() {
        return error;
    }

    public X10MethodInstance error(SemanticException e) {
        if (e == this.error) return this;
        X10MethodInstance_c n = (X10MethodInstance_c) copy();
        n.error = e;
        return n;
    }

    public static void buildSubst(X10MethodInstance mi, List<XVar> ys, List<XVar> xs, XVar thisVar) {
    	XVar mdThisVar = mi.x10Def().thisVar();
        if (mdThisVar != null && mdThisVar != thisVar && ! xs.contains(mdThisVar)) {
            ys.add(thisVar);
            xs.add(mdThisVar);
        }

        buildSubst(mi.container(), ys, xs, thisVar);
    }

    public static void buildSubst(Type t, List<XVar> ys, List<XVar> xs, XVar thisVar) {
        Type container = X10TypeMixin.baseType(t);
        if (container instanceof X10ClassType) {
            X10ClassDef cd = ((X10ClassType) container).x10Def();
            XVar cdThisVar = cd.thisVar();
            if (cdThisVar != null && cdThisVar != thisVar && ! xs.contains(cdThisVar) ) {
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
        CConstraint guard = this.guard();
        if (guard != null)
            sb.append(guard);
        else if (x10Def().guard() != null)
            sb.append(x10Def().guard());
        TypeConstraint typeGuard = this.typeGuard();
        if (typeGuard != null)
            sb.append(typeGuard);
        else if (x10Def().typeGuard() != null)
            sb.append(x10Def().typeGuard());
        Ref<? extends Type> returnType = this.returnTypeRef();
        if (returnType != null && returnType.known()) {
            sb.append(": ");
            sb.append(returnType);
        }
        return sb.toString();
    }


  

    Type rightType;

    public Type rightType() {
        X10TypeSystem xts = (X10TypeSystem) ts;

        if (rightType == null) {
            Type t = returnType();

            // If a property method, replace T with T{self==this}.
            X10Flags flags = X10Flags.toX10Flags(flags());

            if (flags.isProperty() && formalTypes.size() == 0) {
                if (xts.isUnknown(t)) {
                    rightType = t;
                }
                else {
                    CConstraint rc = X10TypeMixin.xclause(t);
                    if (rc == null)
                        rc = new CConstraint();

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

                        CConstraint c = rc.copy();

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

    public boolean isSafe() {
    	return X10Flags.toX10Flags(flags()).isSafe();
    }
    static private String toString( XVar[] ys) {
    	String s = "";
    		for (XVar x : ys) s += x.toString() + " ";
    		return s;
    }

    public boolean isValid() {
        return !(def instanceof ErrorRef_c<?>);
    }
}
