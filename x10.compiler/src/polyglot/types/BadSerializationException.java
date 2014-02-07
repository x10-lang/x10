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
 * when a <code>ClassResolver</code> finds a class file that contains encoded 
 * Polyglot type information, but is unable to read in the serialized class 
 * information. The most likely cause of this exception is that the compiler 
 * (or compiler extension) has been modified since the class file was created, 
 * resulting in incompatible serializations. The solution is to delete the class 
 * file, and recompile it from the source.
 */
public class BadSerializationException extends SemanticException {
    private static final long serialVersionUID = -1947303584600782082L;

    private String className;
    
    private static String message(String className) {
    	return "The deserialization of the Polyglot type information for \"" + 
                className + "\" failed. The most likely cause of this " +
                "failure is that the compiler (or compiler extension) has " +
                "been modified since the class file was created, resulting " +
                "in incompatible serializations. The solution is to delete " +
                "the class file for the class \"" + className + 
                "\", and recompile it from the source.";
    }
    public BadSerializationException(String className) {
        super(message(className)); 
        this.className = className;
    }
    
    public BadSerializationException(String className, Position position) {
		super(message(className), position); 
        this.className = className;
    }
    
    public String getClassName() {
        return className;
    }
}
