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

package x10.core.concurrent;

import java.io.IOException;

import x10.core.Any;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

public final class AtomicLong extends java.util.concurrent.atomic.AtomicLong implements Any, X10JavaSerializable {

    // constructor just for allocation
    public AtomicLong(java.lang.System[] $dummy) {
        super();
    }
    
    public final AtomicLong x10$util$concurrent$AtomicLong$$init$S() {return this;}

    public AtomicLong() {
        super();
    }
    
    public final AtomicLong x10$util$concurrent$AtomicLong$$init$S(long initialValue) {
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
        AtomicLong.class
    );
    public RuntimeType<AtomicLong> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) { return null; }

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

	public void $_serialize(X10JavaSerializer serializer) throws IOException {
		serializer.write(this.get());
	}

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        return $_deserialize_body(null, deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(AtomicLong atomicLong, X10JavaDeserializer deserializer) throws IOException {
        long l = deserializer.readLong();
        AtomicLong al = new AtomicLong(l);
        deserializer.record_reference(al);
        return al;
    }

}
