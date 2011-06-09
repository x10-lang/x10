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

import x10.lang.Place;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.RuntimeType.Variance;
import x10.rtt.Type;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

public final class RemoteIndexedMemoryChunk<T> extends x10.core.Struct implements X10JavaSerializable{

	private static final long serialVersionUID = 1L;
	private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(RemoteIndexedMemoryChunk.class.getName());

    private static final java.util.ArrayList<Object> objects = new java.util.ArrayList<Object>(); // all referenced objects in this place

    public int length;
    public int id; // place local id of referenced object
    public Type<T> type;
    public Place home;
    
    public RemoteIndexedMemoryChunk(java.lang.System[] $dummy) {
        super($dummy);
    }

    public RemoteIndexedMemoryChunk<T> $init(Type<T> type, int length, Object value) {
        this.length = length;
        this.type = type;
        this.home = x10.lang.Runtime.home();

        int size;
        synchronized (objects) {
            size = objects.size();
            for (int id = size - 1; id >= 0; --id) {
                if (objects.get(id) == value) {
                    this.id = id;
                    return this;
                }
            }
            objects.add(value);
        }
        this.id = size;
        return this;
    }

    private RemoteIndexedMemoryChunk(Type<T> type, int length, Object value) {
        this.length = length;
        this.type = type;
        this.home = x10.lang.Runtime.home();

        int size;
        synchronized (objects) {
            size = objects.size();
            for (int id = size - 1; id >= 0; --id) {
                if (objects.get(id) == value) {
                    this.id = id;
                    return;
                }
            }
            objects.add(value);
        }
        this.id = size;
    }

    public static Object getValue(int id) {
        synchronized (objects) {
            return objects.get(id);
        }
    }
    
    public static <T> RemoteIndexedMemoryChunk<T> wrap(IndexedMemoryChunk<T> chunk) {
        return new RemoteIndexedMemoryChunk<T>(chunk.type, chunk.length, chunk.value);
    }
    
    public final IndexedMemoryChunk<T> $apply$G() {
        Object obj;
        synchronized (objects) {
            obj = objects.get(this.id);
        }
        return new IndexedMemoryChunk<T>(type, length, obj);
    }

    public boolean _struct_equals$O(Object o) {
        if (!(o instanceof RemoteIndexedMemoryChunk<?>)) return false;
        RemoteIndexedMemoryChunk<?> that = (RemoteIndexedMemoryChunk<?>)o;
        return this.id == that.id && this.home == that.home;
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

    public static final RuntimeType<RemoteIndexedMemoryChunk<?>> $RTT = new NamedType<RemoteIndexedMemoryChunk<?>>(
        "x10.util.RemoteIndexedMemoryChunk",
        RemoteIndexedMemoryChunk.class,
        new RuntimeType.Variance[] { Variance.INVARIANT },
        new Type[] { x10.rtt.Types.STRUCT }
    );
    
    @Override
    public RuntimeType<RemoteIndexedMemoryChunk<?>> $getRTT() {
        return $RTT;
    }

    @Override
    public Type<?> $getParam(int i) {
        return i == 0 ? type : null;
    }
    
  //TODO Keith  do we need to serialize this?
	public void _serialize(X10JavaSerializer serializer) {
	}

	public int _get_serialization_id() {
		return _serialization_id;
	}

}
