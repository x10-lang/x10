/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.core;

import x10.core.Thread;

import x10.rtt.Type;
import x10.serialization.DeserializationDispatcher;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;


import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Implementation of PlaceLocalHandle service for Java-based runtime.
 */
public final class PlaceLocalHandle<T> implements java.io.Serializable, X10JavaSerializable {

    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = DeserializationDispatcher.addDispatcher(PlaceLocalHandle.class);

    private static final HashMap<java.lang.Long,Object> data = new HashMap<java.lang.Long,Object>();

    private static final int placeShift = 48;
    private static final AtomicLong lastId = new AtomicLong(0L);

    transient private boolean initialized = false;
    transient private Object myData = null;
    private java.lang.Long id;

    // TODO: The X10 code currently ensures that PlaceLocalHandle's are only
    //       created at Place 0 by doing an at.  We've contemplated moving to
    //       more of a SVD style implementation where each place would be able to
    //       create place local handles by either encoding the place in the id like we
    //       did here or by having the places get ids in "chunks" from the master id server
    //       at place 0. 
    //       Since we are thinking about making this change, I went ahead and did a poor-man's
    //       version of it here instead of asserting nextId is only called at place 0 
    //       (which would have been true currently).
    private static long nextId() {
        long here = Thread.currentThread().home().id;
        long newId  = lastId.incrementAndGet();
        assert newId < (1L << placeShift);
        newId |= (here << placeShift);
        return newId;
    }

    private Object readResolve() {
        initialized = false;
        return this;
    }

    // constructor just for allocation
    public PlaceLocalHandle(java.lang.System[] $dummy, Type<T> T) {
    }

    public final PlaceLocalHandle x10$core$PlaceLocalHandle$$init$S() {
        id = nextId();
        return this;
    }

    // not used
    //    public PlaceLocalHandle(Type<T> T) {
    //        id = nextId();
    //    }

    // zero value constructor
    public PlaceLocalHandle(Type<T> T, java.lang.System $dummy) {
        id = nextId();
    }

    public T $apply$G() {
        if (!initialized) {
            synchronized(data) {
                myData = data.get(id);
                initialized = true;
            }
        }
        return (T) myData;
    }

    public void set__0x10$lang$PlaceLocalHandle$$T(T value) {
        synchronized(data) {
            Object old = data.put(id, value);
            assert old == null : "Set called on already initialized local object";
        }
    }

    @Override
    public String toString() {
        return "PlaceLocalHandle(" + this.id + ")";
    }

    public short $_get_serialization_id() {
        return $_serialization_id;
    }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
        $serializer.write((long) id);
    }

    public static X10JavaSerializable $_deserialize_body(PlaceLocalHandle $_obj, X10JavaDeserializer $deserializer) throws IOException {
        $_obj.id = (java.lang.Long) $deserializer.readLong();
        return $_obj;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
        PlaceLocalHandle $_obj = new PlaceLocalHandle((java.lang.System[]) null, (Type<?>) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
    }

}
