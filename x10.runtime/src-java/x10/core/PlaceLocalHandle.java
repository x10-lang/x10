/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import x10.rtt.Type;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;


/**
 * Implementation of PlaceLocalHandle service for Java-based runtime.
 */
public final class PlaceLocalHandle<T> implements java.io.Serializable, X10JavaSerializable {

    private static final long serialVersionUID = 1L;
 
//    private static final HashMap<java.lang.Long,Object> data = new HashMap<java.lang.Long,Object>();
    private static final HashMap<Id,Object> data = new HashMap<Id,Object>();

//    private static final int placeShift = 48;
//    private static final AtomicLong lastId = new AtomicLong(0L);

    transient private boolean initialized = false;
    transient private Object myData = null;
//    private java.lang.Long id;
    
    /*
     * [PLH_GC] GlobalGC support for PlaceLocalHandle (kawatiya 2013/06)
     */
    public static final int PLH_DEBUG = java.lang.Integer.getInteger("PLH_DEBUG", 0); // 0:off, 1:on
    private x10.core.GlobalRef<Sentinel> gref; // Sentinel to monitor global deletion of this PLH
    
    // If GlobalRef's type is this class, Sentinel.cleanup(grefId) will be called back
    public static final class Sentinel {
        public static final x10.rtt.RuntimeType<Sentinel> $RTT =
                x10.rtt.NamedType.<Sentinel> make("x10.core.PlacelocalHandle.Sentinel", Sentinel.class);
        public x10.rtt.RuntimeType<?> $getRTT() { return $RTT; }
        public x10.rtt.Type<?> $getParam(int i) { return null; }
        public static void cleanup(long grefId) {
            if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: Sentinel.cleanup called at " + x10.lang.Runtime.home() + " for grefId=" + grefId);
            PlaceLocalHandle.deleteDataAtAllPlaces(grefId);
        }
    }
    // Set the gref field, this code is equivalent to "this.gref = GlobalRef(new Sentinel());"
    private void initSentinel() {
        Sentinel o = new Sentinel();
        this.gref = new x10.core.GlobalRef<Sentinel>(Sentinel.$RTT, o, (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T)null);
    }
    
    // A class used for the HashMap key, new Id(gref.
    private static final class Id {
        long id1, id2;
        public Id(long id1, long id2) { this.id1 = id1; this.id2 = id2; }
        @Override
        public String toString() { return "Id(" + id1 + "," + id2 + ")"; }
        @Override
        public int hashCode() { return (int)((id1>>32) ^ id1 ^ (id2>>32) ^ id2); }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Id)) return false;
            Id id = (Id)o;
            return ((id.id1==this.id1) && (id.id2==this.id2));
        }
    }
    // Create an Id object from the gref
    private Id getId() {
        return new Id(gref.home.id, gref.getId());
    }

//    // TODO: The X10 code currently ensures that PlaceLocalHandle's are only
//    //       created at Place 0 by doing an at.  We've contemplated moving to
//    //       more of a SVD style implementation where each place would be able to
//    //       create place local handles by either encoding the place in the id like we
//    //       did here or by having the places get ids in "chunks" from the master id server
//    //       at place 0. 
//    //       Since we are thinking about making this change, I went ahead and did a poor-man's
//    //       version of it here instead of asserting nextId is only called at place 0 
//    //       (which would have been true currently).
//    private static long nextId() {
//        long here = Thread.currentThread().home().id;
//        long newId  = lastId.incrementAndGet();
//        assert newId < (1L << placeShift);
//        newId |= (here << placeShift);
//        return newId;
//    }

    private Object readResolve() {
        initialized = false;
        return this;
    }

    // constructor just for allocation
    public PlaceLocalHandle(java.lang.System[] $dummy, Type<T> T) {
    }

    public final PlaceLocalHandle x10$core$PlaceLocalHandle$$init$S() {
//        id = nextId();
        initSentinel();
        if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: PlaceLocalHandle created at " + x10.lang.Runtime.home() + " -> " + getId());
        return this;
    }

    // not used
    //    public PlaceLocalHandle(Type<T> T) {
    //        id = nextId();
    //    }

    // zero value constructor
    public PlaceLocalHandle(Type<T> T, java.lang.System $dummy) {
//        id = nextId();
        initSentinel();
    }

    public T $apply$G() {
        if (!initialized) {
            Id id = getId();
            synchronized(data) {
                myData = data.get(id);
                initialized = true;
            }
        }
        return (T) myData;
    }

    public void set__0x10$lang$PlaceLocalHandle$$T(T value) {
        Id id = getId();
        synchronized(data) {
            if (null == value) {
                if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: local data removed explicitly for " + id + " at " + x10.lang.Runtime.home());
                data.remove(id);
            } else {
                if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: local data set for " + id + " at " + x10.lang.Runtime.home());
                data.put(id, value);
            }
        }
    }
    
    @Override
    public String toString() {
//        return "PlaceLocalHandle(" + this.id + ")";
        Id id = getId();
        return "PlaceLocalHandle(" + id + ")";
    }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
//        $serializer.write((long) id);
        $serializer.write(this.gref);
    }

    public static X10JavaSerializable $_deserialize_body(PlaceLocalHandle $_obj, X10JavaDeserializer $deserializer) throws IOException {
//        $_obj.id = (java.lang.Long) $deserializer.readLong();
        $_obj.gref = $deserializer.readRef();
        return $_obj;
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
        PlaceLocalHandle $_obj = new PlaceLocalHandle((java.lang.System[]) null, (Type<?>) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
    }

    /*
     *  [PLH_GC] Delete the data Id(hereId,grefId) at all places
     *  It is easier to write this logic in PlaceLocalHandle.x10, but it may affect C++ backend...
     */
    // Delete a PLH data at all places
    private static void deleteDataAtAllPlaces(long grefId) {
        long hereId = x10.lang.Runtime.home().id;
        long numPlaces = x10.lang.Place.numPlaces$O();
        for (long placeId = 0; placeId < numPlaces; placeId++) {
            if (placeId == hereId) {
                deleteLocalData(hereId, grefId);
            } else {
                x10.lang.Runtime.runAtSimple(new x10.lang.Place(placeId), new $Closure$0(hereId, grefId), false/*not-wait*/);
            }
        }
    }
    // Delete local data, this should be consistent with PlaceLocalHandle.set(T)
    private static void deleteLocalData(long id1, long id2) {
        Id id = new Id(id1, id2);
        if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: local data removed automatically for " + id + " at " + x10.lang.Runtime.home());
        synchronized(data) { data.remove(id); } // This should work if the data is already deleted explicitly.
    }
    // Closure for calling PlaceLocalHandle.deleteLocalData(id1, id2)
    private static class $Closure$0 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.serialization.X10JavaSerializable {
        private static final long serialVersionUID = 1L;
        public static final x10.rtt.RuntimeType<$Closure$0> $RTT = x10.rtt.StaticVoidFunType.<$Closure$0> make(
        /* base class */$Closure$0.class, /* parents */new x10.rtt.Type[] { x10.core.fun.VoidFun_0_0.$RTT });
        public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
        public Type<?> $getParam(int i) {return null;}

        private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
            if (x10.runtime.impl.java.Runtime.TRACE_SER) {
                java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling");
            }
            oos.defaultWriteObject();
        }
        public static x10.serialization.X10JavaSerializable $_deserialize_body($Closure$0 $_obj, X10JavaDeserializer $deserializer) throws java.io.IOException {
            $_obj.id1 = $deserializer.readLong();
            $_obj.id2 = $deserializer.readLong();
            return $_obj;
        }
        public static x10.serialization.X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws java.io.IOException {
            $Closure$0 $_obj = new $Closure$0((java.lang.System[]) null);
            $deserializer.record_reference($_obj);
            return $_deserialize_body($_obj, $deserializer);
        }
        public void $_serialize(X10JavaSerializer $serializer) throws java.io.IOException {
            $serializer.write(this.id1);
            $serializer.write(this.id2);
        }
        public $Closure$0(final java.lang.System[] $dummy) { super($dummy); }
        public void $apply() { PlaceLocalHandle.deleteLocalData(this.id1, this.id2); }

        public long id1, id2;
        public $Closure$0(final long id1, final long id2) { this.id1 = id1; this.id2 = id2; }
    }

}
