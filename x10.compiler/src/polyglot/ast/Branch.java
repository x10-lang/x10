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
 * A <code>Branch</code> is an immutable representation of a branch
 * statment in Java (a break or continue).
 */
public interface Branch extends Stmt
{
    /** Branch kind: either break or continue. */
    public static enum Kind {
        BREAK("break"), CONTINUE("continue");

        public final String name;
        private Kind(String name) {
            this.name = name;
        }
        @Override public String toString() {
            return name;
        }
    }

    public static final Kind BREAK    = Kind.BREAK;
    public static final Kind CONTINUE = Kind.CONTINUE;

    /**
     * The kind of branch.
     */
    Kind kind();

    /**
     * Set the kind of branch.
     */
    Branch kind(Kind kind);
    
    /**
     * Target label of the branch.
     */
    Id labelNode();
    
    /**
     * Set the target label of the branch.
     */
    Branch labelNode(Id label);
}
