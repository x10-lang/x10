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

package polyglot.util;

/** Exception thrown when the compiler is confused. */
public class InternalCompilerError extends RuntimeException
{
    private static final long serialVersionUID = -5026712857378351230L;

    protected Position pos;

    public InternalCompilerError(String msg) {
        this(msg, (Position)null);
    }

    public InternalCompilerError(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public InternalCompilerError(String msg, Throwable cause) {
        this(msg, null, cause);
    }

    public InternalCompilerError(Position position, String msg) {
	this(msg, position); 
    }

    public InternalCompilerError(String msg, Position position) {
        super(msg); 
        pos = position;
    }
    public InternalCompilerError(String msg, Position position, Throwable cause) {
        super(msg, cause); 
        pos = position;
    }

    public void setPosition(Position pos) {
	this.pos = pos;
    }

    public Position position() {
	return pos;
    }

    public String message() {
	return super.getMessage();
    }

    public String getMessage() {
	return pos == null ? message() : pos + ": " + message();
    }
}
