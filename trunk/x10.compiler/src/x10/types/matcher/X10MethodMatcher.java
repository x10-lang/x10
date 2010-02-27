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

package x10.types.matcher;

import java.util.Collections;
import java.util.List;

import polyglot.types.Context;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c;
import polyglot.util.CollectionUtil;
import x10.types.X10Context;
import x10.types.X10MethodInstance;

public class X10MethodMatcher extends TypeSystem_c.MethodMatcher {
    protected List<Type> typeArgs;

    public X10MethodMatcher(Type container, Name name, List<Type> argTypes, Context context) {
        this(container, name, Collections.EMPTY_LIST, argTypes, context);
    }

    public X10MethodMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context) {
        super(container, name, argTypes, context);
        this.typeArgs = typeArgs;
    }

    public List<Type> arguments() {
        return argTypes;
    }

    @Override
    public String argumentString() {
        return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]") + "(" + CollectionUtil.listToString(argTypes) + ")";
    }

    @Override
    public MethodInstance instantiate(MethodInstance mi) throws SemanticException {
        if (!mi.name().equals(name))
            return null;
        if (mi.formalTypes().size() != argTypes.size())
            return null;
        if (mi instanceof X10MethodInstance) {
            X10MethodInstance xmi = (X10MethodInstance) mi;
            Type c = container != null ? container : xmi.container();
            if (typeArgs.isEmpty() || typeArgs.size() == xmi.typeParameters().size())
                return Matcher.inferAndCheckAndInstantiate((X10Context) context, 
                		xmi, c, typeArgs, argTypes, mi.position());
        }
        return null;
    }
}