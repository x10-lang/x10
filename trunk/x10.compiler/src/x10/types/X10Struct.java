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

import polyglot.types.Type;



/**
 * Interface implemented by types that may represent an X10 struct, viz subtypes of ConstrainedType or X10ClassType.
 * 
 * @author vj
 *
 */
public interface X10Struct extends Type {
	/**
	 * Is this type an X10 struct type?
	 */
	boolean isX10Struct();
	X10Struct makeX10Struct();


}
