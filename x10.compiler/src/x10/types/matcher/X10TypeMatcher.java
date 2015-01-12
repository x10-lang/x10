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

package x10.types.matcher;

import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c.TypeMatcher;
import x10.types.MacroType;

public class X10TypeMatcher extends TypeMatcher {
    public X10TypeMatcher(Name name) {
        super(name);
    }

    @Override
    public Type instantiate(Type t) throws SemanticException {
        Type n = super.instantiate(t);
        // Also check that the name is simple.
        if (n instanceof MacroType) {
            MacroType mt = (MacroType) n;
            if (mt.formalTypes().size() != 0)
                return null;
            if (mt.typeParameters().size() != 0)
                return null;
        }
        return n;
    }
}
