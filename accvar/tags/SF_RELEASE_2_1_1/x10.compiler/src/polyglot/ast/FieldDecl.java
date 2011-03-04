/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
