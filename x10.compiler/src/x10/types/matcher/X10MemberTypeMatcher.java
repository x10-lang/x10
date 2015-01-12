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

import polyglot.types.Context;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c.MemberTypeMatcher;
import polyglot.types.Types;
import x10.types.MacroType;

public class X10MemberTypeMatcher extends MemberTypeMatcher {
    public X10MemberTypeMatcher(Type container, Name name, Context context) {
        super(container, name, context);
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
        // Need to replace the outer type of n with container.
        // container may have clauses that need to be part of n.
        // e.g. A{self.i==3}.Inner will return A.Inner{A.self.i==3}.
        n = Types.addInOuterClauses(n, container);
        return n;
    }
}
