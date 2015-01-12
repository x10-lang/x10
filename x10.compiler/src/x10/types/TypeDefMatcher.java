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

import java.util.List;

import polyglot.types.Context;
import polyglot.types.Matcher;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import x10.errors.Warnings;
import x10.ast.X10ClassBody_c;

public class TypeDefMatcher extends TypeSystem_c.NameMatcher<Type> {
    Type container;
    List<Type> typeArgs;
    List<Type> argTypes;
    Context context;

    public TypeDefMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context) {
        super(name);
        this.container = container;
        this.typeArgs = typeArgs;
        this.argTypes = argTypes;
        this.context = context;
    }

    public String argumentString() {
        return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]")
                + (argTypes.isEmpty() ? "" : "(" + CollectionUtil.listToString(argTypes) + ")");
    }

    public String signature() {
        if (container != null)
            return container.fullName() + "." + name + argumentString();
        else
            return name + argumentString();
    }

    private boolean hasConstraintsOrParameterTypes() {
        for (Type ta : typeArgs) {
            if (hasConstraintsOrParameterTypes(ta)) return true;
        }
        for (Type at : argTypes) {
            if (hasConstraintsOrParameterTypes(at)) return true;
        }
        return false;
    }

    private boolean hasConstraintsOrParameterTypes(Type t) {
        if (Types.isConstrained(t)) return true;
        if (t instanceof ParameterType) return true;
        if (t.isClass()) {
            List<Type> tas = ((X10ParsedClassType) t.toClass()).typeArguments();
            if (tas != null) {
                for (Type ta : tas) {
                    if (hasConstraintsOrParameterTypes(ta)) return true;
                }
            }
        }
        return false;
    }

    public Object key() {
        if (hasConstraintsOrParameterTypes()) return null;
        QName containerName = container != null ? container.fullName() : null;
        return "type " + QName.make(containerName, name) + argumentString();
    }

    public String toString() {
        return "type " + signature();
    }

    public MacroType instantiateAccess(MacroType mi) throws SemanticException {
        Type c = container != null ? container : mi.container();
        // no implicit coercions!
        MacroType result = x10.types.matcher.Matcher.inferAndCheckAndInstantiate((Context) context,
                mi, c, typeArgs, argTypes, mi.position());
        return result;
    }
    public MacroType instantiate(Type n) throws SemanticException {
        if (n instanceof MacroType) {
            MacroType mi = (MacroType) n;
            if (!mi.name().equals(name))
                return null;
            if (mi.formalTypes().size() != argTypes.size())
                return null;
            if (typeArgs.isEmpty() || typeArgs.size() == mi.typeParameters().size())
                return instantiateAccess(mi);
        }
        return null;
    }
}
