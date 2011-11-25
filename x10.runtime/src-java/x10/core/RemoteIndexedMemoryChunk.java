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

package x10.core;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import x10.lang.Place;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.RuntimeType.Variance;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

public final class RemoteIndexedMemoryChunk<T> extends x10.core.Struct implements X10JavaSerializable{

	private static final long serialVersionUID = 1L;
    static {
        x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RemoteIndexedMemoryChunk.class);
    }
	private static short _serialization_id;

    private static AtomicInteger lastId = new AtomicInteger(0);
    // all referenced objects in this place
    private static final ConcurrentHashMap<java.lang.Integer, Object> id2Object = new ConcurrentHashMap<java.lang.Integer, Object>();
    private static final ConcurrentHashMap<Object, java.lang.Integer> object2Id = new ConcurrentHashMap<Object, java.lang.Integer>();

    public int length;
    // TODO change id to Long as needed
    public java.lang.Integer id; // place local id of referenced object
    public Type<T> type;
    public Place home;
    
    // constructor just for allocation
    public RemoteIndexedMemoryChunk(java.lang.System[] $dummy) {
        super($dummy);
    }

    public RemoteIndexedMemoryChunk<T> $init(Type<T> type, int length, Object value) {
        this.length = length;
        this.type = type;
        this.home = x10.lang.Runtime.home();

        java.lang.Integer tmpId = lastId.incrementAndGet(); //TODO: check wraparound
        id2Object.put(tmpId, value);
        java.lang.Integer existingId = object2Id.putIfAbsent(value, tmpId);
        if (existingId != null) {
            this.id = existingId;
            id2Object.remove(tmpId);
        } else {
            this.id = tmpId;
        }
        return this;
    }

    private RemoteIndexedMemoryChunk(Type<T> type, int length, Object value) {
        this.length = length;
        this.type = type;
        this.home = x10.lang.Runtime.home();

        java.lang.Integer tmpId = lastId.incrementAndGet(); //TODO: check wraparound
        id2Object.put(tmpId, value);
        java.lang.Integer existingId = object2Id.putIfAbsent(value, tmpId);
        if (existingId != null) {
            this.id = existingId;
            id2Object.remove(tmpId);
        } else {
            this.id = tmpId;
        }
    }

    public static Object getValue(int id) {
        return id2Object.get(id);
    }
    
    public static <T> RemoteIndexedMemoryChunk<T> wrap(IndexedMemoryChunk<T> chunk) {
        return new RemoteIndexedMemoryChunk<T>(chunk.type, chunk.length, chunk.value);
    }
    
    public final IndexedMemoryChunk<T> $apply$G() {
        Object obj = id2Object.get(this.id);
        return new IndexedMemoryChunk<T>(type, length, obj);
    }

    public boolean _struct_equals$O(Object o) {
        if (!(o instanceof RemoteIndexedMemoryChunk<?>)) return false;
        RemoteIndexedMemoryChunk<?> that = (RemoteIndexedMemoryChunk<?>)o;
        return (int) this.id == (int) that.id && this.home.id == that.home.id;
    }

    // TODO implement remote operations
    public void remoteAdd(int idx, long v) {
    	ThrowableUtilities.UnsupportedOperationException("Remote operations are not implemented.");
    }
    public void remoteAnd(int idx, long v) {
    	ThrowableUtilities.UnsupportedOperationException("Remote operations are not implemented.");
    }
    public void remoteOr(int idx, long v) {
    	ThrowableUtilities.UnsupportedOperationException("Remote operations are not implemented.");
    }
    public void remoteXor(int idx, long v) {
    	ThrowableUtilities.UnsupportedOperationException("Remote operations are not implemented.");
    }

    public void remoteAdd(int idx, x10.core.ULong v) {
    	ThrowableUtilities.UnsupportedOperationException("Remote operations are not implemented.");
    }
    public void remoteAnd(int idx, x10.core.ULong v) {
    	ThrowableUtilities.UnsupportedOperationException("Remote operations are not implemented.");
    }
    public void remoteOr(int idx, x10.core.ULong v) {
    	ThrowableUtilities.UnsupportedOperationException("Remote operations are not implemented.");
    }
    public void remoteXor(int idx, x10.core.ULong v) {
    	ThrowableUtilities.UnsupportedOperationException("Remote operations are not implemented.");    	
    }

    public static final RuntimeType<RemoteIndexedMemoryChunk<?>> $RTT = NamedType.<RemoteIndexedMemoryChunk<?>> make(
        "x10.util.RemoteIndexedMemoryChunk",
        RemoteIndexedMemoryChunk.class,
        RuntimeType.INVARIANTS(1),
        new Type[] { Types.STRUCT }
    );
    @Override
    public RuntimeType<RemoteIndexedMemoryChunk<?>> $getRTT() { return $RTT; }
    @Override
    public Type<?> $getParam(int i) { return i == 0 ? type : null; }
    
	public void $_serialize(X10JavaSerializer serializer) throws IOException {
        serializer.write(length);
        serializer.write((int) id);
        serializer.write(type);
        serializer.write(home);
	}

	public short $_get_serialization_id() {
		return _serialization_id;
	}

    public static void $_set_serialization_id(short id) {
         _serialization_id = id;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        RemoteIndexedMemoryChunk rimc = new RemoteIndexedMemoryChunk(null);
        deserializer.record_reference(rimc);
		return $_deserialize_body(rimc, deserializer);
	}

    public static X10JavaSerializable $_deserialize_body(RemoteIndexedMemoryChunk rimc, X10JavaDeserializer deserializer) throws IOException {
        rimc.length = deserializer.readInt();
        rimc.id = (java.lang.Integer) deserializer.readInt();
        rimc.type = (Type) deserializer.readRef();
        rimc.home = (Place) deserializer.readRef();
        return rimc;
    }

}
