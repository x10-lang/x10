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

/**
 * A <code>Case</code> is a representation of a Java <code>case</code>
 * statement.  It can only be contained in a <code>Switch</code>.
 */
public interface Case extends SwitchElement
{
    /**
     * Get the case label.  This must should a constant expression.
     * The case label is null for the <code>default</code> case.
     */
    Expr expr();

    /**
     * Set the case label.  This must should a constant expression,
     * or null.
     */
    Case expr(Expr expr);

    /** Returns true iff this is the default case. */
    boolean isDefault();

    /**
     * Returns the value of the case label.  This value is only valid
     * after type-checking.
     */
    long value();

    /**
     * Set the value of the case label.
     */
    Case value(long value);
}
