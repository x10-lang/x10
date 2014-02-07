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

import polyglot.types.Flags;
import polyglot.types.MethodDef;

/**
 * A method declaration.
 */
public interface MethodDecl extends ProcedureDecl 
{
    /** The method's flags. */
    FlagsNode flags();

    /** Set the method's flags. */
    MethodDecl flags(FlagsNode flags);

    /** The method's return type.  */
    TypeNode returnType();

    /** Set the method's return type.  */
    MethodDecl returnType(TypeNode returnType);

    /** The method's name. */
    Id name();
    
    /** Set the method's name. */
    MethodDecl name(Id name);

    /** The method's formal parameters.
     * @return A list of {@link polyglot.ast.Formal Formal}.
     */
    List<Formal> formals();

    /** Set the method's formal parameters.
     * @param formals A list of {@link polyglot.ast.Formal Formal}.
     */
    MethodDecl formals(List<Formal> formals);

    /**
     * The method type object.  This field may not be valid until
     * after signature disambiguation.
     */
    MethodDef methodDef();

    /** Set the method's type object. */
    MethodDecl methodDef(MethodDef mi);
}
