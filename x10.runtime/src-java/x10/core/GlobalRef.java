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
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import x10.lang.Runtime.Mortal;

public final class GlobalRef<T> extends x10.core.Struct implements
        Externalizable {

    public static final x10.rtt.RuntimeType<GlobalRef<?>> $RTT = new x10.rtt.NamedType<GlobalRef<?>>(
        "x10.lang.GlobalRef",
        GlobalRef.class,
        new x10.rtt.RuntimeType.Variance[] { x10.rtt.RuntimeType.Variance.INVARIANT },
        new x10.rtt.Type[] { x10.rtt.Types.STRUCT }
    );

    public x10.rtt.RuntimeType<GlobalRef<?>> $getRTT() {
        return $RTT;
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

    private static class WeakGlobalRefEntry extends WeakReference {
        long id;
        final int hashCode;

        public WeakGlobalRefEntry(long id, Object referent,
                ReferenceQueue<WeakGlobalRefEntry> referenceQueue) {
            super(referent, referenceQueue);
            hashCode = System.identityHashCode(referent);
        }

        public int hashCode() {
            return hashCode;
        }
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
            return ((GlobalRefEntry) obj).t == t;
        }
    }

    private static final ReferenceQueue<WeakGlobalRefEntry> referenceQueue = new ReferenceQueue<WeakGlobalRefEntry>();

    private static final GlobalRefEntry $nullEntry = new GlobalRefEntry($null);

    private static final GlobalRefEntry wrapObject(Object t) {
        if (t == $null)
            return $nullEntry;
        return new GlobalRefEntry(t);
    }

    private static AtomicLong lastId = new AtomicLong(0L);
    private static ConcurrentHashMap<Long, Object> id2Object = new ConcurrentHashMap<Long, Object>();
    private static ConcurrentHashMap<GlobalRefEntry, Long> object2Id = new ConcurrentHashMap<GlobalRefEntry, Long>();
    private static WeakHashMap<GlobalRefEntry, Long> mortal2Id = new WeakHashMap<GlobalRefEntry, Long>();

    private x10.rtt.Type<?> T;
    public x10.lang.Place home;
    private long id; // place local id of referenced object
    transient Object t;
    
    public GlobalRef(java.lang.System[] $dummy) {
        super($dummy);
    }

    public GlobalRef() {
        T = null;
        home = null;
        id = 0L;
        t = null;
    }

    public void $init() {
        T = null;
        home = null;
        id = 0L;
        t = null;        
    }

    public void $init(final x10.rtt.Type<?> T, T t, java.lang.Class<?> dummy$0) {
        this.T = T;
        this.home = x10.lang.Runtime.home();
        this.t = t;
    }

    public GlobalRef(final x10.rtt.Type<?> T, T t, java.lang.Class<?> dummy$0) {
        this.T = T;
        this.home = x10.lang.Runtime.home();
        this.t = t;
    }

    // zero value constructor
    public GlobalRef(final x10.rtt.Type<?> T, java.lang.System $dummy) {
    	this(T, null, (java.lang.Class<?>) null);
    }

    private static void poll() {
        WeakGlobalRefEntry weakRef = null;
        while ((weakRef = (WeakGlobalRefEntry) referenceQueue.poll()) != null) {
            id2Object.remove(weakRef.id);
        }
    }

    private void globalize() {
        if (isGlobalized())
            return;//allready allocated

        assert (T != null);
        assert (home != null);

        t = encodeNull(t);

        Long tmpId = lastId.incrementAndGet();

        if (t instanceof Mortal) {
            WeakGlobalRefEntry weakEntry = new WeakGlobalRefEntry(tmpId, t,
                    referenceQueue);
            id2Object.put(tmpId, weakEntry);
            synchronized (referenceQueue) {
                GlobalRefEntry entry = wrapObject(t);
                Long existingId = mortal2Id.get(entry);
                if (existingId != null) {
                    this.id = existingId;
                    mortal2Id.remove(tmpId);
                } else {
                    this.id = tmpId;
                    mortal2Id.put(entry, tmpId);
                }
                poll();
            }
        } else {
            id2Object.put(tmpId, t);//set id first.

            Long existingId = object2Id.putIfAbsent(wrapObject(t), tmpId);//set object second.
            if (existingId != null) {
                this.id = existingId;
                id2Object.remove(tmpId);
            } else {
                this.id = tmpId;
            }
        }
    }

    private boolean isGlobalized() {
        return id != 0L;
    }

    final public T $apply$G() {
        return (T) t;
        //return decodeNull((T) t);
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

        return this._struct_equals$O(otherGref);
    }

    final public boolean equals(x10.core.GlobalRef<T> other) {
        return this._struct_equals(other);
    }

    final public boolean _struct_equals$O(java.lang.Object other) {
        if (!x10.core.GlobalRef.$RTT.instanceof$(other, T)) {
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

        if (home.id == x10.lang.Runtime.home().id) {
            t = id2Object.get(id);
            if (t instanceof WeakGlobalRefEntry) {
                t = ((WeakGlobalRefEntry) t).get();
            }
            if (t == null) {
                throw new IllegalStateException(
                        "referenced object doesn't exist. id=" + id
                                + ", mortal="
                                + (t instanceof WeakGlobalRefEntry));
            }

            t = decodeNull(t);

        } else {
            t = null;
        }

        //System.out.println("GlobalRef is deserialized. home="
        //        + x10.lang.Runtime.home().id + ", ref.home=" + id + ", tgt="
        //        + t);
    }

}
