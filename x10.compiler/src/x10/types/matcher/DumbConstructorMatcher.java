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

import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.types.X10ConstructorInstance;
import polyglot.types.Context;

/** A constructor matcher that only checks name and arity. */
public class DumbConstructorMatcher extends X10ConstructorMatcher {
    public DumbConstructorMatcher(Type container, List<Type> argTypes, Context context) {
        super(container, argTypes, context);
    }

    @Override
    public ConstructorInstance instantiate(ConstructorInstance ci) throws SemanticException {
        if (ci instanceof X10ConstructorInstance) {
            X10ConstructorInstance xci = (X10ConstructorInstance) ci;

            if (xci.formalTypes().size() != argTypes.size())
                return null;

            Type c = container != null ? container : xci.container();
            X10ConstructorInstance newXmi = Matcher.instantiate((Context) context, xci, c, Collections.<Type>emptyList(), argTypes);
            return newXmi;
        }
        return null;
    }
}
