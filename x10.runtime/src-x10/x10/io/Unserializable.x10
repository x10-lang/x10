/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.io;

/**
 * Types that implement this marker interface indicate that 
 * they cannot be serialized from one place to another.
 * Any attempt to serialize an instance of a type will result in
 * a NotSerializableException being thrown during serialization.
 * 
 * @see NotSerializableException
 */
public interface Unserializable {
}
