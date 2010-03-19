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
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance_c;
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
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;
import x10.types.constraints.TypeConstraint;
import x10.types.matcher.Matcher;
import x10.effects.constraints.Effect;

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
        return X10TypeMixin.moreSpecificImpl(this, p, context);
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
    protected CConstraint guard;
    public CConstraint guard() {
        if (guard == null)
            return Types.get(x10Def().guard());
        return guard;
    }
    public X10MethodInstance guard(CConstraint s) { 
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
                    CConstraint rc = X10TypeMixin.xclause(t);
                    if (rc == null)
                        rc = new CConstraint_c();

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
    protected Ref<? extends Effect> effect;
    public x10.effects.constraints.Effect effect() {
        if (effect == null) {
                return ((X10MethodDef) def()).effect().get();
        }
        return Types.get(effect);
    }


}
