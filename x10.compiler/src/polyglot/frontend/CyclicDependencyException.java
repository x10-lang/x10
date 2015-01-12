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

package polyglot.frontend;


/**
 * Thrown during when the compiler tries to run a pass that is
 * already running.
 */
public class CyclicDependencyException extends Exception {
    private static final long serialVersionUID = 6892580409561143970L;

    protected Goal goal;

    public CyclicDependencyException() {
        super();
    }

    public CyclicDependencyException(String m) {
        super(m);
    }
    public CyclicDependencyException(String m, Goal g) {
    	super(m);
    	this.goal = g;
    }
}
