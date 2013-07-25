/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

package x10.io;

import x10.io.Unserializable;
import x10.compiler.NativeClass;

/**
 * A class to enable programmatic serialization of X10 values.
 *
 * @see Deserializer
 */
@NativeClass("java", "x10.serialization", "X10JavaSerializer")
@NativeClass("c++", "x10aux", "serialization_buffer")
public final class Serializer implements Unserializable {

    /**
     * Create and initializer a Serializer
     */
    public native def this();

    /**
     * Write the argument value v (and the object
     * graph reachable from it according to X10 serialization semantics)
     * to this objects serialization stream.
     */
    public native def writeAny(v:Any):void;

    /**
     * Get the serialized values as a Rail[byte].
     * NOTE: After this method is called the Serializer
     *       may no longer support calls to writeAny.
     */
    public native def toRail():Rail[byte];
}
