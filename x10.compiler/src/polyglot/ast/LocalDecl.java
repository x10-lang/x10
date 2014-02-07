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

import polyglot.types.LocalDef;

/** 
 * A local variable declaration statement: a type, a name and an optional
 * initializer.
 */
public interface LocalDecl extends ForInit, VarDecl, VarInit
{
    /** Set the declaration's flags. */
    LocalDecl flags(FlagsNode flags);

    /** Set the declaration's type. */
    LocalDecl type(TypeNode type);

    /** Set the declaration's name. */
    LocalDecl name(Id name);

    /** Get the declaration's initializer expression, or null. */
    Expr init();
    /** Set the declaration's initializer expression. */
    LocalDecl init(Expr init);

    /**
     * Set the type object for the local we are declaring.
     */
    LocalDecl localDef(LocalDef li);
}
