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

import java.util.List;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.visit.ContextVisitor;
import polyglot.visit.TypeChecker;

/**
 * An <code>ArrayInit</code> is an immutable representation of
 * an array initializer, such as { 3, 1, { 4, 1, 5 } }.  Note that
 * the elements of these array may be expressions of any type (e.g.,
 * <code>Call</code>).
 */
public interface ArrayInit extends Expr
{
    /**
     * Get the initializer elements.
     * @return A list of {@link polyglot.ast.Expr Expr}.
     */
    List<Expr> elements();

    /**
     * Set the initializer elements.
     * @param elements A list of {@link polyglot.ast.Expr Expr}.
     */
    ArrayInit elements(List<Expr> elements);

    /**
     * Type check the individual elements of the array initializer against the
     * left-hand-side type.  Each element is checked to see if it can be
     * assigned to a variable of type lhsType.
     * @param tc TODO
     * @param lhsType Type to compare against.
     * @exception SemanticException if there is a type error.
     */
    void typeCheckElements(ContextVisitor tc, Type lhsType);
}
