/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * Copyright (c) 2007 IBM Corporation
 * 
 */

package polyglot.types;

import java.util.List;

/**
 * A <code>ProcedureInstance</code> contains the type information for a Java
 * procedure (either a method or a constructor).
 */
public interface ProcedureDef extends CodeDef
{
    /**
     * List of formal parameter types.
     * @return A list of <code>Type</code>.
     * @see polyglot.types.Type
     */
    List<Ref<? extends Type>> formalTypes();
    void setFormalTypes(List<Ref<? extends Type>> l);
    
    /**
     * List of declared exception types thrown.
     * @return A list of <code>Type</code>.
     * @see polyglot.types.Type
     */
    List<Ref<? extends Type>> throwTypes();
    void setThrowTypes(List<Ref<? extends Type>> l);
    
    /**
     * Returns a String representing the signature of the procedure.
     * This includes just the name of the method (or name of the class, if
     * it is a constructor), and the argument types.
     */
    String signature();

    /**
     * String describing the kind of procedure, (e.g., "method" or "constructor").
     */
    String designator();
}
