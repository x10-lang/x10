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

import x10.core.Any;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

public final class AtomicInteger extends java.util.concurrent.atomic.AtomicInteger implements Any, X10JavaSerializable {

    private static final long serialVersionUID = 1L;
    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AtomicInteger.class);

    // constructor just for allocation
    public AtomicInteger(java.lang.System[] $dummy) {
        super();
    }
    
    public final AtomicInteger x10$util$concurrent$AtomicInteger$$init$S() {return this;}
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public AtomicInteger $init() {return x10$util$concurrent$AtomicInteger$$init$S();}
	
    public AtomicInteger() {
        super();
    }

    public final AtomicInteger x10$util$concurrent$AtomicInteger$$init$S(int initialValue) {
        // TODO
        set(initialValue);
        return this;
    }
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public AtomicInteger $init(int initialValue) {
        return x10$util$concurrent$AtomicInteger$$init$S(initialValue);
    }
    
    public AtomicInteger(int initialValue) {
        super(initialValue);
    }
    
    //
    // Runtime type information
    //
    public static final RuntimeType<AtomicInteger> $RTT = NamedType.<AtomicInteger> make(
        "x10.util.concurrent.AtomicInteger",
        AtomicInteger.class
    );
    public RuntimeType<AtomicInteger> $getRTT() {return $RTT;}
    public Type<?> $getParam(int i) { return null; }
    
	public void $_serialize(X10JavaSerializer serializer) throws IOException {
		serializer.write(this.get());
	}

	public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
		return $_deserialize_body(null, deserializer);
	}

	public static X10JavaSerializable $_deserialize_body(AtomicInteger atomicInteger, X10JavaDeserializer deserializer) throws IOException {
        int i = deserializer.readInt();
        AtomicInteger ai = new AtomicInteger(i);
        deserializer.record_reference(ai);
        return ai;
	}

	public short $_get_serialization_id() {
		return _serialization_id;
	}
}
