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

import java.util.List;

import polyglot.types.Context;
import polyglot.types.Matcher;
import polyglot.types.Name;
import polyglot.types.Named;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CollectionUtil;

class TypeDefMatcher implements Matcher<Named> {
    Type container;
    Name name;
    List<Type> typeArgs;
    List<Type> argTypes;
    Context context;

    public TypeDefMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context) {
        this.container = container;
        this.name = name;
        this.typeArgs = typeArgs;
        this.argTypes = argTypes;
        this.context = context;
    }

    public Name name() {
        return name;
    }

    public String argumentString() {
        return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]")
                + (argTypes.isEmpty() ? "" : "(" + CollectionUtil.listToString(argTypes) + ")");
    }

    public String signature() {
        return name + argumentString();
    }

    public String toString() {
        return signature();
    }

    public MacroType instantiate(Named n) throws SemanticException {
        if (n instanceof MacroType) {
            MacroType mi = (MacroType) n;
            if (!mi.name().equals(name))
                return null;
            if (mi.formalTypes().size() != argTypes.size())
                return null;
            if (mi instanceof MacroType) {
                MacroType xmi = (MacroType) mi;
                Type c = container != null ? container : xmi.container();
                if (typeArgs.isEmpty() || typeArgs.size() == xmi.typeParameters().size()) {
                    // no implicit coercions!
                    MacroType result = x10.types.matcher.Matcher.inferAndCheckAndInstantiate((X10Context) context, 
                    		xmi, c, typeArgs, argTypes, n.position());
                    return result;
                }
            }
        }
        return null;
    }

    public Object key() {
        return null;
    }
}