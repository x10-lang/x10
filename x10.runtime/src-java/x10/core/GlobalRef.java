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

public final class GlobalRef<T> extends x10.core.Struct {

    public static final x10.rtt.RuntimeType<GlobalRef<?>> _RTT = new x10.rtt.RuntimeType<GlobalRef<?>>(
        GlobalRef.class,
        new x10.rtt.RuntimeType.Variance[] { x10.rtt.RuntimeType.Variance.INVARIANT }
    ) {
        @Override
        public String typeName() {
            return "x10.lang.GlobalRef";
        } 
    };

    public x10.rtt.RuntimeType<GlobalRef<?>> getRTT() {
        return _RTT;
    }

    public x10.rtt.Type<?> getParam(int i) {
        if (i == 0) return T;
        return null;
    }

    private final x10.rtt.Type<?> T;

    final public x10.lang.Place home;

//    final private T value;
    private static final java.util.ArrayList<Object> objects = new java.util.ArrayList<Object>(); // all referenced objects in this place
    final private int id; // place local id of referenced object

    public GlobalRef(final x10.rtt.Type<?> T, final T t) {
        this.T = T;
        this.home = x10.lang.Runtime.here();
//        this.value = (T) t;
        int size;
        synchronized (objects) {
            size = objects.size();
            for (int id = size - 1; id >= 0; --id) {
                if (objects.get(id) == t) {
                    this.id = id;
                    return;
                }
            }
            objects.add(t);
        }
        this.id = size;
    }

    final public T apply$G() {
//        return value;
        synchronized (objects) {
            return (T) objects.get(this.id);
        }
    }

    final public x10.lang.Place home() {
        return this.home;
    }

    final public java.lang.String toString() {
//        return "struct x10.core.GlobalRef:" + " home=" + this.home + " it=" + this.value;
        return "struct x10.core.GlobalRef:" + " home=" + this.home + " id=" + this.id;
    }

    final public int hashCode() {
//        return 31 * this.home.hashCode() + System.identityHashCode(this.value); // this.value.hashCode();
        return (this.home.hashCode() << 18) + this.id;
    }

    final public boolean equals(java.lang.Object other) {
        return this._struct_equals(other);
    }

    final public boolean equals(x10.core.GlobalRef<T> other) {
        return this._struct_equals(other);
    }

    final public boolean _struct_equals(java.lang.Object other) {
        if (!x10.core.GlobalRef._RTT.instanceof$(other, T)) {
            return false;
        }
        return this._struct_equals((x10.core.GlobalRef<T>) other);
    }

    final public boolean _struct_equals(x10.core.GlobalRef<T> other) {
        return x10.rtt.Equality.equalsequals(this.home, other.home)
//                && x10.rtt.Equality.equalsequals(this.value, other.value);
                && x10.rtt.Equality.equalsequals(this.id, other.id);
    }

}
