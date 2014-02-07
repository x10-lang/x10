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

package polyglot.types;

import polyglot.util.Position;

/**
 * Signals an error in the class resolver system. This exception is thrown
 * when a <code>ClassResolver</code> is unable to resolve a given class name.
 */
public class NoClassException extends SemanticException {
    private static final long serialVersionUID = 322432889564144097L;

    private String className;
    
    public NoClassException(String className) {
        super("Class \"" + className + "\" not found."); 
        this.className = className;
    }
    
    public NoClassException(String className, TypeObject scope) {
        super("Class \"" + className + "\" not found"
                + (scope != null ? (" in scope of " + scope.toString())
                                 : "."));
        this.className = className;
    }
  
    public NoClassException(String className, Position position) {
        super("Class \"" + className + "\" not found.", position);
        this.className = className;
    }
    
    public String getClassName() {
        return className;
    }
}
