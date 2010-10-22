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

import x10.compiler.Native;
import x10.compiler.NativeClass;
import x10.compiler.NativeString;
import x10.compiler.Pinned;
import x10.compiler.Global;
import x10.compiler.SuppressTransientError;

import x10.io.CustomSerialization;

import x10.lang.Runtime.Mortal;

import x10.util.HashMap;
import x10.util.GrowableRail;
import x10.util.Pair;
import x10.util.Random;
import x10.util.Stack;
import x10.util.concurrent.atomic.AtomicInteger;
import x10.util.Box;
/**
 * @author tardieu
 */
interface FinishState {

    /**
     * An activity is spawned under this finish (called by spawner).
     */
    @Global def notifySubActivitySpawn(place:Place):void;

    /**
     * An activity is created under this finish (called by spawnee).
     */
    @Global def notifyActivityCreation():void;

    /**
     * An activity created under this finish has terminated.
     * Also called be the activity governing the finish when it completes the finish body.
     */
    @Global def notifyActivityTermination():void;

    /**
     * Push an exception onto the stack.
     */
    @Global def pushException(t:Throwable):void;

    /**
     * Wait for pending subactivities to complete.
     */
    def waitForFinish(safe:Boolean):void;

    /**
     * Create a corresponding remote finish
     */
    @Global def makeRemote():RemoteFinishState;
    @Global def home():Place;

    
    @Pinned static class FinishStates implements (FinishState)=>RemoteFinishState {

        private val map = new HashMap[FinishState, RemoteFinishState]();
        private val lock = new Lock();

        public def apply(rootFinish:FinishState):RemoteFinishState{
            lock.lock();
            val finishState = map.getOrElse(rootFinish, null);
            if (null != finishState) {
                lock.unlock();
                return finishState;
            }
            
            val remoteFinish = rootFinish.makeRemote();
            map.put(rootFinish, remoteFinish);
            lock.unlock();
            return remoteFinish;
        }
        public def remove(rootFinish:FinishState) {
            lock.lock();
            map.remove(rootFinish);
            lock.unlock();
        }
    }

    @Pinned static class StatefulReducer[T] {
        val reducer:Reducible[T];
        var result:T;
        val MAX = 1000;
        var resultRail : Rail[T];
        var workerFlag : Rail[Boolean] = Rail.make[Boolean](MAX,(Int) => false);
        def this(r:Reducible[T]) {
            this.reducer=r;
            val zero = reducer.zero();
            this.result=zero;
            this.resultRail = Rail.make[T](MAX, (Int) => zero);
        }
        def accept(t:T) {
            this.result=reducer(result,t);
        }
        def accept(t: T, id : Int ) {
            if ((id >= 0 ) && (id < MAX)) {
                this.resultRail(id) = reducer(this.resultRail(id),t);
                this.workerFlag(id) = true;   
            }    
        }
       def  placeMerge(){
            for(var i:Int =0; i<MAX; i++) {
                if (this.workerFlag(i)) {
                    this.result = reducer(result,resultRail(i));
                    resultRail(i)=reducer.zero();
                }
            }
        }

        def result()=result;
        def reset() {
            result = reducer.zero();
        }
    }

    // Single class translation of an X10 2.0 class
    static class RootCollectingFinish[T] extends RootFinish {
        private val root = GlobalRef[RootCollectingFinish[T]](this);
        transient val sr:StatefulReducer[T];
        val reducer:Reducible[T];
        def this(r:Reducible[T]) {
           super();
           this.reducer=r;
           this.sr=new StatefulReducer[T](r);
        }
        @Global public def makeRemote() = new RemoteCollectingFinish[T](reducer);
        @Global public def equals(a:Any) =
            (a instanceof RootCollectingFinish[T]) && this.root.equals((a as RootCollectingFinish[T]).root);
        @Global public def hashCode():Int = root.hashCode();
        @Global public def home():Place = root.home;
        @Pinned def accept(t:T) {
           lock();
           sr.accept(t);
           unlock();
        }
        @Pinned def accept(t:T, id:Int) {
           sr.accept(t,id);
         }
        @Pinned def notify(rail:Rail[Int], v:T):void {
            var b:Boolean = true;
            lock();
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                counts(i) += rail(i);
                seen(i) |= counts(i) != 0;
                if (counts(i) != 0) b = false;
            }
            accept(v);
            if (b) release();
            unlock();
         }
         @Pinned def notify2(rail:Rail[Pair[Int,Int]], v:T):void {
            lock();
            for(var i:Int=0; i<rail.length; i++) {
                counts(rail(i).first) += rail(i).second;
                seen(rail(i).first) = true;
            }
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0) {
                    accept(v);
                    unlock();
                    return;
                }
            }
            accept(v);
            release();
            unlock();
        }
    
        //Collecting Finish Use: for start merger at each place to collect result
        @Pinned final public def waitForFinishExpr(safe:Boolean):T {
            waitForFinish(safe);
            sr.placeMerge();
        val result = sr.result();
            sr.reset();
            return result;
        }
    }
        
    static class RootFinish implements FinishState, Mortal {
        private val root = GlobalRef[RootFinish](this);
        transient protected val counts:Rail[Int];
        transient protected val seen:Rail[Boolean];
        transient protected var exceptions:Stack[Throwable];
        transient protected val latch:Latch; 
        def this() {
            this(new Latch());
        }
        def this(latch:Latch) {
            this.latch = latch;
            val c = Rail.make[Int](Place.MAX_PLACES, (Int)=>0);
            seen = Rail.make[Boolean](Place.MAX_PLACES, (Int)=>false);
            c(here.id) = 1;
            counts = c;
        }
        @Global public def hashCode() = root.hashCode();
        /**
           Two RootFinish's are equal if they have == root's. Thus if a
           RootFinish makes a round-trip through other places it will
           still be equal to the original RootFinish. This is the way to
           get the effect of 2.0 interning for global object references
           in their home place.
         */
        @Global public def equals(a:Any) =
            (a instanceof RootFinish) && (a as RootFinish).root.equals(this.root);
        @Global public def home():Place = root.home;

        @Pinned public def lock() = root().latch.lock();
        @Pinned public def unlock() = root().latch.unlock();
        @Pinned public def tryLock() = root().latch.tryLock();
        @Pinned public def release() = root().latch.release();
        @Pinned public def await() = root().latch.await();
        @Pinned public def apply() = root().latch.apply();
        
        @Pinned private def notifySubActivitySpawnLocal(place:Place):void {
            lock();
            counts(place.parent().id)++;
            unlock();
        }

        @Pinned private def notifyActivityTerminationLocal():void {
            lock();
            counts(here.id)--;
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0) {
                    unlock();
                    return;
                }
            }
            release();
            unlock();
        }

        @Pinned private def pushExceptionLocal(t:Throwable):void {
            lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            unlock();
        }

        @Pinned public def waitForFinish(safe:Boolean):void {
            if (!Runtime.NO_STEALS && safe) Runtime.worker().join(this.latch);
            await();
            val closure = ()=> @x10.compiler.RemoteInvocation { Runtime.runtime().finishStates.remove(this); };
            seen(Runtime.hereInt()) = false;
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (seen(i)) {
                    Runtime.runClosureAt(i, closure);
                }
            }
            Runtime.dealloc(closure);
            if (null != exceptions) {
                if (exceptions.size() == 1) {
                    val t = exceptions.peek();
                    if (t instanceof Error) {
                        throw t as Error;
                    }
                    if (t instanceof RuntimeException) {
                        throw t as RuntimeException;
                    }
                }
                throw new MultipleExceptions(exceptions);
            }
        }

       @Pinned def notify(rail:Rail[Int]):void {
            var b:Boolean = true;
            lock();
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                counts(i) += rail(i);
                seen(i) |= counts(i) != 0;
                if (counts(i) != 0) b = false;
            }
            if (b) release();
            unlock();
        }

        @Pinned def notify2(rail:Rail[Pair[Int,Int]]):void {
            lock();
            for(var i:Int=0; i<rail.length; i++) {
                counts(rail(i).first) += rail(i).second;
                seen(rail(i).first) = true;
            }
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0) {
                    unlock();
                    return;
                }
            }
            release();
            unlock();
        }

        @Pinned def notify(rail:Rail[Int], t:Throwable):void {
            pushExceptionLocal(t);
            notify(rail);
        }

        @Pinned def notify2(rail:Rail[Pair[Int,Int]], t:Throwable):void {
            pushExceptionLocal(t);
            notify2(rail);
        }
        
        @Global public def makeRemote() = new RemoteFinish();
        
        @Global public def notifySubActivitySpawn(place:Place):void {
            if (here.equals(root.home)) {
                val rf:RootFinish = (root as GlobalRef[RootFinish]{here==root.home})();
                rf.notifySubActivitySpawnLocal(place);
            } else {
                (Runtime.proxy(this) as RemoteFinish).notifySubActivitySpawn(place);
            }
        }

        @Global public def notifyActivityCreation():void {
            if (! here.equals(root.home))
                (Runtime.proxy(this) as RemoteFinish).notifyActivityCreation();
        }

        @Global public def notifyActivityTermination():void {
            if (here.equals(root.home)) {
                val rf:RootFinish = (root as GlobalRef[RootFinish]{here==root.home})();
                rf.notifyActivityTerminationLocal();
            } else {
                (Runtime.proxy(this) as RemoteFinish).notifyActivityTermination(this);
            }
        }

        @Global public def pushException(t:Throwable):void {
            if (here.equals(root.home)) {
                val rf:RootFinish = (root as GlobalRef[RootFinish]{here==root.home})();
                rf.pushExceptionLocal(t);
            } else {
                (Runtime.proxy(this) as RemoteFinish).pushException(t);
            }
        }

    }

    @Pinned static class RemoteCollectingFinish[T] extends RemoteFinish {
        val sr:StatefulReducer[T];
        val stepAtomic :AtomicInteger = new AtomicInteger(0);
        def this(r:Reducible[T]) {
          super();
          this.sr=new StatefulReducer[T](r);
        }
    
    /**
     * An activity created under this finish has terminated.
     */
    public def notifyActivityTermination(r:FinishState):Void {
        lock.lock();
        counts(here.id)--;
        if (count.decrementAndGet() > 0) {
            lock.unlock();
            return;
        }
        val e = exceptions;
        exceptions = null;
        lock.unlock();
        if (2*length > Place.MAX_PLACES) {
            if (null != e) {
                lock.lock();
                val m = Rail.make[Int](counts.length, 0, counts);
                for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
                length = 1;
                lock.unlock();
                val t:Throwable;
                if (e.size() == 1) {
                    t = e.peek();
                } else {
                    t = new MultipleExceptions(e);
                }
            val rrcf = (r as RootCollectingFinish[T]).root;
                val closure = () => @x10.compiler.RemoteInvocation { 
                    val rrcfHere = rrcf as GlobalRef[RootCollectingFinish[T]]{self.home==here};
                    rrcfHere().notify(m, t); 
                    Runtime.deallocObject(m); 
                };
                Runtime.runClosureAt(r.home().id, closure);
                Runtime.dealloc(closure);
                Runtime.deallocObject(m);
            } else {
                val path = pathCompute(r);
                //Fixme : Here should use await().
                while (stepAtomic.get() <path.first) {};
                lock.lock();
                val m = Rail.make[Int](counts.length, 0, counts);
                for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
                length = 1;
                lock.unlock();
                sr.placeMerge();              
                val x = sr.result();
                sr.reset();
                stepAtomic.set(0);
                if(path.second != r.home().id) {
                     val closure =() => @x10.compiler.RemoteInvocation {
                     (Runtime.proxy(r) as RemoteCollectingFinish[T]).notify(m, x);
                     Runtime.deallocObject(m);
                     };
                    Runtime.runClosureAt( path.second, closure);
                    Runtime.dealloc(closure);
                    }
                else {
                     val rrcf = (r as RootCollectingFinish[T]).root;
                     val closure = () => @x10.compiler.RemoteInvocation {
                         val rrcfHere = rrcf as GlobalRef[RootCollectingFinish[T]]{self.home==here};
                         rrcfHere().notify(m, x);
                         Runtime.deallocObject(m);
                     };
                     Runtime.runClosureAt( path.second, closure);
                     Runtime.dealloc(closure);
                    }

                Runtime.deallocObject(m);
            }
            
        } else {
            if (null != e) {
                lock.lock();
                val m = Rail.make[Pair[Int,Int]](length, (i:Int)=>Pair[Int,Int](message(i), counts(message(i))));
                for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
                length = 1;
                lock.unlock();

                val t:Throwable;
                if (e.size() == 1) {
                    t = e.peek();
                } else {
                    t = new MultipleExceptions(e);
                }
                val rrcf = (r as RootCollectingFinish[T]).root;
                val closure = () => @x10.compiler.RemoteInvocation { 
                     val rrcfHere = rrcf  as GlobalRef[RootCollectingFinish[T]]{self.home==here};
                     rrcfHere().notify2(m, t); 
                     Runtime.deallocObject(m); 
                };
                Runtime.runClosureAt(r.home().id, closure);
                Runtime.dealloc(closure);
                Runtime.deallocObject(m);
            } else {
                val path = pathCompute(r);
                //FIXME here should use await(). 
                while(stepAtomic.get() < path.first){};
                lock.lock();
                val m = Rail.make[Pair[Int,Int]](length, (i:Int)=>Pair[Int,Int](message(i), counts(message(i))));
                for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
                length = 1;
                lock.unlock();
                sr.placeMerge();
                val x = sr.result();
                sr.reset();
                stepAtomic.set(0);
                if(path.second != r.home().id) {
                     val closure = () => @x10.compiler.RemoteInvocation {
                     (Runtime.proxy(r) as RemoteCollectingFinish[T]).notify2(m, x);
                     Runtime.deallocObject(m);
                     };
                    Runtime.runClosureAt( path.second, closure);
                    Runtime.dealloc(closure);
                    }
                else {
                     val rrcf = (r as RootCollectingFinish[T]).root;
                     val closure = () => @x10.compiler.RemoteInvocation {
                         val rrcfHere = rrcf  as GlobalRef[RootCollectingFinish[T]]{self.home==here};
                         rrcfHere().notify2(m, x);
                         Runtime.deallocObject(m);
                     };
                     Runtime.runClosureAt( path.second, closure);
                     Runtime.dealloc(closure);
                    }

                Runtime.deallocObject(m);
            }
            
        }
        }
        def accept(t:T) {
            lock.lock();
            sr.accept(t);
            lock.unlock();
        }
        def accept(t:T, id:Int) {
            sr.accept(t,id);
        }
        def notify(rail:Rail[Int], v:T):Void {
            var b:Boolean = true;
            lock.lock();
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                counts(i) += rail(i);
            if (counts(i) != 0) b = false;
            }
            accept(v);
            stepAtomic.getAndIncrement();
//FIXME            if (b) release();
            lock.unlock();
        }

        def notify2(rail:Rail[Pair[Int,Int]], v:T):Void {
            lock.lock();
            
            for(var i:Int=0; i<rail.length; i++) {
                counts(rail(i).first) += rail(i).second;
                message(length++) = rail(i).first;
            }

            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (counts(i) != 0) {
                    accept(v);
                    stepAtomic.getAndIncrement();
                    lock.unlock();
                    return;
                }
            }
            accept(v);
            stepAtomic.getAndIncrement();
//FIXME            release();
            lock.unlock();
         }
        // Pair[Int,Int] the first one is how many steps need to wait
        // the second one is where to run this notify
        def pathCompute(r:FinishState):Pair[Int,Int] {
            val max = Place.MAX_PLACES;
            val id  = here.id;
            val step = Math.log2(max);
            var stage : Int = 0;
            var target : Int = 0;
            var interval : Int = 2;
            if(Runtime.INIT_THREADS==1) return Pair[Int,Int](0,r.home().id);
            if(id!=r.home().id){
                for (var i:Int =0; i < step; i++) {
                    if(id%interval != 0) {
                        val distance = id%interval;
                        stage = i;
                        target = here.id - distance;
                        if((id < r.home().id)&&(r.home().id < id+Math.pow2(stage)))
                            stage --;
                        if(here.id < Math.pow2(step))
                            return Pair[Int,Int](stage,target);
                        else {
                            if(stage > 0) {
                                for(var j:Int = stage; j>0;j--) {
                                    if((id + Math.pow2(j-1)) > (max-1))
                                        stage--;
                                }
                            }
                            return Pair[Int,Int](stage,target);
                        }
                    }
                interval = interval*2;
               }
               if(here.id == Math.pow2(step)) {
               val delta = max - here.id -1;
               if (delta == 0) return Pair[Int,Int](0,r.home().id);
               else  {
                   val subStep = Math.log2(delta);
                   return Pair[Int,Int](subStep+1,r.home().id);
                    }
                 }
             }
            assert (max>1);
            val subStep = Math.log2(max-1);
            if(Math.powerOf2(r.home().id))   return Pair[Int,Int](subStep,r.home().id);
            return Pair[Int,Int](subStep+1,r.home().id);
        }
    
    }

    // FIXME.  This class is not being used consistiently
    //         It claims it is a Pinned class, however when an activity
    //         terminates, instances of this class are then serialized
    //         back to the place of the root finish to transfer exceptions, etc.
    //         So, it really isn't a Pinned class after all...
    //         Probably need to pull out the set of fields that need to be 
    //         sent back to the root place and not send back the whole object
    //         or otherwise refactor.
    @Pinned static class RemoteFinish implements RemoteFinishState {
        /**
         * The Exception Stack is used to collect exceptions
         * issued when activities associated with this finish state terminate abruptly.
         */
        protected var exceptions:Stack[Throwable];

        /**
         * The monitor is used to serialize updates to the finish state.
         */
        @SuppressTransientError protected transient val lock = new Lock(); // FIXME: Marked transient, but this class needs to be refactored.

        /**
         * Keep track of the number of activities associated with this finish state.
         */
        protected val counts = Rail.make[Int](Place.MAX_PLACES, (Int)=>0);

        protected val message = Rail.make[Int](Place.MAX_PLACES, (Int)=>here.id);
        protected var length:Int = 1;

        protected var count:AtomicInteger = new AtomicInteger(0);

        public def notifyActivityCreation():void {
            count.getAndIncrement();
        }

        /**
         * An activity created under this finish has been created. Increment the count
         * associated with the finish.
         */
        public def notifySubActivitySpawn(place:Place):void {
            lock.lock();
            if (counts(place.id)++ == 0 && here.id != place.id) {
                message(length++) = place.id;
            }
            lock.unlock();
        }

        /**
         * An activity created under this finish has terminated.
         */
        public def notifyActivityTermination(r:FinishState):void {
            lock.lock();
            counts(here.id)--;
            if (count.decrementAndGet() > 0) {
                lock.unlock();
                return;
            }
            val e = exceptions;
            exceptions = null;
            if (2*length > Place.MAX_PLACES) {
                val m = Rail.make[Int](counts.length, 0, counts);
                for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
                length = 1;
                lock.unlock();
                if (null != e) {
                    val t:Throwable;
                    if (e.size() == 1) {
                        t = e.peek();
                    } else {
                        t = new MultipleExceptions(e);
                    }
                    val rrf = (r as RootFinish).root;
                    val closure = () => @x10.compiler.RemoteInvocation { 
                        val rrfHere = rrf as GlobalRef[RootFinish]{self.home==here};
                        rrfHere().notify(m, t); 
                        Runtime.deallocObject(m); 
                    };
                    Runtime.runClosureAt(r.home().id, closure);
                    Runtime.dealloc(closure);
                } else {
                    val rrf = (r as RootFinish).root;
                    val closure = () => @x10.compiler.RemoteInvocation {
                        val rrfHere = rrf as GlobalRef[RootFinish]{self.home==here};
                        rrfHere().notify(m);
                        Runtime.deallocObject(m); 
                    };
                    Runtime.runClosureAt(r.home().id, closure);
                    Runtime.dealloc(closure);
                }
                Runtime.deallocObject(m);
            } else {
                val m = Rail.make[Pair[Int,Int]](length, (i:Int)=>Pair[Int,Int](message(i), counts(message(i))));
                for (var i:Int=0; i<Place.MAX_PLACES; i++) counts(i) = 0;
                length = 1;
                lock.unlock();
                if (null != e) {
                    val t:Throwable;
                    if (e.size() == 1) {
                        t = e.peek();
                    } else {
                        t = new MultipleExceptions(e);
                    }
                    val rrf = (r as RootFinish).root;
                    val closure = () => @x10.compiler.RemoteInvocation { 
                        val rrfHere = rrf as GlobalRef[RootFinish]{self.home==here};
                        rrfHere().notify2(m, t); 
                        Runtime.deallocObject(m); 
                    };
                    Runtime.runClosureAt(r.home().id, closure);
                    Runtime.dealloc(closure);
                } else {
                    val rrf = (r as RootFinish).root;
                    val closure = () => @x10.compiler.RemoteInvocation { 
                        val rrfHere = rrf as GlobalRef[RootFinish]{self.home==here};
                        rrfHere().notify2(m); 
                        Runtime.deallocObject(m); 
                    };
                    Runtime.runClosureAt(r.home().id, closure);
                    Runtime.dealloc(closure);
                }
                Runtime.deallocObject(m);
            }
        }

        /**
         * Push an exception onto the stack.
         */
        public def pushException(t:Throwable):void {
            lock.lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            lock.unlock();
        }
     
    }


    /**
     * LocalRootFinish deals with the case that all asyncs in the finish are
     * in the same place as this finish. Therefore, no RemoteFinishState is
     * needed nor does the FinishState need a rail of counters: one is 
     * enough!
     */
    @Pinned static class LocalRootFinish extends Latch implements FinishState, Mortal, CustomSerialization {
        private var counts:int;
        private var exceptions:Stack[Throwable];
        public def this() {
            counts = 1;
        }
        public home()=here;
        public def notifySubActivitySpawnLocal(place:Place):void {
            lock();
            counts++;
            unlock();
            
        }

        public def notifyActivityTerminationLocal():void {
            lock();
            counts--;
            if (counts!= 0) {
                unlock();
                return;
            } 
            release();
            unlock();
            
        }

        public def pushExceptionLocal(t:Throwable):void {
            lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            unlock();
        }

        public def waitForFinish(safe:Boolean):void {
            if (!Runtime.NO_STEALS && safe) Runtime.worker().join(this);
            await();
            if (null != exceptions) {
                if (exceptions.size() == 1) {
                    val t = exceptions.peek();
                    if (t instanceof Error) {
                        throw t as Error;
                    }
                    if (t instanceof RuntimeException) {
                        throw t as RuntimeException;
                    }
                }
                throw new MultipleExceptions(exceptions);
            }
        }

        public def notifySubActivitySpawn(place:Place):void {	
            notifySubActivitySpawnLocal(place);
        }

        public def notifyActivityCreation():void {}

        public def notifyActivityTermination():void {	 
            this.notifyActivityTerminationLocal();
        }

        public def pushException(t:Throwable):void {
            this.pushExceptionLocal(t);
        }
        public def makeRemote():RemoteFinishState = null;

        public def serialize():Any {
            throw new UnsupportedOperationException("Cannot serialize "+typeName());
        }
    private def this(Any) {
            throw new UnsupportedOperationException("Cannot deserialize "+typeName());
        }
    }

    /**
     * SimpleRootFinish and SimpleRemoteFinish are desgined for the "finish"
     * which has asyncs that do not spawn asyncs in other places: in other words,
     * these asyncs in the finish do not have nested remote asyncs. In this case,
     * SimpleRootFinish still requires a rail of counters, but SimpleRemoteFinish
     * only needs a counter
     */
    @Pinned static class SimpleRemoteFinish implements RemoteFinishState {
        /**
         * The Exception Stack is used to collect exceptions
         * issued when activities associated with this finish state terminate abruptly.
         */
        protected var exceptions:Stack[Throwable];

        /**
         * The monitor is used to serialize updates to the finish state.
         */
        protected val lock = new Lock();

        /**
         * Keep track of the number of activities associated with this finish state.
         */
        protected var spawnedActCounts:Int;
        protected var liveActCounts:AtomicInteger = new AtomicInteger(0);

        public def notifyActivityCreation():void {
            liveActCounts.getAndIncrement();
        }

        /**
         * An activity created under this finish has been created. Increment the count
         * associated with the finish.
         */
        public def notifySubActivitySpawn(place:Place):void {
            lock.lock();
            spawnedActCounts++;
            lock.unlock();
        }

        /**
         * An activity created under this finish has terminated.
         */
         public def notifyActivityTermination(r:FinishState):void {
            lock.lock();
            spawnedActCounts--;
            if (liveActCounts.decrementAndGet() > 0) {
                lock.unlock();
                return;
            }
            //if terminated
            val e = exceptions;
            exceptions = null;
            val m = spawnedActCounts;
            spawnedActCounts = 0;
            lock.unlock();
            if (null != e) {
                val t:Throwable;
                if (e.size() == 1) {
                    t = e.peek();
                } else {
                    t = new MultipleExceptions(e);
                }
                val closure = () => @x10.compiler.RemoteInvocation { (r as SimpleRootFinish).notify(m,t);};
                Runtime.runClosureAt(r.home().id, closure);
                Runtime.dealloc(closure);
           } else {
                val closure = () => @x10.compiler.RemoteInvocation { (r as SimpleRootFinish).notify(m);};
                Runtime.runClosureAt(r.home().id, closure);
                Runtime.dealloc(closure);
           }
           Runtime.runtime().finishStates.remove(r);
                
        }
        /**
         * Push an exception onto the stack.
         */
        public def pushException(t:Throwable):void {
            lock.lock();
            if (null == exceptions) exceptions = new Stack[Throwable]();
            exceptions.push(t);
            lock.unlock();
        }
    }



    
     static class SimpleRootFinish extends Latch implements FinishState, Mortal {
         private val root = GlobalRef[SimpleRootFinish](this);
         transient protected var counts:int;
         transient protected var exceptions:Stack[Throwable];
     @SuppressTransientError transient protected val latch = new Latch();
                                        
         public def this() {
             counts = 1;
         }
     private def this(Any) {
             throw new UnsupportedOperationException("Cannot deserialize "+typeName());
         }

         @Global public def equals(a:Any) =
             (a instanceof SimpleRootFinish) && this.root.equals((a as SimpleRootFinish).root);
        @Global public def hashCode() = root.hashCode();
        @Global public def home()=root.home;

        @Pinned public def lock() = latch.lock();
        @Pinned public def unlock() = latch.unlock();
        @Pinned public def tryLock() = latch.tryLock();
        @Pinned public def release() = latch.release();
        @Pinned public def await() = latch.await();
        @Pinned public def apply() = latch.apply();
        
        @Pinned public  def notifySubActivitySpawnLocal(place:Place):void {
             lock();
             counts++;
             unlock();
         }

         @Pinned public def notifyActivityTerminationLocal():void {     
             lock();
             counts--;
             if (counts!= 0) {
                 unlock();
                 return;
             }   	 
             release();
             unlock();
         }
         @Pinned public def pushExceptionLocal(t:Throwable):void {
             lock();
             if (null == exceptions) exceptions = new Stack[Throwable]();
             exceptions.push(t);
             unlock();
         }
         @Pinned def notify(remoteCount:Int):void {
             var b:Boolean = true; 
             lock();
             counts+= remoteCount;
             if (counts == 0) release();
             unlock();
         }
         @Pinned def notify(remoteCount:Int,t:Throwable):void {
             pushExceptionLocal(t);
             notify(remoteCount);
         }
         @Pinned public def waitForFinish(safe:Boolean):void {
             if (!Runtime.NO_STEALS && safe) Runtime.worker().join(this);
             await();
             if (null != exceptions) {
                 if (exceptions.size() == 1) {
                     val t = exceptions.peek();
                     if (t instanceof Error) {
                         throw t as Error;
                     }
                     if (t instanceof RuntimeException) {
                         throw t as RuntimeException;
                     }
                 }
                 throw new MultipleExceptions(exceptions);
             }
         }

         @Global public def notifySubActivitySpawn(place:Place):void {
                if (here.equals(home())) {
                 val srf:SimpleRootFinish = (root as GlobalRef[SimpleRootFinish]{here==root.home})();
                 srf.notifySubActivitySpawnLocal(place);
             } else {
                 (Runtime.proxy(this) as SimpleRemoteFinish).notifySubActivitySpawn(place);
             }
         }
         
         @Global public def notifyActivityCreation():void {
             if (here.equals(root.home)){
                 (Runtime.proxy(this) as SimpleRemoteFinish).notifyActivityCreation();
             } 
         }
         
         @Global public def notifyActivityTermination():void {
             if (here.equals(root.home)) {
                 val srf:SimpleRootFinish = (root as GlobalRef[SimpleRootFinish]{here==root.home})();
                     srf.notifyActivityTerminationLocal();
             } else {
                 (Runtime.proxy(this) as SimpleRemoteFinish).notifyActivityTermination(this);
             }
         }
         
         @Global public def pushException(t:Throwable):void {
             if (here.equals(root.home)) {
                 val srf:SimpleRootFinish = (root as GlobalRef[SimpleRootFinish]{here==root.home})();
                     srf.pushExceptionLocal(t);
             } else {
                     (Runtime.proxy(this) as SimpleRemoteFinish).pushException(t);
             }
         }
         @Global public def makeRemote():RemoteFinishState{
             return new SimpleRemoteFinish();
         }
    }

    static interface RemoteFinishState {
        
        public def notifyActivityCreation():void;

        /**
         * An activity created under this finish has been created. Increment the count
         * associated with the finish.
         */
        public def notifySubActivitySpawn(place:Place):void;

        /**
         * An activity created under this finish has terminated.
         */
        public def notifyActivityTermination(r:FinishState):void;
        /**
         * Push an exception onto the stack.
         */
        public def pushException(t:Throwable):void;
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
