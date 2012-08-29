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

public final class AtomicBoolean extends java.util.concurrent.atomic.AtomicBoolean implements Any, X10JavaSerializable {

    private static final long serialVersionUID = 1L;
    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AtomicBoolean.class);

    // constructor just for allocation
    public AtomicBoolean(java.lang.System[] $dummy) {
        super();
    }
    
    public final AtomicBoolean x10$util$concurrent$AtomicBoolean$$init$S() {return this;}
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public AtomicBoolean $init() {return x10$util$concurrent$AtomicBoolean$$init$S();}

    public AtomicBoolean() {
        super();
    }
    
    public final AtomicBoolean x10$util$concurrent$AtomicBoolean$$init$S(boolean initialValue) {
        // TODO
        set(initialValue);
        return this;
    }
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public AtomicBoolean $init(boolean initialValue) {
        return x10$util$concurrent$AtomicBoolean$$init$S(initialValue);
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

	public short $_get_serialization_id() {
		return _serialization_id;
	}

}
