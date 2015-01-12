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
package x10.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

@SuppressWarnings("serial")
public class LocalVar<T> extends Ref {
    
    public static final RuntimeType<LocalVar<?>> $RTT = NamedType.<LocalVar<?>> make("x10.compiler.LocalVar", LocalVar.class, 1);
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { if (i == 0) return T; return null; }

    private static final Map<java.lang.Long, Object> idToObject = new ConcurrentHashMap<java.lang.Long, Object>();
    private static final Object nullObject = new Object();
    private static AtomicLong lastId = new AtomicLong(0);

    private Type<?> T;
    private long id;

    // constructor just for allocation
    public LocalVar(java.lang.System[] $dummy) {
        super($dummy);
    }

    public final LocalVar<T> x10$compiler$LocalVar$$init$S(final Type<?> T, final T local, __0x10$compiler$LocalVar$$T $dummy) {
        super.x10$lang$Object$$init$S();
        this.T = T;
        long temp = lastId.getAndIncrement();
        while (idToObject.containsKey(temp)) {
            temp = lastId.getAndIncrement();
        }
        id = temp;
        idToObject.put(id, local == null ? nullObject : local);
        return this;
    }

    public LocalVar(final Type<?> T, final T local, __0x10$compiler$LocalVar$$T $dummy) {
        super();
        this.T = T;
        long temp = lastId.getAndIncrement();
        while (idToObject.containsKey(temp)) {
            temp = lastId.getAndIncrement();
        }
        id = temp;
        idToObject.put(id, local == null ? nullObject : local);
    }
    // synthetic type for parameter mangling
    public abstract static class __0x10$compiler$LocalVar$$T {}
    
    public T $apply$G() {
        Object local = idToObject.remove(id);
        return (T) (local == nullObject ? null : local);
    }

    public T get$G() { 
        Object local = idToObject.get(id);
        return (T) (local == nullObject ? null : local);
    }

    public T set__0x10$compiler$LocalVar$$T$G(final T local) {
        idToObject.put(id,local);
        return local;
    }

    final public LocalVar<T> x10$compiler$LocalVar$$x10$compiler$LocalVar$this() {
        return LocalVar.this;
    }
        
    public static X10JavaSerializable $_deserialize_body(LocalVar<?> $_obj, X10JavaDeserializer $deserializer) throws java.io.IOException {
        return $_obj;
    }
    
    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws java.io.IOException { 
        LocalVar $_obj = new LocalVar((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
    }
    
    public void $_serialize(X10JavaSerializer $serializer) throws java.io.IOException {
        
    }
}
