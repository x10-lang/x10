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

import x10.compiler.RemoteInvocation;

import x10.util.HashMap;
import x10.util.Pair;
import x10.util.Stack;
import x10.util.concurrent.atomic.AtomicInteger;
import x10.io.CustomSerialization;
import x10.io.SerialData;

abstract class FinishState {
    abstract def notifySubActivitySpawn(place:Place):void;
    abstract def notifyActivityCreation():void;
    abstract def notifyActivityTermination():void;
    abstract def pushException(t:Throwable):void;
    abstract def waitForFinish(safe:Boolean):void;

    static def deref[T](root:GlobalRef[FinishState]) = (root as GlobalRef[FinishState]{home==here})() as T;

    // a finish with local asyncs only
    static class LocalFinish extends FinishState {
        private val count = new AtomicInteger(1);
        private val latch = new Latch();
        private var exceptions:Stack[Throwable]; // lazily initialized
        private val lock = new Lock();
        public def notifySubActivitySpawn(place:Place) {
            assert place.id == Runtime.hereInt();
            count.getAndIncrement();
        }
        public def notifyActivityCreation() {}
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0) latch.release();
        }
        public def pushException(t:Throwable) {
            lock.lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            lock.unlock();
        }
        public def waitForFinish(safe:Boolean) {
            notifyActivityTermination();
            if (!Runtime.NO_STEALS && safe) Runtime.worker().join(latch);
            latch.await();
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
        }
    }

    // a finish with one async in each place
    static class FinishAtEach extends FinishSkeleton implements CustomSerialization {
        def this() {
            super(new RootFinishAtEach());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(data:SerialData) { 
            super(data.data as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereInt()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = new RemoteFinishAsync(ref);
            }
        }
    }

    static interface NotifyThrowable {
        def notify(t:Throwable):void;
    }

    static class RootFinishAtEach extends RootFinishSkeleton implements NotifyThrowable {
        protected val latch = new Latch();
        private val count = new AtomicInteger(2*Place.MAX_PLACES);
        private var exceptions:Stack[Throwable]; // lazily initialized
        private val lock = new Lock();
        public def notifySubActivitySpawn(place:Place) {}
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0) latch.release();
        }
        public def pushException(t:Throwable) {
            lock.lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            lock.unlock();
        }
        public def waitForFinish(safe:Boolean) {
            if (!Runtime.NO_STEALS && safe) Runtime.worker().join(latch);
            latch.await();
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
        }
        public def notify(t:Throwable):void {
            pushException(t);
            notifyActivityTermination();
        }
    }

    // a finish without nested asyncs in remote asyncs
    static class FinishFlat extends FinishSkeleton implements CustomSerialization {
        def this() {
            super(new RootFinishFlat());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(data:SerialData) {
            super(data.data as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereInt()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = new RemoteFinishAsync(ref);
            }
        }
    }

    static class RootFinishFlat extends RootFinishSkeleton implements NotifyThrowable {
        protected val latch = new Latch();
        private val count = new AtomicInteger(0);
        private var exceptions:Stack[Throwable]; // lazily initialized
        private val lock = new Lock();
        public def notifySubActivitySpawn(place:Place) {
            count.incrementAndGet();
        }
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0) latch.release();
        }
        public def pushException(t:Throwable) {
            lock.lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            lock.unlock();
        }
        public def waitForFinish(safe:Boolean) {
            if (!Runtime.NO_STEALS && safe) Runtime.worker().join(latch);
            latch.await();
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
        }
        public def notify(t:Throwable):void {
            pushException(t);
            notifyActivityTermination();
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
            if (ref.home.id == Runtime.hereInt()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = new RemoteFinishAsync(ref);
            }
        }
    }

    static class RootFinishAsync extends RootFinishSkeleton implements NotifyThrowable {
        protected val latch = new Latch();
        protected var exception:Throwable = null;
        public def notifySubActivitySpawn(place:Place):void {}
        public def notifyActivityTermination():void {
            latch.release();
        }
        public def pushException(t:Throwable):void {
            exception = t;
        }
        public def notify(t:Throwable):void {
            exception = t;
            latch.release();
        }
        public def waitForFinish(safe:Boolean):void {
            if (!Runtime.NO_STEALS && safe) Runtime.worker().join(latch);
            latch.await();
            val t = MultipleExceptions.make(exception);
            if (null != t) throw t;
        }
    }

    static class RemoteFinishAsync extends RemoteFinishSkeleton {
        protected var exception:Throwable;
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifyActivityCreation():void {}
        public def notifySubActivitySpawn(place:Place):void {}
        public def pushException(t:Throwable):void {
            exception = t;
        }
        public def notifyActivityTermination():void {
            val t = MultipleExceptions.make(exception);
            val ref = this.ref();
            val closure:()=>void;
            if (null != t) {
                closure = ()=>@RemoteInvocation { deref[NotifyThrowable](ref).notify(t); };
            } else {
                closure = ()=>@RemoteInvocation { deref[FinishState](ref).notifyActivityTermination(); };
            }
            Runtime.runClosureAt(ref.home.id, closure);
            Runtime.dealloc(closure);
        }
    }

    // finish async there async back
    static class FinishReturn extends FinishSkeleton implements CustomSerialization {
        def this() {
            super(new RootFinishAsync());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(data:SerialData) { 
            super(data.data as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereInt()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = new UncountedFinish();
            }
        }
    }

    // a pseudo finish used to implement @Uncounted async
    static class UncountedFinish extends FinishState {
        public def notifySubActivitySpawn(place:Place) {}
        public def notifyActivityCreation() {}
        public def notifyActivityTermination() {}
        public def pushException(t:Throwable) {
            Runtime.println("Uncaught exception in uncounted activity");
            t.printStackTrace();
        }
        public final def waitForFinish(safe:Boolean) { assert false; }
    }

    // a mapping from finish refs to local finish objects
    static class FinishStates {
        private val map = new HashMap[GlobalRef[FinishState],FinishState]();
        private val lock = new Lock();

        // find or make the local finish for the finish ref
        public def apply(root:GlobalRef[FinishState], factory:()=>FinishState):FinishState{
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
    abstract static class RootFinishSkeleton extends FinishState {
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
        public def waitForFinish(safe:Boolean) { assert false; }
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
        public def pushException(t:Throwable) { me.pushException(t); }
        public def waitForFinish(safe:Boolean) { me.waitForFinish(safe); }
    }

    // the default finish implementation
    static class Finish extends FinishSkeleton implements CustomSerialization {
        protected def this(root:RootFinish) {
            super(root);
        }
        def this(latch:Latch) {
            this(new RootFinish(latch));
        }
        def this() {
            this(new Latch());
        }
        protected def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        private def this(data:SerialData) { 
            super(data.data as GlobalRef[FinishState]);
            if (ref.home.id == Runtime.hereInt()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = Runtime.runtime().finishStates.apply(ref, ()=>new RemoteFinish(ref));
            }
        }
    }

    static class RootFinish extends RootFinishSkeleton {
        protected val latch:Latch;
        protected var exceptions:Stack[Throwable]; // lazily initialized
        protected val counts:Rail[Int] = Rail.make[Int](Place.MAX_PLACES, 0);
        protected val seen:Rail[Boolean] = Rail.make[Boolean](Place.MAX_PLACES, false);
        protected val lock:Lock = new Lock();
        def this(latch:Latch) {
            this.latch = latch;
            counts(Runtime.hereInt())++;
        }
        public def notifySubActivitySpawn(place:Place):void {
            lock.lock();
            counts(place.parent().id)++;
            lock.unlock();
        }
        public def notifyActivityTermination():void {
            lock.lock();
            counts(Runtime.hereInt())--;
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0) {
                    lock.unlock();
                    return;
                }
            }
            latch.release();
            lock.unlock();
        }
        public def process(t:Throwable):void {
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
        }
        public def pushException(t:Throwable):void {
            lock.lock();
            process(t);
            lock.unlock();
        }
        public def waitForFinish(safe:Boolean):void {
            notifyActivityTermination();
            if (!Runtime.NO_STEALS && safe) Runtime.worker().join(latch);
            latch.await();
            val root = ref();
            val closure = ()=>@RemoteInvocation { Runtime.runtime().finishStates.remove(root); };
            seen(Runtime.hereInt()) = false;
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (seen(i)) Runtime.runClosureAt(i, closure);
            }
            Runtime.dealloc(closure);
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
        }

        protected def process(rail:Rail[Int]) {
            var b:Boolean = true;
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                counts(i) += rail(i);
                seen(i) |= counts(i) != 0;
                if (counts(i) != 0) b = false;
            }
            if (b) latch.release();
        }

        def notify(rail:Rail[Int]):void {
            lock.lock();
            process(rail);
            lock.unlock();
        }

        protected def process(rail:Rail[Pair[Int,Int]]):void {
            for(var i:Int=0; i<rail.length; i++) {
                counts(rail(i).first) += rail(i).second;
                seen(rail(i).first) = true;
            }
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0) {
                    return;
                }
            }
            latch.release();
        }

        def notify(rail:Rail[Pair[Int,Int]]):void {
            lock.lock();
            process(rail);
            lock.unlock();
        }

        def notify(rail:Rail[Int], t:Throwable):void {
            lock.lock();
            process(t);
            process(rail);
            lock.unlock();
        }

        def notify(rail:Rail[Pair[Int,Int]], t:Throwable):void {
            lock.lock();
            process(t);
            process(rail);
            lock.unlock();
        }
    }

    static class RemoteFinish extends RemoteFinishSkeleton {
        protected var exceptions:Stack[Throwable];
        protected val lock = new Lock();
        protected val counts = Rail.make[Int](Place.MAX_PLACES, 0);
        protected val places = Rail.make[Int](Place.MAX_PLACES, Runtime.hereInt());
        protected var length:Int = 1;
        protected var count:AtomicInteger = new AtomicInteger(0);
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifyActivityCreation():void {
            count.getAndIncrement();
        }
        public def notifySubActivitySpawn(place:Place):void {
            lock.lock();
            if (counts(place.id)++ == 0 && Runtime.hereInt() != place.id) {
                places(length++) = place.id;
            }
            lock.unlock();
        }
        public def pushException(t:Throwable):void {
            lock.lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            lock.unlock();
        }
        public def notifyActivityTermination():void {
            lock.lock();
            counts(Runtime.hereInt())--;
            if (count.decrementAndGet() > 0) {
                lock.unlock();
                return;
            }
            val t = MultipleExceptions.make(exceptions);
            val ref = this.ref();
            val closure:()=>void;
            if (2*length > Place.MAX_PLACES) {
                val message = Rail.make[Int](counts.length, 0, counts);
                if (null != t) {
                    closure = ()=>@RemoteInvocation { deref[RootFinish](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation { deref[RootFinish](ref).notify(message); };
                }
            } else {
                val message = Rail.make[Pair[Int,Int]](length, (i:Int)=>Pair[Int,Int](places(i), counts(places(i))));
                if (null != t) {
                    closure = ()=>@RemoteInvocation { deref[RootFinish](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation { deref[RootFinish](ref).notify(message); };
                }
            }
            counts.reset(0);
            length = 1;
            exceptions = null;
            lock.unlock();
            Runtime.runClosureAt(ref.home.id, closure);
            Runtime.dealloc(closure);
        }
    }

    static class StatefulReducer[T] {
        val reducer:Reducible[T];
        var result:T;
        var resultRail:Rail[T];
        var workerFlag:Rail[Boolean] = Rail.make[Boolean](Runtime.MAX_WORKERS, false);
        def this(r:Reducible[T]) {
            reducer = r;
            val zero = reducer.zero();
            result = zero;
            resultRail = Rail.make[T](Runtime.MAX_WORKERS, zero);
        }
        def accept(t:T) {
            result = reducer(result, t);
        }
        def accept(t:T, id:Int) {
            if ((id >= 0) && (id < Runtime.MAX_WORKERS)) {
                resultRail(id) = reducer(resultRail(id), t);
                workerFlag(id) = true;
            }
        }
        def placeMerge() {
            for(var i:Int=0; i<Runtime.MAX_WORKERS; i++) {
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
            super(data.superclassData);
            reducer = data.data as Reducible[T];
            if (ref.home.id == Runtime.hereInt()) {
                me = (ref as GlobalRef[FinishState]{home==here})();
            } else {
                me = Runtime.runtime().finishStates.apply(ref, ()=>new RemoteCollectingFinish[T](ref, reducer));
            }
        }
        public def serialize():SerialData = new SerialData(reducer, super.serialize());
        public def accept(t:T, id:Int) { (me as CollectingFinishState[T]).accept(t, id); }
        public def waitForFinishExpr(safe:Boolean) = (me as RootCollectingFinish[T]).waitForFinishExpr(safe);
    }

    static class RootCollectingFinish[T] extends RootFinish implements CollectingFinishState[T] {
        val sr:StatefulReducer[T];
        def this(reducer:Reducible[T]) {
           super(new Latch());
           sr = new StatefulReducer[T](reducer);
        }
        public def accept(t:T, id:Int) {
           sr.accept(t, id);
        }
        def notifyValue(rail:Rail[Int], v:T):void {
            lock.lock();
            sr.accept(v);
            process(rail);
            lock.unlock();
        }
        def notifyValue(rail:Rail[Pair[Int,Int]], v:T):void {
            lock.lock();
            sr.accept(v);
            process(rail);
            lock.unlock();
        }
        final public def waitForFinishExpr(safe:Boolean):T {
            waitForFinish(safe);
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
            counts(Runtime.hereInt())--;
            if (count.decrementAndGet() > 0) {
                lock.unlock();
                return;
            }
            val t = MultipleExceptions.make(exceptions);
            val ref = this.ref();
            val closure:()=>void;
            sr.placeMerge();
            val result = sr.result();
            sr.reset();
            if (2*length > Place.MAX_PLACES) {
                val message = Rail.make[Int](counts.length, 0, counts);
                if (null != t) {
                    closure = ()=>@RemoteInvocation { deref[RootCollectingFinish[T]](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation { deref[RootCollectingFinish[T]](ref).notifyValue(message, result); };
                }
            } else {
                val message = Rail.make[Pair[Int,Int]](length, (i:Int)=>Pair[Int,Int](places(i), counts(places(i))));
                if (null != t) {
                    closure = ()=>@RemoteInvocation { deref[RootCollectingFinish[T]](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation { deref[RootCollectingFinish[T]](ref).notifyValue(message, result); };
                }
            }
            counts.reset(0);
            length = 1;
            exceptions = null;
            lock.unlock();
            Runtime.runClosureAt(ref.home.id, closure);
            Runtime.dealloc(closure);
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
