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

import java.util.List;

import polyglot.types.ProcedureDef;

/**
 * A procedure declaration.  A procedure is the supertype of methods and
 * constructors.
 */
public interface ProcedureDecl extends CodeDecl 
{
    /** The procedure's name. */
    Id name();
    
    /** The procedure's flags. */
    FlagsNode flags();

    /**
     * The procedure's formal parameters.
     * @return A list of {@link polyglot.ast.Formal Formal}.
     */
    List<Formal> formals();

    /**
     * The procedure type object.  This field may not be valid until
     * after signature disambiguation.
     */
    ProcedureDef procedureInstance();

    /** The procedure's return type. */
    TypeNode returnType();
}
