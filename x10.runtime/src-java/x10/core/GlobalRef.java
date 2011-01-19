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
        public java.lang.String typeName() {
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

    private static final java.util.ArrayList<Object> objects = new java.util.ArrayList<Object>(); // all referenced objects in this place
    final private int id; // place local id of referenced object

    public GlobalRef(final x10.rtt.Type<?> T, final T t, java.lang.Class<?> dummy$0) {
        this.T = T;
        this.home = x10.lang.Runtime.home();
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

    final public T $apply$G() {
        synchronized (objects) {
            return (T) objects.get(this.id);
        }
    }

    // this is not an api. only for implementing local assign in at body.
    final public T $set$G(T t) {
        synchronized (objects) {
            objects.set(this.id, t);
        }
        return t;
    }

    final public x10.lang.Place home() {
        return this.home;
    }

    final public java.lang.String toString() {
        return "GlobalRef(" + this.home + "," + this.id + ")";
    }

    final public int hashCode() {
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
                && x10.rtt.Equality.equalsequals(this.id, other.id);
    }

}
