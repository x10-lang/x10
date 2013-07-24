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

/**
 * Types that implement this interface indicate that when instances
 * of the type are copied across an <code>at</code> boundary, that
 * a custom protocol should be used to transfer the objects values 
 * instead of the default system-provided protocol which copies all
 * non-transient fields. This custom protocol is implemented in two 
 * pieces:
 * <ul>
 * <li>A <code>serialize</code> method that will be invoked by the runtime
 *     system when a value that implements CustomSerialization needs to
 *     be copied.  This serialize method will construct and return a
 *     value of type SerialData.  The runtime system will then transfer the
 *     value returned by serialize to the destination place.</li>
 * <li>A constructor that takes a single argument of type <code>SerialData</code>.
 *     The runtime system will invoke this constructor at the destination
 *     place giving it the copy of the object that was constructed by 
 *     <code>serialize</code> at the source place.
 * </ul>
 * Unlike the default serialization implementation, when running the
 * custom serialization protocol the runtime system does not automatically
 * handle cycles in the serialized object graph.<p>
 *
 * @see SerialData
 * @see Unserializable
 */
public interface CustomSerialization {
    /**
     * @return the value that should be serialized
     */
    def serialize():SerialData;
}
