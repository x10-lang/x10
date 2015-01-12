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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import polyglot.types.Type;

/**
 * A <code>Catch</code> represents one half of a <code>try-catch</code>
 * statement.  Specifically, the second half.
 */
public interface Catch extends CompoundStmt
{
    /**
     * The type of the catch's formal.  This is the same as
     * formal().type().type().  May not be valid until after type-checking.
     */
    Type catchType();

    /**
     * The catch block's formal paramter.
     */
    Formal formal();

    /**
     * Set the catch block's formal paramter.
     */
    Catch formal(Formal formal);

    /**
     * The body of the catch block.
     */
    Block body();

    /**
     * Set the body of the catch block.
     */
    Catch body(Block body);
}
