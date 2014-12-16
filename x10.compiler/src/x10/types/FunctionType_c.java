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
import java.util.List;

import polyglot.ast.Expr;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ClassType_c;
import polyglot.types.ConstructorInstance;
import polyglot.types.DerefTransform;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalInstance;
import polyglot.types.Matcher;
import polyglot.types.MethodAsTypeTransform;
import polyglot.types.MethodDef;
import polyglot.types.Package;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.Name;
import polyglot.types.ContainerType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.Types;
import polyglot.types.TypeSystem;
import polyglot.types.ClassDef.Kind;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;

import x10.types.matcher.Subst;

/**
 * A representation of the type of a function interface.
 * Treated as a ClassType representing an interface, with the signature for the
 * function type retrieved from the sole method (the apply method) defined on the
 * class type.
 */
public class FunctionType_c extends X10ParsedClassType_c implements FunctionType {
    private static final long serialVersionUID = 2768150875334536668L;

    public FunctionType_c(final TypeSystem ts, Position pos, Position errorPosition, final X10ClassDef def) {
        super(ts, pos, errorPosition, Types.ref(def));
    }

    @Override
    public X10ParsedClassType typeArguments(List<Type> typeArgs) {
        return super.typeArguments(typeArgs);
    }

    public MethodInstance applyMethod() {
        try {
            return (MethodInstance) methods().get(0);
        } catch (Exception z) {
            return null;
        }
    }

    public Type returnType() {
        return applyMethod().returnType();
    }

    public CConstraint guard() {
        return applyMethod().guard();
    }

    public List<Type> typeParameters() {
        return applyMethod().typeParameters();
    }

    public List<LocalInstance> formalNames() {
        return applyMethod().formalNames();
    }

    public List<Type> argumentTypes() {
        return applyMethod().formalTypes();
    }

    protected static String guardToString(CConstraint guard) {
        if (guard == null || guard.constraints().size() == 0) return "";
        return guard.toString();
    }

    @Override
    public String typeToString() {
        MethodInstance mi = applyMethod();
        if (mi==null) // this could happen if the method is installed before the type is properly formed, e.g. in -report types=2 execution.
            return "???"; 
        StringBuilder sb = new StringBuilder();
        List<LocalInstance> formals = mi.formalNames();
        for (int i=0; i < formals.size(); ++i) {
            LocalInstance f = formals.get(i);
            if (sb.length() > 0)
                sb.append(",");
            sb.append(f.type());
        }
        return "(" + sb.toString() + ")" + guardToString(guard()) + "=>" + mi.returnType();
    }

    @Override
    public int hashCode() {
        return def.get().hashCode();
    }

    @Override
    public boolean equalsImpl(TypeObject o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o instanceof FunctionType_c) {
            FunctionType_c t = (FunctionType_c) o;
            if (!flags().equals(t.flags()))
                return false;
            List<Type> Tl = this.argumentTypes();
            Type T = this.returnType();
            CConstraint h = this.guard();
            List<Type> Sl = t.argumentTypes();
            Type S = t.returnType();
            CConstraint g = t.guard();
            if (Tl.size() != Sl.size()) {
                return false;
            }
            XVar[] ys = Types.toVarArray(Types.toLocalDefList(this.formalNames()));
            XVar[] xs = Types.toVarArray(Types.toLocalDefList(t.formalNames()));
            try {
                T = Subst.subst(T, ys, xs, new Type[]{}, new ParameterType[]{});
                S = Subst.subst(S, ys, xs, new Type[]{}, new ParameterType[]{});
            } catch (SemanticException e) {
                throw new InternalCompilerError("Unexpected exception comparing function types", e);
            }
            if (!ts.typeEquals(T, S, ts.emptyContext())) {
                return false;
            }
            for (int i = 0; i < Sl.size(); i++) {
                Type Si = Sl.get(i);
                Type Ti = Tl.get(i);
                try {
                    Ti = Subst.subst(Ti, ys, xs, new Type[]{}, new ParameterType[]{});
                    Si = Subst.subst(Si, ys, xs, new Type[]{}, new ParameterType[]{});
                } catch (SemanticException e) {
                    throw new InternalCompilerError("Unexpected exception comparing function types", e);
                }
                if (!ts.typeEquals(Ti, Si, ts.emptyContext()))
                    return false;
            }
            if (g != null) {
                try {
                    g = g.substitute(ys, xs);
                } catch (XFailure e) {
                    throw new InternalCompilerError("Unexpected exception comparing function types", this.position(), e);
                }
            }
            if (!ts.env(ts.emptyContext()).entails(h, g) || !ts.env(ts.emptyContext()).entails(g, h)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void print(CodeWriter w) {
        w.write(toString());
    }
}
