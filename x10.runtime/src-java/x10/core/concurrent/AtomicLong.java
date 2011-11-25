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

package x10.core.concurrent;

import x10.core.RefI;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

public final class AtomicLong extends java.util.concurrent.atomic.AtomicLong implements RefI, X10JavaSerializable {

    private static final long serialVersionUID = 1L;
    static {
        x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AtomicLong.class);
    }
    private static short _serialization_id;

    // constructor just for allocation
    public AtomicLong(java.lang.System[] $dummy) {
        super();
    }
    
    public AtomicLong $init() {return this;}

    public AtomicLong() {
        super();
    }
    
    public AtomicLong $init(long initialValue) {
        // TODO
        set(initialValue);
        return this;
    }
    
    public AtomicLong(long initialValue) {
        super(initialValue);
    }
    
    //
    // Runtime type information
    //
    public static final RuntimeType<AtomicLong> $RTT = NamedType.<AtomicLong> make(
        "x10.util.concurrent.AtomicLong",
        AtomicLong.class,
        new Type[] { Types.OBJECT }
    );
    public RuntimeType<AtomicLong> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) { return null; }

	public void $_serialize(X10JavaSerializer serializer) throws IOException {
		serializer.write(this.get());
	}

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        return $_deserialize_body(null, deserializer);
    }

	public short $_get_serialization_id() {
		return _serialization_id;
	}

    public static void $_set_serialization_id(short id) {
         _serialization_id = id;
    }

    public static X10JavaSerializable $_deserialize_body(AtomicLong atomicLong, X10JavaDeserializer deserializer) throws IOException {
        long l = deserializer.readLong();
        AtomicLong al = new AtomicLong(l);
        deserializer.record_reference(al);
        return al;
    }

}
