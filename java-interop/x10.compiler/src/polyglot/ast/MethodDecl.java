/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
