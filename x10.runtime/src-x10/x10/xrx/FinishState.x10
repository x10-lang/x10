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

package x10.xrx;

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

    /**
     * Called by an activity running at the current Place when it
     * is initiating the spawn of an new async at dstPlace.
     *
     * Scheduling note: Will only be called on a full-fledged worker thread;
     *                  this method is allowed to block/pause.
     */
    abstract def notifySubActivitySpawn(dstPlace:Place):void;


    /**
     * Called by an activity running at the current Place when it
     * is "shifting" execution to the dstPlace bacause of an 'at'.
     *
     * Scheduling note: Will only be called on a full-fledged worker thread;
     *                  this method is allowed to block/pause.
     */
    def notifyShiftedActivitySpawn(dstPlace:Place):void {
        notifySubActivitySpawn(dstPlace);
    }

    /**
     * Called by an activity running at the current Place when it
     * has created a continution of itself via a low-level network 
     * interaction (for example a non-blocking collective) that will
     * continue logical execution of the current activity, potentially
     * on a different worker thread.
     *
     * Scheduling note: Will only be called on a full-fledged worker thread;
     *                  this method is allowed to block/pause.
     */
    abstract def notifyRemoteContinuationCreated():void;

    /**
     * Called at the Place at which the argument activity will execute.
     * If this method returns true, the activity will be submitted to
     * the XRX Pool for execution. If this method returns false, the activity
     * will not be submitted. 
     * 
     * Machinations: activity may actually be null when the XRX runtime
     *               is calling this method to simulate the stages in the
     *               Activity life-cycles from lowlevel code (eg implementation
     *               of at or asyncCopy).
     *
     * Scheduling note: May be called on @Immediate worker.
     *                  This method must not block or pause.
     */
    abstract def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean;


    /**
     * Called at the Place at which the argument activity will execute.
     * If this method returns true, the activity should be submitted to
     * the XRX Pool for execution. If this method returns false, the activity
     * should not be submitted. 
     * 
     * Machinations: activity may actually be null when the XRX runtime
     *               is calling this method to simulate the stages in the
     *               Activity life-cycles from lowlevel code (eg implementation
     *               of at or asyncCopy).
     *
     * Scheduling note: This variant may not be called on @Immediate worker.
     *                  Therefore this variant is allowed to block/pause.
     */
    abstract def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean;


    /**
     * Called at the Place at which the argument activity will execute.
     * If this method returns true, the activity should be submitted to
     * the XRX Pool for execution. If this method returns false, the activity
     * should not be submitted. 
     * 
     * Machinations: activity may actually be null when the XRX runtime
     *               is calling this method to simulate the stages in the
     *               Activity life-cycles from lowlevel code (eg implementation
     *               of at or asyncCopy).
     *
     * Scheduling note: This variant may not be called on @Immediate worker.
     *                  Therefore this variant is allowed to block/pause.
     */
    def notifyShiftedActivityCreation(srcPlace:Place, activity:Activity):Boolean {
        return notifyActivityCreationBlocking(srcPlace, activity);
    }


    /**
     * Called at the Place where activity creation failed to indicate
     * that an inbound activity could not be created due to an exceptional
     * condition. 
     *
     * Scheduling note: May be called on @Immediate worker.
     *                  This method must not block or pause.
     */
    abstract def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void; 

    /**
     * Called at the Place where a synthetic activity created for 
     * asyncCopy has been created and executed. 
     * This combines notifyActivityCreation(srcPlace) and
     * notifyActivityTerminated into a single interaction with the FinishState.
     *
     * Scheduling note: May be called on @Immediate worker.
     *                  This method must not block or pause.
     */
    abstract def notifyActivityCreatedAndTerminated(srcPlace:Place):void; 


    /**
     * Called to indicate that the currently executing activity 
     * has terminated successfully.
     *
     * Scheduling note: Will only be called on a full-fledged worker thread;
     *                  this method is allowed to block/pause.
     */
    abstract def notifyActivityTermination():void;

    /**
     * Called to indicate that the currently executing shifted activity 
     * has terminated at the remote place successfully and is logically
     * resuming back at its source Place.
     *
     * Scheduling note: Will only be called on a full-fledged worker thread;
     *                  this method is allowed to block/pause.
     */
    def notifyShiftedActivityCompletion():void {
        notifyActivityTermination();
    }


    /**
     * Called to record the CheckedThrowable which caused the currently executing 
     * activity to terminate abnormally.
     *
     * Scheduling note: Will only be called on a full-fledged worker thread;
     *                  this method is allowed to block/pause.
     */
    abstract def pushException(t:CheckedThrowable):void;

    /**
     * Called when the currently running activity needs to wait for the Finish
     * to terminate.
     *
     * Scheduling note: Will only be called on a full-fledged worker thread;
     *                  this method is allowed to block/pause.
     */
    abstract def waitForFinish():void;

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
        public def notifyRemoteContinuationCreated():void { 
            throw new IllegalOperationException("Cannot create remote continution under a LocalFinish");
        }
        public def notifyActivityCreation(srcPlace:Place, activity:Activity) = true;
        public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity) = true;
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            pushException(t);
            notifyActivityTermination();
        }
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {
            notifyActivityTermination();
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
            if (!Runtime.STRICT_FINISH) Runtime.worker().join(latch);
            latch.await();
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
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
        public def notifyRemoteContinuationCreated():void { } // no-op for this finish
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            pushException(t);
            notifyActivityTermination();
        }
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {
            notifyActivityTermination();
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
        public def notifyRemoteContinuationCreated():void { } // no-op for this finish
        public def notifyActivityCreation(srcPlace:Place, activity:Activity) = true;
        public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity) = true;
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            pushException(t);
            notifyActivityTermination();
        }
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {
            notifyActivityCreation(srcPlace, null);
            notifyActivityTermination();
        }
        public def notifyActivityTermination() {
            if (count.decrementAndGet() == 0n) {
                val excs = exceptions == null || exceptions.isEmpty() ? null : exceptions.toRail();
                val ref = this.ref();
                if (null != excs) {
                    at (ref.home) @Immediate("notifyActivityTermination_1") async {
                        for (e in excs) {
                            deref[FinishState](ref).pushException(e);
                        }
                        deref[FinishState](ref).notifyActivityTermination();
                    };
                } else {
                    at (ref.home) @Immediate("notifyActivityTermination_2") async {
                        deref[FinishState](ref).notifyActivityTermination();
                    };
                }
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

    static class RootFinishAsync extends RootFinishSkeleton {
        @Embed protected val latch = @Embed new SimpleLatch();
        protected var exception:CheckedThrowable = null;
        public def notifySubActivitySpawn(place:Place):void {}
        public def notifyRemoteContinuationCreated():void { } // no-op for this finish
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            pushException(t);
            notifyActivityTermination();
        }
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {
            notifyActivityTermination();
        }
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
    }

    static class RemoteFinishAsync extends RemoteFinishSkeleton {
        protected var exception:CheckedThrowable;
        def this(ref:GlobalRef[FinishState]) {
            super(ref);
        }
        public def notifyActivityCreation(srcPlace:Place, activity:Activity) = true;
        public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity) = true;
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            exception = t;
            notifyActivityTermination();
        }
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {
            notifyActivityTermination();
        }
        public def notifySubActivitySpawn(place:Place):void {}
        public def notifyRemoteContinuationCreated():void { } // no-op for this finish
        public def pushException(t:CheckedThrowable):void {
            exception = t;
        }
        public def notifyActivityTermination():void {
            val exc = exception; // don't capture this in @Immediate body
            val ref = this.ref();
            if (null != exc) {
                at (ref.home) @Immediate("notifyActivityTermination_1") async {
                    deref[FinishState](ref).pushException(exc);
                    deref[FinishState](ref).notifyActivityTermination();
                };
            } else {
                at (ref.home) @Immediate("notifyActivityTermination_2") async {
                    deref[FinishState](ref).notifyActivityTermination();
                };
            }
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
        public def notifyRemoteContinuationCreated():void { } // no-op for this finish
        public def notifyActivityCreation(srcPlace:Place, activity:Activity) = true; 
        public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity) = true;
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            if (!Configuration.silenceInternalWarnings()) {
                Runtime.println("Uncaught exception in uncounted activity");
                t.printStackTrace();
            }
        }
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {}
        public def notifyActivityTermination() {}
        public def pushException(t:CheckedThrowable) {
            if (!Configuration.silenceInternalWarnings()) {
                Runtime.println("Uncaught exception in uncounted activity");
                t.printStackTrace();
            }
        }
        public final def waitForFinish() { assert false; }
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
        public def notifyActivityCreation(srcPlace:Place, activity:Activity) = true;
        public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity) = true;
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {
            notifyActivityTermination();
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
        public def notifyRemoteContinuationCreated():void { } // no-op for remote finish
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
        public def notifyRemoteContinuationCreated():void { me.notifyRemoteContinuationCreated(); }
        public def notifyActivityCreation(srcPlace:Place, activity:Activity) { 
            return me.notifyActivityCreation(srcPlace, activity); 
        }
        public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity) { 
            return me.notifyActivityCreationBlocking(srcPlace, activity); 
        }
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            me.notifyActivityCreationFailed(srcPlace, t); 
        }
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {
            me.notifyActivityCreatedAndTerminated(srcPlace);
        }
        public def notifyActivityTermination() { me.notifyActivityTermination(); }
        public def pushException(t:CheckedThrowable) { me.pushException(t); }
        public def waitForFinish() { me.waitForFinish(); }
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
        def ensureRemoteActivities():void {
            if (remoteActivities == null) {
                remoteActivities = new HashMap[Long,Int]();
            }
        }
       public def notifySubActivitySpawn(place:Place):void {
            val p = place.parent(); // CUDA
            latch.lock();
            if (p == ref().home) {
                count++;
                latch.unlock();
                return;
            }
            ensureRemoteActivities();
            remoteActivities.put(p.id, remoteActivities.getOrElse(p.id, 0n)+1n);
            latch.unlock();
        }
        public def notifyRemoteContinuationCreated():void {
            latch.lock();
            ensureRemoteActivities();
            latch.unlock();
        }
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            pushException(t);
            notifyActivityTermination();
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
        public def process(excs:Rail[CheckedThrowable]):void {
            for (e in excs) {
                process(e);
            }
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
                remoteActivities.remove(here.id);
                for (placeId in remoteActivities.keySet()) {
                    at(Place(placeId)) @Immediate("remoteFinishCleanup") async Runtime.finishStates.remove(root);
                }
            }
            // throw exceptions here if any were collected via the execution of child activities
            val t = MultipleExceptions.make(exceptions);
            if (null != t) throw t;
            
            // if no exceptions, simply return
        }

        protected def process(remoteMap:HashMap[Long, Int]):void {
            ensureRemoteActivities();
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

        def notify(remoteMapBytes:Rail[Byte], excs:Rail[CheckedThrowable]):void {
            remoteMap:HashMap[Long, Int] = new x10.io.Deserializer(remoteMapBytes).readAny() as HashMap[Long, Int];
            latch.lock();
            process(excs);
            process(remoteMap);
            latch.unlock();
        }
        
        protected def process(remoteEntry:Pair[Long, Int]):void {
            ensureRemoteActivities();
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

        def notify(remoteEntry:Pair[Long, Int], excs:Rail[CheckedThrowable]):void {
            latch.lock();
            process(excs);
            process(remoteEntry);
            latch.unlock();
        }
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
        def ensureRemoteActivities() {
            if (remoteActivities == null) {
                remoteActivities = new HashMap[Long,Int]();
            }
        }
        public def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
            local.getAndIncrement();
            return true;
        }
        public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean {
            return notifyActivityCreation(srcPlace, activity);
        }
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            notifyActivityCreation(srcPlace, null);
            pushException(t);
            notifyActivityTermination();
        }
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {
            notifyActivityCreation(srcPlace, null);
            notifyActivityTermination();
        }
        public def notifySubActivitySpawn(place:Place):void {
            val id = Runtime.hereLong();
            lock.lock();
            if (place.id == id) {
                count++;
                lock.unlock();
                return;
            }
            ensureRemoteActivities();
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
            val excs = exceptions == null || exceptions.isEmpty() ? null : exceptions.toRail();
            exceptions = null;
            val ref = this.ref();
            if (remoteActivities != null && remoteActivities.size() != 0) {
                remoteActivities.put(here.id, count); // put our own count into the table
                // pre-serialize the hashmap here
                val serializer = new x10.io.Serializer();
                serializer.writeAny(remoteActivities);
                val serializedTable:Rail[Byte] = serializer.toRail();
                remoteActivities.clear();
                count = 0n;
                lock.unlock();
                if (null != excs) {
                    at(ref.home) @Immediate("notifyActivityTermination_1") async deref[RootFinish](ref).notify(serializedTable, excs);
                } else {
                    at(ref.home) @Immediate("notifyActivityTermination_2") async deref[RootFinish](ref).notify(serializedTable);
                }
            } else {
                val message = new Pair[Long, Int](here.id, count);
                count = 0n;
                lock.unlock();
                if (null != excs) {
                    at(ref.home) @Immediate("notifyActivityTermination_3") async deref[RootFinish](ref).notify(message, excs);
                } else {
                    at(ref.home) @Immediate("notifyActivityTermination_4") async deref[RootFinish](ref).notify(message);
                }
            }
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
        def ensureRemoteActivities():void {
            if (remoteActivities == null) {
                remoteActivities = new HashMap[Long,Int]();
            }
        }
        public def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
            local.getAndIncrement();
            return true;
        }
        public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean {
            return notifyActivityCreation(srcPlace, activity);
        }
        public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
            notifyActivityCreation(srcPlace, null);
            pushException(t);
            notifyActivityTermination();
        }
        public def notifyActivityCreatedAndTerminated(srcPlace:Place) {
            notifyActivityCreation(srcPlace, null);
            notifyActivityTermination();
        }
        public def notifySubActivitySpawn(place:Place):void {
            val id = Runtime.hereLong();
            lock.lock();
            if (place.id == id) {
                count++;
                lock.unlock();
                return;
            }
            ensureRemoteActivities();
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
            val excs = exceptions == null || exceptions.isEmpty() ? null : exceptions.toRail();
            exceptions = null;
            val ref = this.ref();
            val closure:()=>void;
            if (remoteActivities != null && remoteActivities.size() != 0) {
                remoteActivities.put(here.id, count); // put our own count into the table
                // pre-serialize the hashmap here
                val serializer = new x10.io.Serializer();
                serializer.writeAny(remoteActivities);
                val serializedTable:Rail[Byte] = serializer.toRail();
                remoteActivities.clear();
                count = 0n;
                lock.unlock();
                if (null != excs) {
                    closure = ()=>{ deref[RootFinish](ref).notify(serializedTable, excs); };
                } else {
                    closure = ()=>{ deref[RootFinish](ref).notify(serializedTable); };
                }
            } else {
                val message = new Pair[Long, Int](here.id, count);
                count = 0n;
                lock.unlock();
                if (null != excs) {
                    closure = ()=>{ deref[RootFinish](ref).notify(message, excs); };
                } else {
                    closure = ()=>{ deref[RootFinish](ref).notify(message); };
                }
            }
            val h = Runtime.hereInt();
            if ((Place.numPlaces() < 1024) || (h%32n == 0n) || (h-h%32n == (ref.home.id as Int))) {
                at(ref.home) @Immediate("notifyActivityTermination_1") async closure();
            } else {
                at(Place(h-h%32)) @Immediate("notifyActivityTermination_3") async {
                    at(ref.home) @Immediate("notifyActivityTermination_2") async closure();
                }
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
            val excs = exceptions == null || exceptions.isEmpty() ? null : exceptions.toRail();
            exceptions = null;
            val ref = this.ref();
            sr.placeMerge();
            val result = sr.result();
            sr.reset();
            if (remoteActivities != null && remoteActivities.size() != 0) {
            	remoteActivities.put(here.id, count); // put our own count into the table
                // pre-serialize the hashmap here
                val serializer = new x10.io.Serializer();
                serializer.writeAny(remoteActivities);
                val serializedTable:Rail[Byte] = serializer.toRail();
                remoteActivities.clear();
                count = 0n;
                lock.unlock();
            	if (null != excs) {
            		at(ref.home) @Immediate("notifyActivityTermination_1") async deref[RootCollectingFinish[T]](ref).notify(serializedTable, excs);
            	} else {
            		at(ref.home) @Immediate("notifyActivityTermination_2") async deref[RootCollectingFinish[T]](ref).notifyValue(serializedTable, result);
            	}
            } else {
            	val message = new Pair[Long, Int](here.id, count);
            	count = 0n;
            	lock.unlock();
            	if (null != excs) {
            	    at(ref.home) @Immediate("notifyActivityTermination_3") async deref[RootCollectingFinish[T]](ref).notify(message, excs);
            	} else {
            	    at(ref.home) @Immediate("notifyActivityTermination_4") async deref[RootCollectingFinish[T]](ref).notifyValue(message, result);
            	}
            }
        }
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
