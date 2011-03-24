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

public class LocalVar<T> extends x10.core.Ref {

	private static final long serialVersionUID = 1L;

    public static final x10.rtt.RuntimeType<LocalVar<?>> $RTT =
        new x10.rtt.NamedType<LocalVar<?>>(
                "x10.compiler.LocalVar",
                /* base class */ LocalVar.class,
                /* variances */ new x10.rtt.RuntimeType.Variance[] { x10.rtt.RuntimeType.Variance.INVARIANT },
                /* parents */ new x10.rtt.Type[] { x10.rtt.Types.OBJECT }
    );

    public x10.rtt.RuntimeType<?> $getRTT() {
        return $RTT;
    }

    public x10.rtt.Type<?> $getParam(int i) {
        if (i == 0) return T;
        return null;
    }

    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER) {
            java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling");
        }
        oos.defaultWriteObject();
    }

    private static final Map<Long, Object> idToObject = new ConcurrentHashMap<Long, Object>();
    private static final Object nullObject = new Object();
    private static AtomicLong lastId = new AtomicLong(0);

    private x10.rtt.Type<?> T;
    private long id;

    public LocalVar(java.lang.System[] $dummy) {
        super($dummy);
    }

    public void $init(final x10.rtt.Type<?> T, final T local, java.lang.Class<?> $dummy0) {
        super.$init();
        this.T = T;
        long temp = lastId.getAndIncrement();
        while (idToObject.containsKey(temp)) {
            temp = lastId.getAndIncrement();
        }
        id = temp;
        idToObject.put(id, local == null ? nullObject : local);
    }

    public LocalVar(final x10.rtt.Type<?> T, final T local, java.lang.Class<?> $dummy0) {
        super();
        this.T = T;
        long temp = lastId.getAndIncrement();
        while (idToObject.containsKey(temp)) {
            temp = lastId.getAndIncrement();
        }
        id = temp;
        idToObject.put(id, local == null ? nullObject : local);
    }
    
    public T $apply$G() {
        Object local = idToObject.remove(id);
        return (T) (local == nullObject ? null : local);
    }

    public T get$G() { 
        Object local = idToObject.get(id);
        return (T) (local == nullObject ? null : local);
    }

    public T set$G(final T local) {
        idToObject.put(id,local);
        return local;
    }

    final public LocalVar<T> x10$compiler$LocalVar$$x10$compiler$LocalVar$this() {
        return LocalVar.this;
    }
}
