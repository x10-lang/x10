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

package x10.runtime.impl.java;

import x10.rtt.Type;

import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Implementation of PlaceLocalHandle service for Java-based runtime.
 */
public final class PlaceLocalHandle<T> implements java.io.Serializable, X10JavaSerializable {

	private static final long serialVersionUID = 1L;
    static {
        x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PlaceLocalHandle.class);
    }
    private static short _serialization_id;

    private static final HashMap<Long,Object> data = new HashMap<Long,Object>();
    
	private static final int placeShift = 48;
	private static AtomicLong lastId = new AtomicLong(0L);

	transient private boolean initialized = false;
	transient private Object myData = null;
	private Long id;
    
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
    
    public PlaceLocalHandle $init() {
        id = nextId();
        return this;
    }

    // not used
//    public PlaceLocalHandle(Type<T> T) {
//        id = nextId();
//    }

    // TODO haszero
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

	public void $_serialize(X10JavaSerializer serializer) throws IOException {
		serializer.write((long) id);
	}

	public static X10JavaSerializable $_deserialize_body(PlaceLocalHandle placeLocalHandle, X10JavaDeserializer deserializer) throws IOException {
        placeLocalHandle.id = (Long) deserializer.readLong();
        return placeLocalHandle;
	}

	public short $_get_serialization_id() {
		return _serialization_id;
	}

    public static void $_set_serialization_id(short id) {
         _serialization_id = id;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        PlaceLocalHandle placeLocalHandle = new PlaceLocalHandle((java.lang.System[]) null, (Type<?>) null);
        deserializer.record_reference(placeLocalHandle);
		return $_deserialize_body(placeLocalHandle, deserializer);
	}
}
