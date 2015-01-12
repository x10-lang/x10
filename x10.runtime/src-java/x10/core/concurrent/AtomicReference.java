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

public final class AtomicReference<T> extends java.util.concurrent.atomic.AtomicReference<T> implements Any, X10JavaSerializable {

    // constructor just for allocation
    public AtomicReference(java.lang.System[] $dummy) {
        super();
    }
	
    public final AtomicReference x10$util$concurrent$AtomicReference$$init$S(Type<T> T) {
        this.T = T;
        return this;
    }
	
    public AtomicReference(Type<T> T) {
        super();
        this.T = T;
    }

    public final AtomicReference x10$util$concurrent$AtomicReference$$init$S(Type<T> T, T initialValue) {
        set(initialValue);
        this.T = T;
        return this;
    }
    
    public AtomicReference(Type<T> T, T initialValue) {
        super(initialValue);
        this.T = T;
    }
    
    //
    // Runtime type information
    //
    public static final RuntimeType<AtomicReference> $RTT = NamedType.<AtomicReference> make(
        "x10.util.concurrent.AtomicReference",
        AtomicReference.class,
        1
    );
    public RuntimeType<AtomicReference> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) { return i == 0 ? T : null; }
    
    public Type<T> T;

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

	public void $_serialize(X10JavaSerializer serializer) throws IOException {
		serializer.write(this.T);
		serializer.write(get());
	}

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        AtomicReference ar = new AtomicReference((System[])null);
        deserializer.record_reference(ar);
		return $_deserialize_body(ar, deserializer);
	}

	public static X10JavaSerializable $_deserialize_body(AtomicReference ar, X10JavaDeserializer deserializer) throws IOException {
        Type T = (Type) deserializer.readObject();
        ar.T = T;
        Object value = deserializer.readObject();
        ar.set(value);
        return ar;
	}
}
