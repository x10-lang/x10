/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;


/**
 * A <code>FieldInstance</code> contains type information for a field.
 */
public interface FieldDef extends VarDef, MemberDef
{
    FieldInstance asInstance();
    
    InitializerDef initializer();
    void setInitializer(InitializerDef initializer);
}
