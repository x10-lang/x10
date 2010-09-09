/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * Copyright (c) 2007 IBM Corporation
 * 
 */

package polyglot.types;


/**
 * A <code>FunctionInstance</code> represents the type information for a
 * function.
 */
public interface FunctionDef extends ProcedureDef
{
    /**
     * The functions's return type.
     */
    Ref<? extends Type> returnType();
    
    /**
     * Destructively set the functions's return type.
     * @param type
     */
    void setReturnType(Ref<? extends Type> type);
}
