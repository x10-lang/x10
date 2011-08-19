/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import java.util.List;

import polyglot.types.ConstructorDef;
import polyglot.types.Flags;

/**
 * A <code>ConstructorDecl</code> is an immutable representation of a
 * constructor declaration as part of a class body. 
 */
public interface ConstructorDecl extends ProcedureDecl 
{
    /** The constructor's flags. */
    FlagsNode flags();

    /** Set the constructor's flags. */
    ConstructorDecl flags(FlagsNode flags);
    
    /**
     * The constructor's name.  This should be the short name of the
     * containing class.
     */
    Id name();
    
    /** Set the constructor's name. */
    ConstructorDecl name(Id name);

    /** The constructor's formal parameters.
     * @return A list of {@link polyglot.ast.Formal Formal}.
     */
    List<Formal> formals();

    /** Set the constructor's formal parameters.
     * @param formals A list of {@link polyglot.ast.Formal Formal}.
     */
    ConstructorDecl formals(List<Formal> formals);

    /**
     * The constructor type object.  This field may not be valid until
     * after signature disambiguation.
     */
    ConstructorDef constructorDef();

    /** Set the constructor's type object. */
    ConstructorDecl constructorDef(ConstructorDef ci);
}
