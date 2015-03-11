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

import java.io.IOException;
import java.util.HashMap;

import x10.core.fun.VoidFun_0_0;
import x10.lang.DeadPlaceException;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.StaticVoidFunType;
import x10.rtt.Type;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;


/**
 * Implementation of PlaceLocalHandle service for Java-based runtime.
 */
public final class PlaceLocalHandle<T> implements X10JavaSerializable {

    private static final HashMap<Id,Object> data = new HashMap<Id,Object>();

    transient private boolean initialized = false;
    transient private Object myData = null;
    
    /*
     * [PLH_GC] GlobalGC support for PlaceLocalHandle (kawatiya 2013/06)
     */
    public static final int PLH_DEBUG = java.lang.Integer.getInteger("PLH_DEBUG", 0); // 0:off, 1:on
    private GlobalRef<Sentinel> gref; // Sentinel to monitor global deletion of this PLH
    
    // If GlobalRef's type is this class, Sentinel.cleanup(grefId) will be called back
    public static final class Sentinel {
        public static final RuntimeType<Sentinel> $RTT =
                NamedType.<Sentinel> make("$"/* to reduce serialization size*/, Sentinel.class);
//                NamedType.<Sentinel> make("x10.core.PlacelocalHandle.Sentinel", Sentinel.class);
        public RuntimeType<?> $getRTT() { return $RTT; }
        public Type<?> $getParam(int i) { return null; }
        public static void cleanup(long grefId) {
            if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: Sentinel.cleanup called at " + x10.xrx.Runtime.home() + " for grefId=" + grefId);
            PlaceLocalHandle.deleteDataAtAllPlaces(grefId);
        }
    }
    // Set the gref field, this code is equivalent to "this.gref = GlobalRef(new Sentinel());"
    private void initSentinel() {
        Sentinel o = new Sentinel();
        this.gref = new GlobalRef<Sentinel>(Sentinel.$RTT, o, (GlobalRef.__0x10$lang$GlobalRef$$T)null);
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

    // constructor just for allocation
    public PlaceLocalHandle(java.lang.System[] $dummy, Type<T> T) {
    }

    public final PlaceLocalHandle x10$core$PlaceLocalHandle$$init$S() {
        initSentinel();
        if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: PlaceLocalHandle created at " + x10.xrx.Runtime.home() + " -> " + getId());
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
                if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: local data removed explicitly for " + id + " at " + x10.xrx.Runtime.home());
                data.remove(id);
            } else {
                if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: local data set for " + id + " at " + x10.xrx.Runtime.home());
                data.put(id, value);
            }
        }
    }
    
    @Override
    public String toString() {
        Id id = getId();
        return "PlaceLocalHandle(" + id + ")";
    }

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public void $_serialize(X10JavaSerializer $serializer) throws IOException {
        $serializer.write(this.gref);
    }

    public static X10JavaSerializable $_deserialize_body(PlaceLocalHandle $_obj, X10JavaDeserializer $deserializer) throws IOException {
        $_obj.gref = $deserializer.readObject();
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
        long hereId = x10.xrx.Runtime.home().id;
        // TODO numPlaces or maxPlaces?
        long numPlaces = x10.lang.Place.numPlaces$O();
        for (long placeId = 0; placeId < numPlaces; placeId++) {
            if (placeId == hereId) {
                deleteLocalData(hereId, grefId);
            } else {
                try {
                    x10.xrx.Runtime.runAtSimple(new x10.lang.Place(placeId), new $Closure$0(hereId, grefId), false/*not-wait*/);
                } catch (DeadPlaceException e) {
                    if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: place=" + placeId + " is dead");
                }
            }
        }
    }
    // Delete local data, this should be consistent with PlaceLocalHandle.set(T)
    private static void deleteLocalData(long id1, long id2) {
        Id id = new Id(id1, id2);
        if (PLH_DEBUG>=1) System.err.println("PLH_DEBUG: local data removed automatically for " + id + " at " + x10.xrx.Runtime.home());
        synchronized(data) { data.remove(id); } // This should work if the data is already deleted explicitly.
    }
    // Closure for calling PlaceLocalHandle.deleteLocalData(id1, id2)
    private static class $Closure$0 extends Ref implements VoidFun_0_0, X10JavaSerializable {
        public static final RuntimeType<$Closure$0> $RTT = StaticVoidFunType.<$Closure$0> make(
        /* base class */$Closure$0.class, /* parents */new Type[] { VoidFun_0_0.$RTT });
        public RuntimeType<?> $getRTT() {return $RTT;}
        public Type<?> $getParam(int i) {return null;}

        public static X10JavaSerializable $_deserialize_body($Closure$0 $_obj, X10JavaDeserializer $deserializer) throws java.io.IOException {
            $_obj.id1 = $deserializer.readLong();
            $_obj.id2 = $deserializer.readLong();
            return $_obj;
        }
        public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws java.io.IOException {
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
