/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import java.util.*;

import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;
import x10.types.MethodInstance;

/**
 * A <code>ClassBody</code> represents the body of a class or interface
 * declaration or the body of an anonymous class.
 */
public abstract class ClassBody_c extends Term_c implements ClassBody
{
    public ClassBody_c(Position pos, List<ClassMember> members) {
        super(pos);
    }

    protected abstract void duplicateConstructorCheck(ContextVisitor tc);

    protected abstract void duplicateMethodCheck(ContextVisitor tc);

    public abstract Node conformanceCheck(ContextVisitor tc);
}
