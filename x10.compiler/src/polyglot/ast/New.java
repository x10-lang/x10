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

import polyglot.types.ClassDef;
import polyglot.types.ConstructorInstance;
import x10.types.X10ClassDef;
import x10.types.X10ConstructorInstance;
import x10.ast.X10New;
import x10.ast.X10ProcedureCall;

/**
 * A <code>New</code> is an immutable representation of the use of the
 * <code>new</code> operator to create a new instance of a class.  In
 * addition to the type of the class being created, a <code>New</code> has a
 * list of arguments to be passed to the constructor of the object and an
 * optional <code>ClassBody</code> used to support anonymous classes.
 */
public interface New extends Expr, X10ProcedureCall
{
    /** The type object for anonymous classes, or null. */
    X10ClassDef anonType();

    /** Set the type object for anonymous classes. */
    X10New anonType(X10ClassDef anonType);

    /** The constructor invoked by this expression. */
    X10ConstructorInstance constructorInstance();

    /** Set the constructor invoked by this expression. */
    X10New constructorInstance(ConstructorInstance ci);

    /**
     * The qualifier expression for the type, or null. If non-null, this
     * expression creates an inner class of the static type of the qualifier.
     */
    Expr qualifier();

    /** Set the qualifier expression for the type. */
    X10New qualifier(Expr qualifier);

    /** The type we are creating, possibly qualified by qualifier. */
    TypeNode objectType();

    /** Set the type we are creating. */
    X10New objectType(TypeNode t);

    /** Actual arguments to pass to the constructor.
     * @return A list of {@link polyglot.ast.Expr Expr}.
     */
    List<Expr> arguments();

    /** Set the actual arguments to pass to the constructor.
     * @param arguments A list of {@link polyglot.ast.Expr Expr}.
     */
    ProcedureCall arguments(List<Expr> arguments);

    /** The class body for anonymous classes, or null. */
    ClassBody body();

    /** Set the class body for anonymous classes. */
    X10New body(ClassBody b);


	X10New typeArguments(List<TypeNode> args);

	boolean newOmitted();

	X10New newOmitted(boolean val);
}
