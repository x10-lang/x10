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

package x10.lang;

import x10.compiler.*;

import x10.util.GrowableRail;
import x10.util.HashMap;
import x10.util.Pair;

import x10.util.concurrent.AtomicInteger;
import x10.util.concurrent.AtomicBoolean;
import x10.util.concurrent.Lock;
import x10.util.concurrent.SimpleLatch;
import x10.util.ArrayList;

import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;

abstract class FinishState {

    // Turn this on to debug deadlocks within the finish implementation
    static VERBOSE = true;

    abstract def notifySubActivitySpawn(place:Place):void;
    abstract def notifyActivityCreation(srcPlace:Place):Boolean;
    abstract def notifyActivityTermination():void;
    abstract def pushException(t:Exception):void;
    abstract def waitForFinish():void;
    abstract def simpleLatch():SimpleLatch;
    abstract def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void;
    abstract def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any;

    static def deref[T](root:GlobalRef[FinishState]) = (root as GlobalRef[FinishState]{home==here})() as T;

    // a finish with local asyncs only
    static class LocalFinish extends FinishState {
        @Embed private val count = @Embed new AtomicInteger(1n);
        @Embed private val latch = @Embed new SimpleLatch();
        private var exceptions:GrowableRail[Exception]; // lazily initialized
        public def notifySubActivitySpawn(place:Place) {
            assert place.id == Runtime.hereLong();
            count.getAndIncrement();
        }
        public def notifyActivityCreation(srcPlace:Place) = true;
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0n) latch.release();
        }
        public def pushException(t:Exception) {
            latch.lock();
            if (null == exceptions) exceptions = new GrowableRail[Exception]();
            exceptions.add(t);
            latch.unlock();
        }
        public def waitForFinish() {
            notifyActivityTermination();
            if (!Runtime.STRICT_FINISH) Runtime.worker().join(latch);
            latch.await();
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
        }
        public def simpleLatch() = latch;
        public def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void {
            Runtime.runAtNonResilient(place, body, prof);
        }
        public def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any {
            return Runtime.evalAtNonResilient(place, body, prof);
        }
    }

    // a finish without nested remote asyncs in remote asyncs
    static class FinishSPMD extends FinishSkeleton implements CustomSerialization {
        def this() {
            super(new RootFinishSPMD());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(ds:Deserializer) {
            super(ds.readAny() as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereLong()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = new RemoteFinishSPMD(ref);
            }
        }
    }

    static class RootFinishSPMD extends RootFinishSkeleton {
        @Embed protected val latch = @Embed new SimpleLatch();
        @Embed private val count = @Embed new AtomicInteger(1n);
        private var exceptions:GrowableRail[Exception]; // lazily initialized
        public def notifySubActivitySpawn(place:Place) {
            count.incrementAndGet();
        }
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0n) latch.release();
        }
        public def pushException(t:Exception) {
            latch.lock();
            if (null == exceptions) exceptions = new GrowableRail[Exception]();
            exceptions.add(t);
            latch.unlock();
        }
        public def waitForFinish() {
            notifyActivityTermination();
            if ((!Runtime.STRICT_FINISH) && Runtime.STATIC_THREADS) Runtime.worker().join(latch);
            latch.await();
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
        }
        public def simpleLatch() = latch;
    }

    static class RemoteFinishSPMD extends RemoteFinishSkeleton {
        @Embed private val count = @Embed new AtomicInteger(1n);
        private var exceptions:GrowableRail[Exception]; // lazily initialized
        @Embed private val lock = @Embed new Lock();
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifySubActivitySpawn(place:Place) {
            assert place.id == Runtime.hereLong();
            count.getAndIncrement();
        }
        public def notifyActivityCreation(srcPlace:Place) = true;
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0n) {
                val t = MultipleExceptions.make(exceptions);
                val ref = this.ref();
                val closure:()=>void;
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_1") {
                        deref[FinishState](ref).pushException(t);
                        deref[FinishState](ref).notifyActivityTermination();
                    };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_2") {
                        deref[FinishState](ref).notifyActivityTermination();
                    };
                }
                Runtime.x10rtSendMessage(ref.home.id, closure, null);
                Unsafe.dealloc(closure);
            }
        }
        public def pushException(t:Exception) {
            lock.lock();
            if (null == exceptions) exceptions = new GrowableRail[Exception]();
            exceptions.add(t);
            lock.unlock();
        }
    }

    // a finish guarding a unique async
    static class FinishAsync extends FinishSkeleton implements CustomSerialization {
        def this() {
            super(new RootFinishAsync());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(ds:Deserializer) {
            super(ds.readAny() as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereLong()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = new RemoteFinishAsync(ref);
            }
        }
    }

    static class RootFinishAsync extends RootFinishSkeleton{
        @Embed protected val latch = @Embed new SimpleLatch();
        protected var exception:Exception = null;
        public def notifySubActivitySpawn(place:Place):void {}
        public def notifyActivityTermination():void {
            latch.release();
        }
        public def pushException(t:Exception):void {
            exception = t;
        }
        public def waitForFinish():void {
            latch.await();
            val t = MultipleExceptions.make(exception);
            if (null != t) throw t;
        }
        public def simpleLatch() = latch;
    }

    static class RemoteFinishAsync extends RemoteFinishSkeleton {
        protected var exception:Exception;
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifyActivityCreation(srcPlace:Place) = true;
        public def notifySubActivitySpawn(place:Place):void {}
        public def pushException(t:Exception):void {
            exception = t;
        }
        public def notifyActivityTermination():void {
            val t = MultipleExceptions.make(exception);
            val ref = this.ref();
            val closure:()=>void;
            if (null != t) {
                closure = ()=>@RemoteInvocation("notifyActivityTermination_1") {
                    deref[FinishState](ref).pushException(t);
                    deref[FinishState](ref).notifyActivityTermination();
                };
            } else {
                closure = ()=>@RemoteInvocation("notifyActivityTermination_2") {
                    deref[FinishState](ref).notifyActivityTermination();
                };
            }
            Runtime.x10rtSendMessage(ref.home.id, closure, null);
            Unsafe.dealloc(closure);
        }
    }

    // a finish ignoring remote events
    static class FinishHere extends FinishSkeleton implements CustomSerialization {
        def this() {
            super(new RootFinishSPMD());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(ds:Deserializer) { 
            super(ds.readAny() as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereLong()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = UNCOUNTED_FINISH;
            }
        }
    }

    // a pseudo finish used to implement @Uncounted async
    static class UncountedFinish extends FinishState {
        public def notifySubActivitySpawn(place:Place) {}
        public def notifyActivityCreation(srcPlace:Place) = true; 
        public def notifyActivityTermination() {}
        public def pushException(t:Exception) {
            Runtime.println("Uncaught exception in uncounted activity");
            t.printStackTrace();
        }
        public final def waitForFinish() { assert false; }
        public def simpleLatch():SimpleLatch = null;
        public def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void {
            Runtime.runAtNonResilient(place, body, prof);
        }
        public def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any {
            return Runtime.evalAtNonResilient(place, body, prof);
        }
    }
    
    static UNCOUNTED_FINISH = new UncountedFinish();

    // a mapping from finish refs to local finish objects
    static class FinishStates {
        private val map = new HashMap[GlobalRef[FinishState],FinishState]();
        @Embed private val lock = @Embed new Lock();

        // find or make the local finish for the finish ref
        public operator this(root:GlobalRef[FinishState], factory:()=>FinishState):FinishState{
            lock.lock();
            var f:FinishState = map.getOrElse(root, null);
            if (null != f) {
                lock.unlock();
                return f;
            }
            f = factory();
            map.put(root, f);
            lock.unlock();
            return f;
        }

        // remove finish ref from table
        public def remove(root:GlobalRef[FinishState]) {
            lock.lock();
            map.remove(root);
            lock.unlock();
        }
    }

    // the top of the root finish hierarchy
    abstract static class RootFinishSkeleton extends FinishState implements Runtime.Mortal {
        private val xxxx = GlobalRef[FinishState](this);
        def ref() = xxxx;
        public def notifyActivityCreation(srcPlace:Place) = true;
        public def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void {
            Runtime.runAtNonResilient(place, body, prof);
        }
        public def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any {
            return Runtime.evalAtNonResilient(place, body, prof);
        }
    }

    // the top of the remote finish hierarchy
    abstract static class RemoteFinishSkeleton extends FinishState {
        private val xxxx:GlobalRef[FinishState];
        def this(root:GlobalRef[FinishState]) {
            xxxx = root;
        }
        def ref() = xxxx;
        public def waitForFinish() { assert false; }
        public def simpleLatch():SimpleLatch = null;
        public def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void {
            Runtime.runAtNonResilient(place, body, prof);
        }
        public def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any {
            return Runtime.evalAtNonResilient(place, body, prof);
        }
    }

    // the top of the finish hierarchy
    abstract static class FinishSkeleton(ref:GlobalRef[FinishState]) extends FinishState {
        protected transient var me:FinishState; // local finish object
        protected def this(root:RootFinishSkeleton) {
            property(root.ref());
            me = root;
        }
        protected def this(ref:GlobalRef[FinishState]) {
            property(ref);
            me = null;
        }
        public def serialize(s:Serializer) {
            s.writeAny(ref);
        }
        public def notifySubActivitySpawn(place:Place) { me.notifySubActivitySpawn(place); }
        public def notifyActivityCreation(srcPlace:Place) { return me.notifyActivityCreation(srcPlace); }
        public def notifyActivityTermination() { me.notifyActivityTermination(); }
        public def pushException(t:Exception) { me.pushException(t); }
        public def waitForFinish() { me.waitForFinish(); }
        public def simpleLatch() = me.simpleLatch();
        public def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void {
            Runtime.runAtNonResilient(place, body, prof);
        }
        public def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any {
            return Runtime.evalAtNonResilient(place, body, prof);
        }
    }

    // the default finish implementation
    static class Finish extends FinishSkeleton implements CustomSerialization {
        protected def this(root:RootFinish) {
            super(root);
        }
        def this(latch:SimpleLatch) {
            this(new RootFinish(latch));
        }
        def this() {
            this(new RootFinish());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(ds:Deserializer) { 
            super(ds.readAny() as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereLong()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                val _ref = ref;
                me = Runtime.finishStates(ref, ()=>new RemoteFinish(_ref));
            }
        }
    }

    static class RootFinish extends RootFinishSkeleton {
        @Embed protected transient var latch:SimpleLatch;
        protected var count:Int = 1n;
        protected var exceptions:GrowableRail[Exception]; // lazily initialized
        protected var counts:Rail[Int];
        protected var seen:Rail[Boolean];
        def this() {
            latch = @Embed new SimpleLatch();
        }
        def this(latch:SimpleLatch) {
            this.latch = latch;
        }
        public def notifySubActivitySpawn(place:Place):void {
            val p = place.parent(); // CUDA
            latch.lock();
            if (p == ref().home) {
                count++;
                latch.unlock();
                return;
            }
            if (counts == null || counts.size == 0L) {
                counts = new Rail[Int](Place.MAX_PLACES);
                seen = new Rail[Boolean](Place.MAX_PLACES);
            }
            counts(p.id)++;
            latch.unlock();
        }
        public def notifyActivityTermination():void {
            latch.lock();
            if (--count != 0n) {
                latch.unlock();
                return;
            }
            if (counts != null && counts.size != 0L) {
                for(var i:Int=0n; i<Place.MAX_PLACES; i++) {
                    if (counts(i) != 0n) {
                        latch.unlock();
                        return;
                    }
                }
            }
            latch.unlock();
            latch.release();
        }
        public def process(t:Exception):void {
            if (null == exceptions) exceptions = new GrowableRail[Exception]();
            exceptions.add(t);
        }
        public def pushException(t:Exception):void {
            latch.lock();
            process(t);
            latch.unlock();
        }
        public def waitForFinish():void {
            notifyActivityTermination();
            if ((!Runtime.STRICT_FINISH) && (Runtime.STATIC_THREADS || (counts == null || counts.size == 0L))) {
                Runtime.worker().join(latch);
            }
            latch.await();
            if (counts != null && counts.size != 0L) {
                if (Place.MAX_PLACES < 1024) {
                    val root = ref();
                    val closure = ()=>@RemoteInvocation("remoteFinishCleanup") { Runtime.finishStates.remove(root); };
                    seen(Runtime.hereInt()) = false;
                    for(var i:Int=0n; i<Place.MAX_PLACES; i++) {
                        if (seen(i)) Runtime.x10rtSendMessage(i, closure, null);
                    }
                    Unsafe.dealloc(closure);
                } else {
                    // TODO: cleanup with indirect routing
                }
            }
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
        }

        protected def process(rail:Rail[Int]) {
            counts(ref().home.id) = -rail(ref().home.id);
            count += rail(ref().home.id);
            var b:Boolean = count == 0n;
            for(var i:Long=0; i<Place.MAX_PLACES; i++) {
                counts(i) += rail(i);
                seen(i) |= counts(i) != 0n;
                if (counts(i) != 0n) b = false;
            }
            if (b) latch.release();
        }

        def notify(rail:Rail[Int]):void {
            latch.lock();
            process(rail);
            latch.unlock();
        }

        protected def process(rail:Rail[Pair[Int,Int]]):void {
            for(var i:long=0; i<rail.size; i++) {
                counts(rail(i).first as long) += rail(i).second;
                seen(rail(i).first) = true;
            }
            count += counts(ref().home.id);
            counts(ref().home.id) = 0n;
            if (count != 0n) return;
            for(var i:Int=0n; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0n) return;
            }
            latch.release();
        }

        def notify(rail:Rail[Pair[Int,Int]]):void {
            latch.lock();
            process(rail);
            latch.unlock();
        }

        def notify(rail:Rail[Int], t:Exception):void {
            latch.lock();
            process(t);
            process(rail);
            latch.unlock();
        }

        def notify(rail:Rail[Pair[Int,Int]], t:Exception):void {
            latch.lock();
            process(t);
            process(rail);
            latch.unlock();
        }

        public def simpleLatch() = latch;
    }

    static class RemoteFinish extends RemoteFinishSkeleton {
        protected var exceptions:GrowableRail[Exception];
        @Embed protected transient var lock:Lock = @Embed new Lock();
        protected var count:Int = 0n;
        protected var counts:Rail[Int];
        protected var places:Rail[Int];
        protected var length:Int = 1n;
        @Embed protected val local = @Embed new AtomicInteger(0n);
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifyActivityCreation(srcPlace:Place):Boolean {
            local.getAndIncrement();
            return true;
        }
        public def notifySubActivitySpawn(place:Place):void {
            val id = Runtime.hereLong();
            lock.lock();
            if (place.id == id) {
                count++;
                lock.unlock();
                return;
            }
            if (counts == null || counts.size == 0L) {
                counts = new Rail[Int](Place.MAX_PLACES);
                places = new Rail[Int](Place.MAX_PLACES);
                places(0) = id as int; // WARNING: assuming 32 bit places at X10 level.
            }
            val old = counts(place.id);
            counts(place.id)++;
            if (old == 0n && id != place.id) {
                places(length++) = place.id as int; // WARNING: assuming 32 bit places at X10 level.
            }
            lock.unlock();
        }
        public def pushException(t:Exception):void {
            lock.lock();
            if (null == exceptions) exceptions = new GrowableRail[Exception]();
            exceptions.add(t);
            lock.unlock();
        }
        public def notifyActivityTermination():void {
            lock.lock();
            count--;
            if (local.decrementAndGet() > 0) {
                lock.unlock();
                return;
            }
            val t = MultipleExceptions.make(exceptions);
            val ref = this.ref();
            val closure:()=>void;
            if (counts != null && counts.size != 0L) {
                counts(Runtime.hereLong()) = count;
                if (2*length > Place.MAX_PLACES) {
                    val message = Unsafe.allocRailUninitialized[Int](counts.size);
                    Rail.copy(counts, 0L, message, 0L, counts.size);
                    if (null != t) {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_1") { deref[RootFinish](ref).notify(message, t); };
                    } else {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_2") { deref[RootFinish](ref).notify(message); };
                    }
                } else {
                    val message = Unsafe.allocRailUninitialized[Pair[Int,Int]](length);
                    for (i in 0..(length-1)) {
                        message(i) = Pair[Int,Int](places(i), counts(places(i)));
                    }
                    if (null != t) {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_3") { deref[RootFinish](ref).notify(message, t); };
                    } else {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_4") { deref[RootFinish](ref).notify(message); };
                    }
                }
                counts.clear(0, counts.size);
                length = 1n;
            } else {
                val message = Unsafe.allocRailUninitialized[Pair[Int,Int]](1);
                message(0) = Pair[Int,Int](Runtime.hereInt(), count);
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_5") { deref[RootFinish](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_6") { deref[RootFinish](ref).notify(message); };
                }
            }
            count = 0n;
            exceptions = null;
            lock.unlock();
            Runtime.x10rtSendMessage(ref.home.id, closure, null);
            Unsafe.dealloc(closure);
            Runtime.finishStates.remove(ref);
        }
    }

    static class DenseFinish extends FinishSkeleton implements CustomSerialization {
        protected def this(root:RootFinish) {
            super(root);
        }
        def this(latch:SimpleLatch) {
            this(new RootFinish(latch));
        }
        def this() {
            this(new RootFinish());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(ds:Deserializer) { 
            super(ds.readAny() as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereLong()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                val _ref = ref;
                me = Runtime.finishStates(ref, ()=>new DenseRemoteFinish(_ref));
            }
        }
    }

    static class DenseRemoteFinish extends RemoteFinishSkeleton {
        protected var exceptions:GrowableRail[Exception];
        @Embed protected transient var lock:Lock = @Embed new Lock();
        protected var count:Int = 0n;
        protected var counts:Rail[Int];
        protected var places:Rail[Int];
        protected var length:Int = 1n;
        @Embed protected val local = @Embed new AtomicInteger(0n);
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifyActivityCreation(srcPlace:Place):Boolean {
            local.getAndIncrement();
            return true;
        }
        public def notifySubActivitySpawn(place:Place):void {
            val id = Runtime.hereLong();
            lock.lock();
            if (place.id == id) {
                count++;
                lock.unlock();
                return;
            }
            if (counts == null || counts.size == 0L) {
                counts = new Rail[Int](Place.MAX_PLACES);
                places = new Rail[Int](Place.MAX_PLACES);
                places(0) = id as int; // WARNING: assuming 32 bit places at X10 level.
            }
            val old = counts(place.id);
            counts(place.id)++;
            if (old == 0n && id != place.id) {
                places(length++) = place.id as int; // WARNING: assuming 32 bit places at X10 level.
            }
            lock.unlock();
        }
        public def pushException(t:Exception):void {
            lock.lock();
            if (null == exceptions) exceptions = new GrowableRail[Exception]();
            exceptions.add(t);
            lock.unlock();
        }
        public def notifyActivityTermination():void {
            lock.lock();
            count--;
            if (local.decrementAndGet() > 0) {
                lock.unlock();
                return;
            }
            val t = MultipleExceptions.make(exceptions);
            val ref = this.ref();
            val closure:()=>void;
            if (counts != null && counts.size != 0L) {
                counts(Runtime.hereLong()) = count;
                if (2*length > Place.MAX_PLACES) {
                    val message = Unsafe.allocRailUninitialized[Int](counts.size);
                    Rail.copy(counts, 0L, message, 0L, counts.size);
                    if (null != t) {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_1") { deref[RootFinish](ref).notify(message, t); };
                    } else {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_2") { deref[RootFinish](ref).notify(message); };
                    }
                } else {
                    val message = Unsafe.allocRailUninitialized[Pair[Int,Int]](length);
                    for (i in 0..(length-1)) {
                        message(i) = Pair[Int,Int](places(i), counts(places(i)));
                    }
                    if (null != t) {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_3") { deref[RootFinish](ref).notify(message, t); };
                    } else {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_4") { deref[RootFinish](ref).notify(message); };
                    }
                }
                counts.clear(0, counts.size);
                length = 1n;
            } else {
                val message = Unsafe.allocRailUninitialized[Pair[Int,Int]](1);
                message(0) = Pair[Int,Int](Runtime.hereInt(), count);
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_5") { deref[RootFinish](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_6") { deref[RootFinish](ref).notify(message); };
                }
            }
            count = 0n;
            exceptions = null;
            lock.unlock();
            val h = Runtime.hereInt();
            if ((Place.MAX_PLACES < 1024) || (h%32n == 0n) || (h-h%32n == (ref.home.id as int))) {
                Runtime.x10rtSendMessage(ref.home.id, closure, null);
            } else {
                val clx = ()=>@RemoteInvocation("notifyActivityTermination_7") { Runtime.x10rtSendMessage(ref.home.id, closure, null); };
                Runtime.x10rtSendMessage(h-h%32, clx, null);
                Unsafe.dealloc(clx);
            }
            Unsafe.dealloc(closure);
//            Runtime.finishStates.remove(ref);
        }
    }

    static class StatefulReducer[T] {
        val reducer:Reducible[T];
        var result:T;
        var resultRail:Rail[T];
        var workerFlag:Rail[Boolean] = new Rail[Boolean](Runtime.MAX_THREADS);
        def this(r:Reducible[T]) {
            reducer = r;
            val zero = reducer.zero();
            result = zero;
            resultRail = Unsafe.allocRailUninitialized[T](Runtime.MAX_THREADS);
            for (i in 0L..(resultRail.size-1L)) {
                resultRail(i) = zero;
            }
        }
        def accept(t:T) {
            result = reducer(result, t);
        }
        def accept(t:T, id:Int) {
            if ((id >= 0) && (id < Runtime.MAX_THREADS)) {
                resultRail(id) = reducer(resultRail(id), t);
                workerFlag(id) = true;
            }
        }
        def placeMerge() {
            for(var i:Int=0n; i<Runtime.MAX_THREADS; i++) {
                if (workerFlag(i)) {
                    result = reducer(result, resultRail(i));
                    resultRail(i) = reducer.zero();
                }
            }
        }
        def result() = result;
        def reset() {
            result = reducer.zero();
        }
    }

    static interface CollectingFinishState[T] {
        def accept(t:T, id:Int):void;
    }

    static class CollectingFinish[T] extends Finish implements CollectingFinishState[T],CustomSerialization {
        val reducer:Reducible[T];
        def this(reducer:Reducible[T]) {
            super(new RootCollectingFinish(reducer));
            this.reducer = reducer;
        }
        private def this(ds:Deserializer) { 
            super(ds.readAny() as GlobalRef[FinishState]);
            val tmpReducer = ds.readAny() as Reducible[T];
            reducer = tmpReducer;
            if (ref.home.id == Runtime.hereLong()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                val _ref = ref;
                me = Runtime.finishStates(ref, ()=>new RemoteCollectingFinish[T](_ref, tmpReducer));
            }
        }
        public def serialize(s:Serializer) {
            s.writeAny(ref);
            s.writeAny(reducer);
        }
        public def accept(t:T, id:Int) { (me as CollectingFinishState[T]).accept(t, id); }   // Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
        public def waitForFinishExpr() = (me as RootCollectingFinish[T]).waitForFinishExpr();// Warning: This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.
    }

    static class RootCollectingFinish[T] extends RootFinish implements CollectingFinishState[T] {
        val sr:StatefulReducer[T];
        def this(reducer:Reducible[T]) {
           super();
           sr = new StatefulReducer[T](reducer);
        }
        public def accept(t:T, id:Int) {
           sr.accept(t, id);
        }
        def notifyValue(rail:Rail[Int], v:T):void {
            latch.lock();
            sr.accept(v);
            process(rail);
            latch.unlock();
        }
        def notifyValue(rail:Rail[Pair[Int,Int]], v:T):void {
            latch.lock();
            sr.accept(v);
            process(rail);
            latch.unlock();
        }
        final public def waitForFinishExpr():T {
            waitForFinish();
            sr.placeMerge();
            val result = sr.result();
            sr.reset();
            return result;
        }
    }

    static class RemoteCollectingFinish[T] extends RemoteFinish implements CollectingFinishState[T] {
        val sr:StatefulReducer[T];
        def this(ref:GlobalRef[FinishState], reducer:Reducible[T]) {
            super(ref);
            sr = new StatefulReducer[T](reducer);
        }
        public def accept(t:T, id:Int) {
            sr.accept(t, id);
        }
        public def notifyActivityTermination():void {
            lock.lock();
            count--;
            if (local.decrementAndGet() > 0) {
                lock.unlock();
                return;
            }
            val t = MultipleExceptions.make(exceptions);
            val ref = this.ref();
            val closure:()=>void;
            sr.placeMerge();
            val result = sr.result();
            sr.reset();
            if (counts != null && counts.size != 0L) {
                counts(Runtime.hereLong()) = count;
                if (2*length > Place.MAX_PLACES) {
                    val message = Unsafe.allocRailUninitialized[Int](counts.size);
                    Rail.copy(counts, 0L, message, 0L, counts.size);
                    if (null != t) {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_1") { deref[RootCollectingFinish[T]](ref).notify(message, t); };
                    } else {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_2") { deref[RootCollectingFinish[T]](ref).notifyValue(message, result); };
                    }
                } else {
                    val message = Unsafe.allocRailUninitialized[Pair[Int,Int]](length);
                    for (i in 0..(length-1)) {
                        message(i) = Pair[Int,Int](places(i), counts(places(i)));
                    }
                    if (null != t) {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_3") { deref[RootCollectingFinish[T]](ref).notify(message, t); };
                    } else {
                        closure = ()=>@RemoteInvocation("notifyActivityTermination_4") { deref[RootCollectingFinish[T]](ref).notifyValue(message, result); };
                    }
                }
                counts.clear(0, counts.size);
                length = 1n;
            } else {
                val message = Unsafe.allocRailUninitialized[Pair[Int,Int]](1);
                message(0) = Pair[Int,Int](Runtime.hereInt(), count);
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_5") { deref[RootCollectingFinish[T]](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_6") { deref[RootCollectingFinish[T]](ref).notifyValue(message, result); };
                }
            }
            count = 0n;
            exceptions = null;
            lock.unlock();
            Runtime.x10rtSendMessage(ref.home.id, closure, null);
            Unsafe.dealloc(closure);
            Runtime.finishStates.remove(ref);
        }
    }

    static final class FinishResilientPlaceZero(id:Long) extends FinishState {
        private static def parentFinish() : Long {
            val a = Runtime.activity();
            if (a == null) return -1; // creating the main activity (root finish)
            val par = a.finishState();
            if (par instanceof FinishResilientPlaceZero) return (par as FinishResilientPlaceZero).id;
            return -1l;
        }
        def this(parent:Long, latch:SimpleLatch) {
            property(ResilientStorePlaceZero.make(here.id, parent, latch));
            assert latch==null || here.id == 0l;
        }
        def this(latch:SimpleLatch) {
            property(ResilientStorePlaceZero.make(here.id, parentFinish(), latch));
            assert latch==null || here.id == 0l;
        }
        def notifySubActivitySpawn(place:Place) {
            val srcId = here.id;
            val dstId = place.id;
            ResilientStorePlaceZero.notifySubActivitySpawn(id, srcId, dstId);
        }
        def notifyActivityCreation(srcPlace:Place) {
            val srcId = srcPlace.id;
            val dstId = here.id;
            return ResilientStorePlaceZero.notifyActivityCreation(id, srcId, dstId);
        }
        def notifyActivityTermination() {
            val dstId = here.id;
            ResilientStorePlaceZero.notifyActivityTermination(id, dstId);
        }
        def pushException(t:Exception) {
            ResilientStorePlaceZero.pushException(id, t);
        }
        def waitForFinish() {
            ResilientStorePlaceZero.waitForFinish(id);
        }
        def simpleLatch():SimpleLatch = null;
        public def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void {
            Runtime.ensureNotInAtomic();
            if (place.id == Runtime.hereLong()) {
                // local path can be the same as before
                Runtime.runAtNonResilient(place, body, prof);
                return;
            }
                
            val real_finish = this;
            //real_finish.notifySubActivitySpawn(place);

            val tmp_finish = new FinishResilientPlaceZero(id, null);
            // TODO: clockPhases -- clocks not supported in resilient X10 at the moment
            // TODO: This implementation of runAt does not explicitly dealloc things
            val home = here;
            tmp_finish.notifySubActivitySpawn(place);

            // [DC] do not use at (place) async since the finish state is handled internally
            // [DC] go to the lower level...
            val cl = () => @x10.compiler.RemoteInvocation("resilient_place_zero_run_at") {
                val exc_body = () => {
                    if (tmp_finish.notifyActivityCreation(home)) {
                        try {
                            try {
                                body();
                            } catch (e:Runtime.AtCheckedWrapper) {
                                throw e.getCheckedCause();
                            } 
                        } catch (t:CheckedThrowable) {
                            val e = Exception.ensureException(t);
                            tmp_finish.pushException(e);
                        }
                        tmp_finish.notifyActivityTermination();
                    }
                };
                Runtime.execute(new Activity(exc_body, home, real_finish, false, false)); 
            };
            Runtime.x10rtSendMessage(place.id, cl, prof);

            try {
                if (VERBOSE) Runtime.println("Entering resilient at waitForFinish");
                tmp_finish.waitForFinish();
                if (VERBOSE) Runtime.println("Exiting resilient at waitForFinish");
            } catch (e:MultipleExceptions) {
                assert e.exceptions.size == 1l : e.exceptions();
                val e2 = e.exceptions(0);
                if (VERBOSE) Runtime.println("Received from resilient at: "+e2);
                if (e2 instanceof WrappedThrowable) {
                    Runtime.throwCheckedWithoutThrows(e2.getCause());
                } else {
                    throw e2;
                }
            }
        }
        public def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any {
            Runtime.ensureNotInAtomic();
            if (place.id == Runtime.hereLong()) {
                // local path can be the same as before
                return Runtime.evalAtNonResilient(place, body, prof);
            }

            @StackAllocate val me = @StackAllocate new Cell[Any](null);
            val me2 = GlobalRef(me);
            @Profile(prof) at (place) {
                val r : Any = body();
                at (me2) {
                    me2()(r);
                }
            }

            return me();
        }
    }

    static final class FinishResilientZooKeeper(id:Long) extends FinishState {
        // can be null, otherwise should call .release() upon quiescence (i.e. when a call to waitForFinish() would terminate immediately)...
        // note that it is not ok to call .release() within waitForFinish, it must be called when the counters are updated for the last time
        val latch:SimpleLatch;
        def this(latch:SimpleLatch) {
            property(0l);
            this.latch = latch;
            throw new Exception("under implementation");
        }
        def notifySubActivitySpawn(place:Place) {
            throw new Exception("under implementation");
        }
        def notifyActivityCreation(srcPlace:Place) : Boolean {
            throw new Exception("under implementation");
        }
        def notifyActivityTermination() {
            throw new Exception("under implementation");
        }
        def pushException(t:Exception) {
            throw new Exception("under implementation");
        }
        def waitForFinish() {
            throw new Exception("under implementation");
        }
        def simpleLatch():SimpleLatch = null;
        public def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void {
            throw new Exception("under implementation");
        }
        public def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any {
            throw new Exception("under implementation");
        }
    }


    private static def lowLevelFetch[T](dst:Place, cl:()=>T, cell:Cell[T]) : Boolean {
        if (here == dst) {
            cell(cl());
            return true;
        } else {
            val gcell = new GlobalRef(cell);
            Runtime.x10rtSendMessage(dst.id, () => @RemoteInvocation("low_level_fetch_out") {
                try {
                    val r = cl();
                    Runtime.x10rtSendMessage(gcell.home.id, () => @RemoteInvocation("low_level_fetch_back") {
                        gcell.getLocalOrCopy()(r);
                    }, null);
                } catch (t:CheckedThrowable) {
                   t.printStackTrace();
                }
            }, null);
            //Runtime.println("Waiting for reply to message...");
            while (cell() == null) {
                Runtime.probe();
                if (dst.isDead()) {
                    return false;
                }
            }
            //Runtime.println("Got reply.");
            return true;
        }
    }

    private static def lowLevelAt(dst:Place, cl:()=>void) : Boolean {
        if (here == dst) {
            cl();
            return true;
        } else {
            val c = new GlobalRef(new AtomicBoolean());
            Runtime.x10rtSendMessage(dst.id, () => @RemoteInvocation("low_level_at_out") {
                try {
                    cl();
                } catch (t:CheckedThrowable) {
                    t.printStackTrace();
                }
                Runtime.x10rtSendMessage(c.home.id, () => @RemoteInvocation("low_level_at_back") {
                    c.getLocalOrCopy().getAndSet(true);
                }, null);
            }, null);
            //Runtime.println("Waiting for reply to message...");
            while (!c().get()) {
                Runtime.probe();
                if (dst.isDead()) {
                    return false;
                }
            }
            //Runtime.println("Got reply.");
            return true;
        }
    }

    static final class FinishResilientDistributedBackup {

        // guarded by atomic { }
        static MAP = new HashMap[GlobalRef[FinishResilientDistributedRoot], FinishResilientDistributedBackup]();

        val transit : Rail[Int];
        val live : Rail[Int];
        val root : GlobalRef[FinishResilientDistributedRoot];

        private def this(root:GlobalRef[FinishResilientDistributedRoot]) {
            this.transit = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0n);
            this.live = new Rail[Int](Place.MAX_PLACES, 0n);
            this.root = root;
            this.live(root.home.id) = 1n;
        }
        static def make(root:GlobalRef[FinishResilientDistributedRoot]) {
            val nu = new FinishResilientDistributedBackup(root);
            atomic {
                MAP.put(root, nu);
            }
            return GlobalRef[FinishResilientDistributedBackup](nu);
        }

        def notifySubActivitySpawn(srcId:Long, dstId:Long) {
            atomic {
                transit(srcId + dstId*Place.MAX_PLACES)++;
            }
        }
        def notifyActivityCreation(srcId:Long, dstId:Long) {
            atomic {
                transit(srcId + dstId*Place.MAX_PLACES)--;
                live(dstId)++;
            }
        }
        def notifyActivityTermination(dstId:Long) {
            atomic {
                live(dstId)--;
            }
        }
    }

    static final class FinishResilientDistributedRoot {

        // guarded by atomic { }
        static ALL = new ArrayList[FinishResilientDistributedRoot]();

        val transit : Rail[Int];
        val live : Rail[Int];
        var multipleExceptions : GrowableRail[Exception] = null;
        val latch : SimpleLatch;

        var backup : GlobalRef[FinishResilientDistributedBackup];
        var hasBackup : Boolean;

        private def ensureMultipleExceptions() {
            if (multipleExceptions == null) multipleExceptions = new GrowableRail[Exception]();
            return multipleExceptions;
        }
        def addDeadPlaceException(p:Place) {
            val e = new DeadPlaceException(p);
            e.fillInStackTrace();
            ensureMultipleExceptions().add(e);
        }

        private def this(latch:SimpleLatch) {
            this.transit = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0n);
            this.live = new Rail[Int](Place.MAX_PLACES, 0n);
            this.live(here.id) = 1n;
            if (VERBOSE) Runtime.println("    initial live("+here.id+") == 1");
            this.latch = latch;
        }
        static def make(latch:SimpleLatch) {
            val nu = new FinishResilientDistributedRoot(latch);
            val gnu = GlobalRef[FinishResilientDistributedRoot](nu);

            // look for a place to put backup
            var dst : Place = here.next();
            val cell = new Cell[GlobalRef[FinishResilientDistributedBackup]](GlobalRef(null as FinishResilientDistributedBackup));
            val success = lowLevelFetch(dst, ()=>FinishResilientDistributedBackup.make(gnu), cell);
            if (!success) {
                // TODO: try more places 
                throw new Exception("Could not find a backup place");
            }

            if (FinishState.VERBOSE) Runtime.println("make("+nu+") @ "+here.id+" backup is "+cell());


            nu.backup = cell();
            nu.hasBackup = true;

            atomic {
                ALL.add(nu);
            }
            return nu;
        }

        def notifySubActivitySpawn(srcId:Long, dstId:Long) {
            latch.lock();
            if (VERBOSE) Runtime.println("notifySubActivitySpawn("+this+", "+srcId+", "+dstId+")");
            transit(srcId + dstId*Place.MAX_PLACES)++;
            if (VERBOSE) Runtime.println("    transit("+srcId+","+dstId+") == "+transit(srcId + dstId*Place.MAX_PLACES));
            latch.unlock();
            if (hasBackup) {
                val bup = this.backup; // avoid capturing this
                val success = lowLevelAt(bup.home, () => { bup.getLocalOrCopy().notifySubActivitySpawn(srcId, dstId); } );
                if (!success) {
                    // TODO: recreate backup somewhere else
                    throw new Exception("Could not notifySubActivitySpawn() to backup");
                }
            }
        }

        def notifyActivityCreation(srcId:Long, dstId:Long) : Boolean {
            latch.lock();
            if (VERBOSE) Runtime.println("notifyActivityCreation("+this+", "+srcId+", "+dstId+")");
            if (Place(srcId).isDead()) {
                latch.unlock();
                return false;
            }
            live(dstId)++;
            transit(srcId + dstId*Place.MAX_PLACES)--;
            if (VERBOSE) Runtime.println("    live("+dstId+") == "+live(dstId));
            if (VERBOSE) Runtime.println("    transit("+srcId+","+dstId+") == "+transit(srcId + dstId*Place.MAX_PLACES));
            latch.unlock();
            if (hasBackup) {
                val bup = this.backup; // avoid capturing this
                val success = lowLevelAt(bup.home, () => { bup.getLocalOrCopy().notifyActivityCreation(srcId, dstId); } );
                if (!success) {
                    // TODO: recreate backup somewhere else
                    throw new Exception("Could not notifyActivityCreation() to backup");
                }
            }
            return true;
        }

        def notifyActivityTermination(dstId:Long) {
            latch.lock();
            if (VERBOSE) Runtime.println("notifyActivityTermination("+this+", "+dstId+")");
            live(dstId)--;
            if (VERBOSE) Runtime.println("    live("+dstId+") == "+live(dstId));
            if (quiescent()) {
                if (VERBOSE) Runtime.println("    Releasing latch...");
                latch.release();
            }
            latch.unlock();
            if (hasBackup) {
                val bup = this.backup; // avoid capturing this
                val success = lowLevelAt(bup.home, () => { bup.getLocalOrCopy().notifyActivityTermination(dstId); } );
                if (!success) {
                    // TODO: recreate backup somewhere else
                    throw new Exception("Could not notifyActivityTermination() to backup");
                }
            }
        }

        def notifyPlaceDeath() {
            latch.lock();
            if (FinishState.VERBOSE) Runtime.println("notifyPlaceDeath("+this+")");
            if (quiescent()) {
                if (VERBOSE) Runtime.println("    Releasing latch...");
                latch.release();
            }
            latch.unlock();
        }

        static def notifyAllPlaceDeath() {
            atomic {
                for (x in ALL) {
                    x.notifyPlaceDeath();
                }
            }
        }

        def pushException(t:Exception) {
            latch.lock();
            if (VERBOSE) Runtime.println("pushException("+this+", "+t+")");
            ensureMultipleExceptions().add(t);
            latch.unlock();
        }

        // must be called with latch locked
        private def quiescent() : Boolean {

            // overwrite counters with 0 if places have died, accumuluate exceptions
            for (i in 0..(Place.MAX_PLACES-1)) {
                if (Place.isDead(i)) {
                    for (unused in 1..live(i)) {
                        addDeadPlaceException(Place(i));
                    }
                    live(i) = 0n;

                    // kill horizontal and vertical lines in transit matrix
                    for (j in 0..(Place.MAX_PLACES-1)) {
                        for (unused in 1..transit(i + j*Place.MAX_PLACES)) {
                            addDeadPlaceException(Place(i));
                        }
                        transit(i + j*Place.MAX_PLACES) = 0n;

                        for (unused in 1..transit(j + i*Place.MAX_PLACES)) {
                            addDeadPlaceException(Place(i));
                        }
                        transit(j + i*Place.MAX_PLACES) = 0n;
                    }
                }
            }

            // Counters can become negative due to quirky use of finish below main
            if (VERBOSE) Runtime.println("quiescent("+this+")");
            for (i in 0..(Place.MAX_PLACES-1)) {
                if (live(i)>0) {
                    if (VERBOSE) Runtime.println("    "+this+" Live at "+i);
                    return false;
                }
                for (j in 0..(Place.MAX_PLACES-1)) {
                    if (transit(i + j*Place.MAX_PLACES)>0) {
                        if (FinishState.VERBOSE) Runtime.println("    "+this+" In transit from "+i+" -> "+j);
                        return false;
                    }
                }
            }

            return true;
        }

    }

    static final class FinishResilientDistributed extends FinishState {

        val root : GlobalRef[FinishResilientDistributedRoot];

        def this(latch:SimpleLatch) {
            val the_root = FinishResilientDistributedRoot.make(latch);
            //Runtime.println(the_root+" just created."); 
            this.root = new GlobalRef[FinishResilientDistributedRoot](the_root);
        }

        def notifySubActivitySpawn(place:Place) {
            val srcId = here.id;
            val dstId = place.id;
            lowLevelAt(root.home, () => { root.getLocalOrCopy().notifySubActivitySpawn(srcId, dstId); } );
        }

        def notifyActivityCreation(srcPlace:Place) : Boolean {
            val srcId = srcPlace.id;
            val dstId = here.id;
            return lowLevelAt(root.home, () => { root.getLocalOrCopy().notifyActivityCreation(srcId, dstId); } );
        }

        def notifyActivityTermination() {
            val dstId = here.id;
            lowLevelAt(root.home, () => { root.getLocalOrCopy().notifyActivityTermination(dstId); } );
        }

        def pushException(t:Exception) {
            lowLevelAt(root.home, () => { root.getLocalOrCopy().pushException(t); } );
        }

        def waitForFinish() {
            if (VERBOSE) Runtime.println("waitForFinish("+this+")");
            val the_root = root.getLocalOrCopy();
            the_root.notifyActivityTermination(here.id);
            if (!Runtime.STRICT_FINISH) Runtime.worker().join(the_root.latch);
            the_root.latch.await();
            if (the_root.multipleExceptions != null) {
                if (FinishState.VERBOSE) Runtime.println("waitForFinish("+this+") done waiting (throwing exceptions)");
                throw MultipleExceptions.make(the_root.multipleExceptions);
            }
            if (FinishState.VERBOSE) Runtime.println("waitForFinish("+this+") done waiting");
        }

        def simpleLatch():SimpleLatch = (root as GlobalRef[FinishResilientDistributedRoot]{home==here})().latch;

        public def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void {
            Runtime.ensureNotInAtomic();
            if (place.id == Runtime.hereLong()) {
                // local path can be the same as before
                Runtime.runAtNonResilient(place, body, prof);
                return;
            }

            val real_finish = this;

            val tmp_finish = new FinishResilientDistributed(new SimpleLatch());
            // TODO: clockPhases -- clocks not supported in resilient X10 at the moment
            // TODO: This implementation of runAt does not explicitly dealloc things
            val home = here;
            tmp_finish.notifySubActivitySpawn(place);

            // [DC] do not use at (place) async since the finish state is handled internally
            // [DC] go to the lower level...
            val cl = () => @x10.compiler.RemoteInvocation("resilient_place_zero_run_at") {
                val exc_body = () => {
                    if (tmp_finish.notifyActivityCreation(home)) {
                        try {
                            try {
                                body();
                            } catch (e:Runtime.AtCheckedWrapper) {
                                throw e.getCheckedCause();
                            }
                        } catch (t:CheckedThrowable) {
                            val e = Exception.ensureException(t);
                            tmp_finish.pushException(e);
                        }
                        tmp_finish.notifyActivityTermination();
                    }
                };
                Runtime.execute(new Activity(exc_body, home, real_finish, false, false));
            };
            Runtime.x10rtSendMessage(place.id, cl, prof);

            try {
                if (VERBOSE) Runtime.println("Entering resilient at waitForFinish");
                tmp_finish.waitForFinish();
                if (VERBOSE) Runtime.println("Exiting resilient at waitForFinish");
            } catch (e:MultipleExceptions) {
                assert e.exceptions.size == 1l : e.exceptions();
                val e2 = e.exceptions(0);
                if (VERBOSE) Runtime.println("Received from resilient at: "+e2);
                if (e2 instanceof WrappedThrowable) {
                    Runtime.throwCheckedWithoutThrows(e2.getCause());
                } else {
                    throw e2;
                }
            }
        }

        public def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any {
            Runtime.ensureNotInAtomic();
            if (place.id == Runtime.hereLong()) {
                // local path can be the same as before
                return Runtime.evalAtNonResilient(place, body, prof);
            }

            @StackAllocate val me = @StackAllocate new Cell[Any](null);
            val me2 = GlobalRef(me);
            @Profile(prof) at (place) {
                val r : Any = body();
                at (me2) {
                    me2()(r);
                }
            }

            return me();
        }

    }

    static def notifyPlaceDeath() : void {
        if (Runtime.RESILIENT_PLACE_ZERO) {
            if (here.id == 0l) {
                // most finishes are woken up by 'atomic'
                atomic { }
                // the root one also needs to have its latch released
                // also adopt activities of finishes whose homes are dead into closest live parent
                ResilientStorePlaceZero.notifyPlaceDeath((Runtime.rootFinish as FinishResilientPlaceZero).id);
            }
        } else if (Runtime.RESILIENT_ZOO_KEEPER) {
            //
        } else if (Runtime.RESILIENT_DISTRIBUTED) {
            FinishResilientDistributedRoot.notifyAllPlaceDeath();
            // merge backups to parent
        } else {
            throw new Exception("Only resilient X10 handles place death");
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
