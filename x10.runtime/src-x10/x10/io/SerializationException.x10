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
 * An exception indicates an error occured during 
 * serialization or deserialization. 
 */
public class SerializationException extends Exception {

    public def this() { super(); }

    public def this(cause:CheckedThrowable) { super(cause); }

    public def this(message:String) { super(message); }

    public def this(message:String, cause:CheckedThrowable) { 
        super(message, cause); 
    }
}
