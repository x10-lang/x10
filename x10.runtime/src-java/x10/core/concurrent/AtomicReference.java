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
import x10.rtt.RuntimeType.Variance;
import x10.rtt.Type;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

public final class AtomicReference<T> extends java.util.concurrent.atomic.AtomicReference<T> implements RefI, X10JavaSerializable {

	private static final long serialVersionUID = 1L;
    private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(AtomicReference.class.getName());

	public AtomicReference(java.lang.System[] $dummy) {
	    super();
	}
	
	public AtomicReference $init(Type<T> T) {
        this.T = T;
        return this;
    }
	
    public AtomicReference(Type<T> T) {
        super();
        this.T = T;
    }

    public AtomicReference $init(Type<T> T, T initialValue) {
        // TODO
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
    public static final RuntimeType<AtomicReference> $RTT = new NamedType<AtomicReference>(
        "x10.util.concurrent.AtomicReference",
        AtomicReference.class,
        new Variance[] { Variance.INVARIANT },
        new Type[] { x10.rtt.Types.OBJECT }
    );
    public RuntimeType<AtomicReference> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) {
        return i == 0 ? T : null;
    }
    private Type<T> T;

	public void _serialize(X10JavaSerializer serializer) throws IOException {
		serializer.javaSerialize(this);
	}

	public int _get_serialization_id() {
		return _serialization_id;
	}

    public static X10JavaSerializable _deserializer(X10JavaDeserializer deserializer) throws IOException {
		return _deserialize_body(null, deserializer);
	}

	public static X10JavaSerializable _deserialize_body(AtomicReference atomicReference, X10JavaDeserializer deserializer) throws IOException {
        Type T = (Type) deserializer.readRef();
        AtomicReference ar = new AtomicReference(T);
        return ar;
	}
}
