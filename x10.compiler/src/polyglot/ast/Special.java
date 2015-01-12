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
 * A <code>Special</code> is an immutable representation of a
 * reference to <code>this</code> or <code>super</code in Java.  This
 * reference can be optionally qualified with a type such as 
 * <code>Foo.this</code>.
 */
public interface Special extends Expr 
{
    public static enum Kind {
        SUPER("super"),THIS("this"), SELF("self");
        
        private final String name;
        Kind(String name) {
            this.name = name;
        }
        @Override public String toString() {
            return name;
        }
    }

    public static final Kind SUPER = Kind.SUPER;
    public static final Kind THIS  = Kind.THIS;

    /** Get the kind of expression: SUPER or THIS. */
    Kind kind();

    /** Set the kind of expression: SUPER or THIS. */
    Special kind(Kind kind);

    /** Get the outer class qualifier of the expression. */
    TypeNode qualifier();

    /** Set the outer class qualifier of the expression. */
    Special qualifier(TypeNode qualifier);
}
