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

/**
 * An <code>Instanceof</code> is an immutable representation of
 * the use of the <code>instanceof</code> operator.
 */
public interface Instanceof extends Expr 
{
    /** Get the expression to check. */
    Expr expr();
    /** Set the expression to check. */
    Instanceof expr(Expr expr);

    /** Get the type to compare against. */
    TypeNode compareType();
    /** Set the type to compare against. */
    Instanceof compareType(TypeNode compareType);
}
