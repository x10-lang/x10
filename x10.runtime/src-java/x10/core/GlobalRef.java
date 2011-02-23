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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

public final class GlobalRef<T> extends x10.core.Struct implements
        Externalizable {

    public static final x10.rtt.RuntimeType<GlobalRef<?>> _RTT = new x10.rtt.RuntimeType<GlobalRef<?>>(
            GlobalRef.class,
            new x10.rtt.RuntimeType.Variance[] { x10.rtt.RuntimeType.Variance.INVARIANT }) {
        @Override
        public java.lang.String typeName() {
            return "x10.lang.GlobalRef";
        }
    };

    public x10.rtt.RuntimeType<GlobalRef<?>> $getRTT() {
        return _RTT;
    }

    public x10.rtt.Type<?> $getParam(int i) {
        if (i == 0)
            return T;
        return null;
    }

    // a singleton which represents null value
    private static final Object $null = new Object() {
        @Override
        public java.lang.String toString() {
            return "<null>";
        }
    };

    private static final <T> T encodeNull(T t) {
        if (t == null)
            t = (T) $null;
        return t;
    }

    private static final <T> T decodeNull(T t) {
        if (t == $null)
            t = null;
        return t;
    }

    private static class GlobalRefEntry {
        private final Object t;

        GlobalRefEntry(Object t) {
            this.t = t;
        }

        public int hashCode() {
            return System.identityHashCode(t);
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof GlobalRefEntry))
                return false;
            if (((GlobalRefEntry) obj).t == t)
                return true;
            // Note: GlobalRef does not refer structs
            //            if (Types.isStruct(((GlobalRefEntry) obj).t)
            //        		&& ((GlobalRefEntry) obj).t.equals(t))
            //            	return true;
            return false;
        }
    }

    private static final GlobalRefEntry $nullEntry = new GlobalRefEntry($null);

    private static final GlobalRefEntry wrapObject(Object t) {
        if (t == $null)
            return $nullEntry;
        return new GlobalRefEntry(t);
    }

    private static AtomicLong lastId = new AtomicLong(0L);
    private static ConcurrentHashMap<Long, Object> id2Object = new ConcurrentHashMap<Long, Object>();
    private static ConcurrentHashMap<GlobalRefEntry, Long> object2Id = new ConcurrentHashMap<GlobalRefEntry, Long>();

    private x10.rtt.Type<?> T;
    public x10.lang.Place home;
    private long id; // place local id of referenced object
    transient Object t;

    public GlobalRef() {
        T = null;
        home = null;
        id = 0L;
        t = null;
    }

    public GlobalRef(final x10.rtt.Type<?> T, T t, java.lang.Class<?> dummy$0) {
        this.T = T;
        this.t = t;
        this.home = x10.lang.Runtime.home();
    }

    private void globalize() {
        if (isGlobalized())
            return;//allready allocated

        assert (T != null);
        assert (home != null);
        assert (t != null);

        t = encodeNull(t);

        Long tmpId = lastId.incrementAndGet();

        id2Object.put(tmpId, t);//set id first.

        Long existingId = object2Id.putIfAbsent(wrapObject(t), tmpId);//set object second.
        if (existingId != null) {
            this.id = existingId;
            id2Object.remove(tmpId);
        } else {
            this.id = tmpId;
        }
    }

    private boolean isGlobalized() {
        return id != 0L;
    }

    final public T $apply$G() {
        return (T) t;
    }

    //this is not an api. only for implementing local assign in at body.
    final public T $set$G(T t) {
        this.t = t;
        id = 0L;
        return t;
    }

    final public x10.lang.Place home() {
        return this.home;
    }

    final public java.lang.String toString() {
        globalize();
        return "GlobalRef(" + this.home + "," + this.id + ")";
    }

    final public int hashCode() {
        globalize();
        return (this.home.hashCode() << 18) + (int) this.id;
    }

    final public boolean equals(java.lang.Object other) {
        if (!(other instanceof GlobalRef<?>))
            return false;

        GlobalRef<?> otherGref = (GlobalRef<?>) other;

        return this._struct_equals(otherGref);
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
        if (!other.isGlobalized() && !isGlobalized())
            return (t == null && ((GlobalRef<?>) other).t == null)
                    || ((GlobalRef<?>) other).t.equals(t);

        globalize();

        return x10.rtt.Equality.equalsequals(this.home, other.home)
                && x10.rtt.Equality.equalsequals(this.id, other.id);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        globalize();
        out.writeObject(T);
        out.writeObject(home);
        out.writeLong(id);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        T = (x10.rtt.Type<?>) in.readObject();
        home = (x10.lang.Place) in.readObject();
        id = in.readLong();
        if (home.id == x10.lang.Runtime.hereInt())
            t = id2Object.get(id);//TODO waek reference
        else
            t = null;
    }

}
