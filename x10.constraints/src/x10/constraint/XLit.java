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

package x10.constraint;

/**
 * A representation of a literal. A literal is an XVar carrying a
 * payload that is equal to any other XLit carrying the same payload.
 * 
 * This class and its subclasses should not have mutable state.
 * @author vijay
 *
 */
public interface XLit extends XVar  {
	public Object val(); 
}
