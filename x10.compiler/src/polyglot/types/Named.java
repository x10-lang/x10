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

/**
 * A <code>Named</code> is a TypeObject that is named.
 */
public interface Named extends TypeObject
{
    /**
     * Simple name of the type object. Anonymous classes do not have names.
     */
    Name name();

    /**
     * Full dotted-name of the type object. For a package, top level class, 
     * top level interface, or primitive type, this is
     * the fully qualified name. For a member class or interface that is
     * directly enclosed in a class or interface with a fully qualified name,
     * then this is the fully qualified name of the member class or interface. 
     * For local and anonymous classes, this method returns a string that is
     * not the fully qualified name (as these classes do not have fully 
     * qualified names), but that may be suitable for debugging or error 
     * messages. 
     */
    QName fullName();
    
    /**
     * Return true if the class is global; that is top-level or a member of a global class.
     */
    boolean isGloballyAccessible();
}
