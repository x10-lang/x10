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

import java.util.List;

import x10.types.Context;
import x10.types.MethodInstance;
import x10.types.Name;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.Context;
import x10.types.X10MethodInstance;

/** A method matcher that only checks name and arity. */
public class DumbMethodMatcher extends X10MethodMatcher {
    public DumbMethodMatcher(Type container, Name name, List<Type> typeArgs, List<Type> argTypes, Context context) {
        super(container, name, typeArgs, argTypes, context);
    }

    @Override
    public MethodInstance instantiate(MethodInstance mi) throws SemanticException {
        if (mi instanceof X10MethodInstance) {
            X10MethodInstance xmi = (X10MethodInstance) mi;

            if (!xmi.name().equals(name))
                return null;
            if (xmi.formalTypes().size() != argTypes.size())
                return null;

            List<Type> typeArgs = this.typeArgs;
            // don't infer type arguments when doing conversions
            // if (typeArgs.size() != 0 && typeArgs.size() !=
            // xmi.typeParameters().size())
            // return null;
            if (typeArgs.size() != xmi.typeParameters().size())
                return null;

            Type c = container != null ? container : xmi.container();
            // if (typeArgs.isEmpty() && ! xmi.typeParameters().isEmpty()) {
            // Type[] Y = X10MethodInstance_c.inferTypeArguments(xmi, c,
            // argTypes, xmi.formalTypes(), xmi.typeParameters(),
            // (Context) context);
            // typeArgs = Arrays.asList(Y);
            // }
            if (typeArgs.size() == xmi.typeParameters().size()) {
                X10MethodInstance newXmi = Matcher.instantiate((Context) context, xmi, c, typeArgs, argTypes);
                return newXmi;
            }
        }
        return null;
    }
}