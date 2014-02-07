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

import polyglot.types.*;

/**
 * A <code>FieldDecl</code> is an immutable representation of the declaration
 * of a field of a class.
 */
public interface FieldDecl extends ClassMember, VarInit, CodeNode
{
    /** Get the type object for the declaration's type. */
    Type declType();

    /** Get the declaration's flags. */
    FlagsNode flags();

    /** Set the declaration's flags. */
    FieldDecl flags(FlagsNode flags);

    /** Get the declaration's type. */
    TypeNode type();
    /** Set the declaration's type. */
    FieldDecl type(TypeNode type);
    
    /** Get the declaration's name. */
    Id name();
    /** Set the declaration's name. */
    FieldDecl name(Id name);

    /** Get the declaration's initializer, or null. */
    Expr init();
    /** Set the declaration's initializer. */
    FieldDecl init(Expr init);

    /**
     * Get the type object for the field we are declaring.  This field may
     * not be valid until after signature disambiguation.
     */
    FieldDef fieldDef();

    /** Set the type object for the field we are declaring. */
    FieldDecl fieldDef(FieldDef fi);

    /**
     * Get the type object for the initializer expression, or null.
     * We evaluate the initializer expression as if it were in an
     * initializer block (e.g., <code>{ }</code> or </code>static { }<code>).
     */ 
    InitializerDef initializerDef();

    /** Set the type object for the initializer expression. */
    FieldDecl initializerDef(InitializerDef fi);
}
