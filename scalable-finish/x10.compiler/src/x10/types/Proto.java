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
 * Interface implemented by types that may be proto types, viz ConstrainedType or X10ClassType.
 * 
 * @author vj
 *
 */
public interface Proto extends Type {

	boolean isProto();

	/**
	 * Return T if this is proto T; else return this.
	 * @return
	 */
	Proto baseOfProto();

	/**
	 * @return this if this is proto T; else proto this
	 */
	Proto makeProto();  
}
