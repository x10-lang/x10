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
import x10.util.HashSet;
import x10.util.Pair;
import x10.util.Triple;

import x10.util.concurrent.AtomicInteger;
import x10.util.concurrent.AtomicBoolean;
import x10.util.concurrent.Lock;
import x10.util.concurrent.SimpleLatch;

import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;

abstract class FinishState {

    // Turn this on to debug deadlocks within the finish implementation
    static VERBOSE = Configuration.envOrElse("X10_FINISH_VERBOSE", false);

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
            for(var i:Long=0; i<rail.size; i++) {
                counts(rail(i).first as Long) += rail(i).second;
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
                places(0) = id as Int; // WARNING: assuming 32 bit places at X10 level.
            }
            val old = counts(place.id);
            counts(place.id)++;
            if (old == 0n && id != place.id) {
                places(length++) = place.id as Int; // WARNING: assuming 32 bit places at X10 level.
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
                places(0) = id as Int; // WARNING: assuming 32 bit places at X10 level.
            }
            val old = counts(place.id);
            counts(place.id)++;
            if (old == 0n && id != place.id) {
                places(length++) = place.id as Int; // WARNING: assuming 32 bit places at X10 level.
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
            if ((Place.MAX_PLACES < 1024) || (h%32n == 0n) || (h-h%32n == (ref.home.id as Int))) {
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

            val dummy_data = new Empty(); // for XTENLANG-3324
            @StackAllocate val me = @StackAllocate new Cell[Any](dummy_data);
            val me2 = GlobalRef(me);
            @Profile(prof) at (place) {
                val r : Any = body();
                at (me2) {
                    me2()(r);
                }
            }
            // Fix for XTENLANG-3324
            if (me()==dummy_data) { // no result set
                if (VERBOSE) Runtime.println("evalAt returns no result, target place may died");
                if (place.isDead()) throw new DeadPlaceException(place);
                else me(null); // should throw some exception?
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
            // while (!c().get()) {
            //     Runtime.probe();
            //     if (dst.isDead()) {
            //         return false;
            //     }
            // }
            if (!c().get()) { // Fix for XTENLANG-3303/3305
                Runtime.increaseParallelism();
                do {
                    Runtime.x10rtProbe();
                    if (dst.isDead()) {
                        Runtime.decreaseParallelism(1n);
                        return false;
                    }
                } while (!c().get());
                Runtime.decreaseParallelism(1n);
            }
            //Runtime.println("Got reply.");
            return true;
        }
    }

    private static def lowLevelFetch[T](dst:Place, cl:()=>T, cell:Cell[T]) : Boolean {
        if (here == dst) {
            cell(cl());
            return true;
        } else {
            val done = new GlobalRef(new AtomicBoolean(false));
            val gcell = new GlobalRef(cell);
            Runtime.x10rtSendMessage(dst.id, () => @RemoteInvocation("low_level_fetch_out") {
                try {
                    val r = cl();
                    Runtime.x10rtSendMessage(gcell.home.id, () => @RemoteInvocation("low_level_fetch_back") {
                        done.getLocalOrCopy().getAndSet(true);
                        gcell.getLocalOrCopy()(r);
                    }, null);
                } catch (t:CheckedThrowable) {
                   t.printStackTrace();
                }
            }, null);
            //Runtime.println("Waiting for reply to message...");
            // while (!done().get()) {
            //     Runtime.probe();
            //     if (dst.isDead()) {
            //         return false;
            //     }
            // }
            if (!done().get()) { // Fix for XTENLANG-3303/3305
                Runtime.increaseParallelism();
                do {
                    Runtime.x10rtProbe();
                    if (dst.isDead()) {
                        Runtime.decreaseParallelism(1n);
                        return false;
                    }
                } while (!done().get());
                Runtime.decreaseParallelism(1n);
            }
            //Runtime.println("Got reply.");
            return true;
        }
    }

    static final class FinishResilientDistributedBackup implements Runtime.Mortal {

        // guarded by atomic { }
        static MAP = new HashMap[GlobalRef[FinishResilientDistributedMaster], FinishResilientDistributedBackup]();

        static def backupForget (master:GlobalRef[FinishResilientDistributedMaster], backup_place:Place) {
            Runtime.x10rtSendMessage(backup_place.id, () => @RemoteInvocation("backup_forget") {
                atomic {
                    val bup = MAP.put(master, null);
                }
            }, null);
        }

        static def backupLowLevelFetch[T] (master:GlobalRef[FinishResilientDistributedMaster], cl:(FinishResilientDistributedBackup)=>T, cell:Cell[T]) : Boolean {
            var place : Place = master.home;
            while (true) {
                place = place.next();
                if (place == master.home) return false;
                val done = new GlobalRef(new AtomicInteger(0n)); // use an AtomicInteger only for the fence instructions
                val gcell = new GlobalRef(cell);
                if (place == here) {
                    atomic {
                        val bup = MAP.getOrElse(master, null);
                        if (bup == null) {
                            done().set(2n);
                        } else {
                            done().set(1n);
                            cell(cl(bup));
                        }
                    }
                } else {
                    Runtime.x10rtSendMessage(place.id, () => @RemoteInvocation("backup_low_level_fetch_out") {
                        try {
                            Runtime.atomicMonitor.lock(); // would use atomic { } but scope is not lexical
                            val bup = MAP.getOrElse(master, null);
                            if (bup == null) {
                                // couldn't find it
                                Runtime.atomicMonitor.release();
                                Runtime.x10rtSendMessage(gcell.home.id, () => @RemoteInvocation("backup_low_level_fetch_not_found") {
                                    done.getLocalOrCopy().set(2n);
                                }, null);
                            } else {
                                // did find it
                                val r = cl(bup);
                                Runtime.atomicMonitor.release();
                                Runtime.x10rtSendMessage(gcell.home.id, () => @RemoteInvocation("backup_low_level_fetch_back") {
                                    done.getLocalOrCopy().set(1n);
                                    gcell.getLocalOrCopy()(r);
                                }, null);
                            }
                        } catch (t:CheckedThrowable) {
                            Runtime.atomicMonitor.release();
                            t.printStackTrace();
                        }
                    }, null);
                    //Runtime.println("Waiting for reply to message...");
                    // while (done().get() == 0n) {
                    //     Runtime.probe();
                    //     if (place.isDead()) {
                    //         break;
                    //     }
                    // }
                    if (done().get() == 0n) { // Fix for XTENLANG-3303/3305
                        Runtime.increaseParallelism();
                        do {
                            Runtime.x10rtProbe();
                            if (place.isDead()) {
                                Runtime.decreaseParallelism(1n);
                                return false;
                            }
                        } while (done().get() == 0n);
                        Runtime.decreaseParallelism(1n);
                    }
                }
                if (done().get() == 1n) return true;
                // otherwise, try again until we have tried every place
            }
        }

        static def backupLowLevelAt (master:GlobalRef[FinishResilientDistributedMaster], cl:(FinishResilientDistributedBackup)=>void) : Boolean {
            var place : Place = master.home;
            while (true) {
                place = place.next();
                if (place == master.home) return false;
                val found = new Cell[Boolean](false);
                val success = lowLevelFetch[Boolean](place, ()=>{
                    atomic {
                        val r = MAP.getOrElse(master, null);
                        if (r == null) return false;
                        cl(r);
                        return true;
                    }
                }, found);
                if (success) return found();
                // otherwise, try again until we have tried every place
            }
        }

        val transit : Rail[Int];
        val live : Rail[Int];
        val children  = new GrowableRail[GlobalRef[FinishResilientDistributedMaster]]();
        var adopted : Boolean = false;
        var adoptedRoot : GlobalRef[FinishResilientDistributedMaster];

        private def this(root:GlobalRef[FinishResilientDistributedMaster]) {
            this.transit = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0n);
            this.live = new Rail[Int](Place.MAX_PLACES, 0n);
            this.adoptedRoot = root;
        }
        static def make(root:GlobalRef[FinishResilientDistributedMaster]) {
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
                // may have problem with backup place's view on place death being different to master's?
                if (Place(srcId).isDead()) {
                    return false;
                }
                transit(srcId + dstId*Place.MAX_PLACES)--;
                live(dstId)++;
            }
            return true;
        }
        def notifyActivityTermination(dstId:Long) {
            atomic {
                live(dstId)--;
            }
        }
    }

    static final class FinishResilientDistributedMaster implements Runtime.Mortal {

        // guarded by atomic { }
        static ALL = new HashSet[FinishResilientDistributedMaster]();

        static val nameCounter = new AtomicInteger();

        val transit : Rail[Int];
        val live : Rail[Int];
        val transitAdopted : Rail[Int];
        val liveAdopted : Rail[Int];
        var totalCounter : Long;
        val children : GrowableRail[GlobalRef[FinishResilientDistributedMaster]];
        var numDead : Long;

        var multipleExceptions : GrowableRail[Exception] = null;
        val latch : SimpleLatch;

        var backup : GlobalRef[FinishResilientDistributedBackup];
        var hasBackup : Boolean;

        val name : String;

        def transitInc(src:Long, dst:Long, v:Int) { transit(src + dst*Place.MAX_PLACES) += v; }
        def transitDec(src:Long, dst:Long) { transit(src + dst*Place.MAX_PLACES)--; }
        def transitGet(src:Long, dst:Long) = transit(src + dst*Place.MAX_PLACES);
        def transitSet(src:Long, dst:Long, v:Int) { transit(src + dst*Place.MAX_PLACES) = v; }
        def transitAdoptedInc(src:Long, dst:Long, v:Int) { transitAdopted(src + dst*Place.MAX_PLACES) += v; }
        def transitAdoptedDec(src:Long, dst:Long) { transitAdopted(src + dst*Place.MAX_PLACES)--; }
        def transitAdoptedGet(src:Long, dst:Long) = transitAdopted(src + dst*Place.MAX_PLACES);
        def transitAdoptedSet(src:Long, dst:Long, v:Int) { transitAdopted(src + dst*Place.MAX_PLACES) = v; }

        def transitInc(src:Long, dst:Long) { transitInc(src,dst,1n); }
        def transitAdoptedInc(src:Long, dst:Long) { transitAdoptedInc(src,dst,1n); }


        private def recalculateTotal() {
            totalCounter = 0;
            for (i in 0..(Place.MAX_PLACES-1)) {
                totalCounter += live(i);
                totalCounter += liveAdopted(i);
                for (j in 0..(Place.MAX_PLACES-1)) {
                    totalCounter += transitGet(j, i);
                    totalCounter += transitAdoptedGet(j, i);
                }
            }
        }


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
            val name = VERBOSE ? nameCounter.getAndIncrement()+"@"+here.id : null;
            this.name = name;
            if (VERBOSE) Runtime.println("Creating master finish state ("+name+")...");
            this.transit = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0n);
            this.live = new Rail[Int](Place.MAX_PLACES, 0n);
            this.transitAdopted = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0n);
            this.liveAdopted = new Rail[Int](Place.MAX_PLACES, 0n);
            this.children = new GrowableRail[GlobalRef[FinishResilientDistributedMaster]]();
            this.live(here.id) = 1n;
            this.totalCounter = 1;
            this.numDead = 0;
            if (VERBOSE) Runtime.println("    initial live("+here.id+") == 1");
            this.latch = latch;
        }
        def addChild(child:GlobalRef[FinishResilientDistributedMaster]) {
            latch.lock();
            children.add(child);
            latch.unlock();
            // TODO: add to backup as well...
        }
        static def make(parent:GlobalRef[FinishResilientDistributedMaster], latch:SimpleLatch) {
            val nu = new FinishResilientDistributedMaster(latch);
            val gnu = GlobalRef[FinishResilientDistributedMaster](nu);

            if (FinishState.VERBOSE) Runtime.println("    "+gnu+" parent is "+parent);
            if (here.id == 0) {
                // we can never die, so no need to add to parents' children
                // (this conveniently also covers the case of the root finish, where parent() == null)
            } else {
                val success = lowLevelAt(parent.home, () => { parent.getLocalOrCopy().addChild(gnu); });
                if (!success) {
                    Runtime.println("TODO: registering new child with parent: handle case where parent is dead (find nearest workable parent or backup)");
                }
            }

            if (here.id == 0) {
                // at place 0, do not need a backup
                nu.hasBackup = false;
            } else {
                // look for a place to put backup
                var dst : Place = here.next();
                val cell = new Cell[GlobalRef[FinishResilientDistributedBackup]](GlobalRef(null as FinishResilientDistributedBackup));
                val success2 = lowLevelFetch(dst, ()=>FinishResilientDistributedBackup.make(gnu), cell);
                if (!success2) {
                    // TODO: try more places 
                    Runtime.println("Could not find a backup place");
                } else {
                    if (FinishState.VERBOSE) Runtime.println("    backup is "+cell());

                    nu.backup = cell();
                    nu.hasBackup = true;
                }
            }

            atomic {
                ALL.add(nu);
            }
            return nu;
        }

        def notifyAdoptedSubActivitySpawn(srcId:Long, dstId:Long) {
            latch.lock();
            if (VERBOSE) Runtime.println("("+name+").notifyAdoptedSubActivitySpawn("+srcId+", "+dstId+")");
            transitAdopted(srcId + dstId*Place.MAX_PLACES)++;
            totalCounter++;
            if (VERBOSE) Runtime.println("    transitAdopted("+srcId+","+dstId+") == "+transitAdopted(srcId + dstId*Place.MAX_PLACES));
            latch.unlock();
            if (hasBackup && !(srcId==here.id && dstId==here.id)) {
                val bup = this.backup; // avoid capturing this
                val success = lowLevelAt(bup.home, () => { bup.getLocalOrCopy().notifySubActivitySpawn(srcId, dstId); } );
                if (!success) {
                    // TODO: recreate backup somewhere else
                    Runtime.println("Could not back up notifyAdoptedSubActivitySpawn(), backup place dead");
                    hasBackup = false;
                }
            }
        }

        def notifySubActivitySpawn(srcId:Long, dstId:Long) {
            latch.lock();
            if (VERBOSE) Runtime.println("("+name+").notifySubActivitySpawn("+srcId+", "+dstId+")");
            transit(srcId + dstId*Place.MAX_PLACES)++;
            totalCounter++;
            if (VERBOSE) Runtime.println("    transit("+srcId+","+dstId+") == "+transit(srcId + dstId*Place.MAX_PLACES));
            latch.unlock();
            if (hasBackup && !(srcId==here.id && dstId==here.id)) {
                val bup = this.backup; // avoid capturing this
                val success = lowLevelAt(bup.home, () => { bup.getLocalOrCopy().notifySubActivitySpawn(srcId, dstId); } );
                if (!success) {
                    // TODO: recreate backup somewhere else
                    Runtime.println("Could not back up notifySubActivitySpawn(), backup place dead");
                    hasBackup = false;
                }
            }
        }

        def notifyAdoptedActivityCreation(srcId:Long, dstId:Long) : Boolean {
            latch.lock();
            if (VERBOSE) Runtime.println("("+name+").notifyAdoptedActivityCreation("+srcId+", "+dstId+")");
            if (Place(srcId).isDead()) {
                latch.unlock();
                return false;
            }
            liveAdopted(dstId)++;
            transitAdopted(srcId + dstId*Place.MAX_PLACES)--;
            if (VERBOSE) Runtime.println("    liveAdopted("+dstId+") == "+liveAdopted(dstId));
            if (VERBOSE) Runtime.println("    transitAdopted("+srcId+","+dstId+") == "+transitAdopted(srcId + dstId*Place.MAX_PLACES));
            latch.unlock();
            if (hasBackup && !(srcId==here.id && dstId==here.id)) {
                val bup = this.backup; // avoid capturing this
                val success = lowLevelAt(bup.home, () => { bup.getLocalOrCopy().notifyActivityCreation(srcId, dstId); } );
                if (!success) {
                    // TODO: recreate backup somewhere else
                    Runtime.println("Could not back up notifyAdoptedActivityCreation(), backup place dead");
                    hasBackup = false;
                }
            }
            return true;
        }

        def notifyActivityCreation(srcId:Long, dstId:Long) : Boolean {
            latch.lock();
            if (VERBOSE) Runtime.println("("+name+").notifyActivityCreation("+srcId+", "+dstId+")");
            if (Place(srcId).isDead()) {
                latch.unlock();
                return false;
            }
            live(dstId)++;
            transit(srcId + dstId*Place.MAX_PLACES)--;
            if (VERBOSE) Runtime.println("    live("+dstId+") == "+live(dstId));
            if (VERBOSE) Runtime.println("    transit("+srcId+","+dstId+") == "+transit(srcId + dstId*Place.MAX_PLACES));
            latch.unlock();
            if (hasBackup && !(srcId==here.id && dstId==here.id)) {
                val bup = this.backup; // avoid capturing this
                val success = lowLevelAt(bup.home, () => { bup.getLocalOrCopy().notifyActivityCreation(srcId, dstId); } );
                if (!success) {
                    // TODO: recreate backup somewhere else
                    Runtime.println("Could not back up notifyActivityCreation(), backup place dead");
                    hasBackup = false;
                }
            }
            return true;
        }

        def notifyAdoptedActivityTermination(dstId:Long) {
            latch.lock();
            if (VERBOSE) Runtime.println("("+name+").notifyAdoptedActivityTermination("+dstId+")");
            liveAdopted(dstId)--;
            totalCounter--;
            if (VERBOSE) Runtime.println("    liveAdopted("+dstId+") == "+liveAdopted(dstId));
            if (quiescent()) {
                if (VERBOSE) Runtime.println("    Releasing latch...");
                latch.release();
            }
            latch.unlock();
            if (hasBackup && dstId!=here.id) {
                val bup = this.backup; // avoid capturing this
                val success = lowLevelAt(bup.home, () => { bup.getLocalOrCopy().notifyActivityTermination(dstId); } );
                if (!success) {
                    // TODO: recreate backup somewhere else
                    Runtime.println("Could not back up notifyAdoptedActivityTermination(), backup place dead");
                    hasBackup = false;
                }
            }
        }

        def notifyActivityTermination(dstId:Long) {
            latch.lock();
            if (VERBOSE) Runtime.println("("+name+").notifyActivityTermination("+dstId+")");
            live(dstId)--;
            totalCounter--;
            if (VERBOSE) Runtime.println("    live("+dstId+") == "+live(dstId));
            if (quiescent()) {
                if (VERBOSE) Runtime.println("    Releasing latch...");
                latch.release();
            }
            latch.unlock();
            if (hasBackup && dstId!=here.id) {
                val bup = this.backup; // avoid capturing this
                val success = lowLevelAt(bup.home, () => { bup.getLocalOrCopy().notifyActivityTermination(dstId); } );
                if (!success) {
                    // TODO: recreate backup somewhere else
                    Runtime.println("Could not back up notifyActivityTermination(), backup place dead");
                    hasBackup = false;
                }
            }
        }

        def notifyPlaceDeath() {
            latch.lock();
            if (FinishState.VERBOSE) Runtime.println("("+name+").notifyPlaceDeath()");
            if (quiescent()) {
                if (VERBOSE) Runtime.println("    Releasing latch...");
                latch.release();
            }
            latch.unlock();
        }

        static def notifyAllPlaceDeath() {
            atomic {
                for (x in ALL) {
                    if (x != null) x.notifyPlaceDeath();
                }
            }
        }

        def pushException(t:Exception) {
            latch.lock();
            if (VERBOSE) Runtime.println("("+name+").pushException("+t+")");
            ensureMultipleExceptions().add(t);
            latch.unlock();
        }

        // for each child, if that child is dead, take its counters and flag it
        // recurse for other finishes under that one, if they are dead
        private def pullUpDeadChildFinishes() {
            val this_ = GlobalRef[FinishResilientDistributedMaster](this);
            latch.lock();
            for (var chindex:Long=0 ; chindex<children.size() ; ++chindex) {
                val child = children(chindex);
                if (!child.home.isDead()) continue;

                if (VERBOSE) Runtime.println("Adopting child finish...");

                // remove child
                if (chindex!=children.size()-1) {
                    children(chindex) = children(children.size()-1);
                }
                children.removeLast();
                chindex--; // don't advance this iteration

                // TODO: race condition -- what if we die after fetchBackup but before committing to our backup
                // state would be inconsistent
                val backup_cell = new Cell[FinishResilientDistributedBackup](null);
                val found = FinishResilientDistributedBackup.backupLowLevelFetch(child, (r:FinishResilientDistributedBackup)=>{
                    if (VERBOSE) Runtime.println("Setting forwarding pointer on backup "+r);
                    if (r.adopted) {
                        Runtime.println("should not be adopted already! FinishResilientDistributedBackup.fetchBackup");
                    }
                    r.adopted = true;
                    r.adoptedRoot = this_;
                    return r;
                }, backup_cell);
                if (!found) {
                    Runtime.println("Fatal error: both master and backup finish store lost due to place failure.");
                }
                val bup = backup_cell();

                children.addAll(bup.children);
                for (i in 0..(Place.MAX_PLACES-1)) {
                    if (VERBOSE && bup.live(i)!=0n) Runtime.println("live at adopted finish ("+i+") = "+bup.live(i));
                    liveAdopted(i) += bup.live(i);
                    for (j in 0..(Place.MAX_PLACES-1)) {
                        if (VERBOSE && bup.transit(i + j*Place.MAX_PLACES)!=0n) Runtime.println("transit at adopted finish ("+i+","+j+") = "+bup.transit(i + j*Place.MAX_PLACES));
                        transitAdopted(i + j*Place.MAX_PLACES) += bup.transit(i + j*Place.MAX_PLACES);
                    }
                }

                recalculateTotal();

                //TODO: commit new children to backup
            }
            latch.unlock();
        }


        // must be called with latch locked
        private def quiescent() : Boolean {

            // There is actually a race condition here (despite quiescent being called in a latch lock)
            // The Place.isDead() can go to false between the pushUp() and the code after it, causing
            // a finish to 
            // TODO: store dead places in an array, use the same data to drive pushUp() and DPE generation

            val nd = Place.numDead();
            if (nd != numDead) {
                numDead = nd;
                pullUpDeadChildFinishes();
            }

            // overwrite counters with 0 if places have died, accumuluate exceptions
            var need_recalculate : Boolean = false;
            for (i in 0..(Place.MAX_PLACES-1)) {
                if (Place.isDead(i)) {
                    for (unused in 1..live(i)) {
                        addDeadPlaceException(Place(i));
                    }
                    live(i) = 0n;
                    liveAdopted(i) = 0n;

                    // kill horizontal and vertical lines in transit matrix
                    for (j in 0..(Place.MAX_PLACES-1)) {
                        // Do not generate DPEs for these activities, they were never sent
                        //for (unused in 1..transit(i + j*Place.MAX_PLACES)) {
                        //    addDeadPlaceException(Place(i));
                        //}
                        transit(i + j*Place.MAX_PLACES) = 0n;
                        transitAdopted(i + j*Place.MAX_PLACES) = 0n;

                        for (unused in 1..transit(j + i*Place.MAX_PLACES)) {
                            addDeadPlaceException(Place(i));
                        }
                        transit(j + i*Place.MAX_PLACES) = 0n;
                        transitAdopted(j + i*Place.MAX_PLACES) = 0n;
                    }
                    need_recalculate = true;
                }
            }
            if (need_recalculate) recalculateTotal();

            // Counters can become negative due to quirky use of finish below main
            if (totalCounter <= 0) return true;

            if (VERBOSE) {
                Runtime.println("("+name+").quiescent()");
                for (i in 0..(Place.MAX_PLACES-1)) {
                    if (live(i)>0) {
                        if (VERBOSE) Runtime.println("    ("+name+") Live at "+i);
                        return false;
                    }
                    for (j in 0..(Place.MAX_PLACES-1)) {
                        if (transit(i + j*Place.MAX_PLACES)>0) {
                            if (FinishState.VERBOSE) Runtime.println("    ("+name+") In transit from "+i+" -> "+j);
                            return false;
                        }
                    }
                }
                for (i in 0..(Place.MAX_PLACES-1)) {
                    if (liveAdopted(i)>0) {
                        if (VERBOSE) Runtime.println("    ("+name+") Live (adopted) at "+i);
                        return false;
                    }
                    for (j in 0..(Place.MAX_PLACES-1)) {
                        if (transitAdopted(i + j*Place.MAX_PLACES)>0) {
                            if (FinishState.VERBOSE) Runtime.println("    ("+name+") In transit (adopted) from "+i+" -> "+j);
                            return false;
                        }
                    }
                }
            }

            return false;
        }

    }

    static final class FinishResilientDistributed extends FinishState {

        val root : GlobalRef[FinishResilientDistributedMaster];

        private static def parentFinish() : GlobalRef[FinishResilientDistributedMaster] {
            val a = Runtime.activity();
            if (a == null) return GlobalRef(null as FinishResilientDistributedMaster); // creating the main activity (root finish)
            val par = a.finishState();
            if (par instanceof FinishResilientDistributed) return (par as FinishResilientDistributed).root;
            return GlobalRef(null as FinishResilientDistributedMaster);
        }

        def this(latch:SimpleLatch) {

            val the_root = FinishResilientDistributedMaster.make(parentFinish(), latch);
            //Runtime.println(the_root+" just created."); 
            this.root = new GlobalRef[FinishResilientDistributedMaster](the_root);
        }

        def notifySubActivitySpawn(place:Place) {
            val srcId = here.id;
            val dstId = place.id;

            var success : Boolean = false;
            var the_root : GlobalRef[FinishResilientDistributedMaster] = root;
            var adopted : Boolean = false;
            while (true) {
                val the_root_ = the_root;

                if (adopted) {
                    success = lowLevelAt(the_root_.home, () => { the_root_.getLocalOrCopy().notifyAdoptedSubActivitySpawn(srcId, dstId); } );
                } else {
                    success = lowLevelAt(the_root_.home, () => { the_root_.getLocalOrCopy().notifySubActivitySpawn(srcId, dstId); } );
                }

                if (success) break;

                // return true if it was adopted (and the new master) or false meaning we updated the backup and all is good
                val cell = new Cell(Pair[Boolean, GlobalRef[FinishResilientDistributedMaster]](false, the_root_));
                success = FinishResilientDistributedBackup.backupLowLevelFetch(the_root_, (bup:FinishResilientDistributedBackup)=>{
                    // already in an atomic
                    if (!bup.adopted) {
                        bup.notifySubActivitySpawn(srcId,dstId);
                    }
                    return Pair[Boolean, GlobalRef[FinishResilientDistributedMaster]](bup.adopted, bup.adoptedRoot);
                }, cell);

                if (!success) {
                    Runtime.println("Fatal Error: master and backup dead, in notifySubActivitySpawn()");
                    break;
                }

                if (!cell().first) break;

                adopted = true;
                the_root = cell().second;
            }
        }

        def notifyActivityCreation(srcPlace:Place) : Boolean {
            val srcId = srcPlace.id;
            val dstId = here.id;

            var success : Boolean = false;
            var the_root : GlobalRef[FinishResilientDistributedMaster] = root;
            var adopted : Boolean = false;
            while (true) {
                val the_root_ = the_root;

                val simple_cell = new Cell[Boolean](false);
                if (adopted) {
                    success = lowLevelFetch(the_root_.home, () => { return the_root_.getLocalOrCopy().notifyAdoptedActivityCreation(srcId, dstId); }, simple_cell );
                } else {
                    success = lowLevelFetch(the_root_.home, () => { return the_root_.getLocalOrCopy().notifyActivityCreation(srcId, dstId); }, simple_cell );
                }

                if (success) return simple_cell();

                // return true if it was adopted (and the new master) or false meaning we updated the backup and all is good
                val cell = new Cell(Triple[Boolean, GlobalRef[FinishResilientDistributedMaster], Boolean](false, the_root_, false));
                success = FinishResilientDistributedBackup.backupLowLevelFetch(the_root_, (bup:FinishResilientDistributedBackup)=>{
                    // already in an atomic
                    var r:Boolean = false;
                    if (!bup.adopted) {
                        r = bup.notifyActivityCreation(srcId, dstId);
                    }
                    return Triple[Boolean, GlobalRef[FinishResilientDistributedMaster], Boolean](bup.adopted, bup.adoptedRoot, r);
                }, cell);

                if (!success) {
                    Runtime.println("Fatal Error: master and backup dead, in notifyActivityCreation()");
                    break;
                }

                if (!cell().first) return cell().third;

                adopted = true;
                the_root = cell().second;
            }

            // never happens
            assert false;
            return true;
        }

        def notifyActivityTermination() {
            val dstId = here.id;

            var success : Boolean = false;
            var the_root : GlobalRef[FinishResilientDistributedMaster] = root;
            var adopted : Boolean = false;
            while (true) {
                val the_root_ = the_root;

                if (adopted) {
                    success = lowLevelAt(the_root_.home, () => { the_root_.getLocalOrCopy().notifyAdoptedActivityTermination(dstId); } );
                } else {
                    success = lowLevelAt(the_root_.home, () => { the_root_.getLocalOrCopy().notifyActivityTermination(dstId); } );
                }

                if (success) break;

                if (VERBOSE) Runtime.println(the_root+" master dead, looking for backup");

                // return true if it was adopted (and the new master) or false meaning we updated the backup and all is good
                val cell = new Cell(Pair[Boolean, GlobalRef[FinishResilientDistributedMaster]](false, the_root_));
                success = FinishResilientDistributedBackup.backupLowLevelFetch(the_root_, (bup:FinishResilientDistributedBackup)=>{
                    // already in an atomic
                    if (!bup.adopted) {
                        bup.notifyActivityTermination(dstId);
                    }
                    return Pair[Boolean, GlobalRef[FinishResilientDistributedMaster]](bup.adopted, bup.adoptedRoot);
                }, cell);

                if (!success) {
                    Runtime.println("Fatal Error: master and backup dead, in notifyActivityTermination()");
                    break;
                }

                if (!cell().first) {
                    if (VERBOSE) Runtime.println(the_root+" found backup, updated it");
                    break;
                }

                if (VERBOSE) Runtime.println(the_root+" found backup, using forwarding pointer: "+cell().second);

                adopted = true;
                the_root = cell().second;

            }
        }

        def pushException(t:Exception) {
            val success = lowLevelAt(root.home, () => { root.getLocalOrCopy().pushException(t); } );
            if (!success) {
                // do nothing here, exceptions should be dropped if home place dies
            }
        }

        def waitForFinish() {
            val the_root = root.getLocalOrCopy();
            if (VERBOSE) Runtime.println("("+the_root.name+").waitForFinish()");
            the_root.notifyActivityTermination(here.id);
            // if (!Runtime.STRICT_FINISH) Runtime.worker().join(the_root.latch); // removed as a tentative fix for XTENLANG-3304
            the_root.latch.await();
            atomic { //TODO: delete the state only if it finishes without being adopted?? (XTENLANG-3323)
                FinishResilientDistributedMaster.ALL.remove(the_root);
            }
            if (the_root.multipleExceptions != null) {
                if (FinishState.VERBOSE) Runtime.println("("+the_root.name+").waitForFinish() done waiting (throwing exceptions)");
                throw MultipleExceptions.make(the_root.multipleExceptions);
            }
            if (FinishState.VERBOSE) Runtime.println("("+the_root.name+").waitForFinish() done waiting");
        }

        def simpleLatch():SimpleLatch = (root as GlobalRef[FinishResilientDistributedMaster]{home==here})().latch;

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

            val dummy_data = new Empty(); // for XTENLANG-3324
            @StackAllocate val me = @StackAllocate new Cell[Any](dummy_data);
            val me2 = GlobalRef(me);
            @Profile(prof) at (place) {
                val r : Any = body();
                at (me2) {
                    me2()(r);
                }
            }
            // Fix for XTENLANG-3324
            if (me()==dummy_data) { // no result set
                if (VERBOSE) Runtime.println("evalAt returns no result, target place may died");
                if (place.isDead()) throw new DeadPlaceException(place);
                else me(null); // should throw some exception?
            }

            return me();
        }

    }

    static def notifyPlaceDeath() : void {
        switch (Runtime.RESILIENT_MODE) {
        case Configuration.RESILIENT_MODE_PLACE_ZERO:
            if (here.id == 0l) {
                // most finishes are woken up by 'atomic'
                atomic { }
                // the root one also needs to have its latch released
                // also adopt activities of finishes whose homes are dead into closest live parent
                ResilientStorePlaceZero.notifyPlaceDeath((Runtime.rootFinish as FinishResilientPlaceZero).id);
            }
            break;
        case Configuration.RESILIENT_MODE_DISTRIBUTED:
            FinishResilientDistributedMaster.notifyAllPlaceDeath();
            // merge backups to parent
            break;
        case Configuration.RESILIENT_MODE_ZOO_KEEPER:
            //
            break;
        default:
            // This case seems occur naturally on shutdown, so transparently ignore it.
            // The launcher is responsible for tear-down in the case of place death, nothing we need to do.
            //throw new Exception("Only resilient X10 handles place death");
            break;
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
