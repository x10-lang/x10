/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * Copyright (c) 2007 IBM Corporation
 * 
 */

package polyglot.types;


/**
 * A <code>ConstructorInstance</code> contains type information for a
 * constructor.
 */
public interface ConstructorDef extends ProcedureDef, MemberDef, Def
{
    ConstructorInstance asInstance();
}
