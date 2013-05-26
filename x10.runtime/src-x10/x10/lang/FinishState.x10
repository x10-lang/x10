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
import x10.util.concurrent.Lock;
import x10.util.concurrent.SimpleLatch;

import x10.io.CustomSerialization;
import x10.io.SerialData;

abstract class FinishState {
    abstract def notifySubActivitySpawn(place:Place):void;
    abstract def notifyActivityCreation():void;
    abstract def notifyActivityTermination():void;
    abstract def pushException(t:Exception):void;
    abstract def waitForFinish():void;
    abstract def simpleLatch():SimpleLatch;

    static def deref[T](root:GlobalRef[FinishState]) = (root as GlobalRef[FinishState]{home==here})() as T;

    // a finish with local asyncs only
    static class LocalFinish extends FinishState {
        @Embed private val count = @Embed new AtomicInteger(1);
        @Embed private val latch = @Embed new SimpleLatch();
        private var exceptions:GrowableRail[Exception]; // lazily initialized
        public def notifySubActivitySpawn(place:Place) {
            assert place.id == Runtime.hereLong();
            count.getAndIncrement();
        }
        public def notifyActivityCreation() {}
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0) latch.release();
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
    }

    // a finish without nested remote asyncs in remote asyncs
    static class FinishSPMD extends FinishSkeleton implements CustomSerialization {
        def this() {
            super(new RootFinishSPMD());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(data:SerialData) {
            super(data.data as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereLong()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = new RemoteFinishSPMD(ref);
            }
        }
    }

    static class RootFinishSPMD extends RootFinishSkeleton {
        @Embed protected val latch = @Embed new SimpleLatch();
        @Embed private val count = @Embed new AtomicInteger(1);
        private var exceptions:GrowableRail[Exception]; // lazily initialized
        public def notifySubActivitySpawn(place:Place) {
            count.incrementAndGet();
        }
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0) latch.release();
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
        @Embed private val count = @Embed new AtomicInteger(1);
        private var exceptions:GrowableRail[Exception]; // lazily initialized
        @Embed private val lock = @Embed new Lock();
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifySubActivitySpawn(place:Place) {
            assert place.id == Runtime.hereLong();
            count.getAndIncrement();
        }
        public def notifyActivityCreation() {}
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0) {
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
                Runtime.dealloc(closure);
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
        private def this(data:SerialData) {
            super(data.data as GlobalRef[FinishState]);
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
        public def notifyActivityCreation():void {}
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
            Runtime.dealloc(closure);
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
        private def this(data:SerialData) { 
            super(data.data as GlobalRef[FinishState]);
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
        public def notifyActivityCreation() {}
        public def notifyActivityTermination() {}
        public def pushException(t:Exception) {
            Runtime.println("Uncaught exception in uncounted activity");
            t.printStackTrace();
        }
        public final def waitForFinish() { assert false; }
        public def simpleLatch():SimpleLatch = null;
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
        public def notifyActivityCreation():void {}
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
        public def serialize():SerialData = new SerialData(ref, null);
        public def notifySubActivitySpawn(place:Place) { me.notifySubActivitySpawn(place); }
        public def notifyActivityCreation() { me.notifyActivityCreation(); }
        public def notifyActivityTermination() { me.notifyActivityTermination(); }
        public def pushException(t:Exception) { me.pushException(t); }
        public def waitForFinish() { me.waitForFinish(); }
        public def simpleLatch() = me.simpleLatch();
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
        private def this(data:SerialData) { 
            super(data.data as GlobalRef[FinishState]);
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
        protected var count:Int = 1;
        protected var exceptions:GrowableRail[Exception]; // lazily initialized
        protected var counts:Rail[Int];
//        protected var seen:Rail[Boolean];
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
//                seen = new Rail[Boolean](Place.MAX_PLACES);
            }
            counts(p.id)++;
            latch.unlock();
        }
        public def notifyActivityTermination():void {
            latch.lock();
            if (--count != 0) {
                latch.unlock();
                return;
            }
            if (counts != null && counts.size != 0L) {
                for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                    if (counts(i) != 0) {
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
            /*
            if (counts != null && counts.size != 0L) {
                val root = ref();
                val closure = ()=>@RemoteInvocation { Runtime.finishStates.remove(root); };
                seen(Runtime.hereInt()) = false;
                for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                    if (seen(i)) Runtime.x10rtSendMessage(i, closure, null);
                }
                Runtime.dealloc(closure);
            }
            */
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
        }

        protected def process(rail:Rail[Int]) {
            counts(ref().home.id) = -rail(ref().home.id);
            count += rail(ref().home.id);
            var b:Boolean = count == 0;
            for(var i:Long=0; i<Place.MAX_PLACES; i++) {
                counts(i) += rail(i);
//                seen(i) |= counts(i) != 0;
                if (counts(i) != 0) b = false;
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
//                seen(rail(i).first) = true;
            }
            count += counts(ref().home.id);
            counts(ref().home.id) = 0;
            if (count != 0) return;
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0) return;
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
        protected var count:Int = 0;
        protected var counts:Rail[Int];
        protected var places:Rail[Int];
        protected var length:Int = 1;
        @Embed protected val local = @Embed new AtomicInteger(0);
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifyActivityCreation():void {
            local.getAndIncrement();
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
            if (old == 0 && id != place.id) {
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
                length = 1;
            } else {
                val message = Unsafe.allocRailUninitialized[Pair[Int,Int]](1);
                message(0) = Pair[Int,Int](Runtime.hereInt(), count);
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_5") { deref[RootFinish](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_6") { deref[RootFinish](ref).notify(message); };
                }
            }
            count = 0;
            exceptions = null;
            lock.unlock();
            Runtime.x10rtSendMessage(ref.home.id, closure, null);
            Runtime.dealloc(closure);
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
        private def this(data:SerialData) { 
            super(data.data as GlobalRef[FinishState]);
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
        protected var count:Int = 0;
        protected var counts:Rail[Int];
        protected var places:Rail[Int];
        protected var length:Int = 1;
        @Embed protected val local = @Embed new AtomicInteger(0);
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifyActivityCreation():void {
            local.getAndIncrement();
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
            if (old == 0 && id != place.id) {
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
                length = 1;
            } else {
                val message = Unsafe.allocRailUninitialized[Pair[Int,Int]](1);
                message(0) = Pair[Int,Int](Runtime.hereInt(), count);
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_5") { deref[RootFinish](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_6") { deref[RootFinish](ref).notify(message); };
                }
            }
            count = 0;
            exceptions = null;
            lock.unlock();
            val h = Runtime.hereInt();
            if ((Place.MAX_PLACES < 1024) || (h%32 == 0) || (h-h%32 == (ref.home.id as int))) {
                Runtime.x10rtSendMessage(ref.home.id, closure, null);
            } else {
                val clx = ()=>@RemoteInvocation("notifyActivityTermination_7") { Runtime.x10rtSendMessage(ref.home.id, closure, null); };
                Runtime.x10rtSendMessage(h-h%32, clx, null);
                Runtime.dealloc(clx);
            }
            Runtime.dealloc(closure);
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
            for(var i:Int=0; i<Runtime.MAX_THREADS; i++) {
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
        private def this(data:SerialData) { 
            super(data.superclassData.data as GlobalRef[FinishState]);
            val tmpReducer = data.data as Reducible[T];
            reducer = tmpReducer;
            if (ref.home.id == Runtime.hereLong()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                val _ref = ref;
                me = Runtime.finishStates(ref, ()=>new RemoteCollectingFinish[T](_ref, tmpReducer));
            }
        }
        public def serialize():SerialData = new SerialData(reducer, super.serialize());
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
                length = 1;
            } else {
                val message = Unsafe.allocRailUninitialized[Pair[Int,Int]](1);
                message(0) = Pair[Int,Int](Runtime.hereInt(), count);
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_5") { deref[RootCollectingFinish[T]](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_6") { deref[RootCollectingFinish[T]](ref).notifyValue(message, result); };
                }
            }
            count = 0;
            exceptions = null;
            lock.unlock();
            Runtime.x10rtSendMessage(ref.home.id, closure, null);
            Runtime.dealloc(closure);
            Runtime.finishStates.remove(ref);
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
