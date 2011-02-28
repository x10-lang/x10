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

public final class RemoteIndexedMemoryChunk<T> extends x10.core.Struct {

	private static final long serialVersionUID = 1L;

    private static final java.util.ArrayList<Object> objects = new java.util.ArrayList<Object>(); // all referenced objects in this place

    public final int length;
    public final int id; // place local id of referenced object
    public final Type<T> type;
    public final Place home;

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


    @Override
    public boolean _struct_equals(Object o) {
        if (!(o instanceof RemoteIndexedMemoryChunk<?>)) return false;
        RemoteIndexedMemoryChunk<?> that = (RemoteIndexedMemoryChunk<?>)o;
        return this.id == that.id && this.home == that.home;
    }

    public static final RuntimeType<RemoteIndexedMemoryChunk<?>> $RTT = new NamedType<RemoteIndexedMemoryChunk<?>>(
        "x10.util.RemoteIndexedMemoryChunk",
        RemoteIndexedMemoryChunk.class,
        new RuntimeType.Variance[] { Variance.INVARIANT }
    );
    
    @Override
    public RuntimeType<RemoteIndexedMemoryChunk<?>> $getRTT() {
        return $RTT;
    }

    @Override
    public Type<?> $getParam(int i) {
        return i == 0 ? type : null;
    }
}
