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
     * Create and initialize a Serializer that will serialize
     * values to an in-memory Rail[] that can be accessed
     * by calling {@link #toRail}.
     */
    public native def this();
    
    /**
     * Create and initialize a Serializer that will serialize
     * values to the argument OutputStreamWriter.
     * Serializer instances constructed using this constructor
     * do not support the {@link #toRail()} and cannot be
     * used as arguments to the {@link Deserializer(Serializer)}
     * constructor of the Deserializer class.
     * 
     * NOTE: This constructor is currently only implemented for ManagedX10
     */
    public native def this(o:OutputStreamWriter);

    /**
     * Write the argument value v (and the object
     * graph reachable from it according to X10 serialization semantics)
     * to this objects serialization stream.
     */
    public native def writeAny(v:Any):void;

    /**
     * Get the serialized values as a Rail[byte].
     * If this Serializer was created with a user-provided
     * OutputStreamWriter, then toRail will throw an
     * UnsupportedOperationException.
     */
    public native def toRail():Rail[byte];
    
    /**
     * Return the number of bytes of data that
     * have been serialized so far.
     */
    public native def dataBytesWritten():Int;
    
    /**
     * Indicates that the program wants to serialize a logically
     * new object graph.  This resets the internal state that is used
     * to detect duplicate objects and preserve object identity when
     * the object graph is deserialized.  So even if the same object
     * has already been serialized before the call to newObjectGraph,
     * it will be fully serialized again and when deserialized two
     * copies of the object will be created (instead of one).
     * 
     * Note: This function is currently only implemented for Managed X10
     */
    public native def newObjectGraph():void;
    
}
