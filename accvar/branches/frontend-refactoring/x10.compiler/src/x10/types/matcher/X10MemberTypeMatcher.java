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

import x10.types.Context;
import x10.types.MacroType;
import x10.types.Name;
import x10.types.Named;
import x10.types.SemanticException;
import x10.types.Type;
import x10.types.TypeSystem_c.MemberTypeMatcher;

public class X10MemberTypeMatcher extends MemberTypeMatcher {
    public X10MemberTypeMatcher(Type container, Name name, Context context) {
        super(container, name, context);
    }

    @Override
    public Named instantiate(Named t) throws SemanticException {
        Named n = super.instantiate(t);
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