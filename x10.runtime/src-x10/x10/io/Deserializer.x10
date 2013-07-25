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

package x10.io;

import x10.io.Unserializable;
import x10.compiler.NativeClass;

/**
 * A class to enable programatic deserialization of X10 values.
 *
 * @see Serializer
 */
@NativeClass("java", "x10.serialization", "X10JavaDeserializer")
@NativeClass("c++", "x10aux", "deserialization_buffer")
public final class Deserializer implements Unserializable {

    /**
     * Create a Deserializer that will read serialized values
     * from the argument Serializer.
     */
    public native def this(Serializer);

    /**
     * Create a Deserializer that will read serialized values
     * from the argument Rail[byte].  The contents of the argument 
     * Rail should have orginated from a call to toRail() on 
     * a Serializer object.
     */
    public native def this(Rail[byte]);

    /**
     * Deserialize a value
     */
    public native def readAny():Any;
}
