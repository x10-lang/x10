/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10.core;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import x10.core.fun.Fun_0_0;
import x10.core.fun.Fun_0_1;
import x10.core.fun.VoidFun_0_0;
import x10.lang.Place;
import x10.lang.Runtime.Mortal;
import x10.rtt.NamedType;
import x10.rtt.ParameterizedType;
import x10.rtt.RuntimeType;
import x10.rtt.StaticFunType;
import x10.rtt.StaticVoidFunType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.rtt.UnresolvedType;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

/*
 * [GlobalGC] Managed X10 (X10 on Java) supports distributed GC, which is mainly implemented here, in GlobalRef.java.
 *            See a paper "Distributed Garbage Collection for Managed X10" in ACM SIGPLAN 2012 X10 Workshop, June 2012.
 */
public final class GlobalRef<T> extends Struct implements X10JavaSerializable {
	
    public static final RuntimeType<GlobalRef<?>> $RTT = NamedType.<GlobalRef<?>> make(
        "x10.lang.GlobalRef",
        GlobalRef.class,
        1,
        new Type[] { Types.STRUCT }
    );
    public RuntimeType<GlobalRef<?>> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return i == 0 ? T : null; }

    /*
     * GlobalGC debug and support methods
     */
    public static final boolean GLOBALGC_DISABLE = java.lang.Boolean.getBoolean("GLOBALGC_DISABLE");
    public static final int GLOBALGC_WEIGHT = java.lang.Integer.getInteger("GLOBALGC_WEIGHT", 100); // initial weight for a remote ref
    public static final int GLOBALGC_DEBUG = java.lang.Integer.getInteger("GLOBALGC_DEBUG", 0); // 0:nothing, 1:important, 2:info, 3: all
    
    private static int inited = 0;
    { if (inited++ == 0) if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GLOBALGC_DISABLE=" + GLOBALGC_DISABLE + " GLOBALGC_WEIGHT=" + GLOBALGC_WEIGHT + " GLOBALGC_DEBUG=" + GLOBALGC_DEBUG); }
    private static final Object debugSync = new Object();
    private static void GlobalGCDebug(String msg) {
        //if (level > GLOBALGC_DEBUG) return; // XTENLANG-3168: debug level should be checked in caller
        long time = System.nanoTime();
        int placeId = (int)x10.lang.Runtime.home().id;
        String output = "[GlobalGC(time=" + time + ",place=" + placeId + ")] " + msg;
        synchronized (debugSync) {
            System.err.println(output); System.err.flush();
        }
    }
    
    private static class GlobalizedObjectTracker extends WeakReference<Object> {
        private static final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
        private static AtomicLong lastId = new AtomicLong(0L);
        private static AtomicLong lastMortalId = new AtomicLong(0L); // used for Mortal objects
        // isNull support (kawatiya 2013/09/09)
        private static final long NULL_ID_START = 8000000000000000000L;
        private static AtomicLong lastNullId = new AtomicLong(NULL_ID_START);
        
        private /*final*/ long id;  // unique id for the corresponding {T,localObj}
                                    // id==0 means not assigned, negative ids are assigned to Mortal objects
                                    // id>=NULL_ID_START is used for isNull (kawatiya 2013/09/09)
        private Object strongRef;   // strong reference to the localObj, used to prevent the collection
        private Type<?> T;          // T of corresponding GlobalRef[T](localObj)
        private int remoteCount;    // number of active remote GlobalRefs, should be < #places
        private final int hashCode; // pre-calculated hashCode
        
        // a singleton which represents null value
        private static final Object $null = new Object() {
            @Override
            public String toString() {
                return "<null>";
            }
        };
        private static final Object encodeNull(Object obj) {
            if (obj == null) return $null;
            return obj;
        }
        private static final Object decodeNull(Object obj) {
            if (obj == $null) return null;
            return obj;
        }
        
        private GlobalizedObjectTracker(Object obj, Type<?> T) {
            super(encodeNull(obj), referenceQueue);
            obj = encodeNull(obj); // if null is allowed for obj, we cannot distinguish the situation that weak reference is removed
            
            this.T = T;
            remoteCount = 0;
            if (GLOBALGC_DISABLE && !(obj instanceof Mortal))
                remoteCount = 1000; // never collect non-Mortal globalized objects
            strongRef = (remoteCount > 0) ? obj : null; // prevent the collection of the obj
            hashCode = System.identityHashCode(obj); // should include T?
            
            // assign an id
            boolean isMortal = obj instanceof Mortal;
            while (true) {
                if (obj == $null) { // isNull support (kawatiya 2013/09/09)
                    id = lastNullId.incrementAndGet();
                    if (id >= NULL_ID_START) {
                        // try to use the id
                    } else { // wraparound
                        if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GlobalizedObjectTracker.<init>: resetting lastNullId");
                        synchronized(lastNullId) { if (lastNullId.get() < NULL_ID_START) lastNullId.set(NULL_ID_START); }
                        continue; // retry
                    }                    
                } else if (!isMortal) {
                    id = lastId.incrementAndGet();
                    if (id < NULL_ID_START) {
                        // try to use the id
                    } else { // wraparound
                        if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GlobalizedObjectTracker.<init>: resetting lastId");
                        synchronized(lastId) { if (lastId.get() >= NULL_ID_START) lastId.set(0L); }
                        continue; // retry
                    }
                } else { // for Mortal objects, use negative id
                    id = lastMortalId.decrementAndGet();
                    if (id < 0) {
                        // try to use the id
                    } else { // wraparound
                        if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GlobalizedObjectTracker.<init>: resetting lastMortalId");
                        synchronized(lastMortalId) { if (lastMortalId.get() > 0) lastMortalId.set(0L); }
                        continue; // retry
                    }
                }
                assert(id != 0L);
                if (id2got.putIfAbsent(id, this) == null) break; // break if successfully put
                if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GlobalizedObjectTracker.<init>: id=" + id + " still exists, retry");
            }
            if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalizedObjectTracker.<init>(obj=" + obj + ", T=" + T + "), id=" + id);
        }
        
        @Override
        public int hashCode() {
            return hashCode;
        }
        
        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof GlobalizedObjectTracker)) return false;
            GlobalizedObjectTracker got = (GlobalizedObjectTracker)other;
            if (hashCode != got.hashCode) return false;
            if (!T.equals(got.T)) return false; // if T is different, return false even if obj is the same 
            Object obj = get();
            if (obj == null) return false; // weak reference is removed, this GlobalizedObjectTracker is dead and will be removed soon
            return obj == got.get(); // should use ==
        }
        
        private static ConcurrentHashMap<java.lang.Long, GlobalizedObjectTracker> id2got = new ConcurrentHashMap<java.lang.Long, GlobalizedObjectTracker>();
        private static ConcurrentHashMap<GlobalizedObjectTracker, java.lang.Long> got2id = new ConcurrentHashMap<GlobalizedObjectTracker, java.lang.Long>();
        
        private static long assignId(Object obj, Type<?> T) {
            cleanup(); // remove unused GlobalRefs first
            GlobalizedObjectTracker tmpGot = new GlobalizedObjectTracker(obj, T); // id2got.put was done in the constructor
            long tmpId = tmpGot.id;
            java.lang.Long existingId = got2id.putIfAbsent(tmpGot, tmpId);
            if (existingId != null) { // appropriate GlobalizedObjectTracker already exists
                id2got.remove(tmpId); // tmpGot will be collected by the next GC
                if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalizedObjectTracker.assignId: id=" + tmpId + " found for obj=" + obj + ", T=" + T);
                return existingId;
            } else {
                if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalizedObjectTracker.assignId: id=" + tmpId + " assigned for obj=" + obj + ", T=" + T + ", #GOT=" + got2id.size());
                return tmpId;
            }
        }
        
        private static Object getObject(long id) {
            cleanup();
            GlobalizedObjectTracker got = id2got.get(id);
            if (got == null) {
                if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GlobalizedObjectTracker.getObject: id=" + id + ", no GlobalizedObjectTracker for the id!");
                throw new IllegalStateException("No GlobalizedObjectTracker for id=" + id);
            }
            Object obj = got.get();
            if (obj == null) {
                if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GlobalizedObjectTracker.getObject: id=" + id + ", no object for the GlobalizedObjectTracker!");
                throw new IllegalStateException("GlobalizedObjectTracker for id=" + id + " has no object");
            }
            return decodeNull(obj);
        }
        
        private static void cleanup() { //TODO: this method is better to be called from GC (or periodically)
            GlobalizedObjectTracker got = null;
            while ((got = (GlobalizedObjectTracker)referenceQueue.poll()) != null) {
                assert(got.strongRef==null && got.id!=0L && got.get()==null);
                if (got.T.equals(PlaceLocalHandle.Sentinel.$RTT)) { // [PLH_GC] GlobalGC support for PlaceLocalHandle (kawatiya 2013/06)
                    PlaceLocalHandle.Sentinel.cleanup(got.id);
                    if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalizedObjectTracker.cleanup: id=" + got.id + " PLH deleted");
                }
                id2got.remove(got.id); // this may return null for tmpGot
                got2id.remove(got);    // this may return null for tmpGot
                if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalizedObjectTracker.cleanup: id=" + got.id + " removed, #GOT=" + got2id.size());
                // got will be collected by the next GC
            }
        }
        
        private static void changeRemoteCount(long id, int delta) {
            if (GLOBALGC_DISABLE) return;
            if (delta == 0) return;
            assert(id != 0L);
            
            cleanup();
            GlobalizedObjectTracker got = id2got.get(id);
            if (got == null) {
                if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GlobalizedObjectTracker.changeRemoteCount: id=" + id + ", no GlobalizedObjectTracker for the id!");
                throw new IllegalStateException("No GlobalizedObjectTracker for id=" + id);
            }
            
            if (id < 0) { // Mortal object
                if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalizedObjectTracker.changeRemoteCount: id=" + id + " is Mortal, remoteCount=" + got.remoteCount);
                assert(got.remoteCount == 0);
                return;
            }
            
            synchronized (got) {
                if (delta > 0) {
                    if (got.remoteCount == 0) {
                        Object obj = got.get();
                        if (obj == null) {
                            if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GlobalizedObjectTracker.changeRemoteCount: id=" + id + ", no object for the GlobalizedObjectTracker!");
                            throw new IllegalStateException("GlobalizedObjectTracker for id=" + id + " has no object");
                        }
                        got.strongRef = obj;
                    }
                    got.remoteCount += delta; // increment
                    if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalizedObjectTracker.changeRemoteCount: id=" + id + " incremented to remoteCount=" + got.remoteCount);
                } else if (delta < 0) {
                    assert(got.strongRef != null);
                    got.remoteCount += delta; // decrement
                    if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalizedObjectTracker.changeRemoteCount: id=" + id + " decremented to remoteCount=" + got.remoteCount);
                    assert(got.remoteCount >= 0);
                    if (got.remoteCount == 0) {
                        got.strongRef = null;
                    }
                }
            }
        }
    } // class GlobalizedObjectTracker
    
    private static class RemoteReferenceTracker extends WeakReference<GlobalRef<?>> {
        private static final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
        
        private final long id;
        private x10.lang.Place home;
        private int weightCount; // weight of this remote ref, should be >0
        private final int hashCode; // pre-calculated hashCode
        
        private RemoteReferenceTracker(GlobalRef<?> gr, int weight) {
            super(gr, referenceQueue); // gr is stored as weak reference
            id = gr.id;
            home = gr.home;
            weightCount = weight;
            hashCode = gr.hashCode();
        }
        
        synchronized private int changeWeightCount(int delta) {
            weightCount += delta;
            return weightCount;
        }
        
        @Override
        public int hashCode() {
            return hashCode;
        }
        
        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof RemoteReferenceTracker)) return false;
            RemoteReferenceTracker rrt = (RemoteReferenceTracker)other;
            if (hashCode != rrt.hashCode) return false;
            if (id != rrt.id) return false; 
            if (home.id != rrt.home.id) return false;
            return true; // two rrts contain the same remote GlobalRef (other should be temporary rrt and will be disposed)
        }
        
        private static ConcurrentHashMap<RemoteReferenceTracker, RemoteReferenceTracker> rrtTable = new ConcurrentHashMap<RemoteReferenceTracker, RemoteReferenceTracker>();
        
        private static GlobalRef<?> registerRemoteGlobalRef(GlobalRef<?> gr, int weight) {
            //if (GLOBALGC_DISABLE) return gr;
            // it is better to merge remote GlobalRefs even if GlobalGC is disabled, since same FinishState may be passed many times
            
            if (gr.id < 0) {
                return gr; // Do not register/track mortal references to avoid having to release the resources later
            }
            
            RemoteReferenceTracker rrt = new RemoteReferenceTracker(gr, weight);
            while (true) {
                cleanup();
                RemoteReferenceTracker existingRrt = rrtTable.putIfAbsent(rrt, rrt);
                if (existingRrt == null) { // appropriate RemoteReferenceTracker does not exist
                    if(GLOBALGC_DEBUG>=2)GlobalGCDebug("RemoteReferenceTracker.registerRemoteGlobalRef: id=" + rrt.id + " at place=" + rrt.home.id + " registered with weightCount=" + rrt.weightCount + ", #RRT=" + rrtTable.size());
                    //changeRemoteCount(rrt.home, rrt.id, +weight); // remoteCount was speculatively incremented (or divided) by sender
                    return gr;
                } else { // appropriate RemoteReferenceTracker already exists
                    GlobalRef<?> existingGr = existingRrt.get();
                    if (existingGr == null) continue; // racing condition, cleanup and retry
                    //changeRemoteCount(rrt.home, rrt.id, -weight); // decrement speculatively-incremented remoteCount
                    existingRrt.changeWeightCount(+weight); // merge the weight instead of decrement
                    if(GLOBALGC_DEBUG>=2)GlobalGCDebug("RemoteReferenceTracker.registerRemoteGlobalRef: id=" + rrt.id + " at place=" + rrt.home.id + " found with new weightCount=" + existingRrt.weightCount);
                    rrt.weightCount = 0; // this is unnecessary, but for fail-safe
                    return existingGr;
                }
            }
        }
        
        private static RemoteReferenceTracker get(GlobalRef<?> gr) { // get corresponding RemoteReferenceTracker for the remote GlobalRef
            if (gr.id < 0) return null; // do not track global refs of mortal objects
            assert(gr.home.id != x10.lang.Runtime.home().id);
            RemoteReferenceTracker rrt = new RemoteReferenceTracker(gr, 0);
            RemoteReferenceTracker existingRrt = rrtTable.get(rrt);
            assert(existingRrt != null);
            return existingRrt;
        }
        
        private static void cleanup() { //TODO: this method is better to be called from GC (or periodically)
            RemoteReferenceTracker rrt = null;
            while ((rrt = (RemoteReferenceTracker)referenceQueue.poll()) != null) {
                assert(rrt.get() == null);
                if (rrtTable.remove(rrt) != null) { // if correctly removed, call decrement
                    if(GLOBALGC_DEBUG>=2)GlobalGCDebug("RemoteReferenceTracker.cleanup: id=" + rrt.id + " at place=" + rrt.home.id + " removed with weightCount=" + rrt.weightCount + ", #RRT=" + rrtTable.size());
                    if (rrt.weightCount > 0) changeRemoteCount(rrt.home, rrt.id, -rrt.weightCount);
                }
                rrt.weightCount = 0; // this is unnecessary, but for fail-safe. rrt will be collected by the next GC
            }
        }
        
        private static void changeRemoteCount(Place place, long id, int delta) {
            if (GLOBALGC_DISABLE) return;
            if (delta == 0) return;
            assert(id != 0L);
            
            if (id < 0) { // Mortal object
                if(GLOBALGC_DEBUG>=2)GlobalGCDebug("RemoteReferenceTracker.changeRemoteCount: id=" + id + " at place=" + place.id + " delta=" + delta + ", Mortal -> do nothing");
                return;
            }
            if (place.id == x10.lang.Runtime.home().id) { // local GlobalRef
                if(GLOBALGC_DEBUG>=2)GlobalGCDebug("RemoteReferenceTracker.changeRemoteCount: id=" + id + " at place=" + place.id + " delta=" + delta + ", local -> adjust directly");
                GlobalizedObjectTracker.changeRemoteCount(id, delta);
            } else if (delta > 0) { // remote GlobalRef, increment
                if(GLOBALGC_DEBUG>=2)GlobalGCDebug("RemoteReferenceTracker.changeRemoteCount: id=" + id + " at place=" + place.id + " delta=" + delta + ", remote -> call runAt and wait");
                //x10.lang.Runtime.runAt(placeId, new $Closure$0(id, delta)/*should be KIND_NOT_ASYNC*/); // this does not work inside deserializer
                //x10.runtime.impl.java.Runtime.runClosureAt(placeId, new $Closure$0(id, delta)); // this does not wait for the execution
                x10.lang.Runtime.runAtSimple(place, new $Closure$0(id, delta), true/*wait*/); // special simplified version of runAt
            } else { // remote GlobalRef, decrement can be asynchronous
                if(GLOBALGC_DEBUG>=2)GlobalGCDebug("RemoteReferenceTracker.changeRemoteCount: id=" + id + " at place=" + place.id + " delta=" + delta + ", remote -> call runAt and not-wait");
                x10.lang.Runtime.runAtSimple(place, new $Closure$0(id, delta), false/*not-wait*/);
            }
        }
        
        // Closure for calling GlobalizedObjectTracker.changeRemoteCount(id, delta)
        // modified from the compiled code of "async at (place) { changeRemoteCount(id, delta); }"
        private /*@@public@@*/ static class $Closure$0 extends Ref implements VoidFun_0_0, X10JavaSerializable {

            public static final RuntimeType<$Closure$0> $RTT = StaticVoidFunType.<$Closure$0> make(
            /* base class */$Closure$0.class, /* parents */new Type[] { VoidFun_0_0.$RTT });

            public RuntimeType<?> $getRTT() {return $RTT;}
            public Type<?> $getParam(int i) {return null;}

            public static X10JavaSerializable $_deserialize_body($Closure$0 $_obj, X10JavaDeserializer $deserializer) throws java.io.IOException {
                if (x10.runtime.impl.java.Runtime.TRACE_SER) {
                    x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of "
                            + $Closure$0.class + " calling");
                }
                $_obj.id = $deserializer.readLong();
                $_obj.delta = $deserializer.readInt();
                return $_obj;
            }

            public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws java.io.IOException {
                $Closure$0 $_obj = new $Closure$0((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
            }

            public void $_serialize(X10JavaSerializer $serializer) throws java.io.IOException {
                $serializer.write(this.id);
                $serializer.write(this.delta);
            }

            // constructor just for allocation
            public $Closure$0(final java.lang.System[] $dummy) {
                super($dummy);
            }

            public void $apply() {
                //@@RunAtTest.changeRemoteCount((long) (this.id), (int) (this.delta));
                GlobalizedObjectTracker.changeRemoteCount((long) (this.id), (int) (this.delta));
            }

            public long id;
            public int delta;

            public $Closure$0(final long id, final int delta) {
                {
                    this.id = id;
                    this.delta = delta;
                }
            }

        }

    } // class RemoteReferenceTracker

    private Type<?> T;
    public long epoch;
    public x10.lang.Place home;
    private long id; // place local id of referenced object
    private transient Object obj;
    
    // constructor just for allocation
    public GlobalRef(java.lang.System[] $dummy) {
        // call default constructor instead of "constructor just for allocation" for x10.core.Struct
//        super($dummy);
        if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalRef.<init>($dummy) called");
    }

    public GlobalRef() {
        if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalRef.<init>() called");
        T = null;
        home = null;
        id = 0L;
        obj = null;
    }

    public final GlobalRef<T> x10$lang$GlobalRef$$init$S() {
        if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalRef.$init() called");
        T = null;
        home = null;
        id = 0L;
        obj = null;
        return this;
    }

    public final GlobalRef<T> x10$lang$GlobalRef$$init$S(final Type<?> T, T obj, __0x10$lang$GlobalRef$$T $dummy) {
        if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalRef.$init(T=" + T + ", obj=" + obj + ", $dummy) called, isMortal=" + (obj instanceof Mortal));
        this.T = T;
        x10.lang.Activity activity = x10.lang.Runtime.activity();
        this.epoch = activity == null ? x10.lang.Runtime.epoch$O() : activity.epoch;
        this.home = x10.lang.Runtime.home();
        this.obj = obj;
        return this;
    }

    public GlobalRef(final Type<?> T, T obj, __0x10$lang$GlobalRef$$T $dummy) {
        if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalRef.<init>(T=" + T + ", obj=" + obj + ", $dummy) called, isMortal=" + (obj instanceof Mortal));
        this.T = T;
        x10.lang.Activity activity = x10.lang.Runtime.activity();
        this.epoch = activity == null ? x10.lang.Runtime.epoch$O() : activity.epoch;
        this.home = x10.lang.Runtime.home();
        this.obj = obj;
    }
    // zero value constructor
    public GlobalRef(final Type<?> T, java.lang.System $dummy) {
        this(T, null, (__0x10$lang$GlobalRef$$T) null);
    }
    // synthetic type for parameter mangling
    public abstract static class __0x10$lang$GlobalRef$$T {}

    private void globalize() {
        if (isGlobalized()) return; // allready allocated
        assert (T != null);
        assert (home != null);
        id = GlobalizedObjectTracker.assignId(obj, T);
        if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalRef.globalize: id=" + id + " used for obj=" + obj + ", T=" + T);
    }

    private boolean isGlobalized() {
        return id != 0L;
    }

    final public T $apply$G() {
        return (T)obj; //TODO: should raise Exception if home!=here?
    }

    final public x10.lang.Place home() {
        return this.home;
    }

    final public long getId() { // [PLH_GC] Backdoor for PlaceLocalHandle ingegration (kawatiya 2013/06)
        globalize(); // necessary to decide the id for this object
        return this.id;
    }

    @Override
    final public String toString() {
        globalize(); // necessary to decide the id for this object
//      return "GlobalRef(" + this.home + "," + this.id + ")";
        // isNull support (2013/09/09)
        return "GlobalRef["+T+"](" + home + "," + id + "," + (isNull() ? "isNull" : "nonNull") + ")";
    }

    @Override
    final public int hashCode() {
        globalize(); // necessary to decide the id for this object
        return (this.home.hashCode() << 18) + (int) this.id;
    }

    @Override
    final public boolean equals(Object other) {
        if (!(other instanceof GlobalRef<?>))
            return false;

        GlobalRef<?> otherGref = (GlobalRef<?>) other;

        return this._struct_equals$O(otherGref);
    }

    final public boolean equals(GlobalRef<T> other) {
        return this._struct_equals(other);
    }

    final public boolean _struct_equals$O(Object other) {
        if (!GlobalRef.$RTT.isInstance(other, T)) {
            return false;
        }
        return this._struct_equals((GlobalRef<T>) other);
    }

    final public boolean _struct_equals(GlobalRef<T> other) {
        //if (T != other.T || home != other.home) return false;
        if (!T.equals(other.T) || home.id != other.home.id) return false;
        if (home.id == x10.lang.Runtime.home().id) { // local GlobalRefs
            return obj == other.obj;
        } else { // remote GlobalRefs
            return id == other.id;
        }
    }

    public boolean isNull() { // isNull support (kawatiya 2013/09/09)
        if (home.id == x10.lang.Runtime.home().id) { // local GlobalRef
            return obj == null;
        } else { // remote GlobalRefs
            return id >= GlobalizedObjectTracker.NULL_ID_START;
        }
    }

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
        globalize();
        int weight = adjustWeight();
        assert((id > 0 && weight > 0) || (id < 0 && weight == 0)); // weight should be > 0 for non-Mortal GlobalRef
        if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalRef.$_serialize: id=" + id + " at place=" + home.id + " serializing, weight=" + weight);
        //changeRemoteCount(+1); // speculatively increment the remoteCount to avoid racing
        $serializer.write(T);
        $serializer.write(home);
        $serializer.write(id);
        $serializer.write(epoch);
        $serializer.write(weight); // send the weight 
        
        $serializer.addToGrefMap(this, weight); // to adjust the weight when serialized data is used more than once
	}

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws java.io.IOException {
        GlobalRef $_obj = new GlobalRef();
        X10JavaSerializable returned = $_deserialize_body($_obj, $deserializer); // remote GlobalRefs are merged if they have same id
        return returned;
    }

    public static X10JavaSerializable $_deserialize_body(GlobalRef $_obj, X10JavaDeserializer $deserializer) throws IOException {
        $_obj.T = $deserializer.readObject();
        $_obj.home = $deserializer.readObject();
        long id = $deserializer.readLong();
        $_obj.id = id;
        $_obj.epoch = $deserializer.readLong();
        int weight = $deserializer.readInt(); // weight got from sender
        assert((id > 0 && weight > 0) || (id < 0 && weight == 0)); // weight should be > 0 for non-Mortal GlobalRef
        if ($_obj.home.id == x10.lang.Runtime.home().id) { // local GlobalRef
            $_obj.obj = GlobalizedObjectTracker.getObject(id);
            if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalRef.$_deserialize_body: id=" + $_obj.id + " deserialized, weight=" + weight + ", localObj=" + $_obj.obj);
            GlobalizedObjectTracker.changeRemoteCount($_obj.id, -weight); // decrement speculatively-incremented remoteCount, this must be done after the object is got
        } else { // remote GlobalRef
            $_obj.obj = null;
            if(GLOBALGC_DEBUG>=3)GlobalGCDebug("GlobalRef.$_deserialize_body: id=" + $_obj.id + " at place=" + $_obj.home.id + " deserialized, weight=" + weight);
            $_obj = RemoteReferenceTracker.registerRemoteGlobalRef($_obj, weight); // remoteCount may be merged
        }
        return $_obj;
    }
    
    private void changeRemoteCount(int delta) {
        globalize();
        if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalRef.changeRemoteCount: id=" + id + " at place=" + home.id + " delta=" + delta);
        RemoteReferenceTracker.changeRemoteCount(home, id, delta);
    }
    
    // Adjust speculative increment of remoteCounts of GlobalRefs (for the case that serialized data is used more than once)
    public static void adjustRemoteCountsInMap(java.util.Map<GlobalRef<?>,Integer> map, int multiply) {
        if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalRef.adjustRemoteCountsInMap: multiply=" + multiply);
        if (multiply == 0) return;
        for (GlobalRef<?> gr: map.keySet()) {
            int weight = map.get(gr);
            gr.changeRemoteCount(weight * multiply);
        }
    }
    
    private int adjustWeight() {
        if (id < 0) return 0; // No weight for Mortal GlobalRef
        if (home.id == x10.lang.Runtime.home().id) { // local GlobalRef
            int weight = GLOBALGC_WEIGHT;
            GlobalizedObjectTracker.changeRemoteCount(id, +weight); // add the initial weight
            if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalRef.adjustWeight(local): id=" + id + " at place=" + home.id + ", weight=" + weight);
            return weight;
        } else { // remote GlobalRef
            RemoteReferenceTracker rrt = RemoteReferenceTracker.get(this);
            if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalRef.adjustWeight(remote): id=" + id + " at place=" + home.id + ", weightCount=" + rrt.weightCount);
            assert(rrt.weightCount > 0);
            int weightCount, weight;
            while (true) {
                if (rrt.weightCount == 1) { // add weight to make it dividable
                    RemoteReferenceTracker.changeRemoteCount(home, id, +GLOBALGC_WEIGHT);
                    rrt.changeWeightCount(+GLOBALGC_WEIGHT);
                }
                synchronized (rrt) {
                    weightCount = rrt.weightCount;
                    if (weightCount < 2) { // someone stole my weight, retry the addition
                        if(GLOBALGC_DEBUG>=1)GlobalGCDebug("GlobalRef.adjustWeight(remote): retry addition\n");
                        continue;
                    }
                    weight = weightCount * 2 / 10; // send 20% of weightCount
                    // if we can know the target place, we could assign 1 if it is gref's home
                    if (weight == 0) weight = 1;
                    weightCount = rrt.changeWeightCount(-weight);
                    assert(weightCount > 0);
                    break;
                }
            }
            if(GLOBALGC_DEBUG>=2)GlobalGCDebug("GlobalRef.adjustWeight(remote): id=" + id + " at place=" + home.id + ", weight=" + weight + " remaining=" + weightCount);
            return weight;
        }
    }

    public static class LocalEval extends Ref {

	public static final RuntimeType<LocalEval> $RTT = NamedType.<LocalEval> make("x10.lang.GlobalRef.LocalEval", LocalEval.class);
	public RuntimeType<?> $getRTT() {return $RTT;}
	public Type<?> $getParam(int i) {return null;}
    
	// constructor just for allocation
	public LocalEval(final java.lang.System[] $dummy) { super($dummy);}
	
        public final LocalEval x10$lang$GlobalRef$LocalEval$$init$S() {return this;}
	
	// creation method for java code (1-phase java constructor)
        public LocalEval() {
            this((java.lang.System[]) null);
            x10$lang$GlobalRef$LocalEval$$init$S();
        }
        

	public static <$T, $U> $U evalAtHome(Type $T, Type $U, GlobalRef<$T> ref, Fun_0_1<$T,$U> eval) {
	    if (x10.lang.Runtime.home().id == ref.home.id) {
		return eval.$apply(ref.$apply$G(),$T);
	    } else {
                return x10.lang.Runtime.<$U>evalAt__1$1x10$lang$Runtime$$T$2$G($U, ref.home, new $Closure$Eval<$T, $U>($T, $U, ref, eval, (GlobalRef.LocalEval.$Closure$Eval.__0$1x10$lang$GlobalRef$LocalEval$$Closure$Eval$$T$2__1$1x10$lang$GlobalRef$LocalEval$$Closure$Eval$$T$3x10$lang$GlobalRef$LocalEval$$Closure$Eval$$U$2) null), null);
	    }
	}
        
        
	public static <$T> $T getLocalOrCopy(Type $T, GlobalRef<$T> ref) {
            if (x10.lang.Runtime.home().id == ref.home.id) {
		return ref.$apply$G();
	    } else {
                return x10.lang.Runtime.<$T>evalAt__1$1x10$lang$Runtime$$T$2$G($T, ref.home, new $Closure$Apply<$T>($T, ref, (GlobalRef.LocalEval.$Closure$Apply.__0$1x10$lang$GlobalRef$LocalEval$$Closure$Apply$$T$2) null), null);
	    }
	}

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
	}

	public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws java.io.IOException {
       LocalEval $_obj = new LocalEval((System []) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
	}

	public static X10JavaSerializable $_deserialize_body(LocalEval $_obj, X10JavaDeserializer $deserializer) throws IOException {
           return $_obj;
    }


	public static class $Closure$Eval<$T, $U> extends Ref implements Fun_0_0 {
	    public static final RuntimeType<$Closure$Eval> $RTT =
		StaticFunType.<$Closure$Eval> make($Closure$Eval.class, 2,
							 new Type[] {ParameterizedType.make(Fun_0_0.$RTT, UnresolvedType.PARAM(1))});
	    public RuntimeType<?> $getRTT() {return $RTT;}
	    public Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}

	    // constructor just for allocation
	    public $Closure$Eval(final java.lang.System[] $dummy) { super($dummy);}
            public $Closure$Eval(Type $T, Type $U, GlobalRef<$T> ref, Fun_0_1<$T,$U> eval, __0$1x10$lang$GlobalRef$LocalEval$$Closure$Eval$$T$2__1$1x10$lang$GlobalRef$LocalEval$$Closure$Eval$$T$3x10$lang$GlobalRef$LocalEval$$Closure$Eval$$U$2 $dummy) {
                this.$T = $T;
                this.$U = $U;
                this.ref = ref;
                this.eval = eval;
            }
            // synthetic type for parameter mangling
            public abstract static class __0$1x10$lang$GlobalRef$LocalEval$$Closure$Eval$$T$2__1$1x10$lang$GlobalRef$LocalEval$$Closure$Eval$$T$3x10$lang$GlobalRef$LocalEval$$Closure$Eval$$U$2 {}

	    private Type $T;
	    private Type $U;

	    public GlobalRef<$T> ref;
	    public Fun_0_1<$T,$U> eval;
                
	    public $U $apply$G() {
		return this.eval.$apply(this.ref.$apply$G(),$T);
	    }

        public void $_serialize(X10JavaSerializer $serializer) throws IOException {
            $serializer.write($T);
            $serializer.write($U);
            $serializer.write(ref);
            $serializer.write(eval);
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws java.io.IOException {
            $Closure$Eval $_obj = new $Closure$Eval((System[]) null);
            $deserializer.record_reference($_obj);
            return $_deserialize_body($_obj, $deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$Eval $_obj, X10JavaDeserializer $deserializer) throws IOException {
            $_obj.$T = $deserializer.readObject();
            $_obj.$U = $deserializer.readObject();
            $_obj.ref = $deserializer.readObject();
            $_obj.eval = $deserializer.readObject();
            return $_obj;
        }
	}

            
	public static class $Closure$Apply<$T> extends Ref implements Fun_0_0 {
	    public static final RuntimeType<$Closure$Apply> $RTT =
		StaticFunType.<$Closure$Apply> make($Closure$Apply.class, 1,
							  new Type[] {ParameterizedType.make(Fun_0_0.$RTT, UnresolvedType.PARAM(0))});
	    public RuntimeType<?> $getRTT() {return $RTT;}
	    public Type<?> $getParam(int i) {if (i ==0)return $T;return null;}

	    // constructor just for allocation
	    public $Closure$Apply(final java.lang.System[] $dummy) { super($dummy);}
            public $Closure$Apply(Type $T, GlobalRef<$T> ref, __0$1x10$lang$GlobalRef$LocalEval$$Closure$Apply$$T$2 $dummy) {
                this.$T = $T;
                this.ref = ref;
            }
	    // synthetic type for parameter mangling
	    public abstract static class __0$1x10$lang$GlobalRef$LocalEval$$Closure$Apply$$T$2 {}
	
	    private Type $T;

	    public GlobalRef<$T> ref;

	    public $T $apply$G() {
		return this.ref.$apply$G();
	    }

        public void $_serialize(X10JavaSerializer $serializer) throws IOException {
            $serializer.write($T);
            $serializer.write(ref);
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws java.io.IOException {
            $Closure$Apply $_obj = new $Closure$Apply((System[]) null);
            $deserializer.record_reference($_obj);
            return $_deserialize_body($_obj, $deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$Apply $_obj, X10JavaDeserializer $deserializer) throws IOException {
            $_obj.$T = $deserializer.readObject();
            $_obj.ref = $deserializer.readObject();
            return $_obj;
        }
	}
    }
}
