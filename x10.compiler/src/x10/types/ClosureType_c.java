/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
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
import polyglot.types.CodeInstance;
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
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.Types;
import polyglot.types.TypeSystem;
import polyglot.types.ClassDef.Kind;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import x10.types.constraints.CConstraint;

/**
 * A representation of the type of a closure.
 * Treated as a ClassType implementing a FunctionType, with the signature
 * for the function type retrieved from the sole method (the apply method)
 * defined on the class type.
 */
public class ClosureType_c extends FunctionType_c implements ClosureType {
    private static final long serialVersionUID = 331189963001388621L;

    public ClosureType_c(final TypeSystem ts, Position pos, Position errorPosition, X10ClassDef def, CodeInstance<?> methodContainer) {
        super(ts, pos, errorPosition, def);
        this.methodContainer = methodContainer;
    }

    protected CodeInstance<?> methodContainer;

    public CodeInstance<?> methodContainer() {
        return methodContainer;
    }

    public ClosureType methodContainer(CodeInstance<?> methodContainer) {
        ClosureType_c ct = (ClosureType_c) copy();
        ct.methodContainer = methodContainer;
        return ct;
    }

    protected ClosureInstance ci;

    public ClosureInstance closureInstance() {
        return ci;
    }

    public ClosureType closureInstance(ClosureInstance ci) {
        ClosureType_c ct = (ClosureType_c) copy();
        ct.ci = ci;
        return ct;
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

    public FunctionType functionInterface() {
        for (Type itype : interfaces()) {
            return (FunctionType) itype;
        }
        throw new InternalCompilerError("Found a closure type "+typeToString()+" at "+position()+" that does not implement a function interface");
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
                sb.append(", ");
            sb.append(f.name());
            sb.append(':');
            sb.append(f.type());
        }
        return "(" + sb.toString() + ")" + guardToString(guard()) + "=> " + mi.returnType();
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
        if (o instanceof ClosureType_c) {
            ClosureType_c t = (ClosureType_c) o;
            if (! flags().equals(t.flags()))
                return false;
            if (def != t.def) {
                if (def == null || t.def == null)
                    return false;
                else if (!Types.get(def).equals(Types.get(t.def)))
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
