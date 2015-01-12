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
 * This exception will be throw when the program attempts
 * to serialize a non-serializable class
 * (eg one that implements Unserializable).
 */
public class NotSerializableException extends IOException {

    public def this() { super(); }

    public def this(message: String) { super(message); }

}
