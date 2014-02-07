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
