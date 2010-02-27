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

import polyglot.ast.Expr;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c;
import polyglot.util.CollectionUtil;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;

public class X10ConstructorMatcher extends TypeSystem_c.ConstructorMatcher {
    protected List<Type> typeArgs;

    protected List<Expr> args;
    
    public X10ConstructorMatcher(Type container, List<Type> argTypes, Context context) {
        this(container, Collections.EMPTY_LIST, argTypes, context);
    }

    public X10ConstructorMatcher(Type container, List<Type> typeArgs, List<Type> argTypes, Context context) {
    	this(container, typeArgs, null, argTypes, context);
    }
    
    public X10ConstructorMatcher(Type container, List<Type> typeArgs, List<Expr> args, 
    		List<Type> argTypes, Context context) {
        super(container, argTypes, context);
        this.typeArgs = typeArgs;
        this.args = args;
    }


    public List<Type> arguments() {
        return argTypes;
    }

    @Override
    public String argumentString() {
        return (typeArgs.isEmpty() ? "" : "[" + CollectionUtil.listToString(typeArgs) + "]") + "(" + CollectionUtil.listToString(argTypes) + ")";
    }

    @Override
    public ConstructorInstance instantiate(ConstructorInstance ci) throws SemanticException {
        if (ci.formalTypes().size() != argTypes.size())
            return null;
        if (ci instanceof X10ConstructorInstance) {
            X10ConstructorInstance xmi = (X10ConstructorInstance) ci;
            Type c = container != null ? container : xmi.container();
            if (typeArgs.isEmpty() || typeArgs.size() == xmi.typeParameters().size())
                return Matcher.inferAndCheckAndInstantiate((X10Context) context(), 
                		xmi, c, typeArgs, argTypes, ci.position());
        }
        return null;
    }
}