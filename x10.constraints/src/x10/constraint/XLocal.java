/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.constraint;


/**
 * The representation of a local variable reference in the constraint system.
 * The name of the local variable is supplied by an XName. Two XLocal's are equal
 * if their contained XName's are equal.
 * 
 * @author vj
 * 
 */
public interface XLocal<T> extends XVar  {
	public T name(); 
}
