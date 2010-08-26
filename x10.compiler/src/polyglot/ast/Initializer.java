/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import polyglot.types.Flags;
import polyglot.types.InitializerDef;

/**
 * An <code>Initializer</code> is an immutable representation of an
 * initializer block in a Java class (which appears outside of any
 * method).  Such a block is executed before the code for any of the
 * constructors.  Such a block can optionally be static, in which case
 * it is executed when the class is loaded.  
 */
public interface Initializer extends CodeDecl 
{
    /** Get the initializer's flags. */
    FlagsNode flags();
    /** Set the initializer's flags. */
    Initializer flags(FlagsNode flags);

    /**
     * Get the initializer's type object.  This field may not be valid until
     * after signature disambiguation.
     */
    InitializerDef initializerDef();

    /** Set the initializer's type object. */
    Initializer initializerDef(InitializerDef ii);
}
