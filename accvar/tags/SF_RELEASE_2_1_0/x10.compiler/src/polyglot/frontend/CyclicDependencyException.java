/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
