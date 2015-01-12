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

import java.util.List;

/**
 * A <code>NewArray</code> represents a new array expression such as <code>new
 * File[8][] { null }</code>.  It consists of an element type (e.g.,
 * <code>File</code>), a list of dimension expressions (e.g., 8), 0 or more
 * additional dimensions (e.g., 1 for []), and an array initializer.  The
 * dimensions of the array initializer must equal the number of additional "[]"
 * dimensions.
 */
public interface NewArray extends Expr
{
    /** The array's base type. */
    TypeNode baseType();

    /** Set the array's base type. */
    NewArray baseType(TypeNode baseType);

    /**
     * The number of array dimensions.
     * Same as dims().size() + additionalDims().
     */
    int numDims();

    /** List of dimension expressions.
     * @return A list of {@link polyglot.ast.Expr Expr}.
     */
    List<Expr> dims();

    /** Set the list of dimension expressions.
     * @param dims A list of {@link polyglot.ast.Expr Expr}.
     */
    NewArray dims(List<Expr> dims);

    /** The number of additional dimensions. */
    int additionalDims();

    /** Set the number of additional dimensions. */
    NewArray additionalDims(int addDims);

    /** The array initializer, or null. */
    ArrayInit init();

    /** Set the array initializer. */
    NewArray init(ArrayInit init);
}
