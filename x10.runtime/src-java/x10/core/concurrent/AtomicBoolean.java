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

public final class AtomicBoolean extends java.util.concurrent.atomic.AtomicBoolean implements Any, X10JavaSerializable {

    // constructor just for allocation
    public AtomicBoolean(java.lang.System[] $dummy) {
        super();
    }
    
    public final AtomicBoolean x10$util$concurrent$AtomicBoolean$$init$S() {return this;}

    public AtomicBoolean() {
        super();
    }
    
    public final AtomicBoolean x10$util$concurrent$AtomicBoolean$$init$S(boolean initialValue) {
        // TODO
        set(initialValue);
        return this;
    }

    public AtomicBoolean(boolean initialValue) {
        super(initialValue);
    }
    
    //
    // Runtime type information
    //
    public static final RuntimeType<AtomicBoolean> $RTT = NamedType.<AtomicBoolean> make(
        "x10.util.concurrent.AtomicBoolean",
        AtomicBoolean.class
    );
    public RuntimeType<AtomicBoolean> $getRTT() {return $RTT;}
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

    public static X10JavaSerializable $_deserialize_body(AtomicBoolean atomicBoolean, X10JavaDeserializer deserializer) throws IOException {
        boolean b = deserializer.readBoolean();
        AtomicBoolean ab = new AtomicBoolean(b);
        deserializer.record_reference(ab);
        return ab;
    }
}
