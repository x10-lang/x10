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

package polyglot.types;

import java.util.Map;

import polyglot.main.Report;
import polyglot.util.Position;

/**
 * Thrown during any number of phases of the compiler during which a semantic
 * error may be detected.
 */
public class SemanticException extends Exception {
    private static final long serialVersionUID = -7523638195883713606L;

    protected Position position;
    Map<String, Object> attributes;

	public SemanticException() {
        super();
    }

    public SemanticException(Throwable cause) {
        super(cause);
    }

    public SemanticException(String m) {
        super(m);
    }

    public SemanticException(String m, Throwable cause) {
        super(m, cause);
    }

    public SemanticException(String m, Position position) {
    	super(m);
		this.position = position;
    }

    public void setPosition(Position p) {
    	position = p;
    }
    public Position position() {
    	return position;
    }
    
    public Map<String, Object> attributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
    
    private static boolean init = false;
    public static boolean fillInStackTrace = true;
    
    public synchronized Throwable fillInStackTrace() {
        if (! fillInStackTrace) {
            // fast path: init==true, fillInStackTrace==false
            return this;
        }
        if (! init) {
            fillInStackTrace = Report.trace;
            init = true;
            if (! fillInStackTrace) {
                return this;
            }
        }
        return super.fillInStackTrace();
    }
}
