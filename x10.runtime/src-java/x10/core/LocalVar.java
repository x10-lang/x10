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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import x10.rtt.RuntimeType;
import x10.rtt.RuntimeType.Variance;
import x10.rtt.Type;
import x10.rtt.Types;

public class LocalVar<T> extends x10.core.Ref {

    private static final long serialVersionUID = 1L;
    
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LocalVar.class);

    public static final RuntimeType<LocalVar<?>> $RTT =
        x10.rtt.NamedType.<LocalVar<?>> make(
                "x10.compiler.LocalVar",
                /* base class */ LocalVar.class,
                /* variances */ RuntimeType.INVARIANTS(1),
                /* parents */ new Type[] { Types.OBJECT }
    );
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { if (i == 0) return T; return null; }

    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER) {
            java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling");
        }
        oos.defaultWriteObject();
    }

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
        // XTENLANG-3063
//        super.$init();
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
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public LocalVar<T> $init(final Type<?> T, final T local, __0x10$compiler$LocalVar$$T $dummy) {
        return x10$compiler$LocalVar$$init$S(T, local, $dummy);
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
        
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Ref $_obj, x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException {
        return $_obj;
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
        LocalVar $_obj = new LocalVar((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
    }
    
    public short $_get_serialization_id() {
         return $_serialization_id;
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
        
    }
}
