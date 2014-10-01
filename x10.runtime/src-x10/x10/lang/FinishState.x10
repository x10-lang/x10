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

package x10.lang;

import x10.compiler.*;

import x10.util.GrowableRail;
import x10.util.HashMap;
import x10.util.HashSet;
import x10.util.Map;
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
    abstract def pushException(t:CheckedThrowable):void;
    abstract def waitForFinish():void;
    abstract def simpleLatch():SimpleLatch;
    abstract def runAt(place:Place, body:()=>void, prof:Runtime.Profile):void;
    abstract def evalAt(place:Place, body:()=>Any, prof:Runtime.Profile):Any;

    static def deref[T](root:GlobalRef[FinishState]) = (root as GlobalRef[FinishState]{home==here})() as T;

    // a finish with local asyncs only
    static class LocalFinish extends FinishState {
        @Embed private val count = @Embed new AtomicInteger(1n);
        @Embed private val latch = @Embed new SimpleLatch();
        private var exceptions:GrowableRail[CheckedThrowable]; // lazily initialized
        public def notifySubActivitySpawn(place:Place) {
            assert place.id == Runtime.hereLong();
            count.getAndIncrement();
        }
        public def notifyActivityCreation(srcPlace:Place) = true;
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0n) latch.release();
        }
        public def pushException(t:CheckedThrowable) {
            latch.lock();
            if (null == exceptions) exceptions = new GrowableRail[CheckedThrowable]();
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
        private var exceptions:GrowableRail[CheckedThrowable]; // lazily initialized
        public def notifySubActivitySpawn(place:Place) {
            count.incrementAndGet();
        }
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0n) latch.release();
        }
        public def pushException(t:CheckedThrowable) {
            latch.lock();
            if (null == exceptions) exceptions = new GrowableRail[CheckedThrowable]();
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
        private var exceptions:GrowableRail[CheckedThrowable]; // lazily initialized
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
        public def pushException(t:CheckedThrowable) {
            lock.lock();
            if (null == exceptions) exceptions = new GrowableRail[CheckedThrowable]();
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
        protected var exception:CheckedThrowable = null;
        public def notifySubActivitySpawn(place:Place):void {}
        public def notifyActivityTermination():void {
            latch.release();
        }
        public def pushException(t:CheckedThrowable):void {
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
        protected var exception:CheckedThrowable;
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifyActivityCreation(srcPlace:Place) = true;
        public def notifySubActivitySpawn(place:Place):void {}
        public def pushException(t:CheckedThrowable):void {
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
        public def pushException(t:CheckedThrowable) {
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
        private var epoch:long = 42;
        private val map = new HashMap[GlobalRef[FinishState],FinishState]();
        @Embed private val lock = @Embed new Lock();

        // find or make the local finish for the finish ref
        public operator this(root:GlobalRef[FinishState], factory:()=>FinishState):FinishState{
            lock.lock();
            if (Runtime.CANCELLABLE) clear(root.epoch);
            var f:FinishState = map.getOrElse(root, null);
            if (null != f) {
                lock.unlock();
                return f;
            }
            f = factory();
            if(!Runtime.CANCELLABLE || root.epoch >= epoch) map.put(root, f);
            lock.unlock();
            return f;
        }

        // remove finish ref from table
        public def remove(root:GlobalRef[FinishState]) {
            lock.lock();
            map.remove(root);
            lock.unlock();
        }
        
        public def clear(epoch:long) {
            lock.lock();
            if (this.epoch < epoch) {
                map.clear();
                this.epoch = epoch;
            }
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
        public def pushException(t:CheckedThrowable) { me.pushException(t); }
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
        protected var count:Int = 1n; // locally created activities
        protected var exceptions:GrowableRail[CheckedThrowable]; // captured remote exceptions.  lazily initialized
        // remotely spawned activities (created RemoteFinishes). lazily initialized
        protected var remoteActivities:HashMap[Long,Int]; // key is place id, value is count for that place.

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
            if (remoteActivities == null) {
                remoteActivities = new HashMap[Long,Int]();
            }
            remoteActivities.put(p.id, remoteActivities.getOrElse(p.id, 0n)+1n);
            latch.unlock();
        }
        public def notifyActivityTermination():void {
            latch.lock();
            if (--count != 0n) {
                latch.unlock();
                return;
            }
            if (remoteActivities != null && remoteActivities.size() != 0) {
                for (entry in remoteActivities.entries()) {
                    if (entry.getValue() != 0n) {
                        latch.unlock();
                        return;
                    }
                }
            }
            latch.unlock();
            latch.release();
        }
        public def process(t:CheckedThrowable):void {
            if (null == exceptions) exceptions = new GrowableRail[CheckedThrowable]();
            exceptions.add(t);
        }
        public def pushException(t:CheckedThrowable):void {
            latch.lock();
            process(t);
            latch.unlock();
        }
        public def waitForFinish():void {
            notifyActivityTermination(); // remove our own activity from count
            if ((!Runtime.STRICT_FINISH) && (Runtime.STATIC_THREADS || remoteActivities == null)) {
                Runtime.worker().join(latch);
            }
            latch.await(); // sit here, waiting for all child activities to complete

            // if there were remote activities spawned, clean up the RemoteFinish objects which tracked them
            if (remoteActivities != null && remoteActivities.size() != 0) {
                val root = ref();
                val closure = ()=>@RemoteInvocation("remoteFinishCleanup") { Runtime.finishStates.remove(root); };
                remoteActivities.remove(here.id);
                for (placeId in remoteActivities.keySet()) {
                    Runtime.x10rtSendMessage(placeId, closure, null);
                }
                Unsafe.dealloc(closure);
            }
            // throw exceptions here if any were collected via the execution of child activities
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
            
            // if no exceptions, simply return
        }

        protected def process(remoteMap:HashMap[Long, Int]):void {
            // add the remote set of records to the local set
            for (remoteEntry in remoteMap.entries()) {
                remoteActivities.put(remoteEntry.getKey(), remoteActivities.getOrElse(remoteEntry.getKey(), 0n)+remoteEntry.getValue());
            }
        
            // add anything in the remote set which ran here to my local count, and remove from the remote set
            count += remoteActivities.getOrElse(ref().home.id, 0n);
            remoteActivities.remove(ref().home.id);
            
            // check if anything is pending locally
            if (count != 0n) return;
            
            // check to see if anything is still pending remotely
            for (entry in remoteActivities.entries()) {
                if (entry.getValue() != 0n) return;
            }
            
            // nothing is pending.  Release the latch
            latch.release();
        }

        def notify(remoteMapBytes:Rail[Byte]):void {
            remoteMap:HashMap[Long, Int] = new x10.io.Deserializer(remoteMapBytes).readAny() as HashMap[Long, Int]; 
            latch.lock();
            process(remoteMap);
            latch.unlock();
        }

        def notify(remoteMapBytes:Rail[Byte], t:CheckedThrowable):void {
            remoteMap:HashMap[Long, Int] = new x10.io.Deserializer(remoteMapBytes).readAny() as HashMap[Long, Int];
            latch.lock();
            process(t);
            process(remoteMap);
            latch.unlock();
        }
        
        protected def process(remoteEntry:Pair[Long, Int]):void {
            // add the remote record to the local set
            remoteActivities.put(remoteEntry.first, remoteActivities.getOrElse(remoteEntry.first, 0n)+remoteEntry.second);
        
            // check if anything is pending locally
            if (count != 0n) return;
        
            // check to see if anything is still pending remotely
            for (entry in remoteActivities.entries()) {
                if (entry.getValue() != 0n) return;
            }
        
            // nothing is pending.  Release the latch
            latch.release();
        }

        def notify(remoteEntry:Pair[Long, Int]):void {
            latch.lock();
            process(remoteEntry);
            latch.unlock();
        }

        def notify(remoteEntry:Pair[Long, Int], t:CheckedThrowable):void {
            latch.lock();
            process(t);
            process(remoteEntry);
            latch.unlock();
        }

        public def simpleLatch() = latch;
    }

    static class RemoteFinish extends RemoteFinishSkeleton {
        protected var exceptions:GrowableRail[CheckedThrowable];
        @Embed protected transient var lock:Lock = @Embed new Lock();
        protected var count:Int = 0n;
        protected var remoteActivities:HashMap[Long,Int]; // key is place id, value is count for that place.
        @Embed protected val local = @Embed new AtomicInteger(0n); // local count

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
            if (remoteActivities == null) {
                remoteActivities = new HashMap[Long,Int]();
            }
            val old = remoteActivities.getOrElse(place.id, 0n);
            remoteActivities.put(place.id, old+1n);
            lock.unlock();
        }
        public def pushException(t:CheckedThrowable):void {
            lock.lock();
            if (null == exceptions) exceptions = new GrowableRail[CheckedThrowable]();
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
            if (remoteActivities != null && remoteActivities.size() != 0) {
                remoteActivities.put(here.id, count); // put our own count into the table
                // pre-serialize the hashmap here
                val serializer = new x10.io.Serializer();
                serializer.writeAny(remoteActivities);
                val serializedTable:Rail[Byte] = serializer.toRail();
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_1") { deref[RootFinish](ref).notify(serializedTable, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_2") { deref[RootFinish](ref).notify(serializedTable); };
                }
                remoteActivities.clear();
            } else {
                val message = new Pair[Long, Int](here.id, count);
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_3") { deref[RootFinish](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_4") { deref[RootFinish](ref).notify(message); };
                }
            }
            count = 0n;
            exceptions = null;
            lock.unlock();
            Runtime.x10rtSendMessage(ref.home.id, closure, null);
            Unsafe.dealloc(closure);
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
        protected var exceptions:GrowableRail[CheckedThrowable];
        @Embed protected transient var lock:Lock = @Embed new Lock();
        protected var count:Int = 0n;
        protected var remoteActivities:HashMap[Long,Int]; // key is place id, value is count for that place.
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
            if (remoteActivities == null) {
                remoteActivities = new HashMap[Long,Int]();
            }

            val old = remoteActivities.getOrElse(place.id, 0n);
            remoteActivities.put(place.id, old+1n);
            lock.unlock();
        }
        public def pushException(t:CheckedThrowable):void {
            lock.lock();
            if (null == exceptions) exceptions = new GrowableRail[CheckedThrowable]();
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
            if (remoteActivities != null && remoteActivities.size() != 0) {
                remoteActivities.put(here.id, count); // put our own count into the table
                // pre-serialize the hashmap here
                val serializer = new x10.io.Serializer();
                serializer.writeAny(remoteActivities);
                val serializedTable:Rail[Byte] = serializer.toRail();
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_1") { deref[RootFinish](ref).notify(serializedTable, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_2") { deref[RootFinish](ref).notify(serializedTable); };
                }
                remoteActivities.clear();
            } else {
                val message = new Pair[Long, Int](here.id, count);
                if (null != t) {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_3") { deref[RootFinish](ref).notify(message, t); };
                } else {
                    closure = ()=>@RemoteInvocation("notifyActivityTermination_4") { deref[RootFinish](ref).notify(message); };
                }
            }
            count = 0n;
            exceptions = null;
            lock.unlock();
            val h = Runtime.hereInt();
            if ((Place.numPlaces() < 1024) || (h%32n == 0n) || (h-h%32n == (ref.home.id as Int))) {
                Runtime.x10rtSendMessage(ref.home.id, closure, null);
            } else {
                val clx = ()=>@RemoteInvocation("notifyActivityTermination_5") { Runtime.x10rtSendMessage(ref.home.id, closure, null); };
                Runtime.x10rtSendMessage(h-h%32, clx, null);
                Unsafe.dealloc(clx);
            }
            Unsafe.dealloc(closure);
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
            for (i in 0..(resultRail.size-1)) {
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
        def notifyValue(remoteMapBytes:Rail[Byte], v:T):void {
            remoteMap:HashMap[Long, Int] = new x10.io.Deserializer(remoteMapBytes).readAny() as HashMap[Long, Int];
            latch.lock();
            sr.accept(v);
            process(remoteMap);
            latch.unlock();
        }
        def notifyValue(remoteEntry:Pair[Long, Int], v:T):void {
            latch.lock();
            sr.accept(v);
            process(remoteEntry);
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
            if (remoteActivities != null && remoteActivities.size() != 0) {
            	remoteActivities.put(here.id, count); // put our own count into the table
                // pre-serialize the hashmap here
                val serializer = new x10.io.Serializer();
                serializer.writeAny(remoteActivities);
                val serializedTable:Rail[Byte] = serializer.toRail();
            	if (null != t) {
            		closure = ()=>@RemoteInvocation("notifyActivityTermination_1") { deref[RootCollectingFinish[T]](ref).notify(serializedTable, t); };
            	} else {
            		closure = ()=>@RemoteInvocation("notifyActivityTermination_2") { deref[RootCollectingFinish[T]](ref).notifyValue(serializedTable, result); };
            	}
            	remoteActivities.clear();
            } else {
            	val message = new Pair[Long, Int](here.id, count);
            	if (null != t) {
            		closure = ()=>@RemoteInvocation("notifyActivityTermination_3") { deref[RootCollectingFinish[T]](ref).notify(message, t); };
            	} else {
            		closure = ()=>@RemoteInvocation("notifyActivityTermination_4") { deref[RootCollectingFinish[T]](ref).notifyValue(message, result); };
            	}
            }
            count = 0n;
            exceptions = null;
            lock.unlock();
            Runtime.x10rtSendMessage(ref.home.id, closure, null);
            Unsafe.dealloc(closure);
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
