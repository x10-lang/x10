/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
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
