/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package x10.ast;

import java.util.List;

import polyglot.visit.ContextVisitor;
import polyglot.visit.TypeChecker;
import x10.types.SemanticException;
import x10.types.Type;

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
     * @return A list of {@link x10.ast.Expr Expr}.
     */
    List<Expr> elements();

    /**
     * Set the initializer elements.
     * @param elements A list of {@link x10.ast.Expr Expr}.
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
    void typeCheckElements(ContextVisitor tc, Type lhsType) throws SemanticException;
}
