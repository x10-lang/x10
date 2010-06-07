/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

/**
 * Represents a named X10Type. Such types are not anonymous.
 * Examples of such types are X10PrimitiveTypes, X10ParsedClassTypes, X10ArrayTypes.
 */
import polyglot.types.Named;
import polyglot.types.Type;

public interface X10NamedType extends Named, Type {
	
	
}
