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
@Pinned public final class Runtime {

    @Native("java", "java.lang.System.err.println(#1)")
    @Native("c++", "x10aux::system_utils::println((#1)->toString()->c_str())")
    public native static def println(o:Object) : void;

    @Native("java", "java.lang.System.err.println()")
    @Native("c++", "x10aux::system_utils::println(\"\")")
    public native static def println() : void;

    @Native("java", "java.lang.System.err.printf(#4, #5)")
    @Native("c++", "x10aux::system_utils::printf(#4, #5)")
    public native static def printf[T](fmt:String, t:T) : void;

    @Native("c++", "(#1)._val")
    public static def nativeThis(x:Object) = 0L;

    @Native("c++", "((x10aux::ref<x10::lang::Closure>)(#4))->toNativeString()")
    public static def nativeClosureName[T](cl:T) = cl.toString();

    // Configuration options

    @Native("java", "x10.runtime.impl.java.Runtime.NO_STEALS")
    @Native("c++", "x10aux::no_steals()")
    public static NO_STEALS = false;

    @Native("java", "x10.runtime.impl.java.Runtime.INIT_THREADS")
    @Native("c++", "x10aux::num_threads()")
    public static INIT_THREADS = 1;

    @Native("java", "x10.runtime.impl.java.Runtime.STATIC_THREADS")
    @Native("c++", "x10aux::static_threads()")
    public static STATIC_THREADS = false;

    /**
     * Run body at place(id).
     * May be implemented synchronously or asynchronously.
     * Body cannot spawn activities, use clocks, or raise exceptions.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runAt(#1, #2)")
    @Native("c++", "x10aux::run_at(#1, #2)")
    public static def runAtNative(id:Int, body:()=>void):void { body(); }

    /**
     * Run body at place(id).
     * May be implemented synchronously or asynchronously.
     * Body cannot spawn activities, use clocks, or raise exceptions.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.deepCopy(#4)")
    @Native("c++", "x10aux::deep_copy<#1 >(#4)")
    public static native def deepCopy[T](o:T):T;

    /**
     * Java: run body synchronously at place(id) in the same node as the current place.
     * C++: run body. (no need for a native implementation)
     */
    @Native("java", "x10.runtime.impl.java.Runtime.runAtLocal(#1, #2)")
    public static def runAtLocal(id:Int, body:()=>void):void { body(); }

    /**
     * Java: pretend receiver is local.
     */
    @Native("java", "#4")
    public static def pretendLocal[T](x:T):T = x;

    /**
     * Return true if place(id) is in the current node.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.local(#1)")
    public static def isLocal(id:Int):Boolean = id == here.id;

    /**
     * Process one incoming message if any (non-blocking).
     */
    @Native("c++", "x10aux::event_probe()")
    public static def event_probe():void {}

    /** Accessors for native performance counters
     */
    @Native("c++","x10aux::asyncs_sent")
    static def getAsyncsSent() = 0 as Long;

    @Native("c++","x10aux::asyncs_sent = #1")
    static def setAsyncsSent(v:Long) { }

    @Native("c++","x10aux::asyncs_received")
    static def getAsyncsReceived() = 0 as Long;

    @Native("c++","x10aux::asyncs_received = #1")
    static def setAsyncsReceived(v:Long) { }

    @Native("c++","x10aux::serialized_bytes")
    static def getSerializedBytes() = 0 as Long;

    @Native("c++","x10aux::serialized_bytes = #1")
    static def setSerializedBytes(v:Long) { }

    @Native("c++","x10aux::deserialized_bytes")
    static def getDeserializedBytes() = 0 as Long;

    @Native("c++","x10aux::deserialized_bytes = #1")
    static def setDeserializedBytes(v:Long) { }

    @Native("c++", "x10::lang::Object::dealloc_object((x10::lang::Object*)#1.operator->())")
    public static def deallocObject (o:Object) { }

    @Native("c++", "x10aux::dealloc(#4.operator->())")
    public static def dealloc[T] (o:()=>T) { }

    @Native("c++", "x10aux::dealloc(#1.operator->())")
    public static def dealloc (o:()=>void) { }

    @Native("c++", "x10aux::DeserializationDispatcher::registerHandlers()")
    public static def registerHandlers() {}


    @NativeClass("java", "x10.runtime.impl.java", "Deque")
    @NativeClass("c++", "x10.lang", "Deque")
    @Pinned static final class Deque implements CustomSerialization {
        public native def this();

        public native def size():Int;

        public native def poll():Object;

        public native def push(t:Object):void;

        public native def steal():Object;

        public def serialize():Any {
            throw new UnsupportedOperationException("Cannot serialize "+typeName());
        }
	private def this(Any) {
            throw new UnsupportedOperationException("Cannot deserialize "+typeName());
        }
    }


  
  

  
    static class ClockPhases extends HashMap[Clock,Int] {
        static def make(clocks:Array[Clock]{rail}, phases:Array[Int]{rail}):ClockPhases {
            val clockPhases = new ClockPhases();
            for(var i:Int = 0; i < clocks.size; i++) 
            	clockPhases.put(clocks(i), phases(i));
            return clockPhases;
        }

        def register(clocks:Array[Clock]{rail}) {
            return new Array[Int](clocks.size, (i:Int)=>clocks(i).register());
        }

        def next() {
            for(clock:Clock in keySet()) clock.resumeUnsafe();
            for(clock:Clock in keySet()) clock.nextUnsafe();
        }
        def resume() {
            for(clock:Clock in keySet()) clock.resume();
        }

        def drop() {
            for(clock:Clock in keySet()) clock.dropInternal();
            clear();
        }

	// HashMap implments CustomSerialization, so we must as well
	// Only constructor is actually required, but stub out serialize as well
	// as a reminder that if instance fields are added to ClockPhases then
	// work will have to be done here to serialize them.
	public def serialize() = super.serialize(); 
	def this() { super(); }
	def this(a:Any) { super(a); }
    }


    /**
     * A mortal object is garbage collected when there are no remaining local refs even if remote refs might still exist
     */
    public interface Mortal { }


    static interface FinishState {

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
    }

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
	    latch = new Latch();
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
            if (!NO_STEALS && safe) worker().join(this.latch);
            await();
            val closure = ()=>runtime().finishStates.remove(this);
            seen(hereInt()) = false;
            for(var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (seen(i)) {
                    runAtNative(i, closure);
                }
            }
            dealloc(closure);
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
                val closure = () => { 
                    val rrcfHere = rrcf as GlobalRef[RootCollectingFinish[T]]{self.home==here};
                    rrcfHere().notify(m, t); 
                    deallocObject(m); 
                };
                runAtNative(r.home().id, closure);
                dealloc(closure);
                deallocObject(m);
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
                     val closure = () => {
                     (Runtime.proxy(r) as RemoteCollectingFinish[T]).notify(m, x);
                     deallocObject(m);
                     };
                    runAtNative( path.second, closure);
                    dealloc(closure);
                    }
                else {
                     val rrcf = (r as RootCollectingFinish[T]).root;
                     val closure = () => {
                         val rrcfHere = rrcf as GlobalRef[RootCollectingFinish[T]]{self.home==here};
                         rrcfHere().notify(m, x);
                         deallocObject(m);
                     };
                     runAtNative( path.second, closure);
                     dealloc(closure);
                    }

                deallocObject(m);
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
                val closure = () => { 
                     val rrcfHere = rrcf  as GlobalRef[RootCollectingFinish[T]]{self.home==here};
                     rrcfHere().notify2(m, t); 
                     deallocObject(m); 
                };
                runAtNative(r.home().id, closure);
                dealloc(closure);
                deallocObject(m);
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
                     val closure = () => {
                     (Runtime.proxy(r) as RemoteCollectingFinish[T]).notify2(m, x);
                     deallocObject(m);
                     };
                    runAtNative( path.second, closure);
                    dealloc(closure);
                    }
                else {
                     val rrcf = (r as RootCollectingFinish[T]).root;
                     val closure = () => {
                         val rrcfHere = rrcf  as GlobalRef[RootCollectingFinish[T]]{self.home==here};
                         rrcfHere().notify2(m, x);
                         deallocObject(m);
                     };
                     runAtNative( path.second, closure);
                     dealloc(closure);
                    }

                deallocObject(m);
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
            if (b) release();
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
            release();
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
            if(INIT_THREADS==1) return Pair[Int,Int](0,r.home().id);
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
                    val closure = () => { 
                        val rrfHere = rrf as GlobalRef[RootFinish]{self.home==here};
                        rrfHere().notify(m, t); 
                        deallocObject(m); 
                    };
                    runAtNative(r.home().id, closure);
                    dealloc(closure);
                } else {
                    val rrf = (r as RootFinish).root;
                    val closure = () => {
                        val rrfHere = rrf as GlobalRef[RootFinish]{self.home==here};
                        rrfHere().notify(m);
                        deallocObject(m); 
                    };
                    runAtNative(r.home().id, closure);
                    dealloc(closure);
                }
                deallocObject(m);
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
                    val closure = () => { 
                        val rrfHere = rrf as GlobalRef[RootFinish]{self.home==here};
                        rrfHere().notify2(m, t); 
                        deallocObject(m); 
                    };
                    runAtNative(r.home().id, closure);
                    dealloc(closure);
                } else {
                    val rrf = (r as RootFinish).root;
                    val closure = () => { 
                        val rrfHere = rrf as GlobalRef[RootFinish]{self.home==here};
                        rrfHere().notify2(m); 
                        deallocObject(m); 
                    };
                    runAtNative(r.home().id, closure);
                    dealloc(closure);
                }
                deallocObject(m);
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
            if (!NO_STEALS && safe) worker().join(this);
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
                val closure = () => { (r as SimpleRootFinish).notify(m,t);};
                runAtNative(r.home().id, closure);
                dealloc(closure);
           } else {
                val closure = () => { (r as SimpleRootFinish).notify(m);};
                runAtNative(r.home().id, closure);
                dealloc(closure);
           }
           runtime().finishStates.remove(r);
                
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
             if (!NO_STEALS && safe) worker().join(this);
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


    @NativeClass("java", "x10.runtime.impl.java", "Thread")
    @NativeClass("c++", "x10.lang", "Thread")
    @Pinned final static class Thread implements CustomSerialization {

        /**
         * Allocates new thread in current place
         */
        public native def this(body:()=>void, name:String);

        public static native def currentThread():Thread;

        public native def start():void;

        public native static def sleep(millis:Long):void; //throwsInterruptedException;

        public native static def sleep(millis:Long, nanos:Int):void; //throwsInterruptedException;

        public native static def park():void;

        public native static def parkNanos(nanos:Long):void;

        public native def unpark():void;

        public native def worker():Object;

        public native def worker(worker:Worker):void;

        public native def name():String;

        public native def name(name:String):void;

        public native def locInt():Int;

        public static native def getTid():Long;
        public native def home():Place;

        public def serialize():Any {
            throw new UnsupportedOperationException("Cannot serialize "+typeName());
        }
	private def this(Any) {
            throw new UnsupportedOperationException("Cannot deserialize "+typeName());
        }
    }


    @Pinned public final static class Worker implements ()=>void {
        val latch:Latch;
        // release the latch to stop the worker

        // bound on loop iterations to help j9 jit
        static BOUND = 100;

        // activity (about to be) executed by this worker
        private var activity:Activity = null;

        // pending activities
        private val queue = new Deque();

        // random number generator for this worker
        private val random:Random;

        // blocked activities (debugging info)
        private val debug = new GrowableRail[Activity]();

        private var tid:Long;

        //Worker Id for CollectingFinish
        private var workerId :Int;
        public def setWorkerId(id:Int) {
            workerId = id;
        }

        def this(latch:Latch, p:Int) {
            this.latch = latch;
            random = new Random(p + (p << 8) + (p << 16) + (p << 24));
        }

        // all methods are local

        // return size of the deque
        public def size():Int = queue.size();

        // return activity executed by this worker
        def activity() = activity;

        // poll activity from the bottom of the deque
        private def poll() = queue.poll() as Activity;

        // steal activity from the top of the deque
        def steal() = queue.steal() as Activity;

        // push activity at the bottom of the deque
        def push(activity:Activity):void = queue.push(activity);

        // run pending activities
        public def apply():void {
            tid = Thread.getTid();
            try {
                while (loop(latch, true));
            } catch (t:Throwable) {
                println("Uncaught exception in worker thread");
                t.printStackTrace();
            } finally {
                Runtime.report();
            }
        }

        // run activities while waiting on finish
        def join(latch:Latch):void {
            val tmp = activity; // save current activity
            while (loop(latch, false));
            activity = tmp; // restore current activity
        }

        // inner loop to help j9 jit
        private def loop(latch:Latch, block:Boolean):Boolean {
            for (var i:Int = 0; i < BOUND; i++) {
                if (latch()) return false;
                activity = poll();
                if (activity == null) {
                    activity = Runtime.scan(random, latch, block);
                    if (activity == null) return false;
                }
                debug.add(pretendLocal(activity));
                runAtLocal(activity.home().id, (activity as Activity).run.());
                debug.removeLast();
            }
            return true;
        }

        public def probe () : void {
            // process all queued activities
            val tmp = activity; // save current activity
            while (true) {
                activity = poll();
                if (activity == null) {
                    activity = tmp; // restore current activity
                    return;
                }
                debug.add(pretendLocal(activity));
                runAtLocal(activity.home().id, (activity as Activity).run.());
                debug.removeLast();
            }
        }

        def dump(id:Int, thread:Thread) {
            Runtime.printf(@NativeString "WORKER %d", id);
            Runtime.printf(@NativeString " = THREAD %#lx\n", tid);
            for (var i:Int=debug.length()-1; i>=0; i--) {
                debug(i).dump();
            }
            Runtime.println();
        }
    }

    public static def probe () {
        event_probe();
        worker().probe();
    }

    @Pinned static class Pool implements ()=>void {
        private val latch:Latch;
        private var size:Int; // the number of workers in the pool

        private var spares:Int = 0; // the number of spare workers in the pool

        private val lock = new Lock();

        private val semaphore = new Semaphore(0);

        // an upper bound on the number of workers
        private static MAX = 1000;

        // the workers in the pool
        private val workers:Rail[Worker];

        // the threads in the pool
        private val threads:Rail[Thread];

        def this(latch:Latch, size:Int) {
            this.latch = latch;
            this.size = size;
            val workers = Rail.make[Worker](MAX);
            val threads = Rail.make[Thread](size);

            // worker for the master thread
            val master = new Worker(latch, 0);
            workers(0) = master;
            threads(0) = Thread.currentThread();
            Thread.currentThread().worker(master);
            workers.apply(0).setWorkerId(0);

            // other workers
            for (var i:Int = 1; i<size; i++) {
                val worker = new Worker(latch, i);
                workers(i) = worker;
                threads(i) = new Thread(worker.apply.(), "thread-" + i);
                threads(i).worker(worker);
                (workers.apply(i)).setWorkerId(i);
            }
            this.workers = workers;
            this.threads = threads;
        }

        public def apply():void {
            val s = size;
            for (var i:Int = 1; i<s; i++) {
                threads(i).start();
            }
            workers(0)();
            while (size > 0) Thread.park();
        }

        // all methods are local

        // notify the pool a worker is about to execute a blocking operation
        def increase():void {
            lock.lock();
            if (spares > 0) {
                // if a spare is available increase parallelism
                spares--;
                lock.unlock();
                semaphore.release();
            } else {
                // allocate and start a new worker
                val i = size++;
                lock.unlock();
                assert (i < MAX);
                if (i >= MAX) {
                    println("TOO MANY THREADS... ABORTING");
                    System.exit(1);
                }
                val worker = new Worker(latch, i);
                workers(i) = worker;
                val thread = new Thread(worker.apply.(), "thread-" + i);
                thread.worker(worker);
                thread.start();
            }
        }

        // notify the pool a worker resumed execution after a blocking operation
        def decrease(n:Int):void {
            // increase number or spares
            lock.lock();
            spares += n;
            lock.unlock();
            // reduce parallelism
            semaphore.reduce(n);
        }

        // release permit (called by worker upon termination)
        def release() {
            semaphore.release();
            lock.lock();
            size--;
            if (size == 0) threads(0).unpark();
            lock.unlock();
        }

        // scan workers for activity to steal
        def scan(random:Random, latch:Latch, block:Boolean):Activity {
            var activity:Activity = null;
            var next:Int = random.nextInt(size);
            for (;;) {
                event_probe();
                if (null != workers(next)) { // avoid race with increase method
                    activity = workers(next).steal();
                }
                if (null != activity || latch()) return activity;
                if (semaphore.available() < 0) {
                    if (block) {
                        semaphore.release();
                        semaphore.acquire();
                    } else {
                        return activity;
                    }
                }
                if (++next == size) next = 0;
            }
        }

        def dump() {
            for (var i:Int=0; i<size; i++) {
                workers(i).dump(i, threads(i));
            }
        }
    }


    // for debugging
    static PRINT_STATS = false;

    static public def dump() {
        runtime().pool.dump();
    }

    // instance fields

    // per process members
    private transient val pool:Pool;

    // per place members
    @SuppressTransientError private transient val monitor = new Monitor();
    @SuppressTransientError private transient val finishStates = new FinishStates();

    // constructor

    private def this(pool:Pool):Runtime {
        this.pool = pool;
    }

    /**
     * The runtime instance associated with each place
     */
    private static runtime = PlaceLocalHandle[Runtime]();

    static def proxy(rootFinish:FinishState) = runtime().finishStates(rootFinish);

    /**
     * Return the current worker
     */
    private static def worker():Worker =
        pretendLocal(Thread.currentThread().worker() as Worker);

    /**
     * Return the current activity
     */
    public static def activity():Activity
               = worker().activity() as Activity;

    /**
     * Return the current place
     */
    @Native("c++", "x10::lang::Place_methods::_make(x10aux::here)")
    public static def here():Place = Thread.currentThread().home();

    /**
     * Return the id of the current place
     */
    @Native("c++", "x10aux::here")
    public static def hereInt():int = Thread.currentThread().locInt();

    /**
     * The amount of unscheduled activities currently available to this worker thread.
     * Intended for use in heuristics that control async spawning
     * based on the current amount of surplus work.
     */
    public static def surplusActivityCount():int = worker().size();

    /**
     * Run main activity in a finish
     */
    public static def start(init:()=>void, body:()=>void):void {
        val rootFinish = new RootFinish();
        val pool = new Pool(rootFinish.latch, INIT_THREADS);
        try {
            for (var i:Int=0; i<Place.MAX_PLACES; i++) {
                if (isLocal(i)) {
                    // needed because the closure can be invoked in places other than the p
                    runAtLocal(i, ()=>runtime.set(new Runtime(pretendLocal(pool))));
                }
            }
            registerHandlers();
            if (hereInt() == 0) {
                execute(new Activity(()=>{finish init(); body();}, rootFinish, true));
                pool();
                if (!isLocal(Place.MAX_PLACES - 1)) {
                    for (var i:Int=1; i<Place.MAX_PLACES; i++) {
                        runAtNative(i, worker().latch.release.());
                    }
                }
                rootFinish.waitForFinish(false);
            } else {
                pool();
            }
        } finally {
            if (PRINT_STATS) {
                println("ASYNC SENT AT PLACE " + here.id +" = " + getAsyncsSent());
                println("ASYNC RECV AT PLACE " + here.id +" = " + getAsyncsReceived());
            }
        }
    }

    static def report():void {
        runtime().pool.release();
    }

    // async -> at statement -> at expression -> future
    // do not introduce cycles!!!

    /**
     * Run async
     */
    public static def runAsync(place:Place, clocks:Array[Clock]{rail}, body:()=>void):void {
    	// Do this before anything else
        activity().ensureNotInAtomic();
        
        val state = currentState();
        val phases = clockPhases().register(clocks);
        state.notifySubActivitySpawn(place);
        if (place.id == hereInt()) {
            execute(new Activity(deepCopy(body), state, clocks, phases));
        } else {
            val c = ()=>execute(new Activity(body, state, clocks, phases));
            runAtNative(place.id, c);
        }
    }

    public static def runAsync(place:Place, body:()=>void):void {
    	// Do this before anything else
        activity().ensureNotInAtomic();
        
        val state = currentState();
        state.notifySubActivitySpawn(place);
        val ok = safe();
        if (place.id == hereInt()) {
            execute(new Activity(deepCopy(body), state, ok));
        } else {
            var closure:()=>void;
            // Workaround for XTENLANG_614
            if (ok) {
                closure = ()=>execute(new Activity(body, state, true));
            } else {
                closure = ()=>execute(new Activity(body, state, false));
            }
            runAtNative(place.id, closure);
            dealloc(closure);
        }
    }

    public static def runAsync(clocks:Array[Clock]{rail}, body:()=>void):void {
    	// Do this before anything else
        activity().ensureNotInAtomic();
        
        val state = currentState();
        val phases = clockPhases().register(clocks);
        state.notifySubActivitySpawn(here);
        execute(new Activity(body, state, clocks, phases));
    }

    public static def runAsync(body:()=>void):void {
    	// Do this before anything else
        activity().ensureNotInAtomic();
        
        val state = currentState();
        state.notifySubActivitySpawn(here);
        execute(new Activity(body, state, safe()));
    }

    public static def runUncountedAsync(place:Place, body:()=>void):void {
    	// Do this before anything else
        activity().ensureNotInAtomic();
        
        val ok = safe();
        if (place.id == hereInt()) {
            execute(new Activity(deepCopy(body), ok));
        } else {
            var closure:()=>void;
            // Workaround for XTENLANG_614
            if (ok) {
                closure = ()=>execute(new Activity(body, true));
            } else {
                closure = ()=>execute(new Activity(body, false));
            }
            runAtNative(place.id, closure);
            dealloc(closure);
        }
    }

    public static def runUncountedAsync(body:()=>void):void {
    	// Do this before anything else
        activity().ensureNotInAtomic();
        
        execute(new Activity(body, safe()));
    }

    /**
     * Run at statement
     */
    static class RemoteControl {
    	private val root = GlobalRef[RemoteControl](this);
        transient var e:Box[Throwable] = null;
        @SuppressTransientError transient val latch = new Latch();
        @Global public def equals(a:Any) =
        	(a instanceof RemoteControl) && this.root.equals((a as RemoteControl).root);
        @Global public def hashCode()=root.hashCode();
        @Global public def home() = root.home();
    }

    public static def runAt(place:Place, body:()=>void):void {
    	Runtime.ensureNotInAtomic();
        val box = (new RemoteControl()).root;
        async at(place) {
            try {
                body();
                async at(box.home) {
                	val me = box();
                	me.latch.release();
                }
            } catch (e:Throwable) {
                async at(box.home) {
                	val me = box();
                    me.e = new Box[Throwable](e);
                    me.latch.release();
                }
            }
        }
        val me = box();
        if (!NO_STEALS && safe()) worker().join(me.latch);
        me.latch.await();
        if (null != me.e) {
            val x = me.e.value;
            if (x instanceof Error)
                throw x as Error;
            if (x instanceof RuntimeException)
                throw x as RuntimeException;
        }
    }

    /**
     * Eval at expression
     */
    static class Remote[T] {
        transient var t:Box[T] = null;
        transient var e:Box[Throwable] = null;
        @SuppressTransientError transient val latch = new Latch();
        private val root = GlobalRef[Remote](this);
        @Global public def equals(a:Any)=
        	(a instanceof Remote[T]) && this.root.equals((a as Remote[T]).root);
        @Global public def hashCode()=root.hashCode();
        @Global public def home() = root.home();
    }

    public static def evalAt[T](place:Place, eval:()=>T):T {
        val box = (new Remote[T]()).root;
        async at(place) {
            try {
                val result = eval();
                async at(box.home) {
                	val me = box();
                    me.t = result;
                    me.latch.release();
                }
            } catch (e:Throwable) {
                async at(box.home) {
                	val me = box();
                	me.e = e;
                    me.latch.release();
                }
            }
        }
        val me = box();
        if (!NO_STEALS && safe()) worker().join(me.latch);
        me.latch.await();
        if (null != me.e) {
            val x = me.e.value;
            if (x instanceof Error)
                throw x as Error;
            if (x instanceof RuntimeException)
                throw x as RuntimeException;
        }
        return me.t.value;
    }

    /**
     * Eval future expression
  
    public static def evalFuture[T](place:Place, eval:()=>T):Future[T] {
        val f = at (place) {
                   val f1 = new Future[T](eval);
                   async f1.run();
                   f1
                };
        return f;
    }
   */

    // atomic, await, when

    /**
     * Lock current place
     * not reentrant!
     */
    public static def lock():void {
        runtime().monitor.lock();
    }
    
    public static def enterAtomic() {
    	lock();
    	activity().pushAtomic();
    }
    public static def inAtomic():boolean = activity().inAtomic();
    public static def ensureNotInAtomic() {
    	val act = activity();
    	if (act != null) // could be null in main thread?
    	   act.ensureNotInAtomic();
    }
    public static def exitAtomic() {
    	activity().popAtomic();
    	release();
    }

    /**
     * Wait on current place lock
     * Must be called while holding the place lock
     */
    public static def await():void {
        runtime().monitor.await();
    }

    /**
     * Unlock current place
     * Notify all
     */
    public static def release():void {
        runtime().monitor.release();
    }


    // clocks

    /**
     * Return the clock phases for the current activity
     */
    static def clockPhases():ClockPhases {
        val a = activity();
        if (null == a.clockPhases)
            a.clockPhases = new ClockPhases();
        return a.clockPhases;
    }

    /**
     * Next statement = next on all clocks in parallel.
     */
    public static def next():void {
    	ensureNotInAtomic();
    	clockPhases().next();
    }
    
    /**
     * Resume statement = resume on all clocks in parallel.
     */
    public static def resume():void = clockPhases().resume();


    // finish

    /**
     * Return the innermost finish state for the current activity
     */
    private static def currentState():FinishState {
        val a = activity();
        if (null == a.finishStack || a.finishStack.isEmpty())
            return a.finishState;
        return a.finishStack.peek();
    }

    /**
     * Start executing current activity synchronously
     * (i.e. within a finish statement).
     */
    public static def startFinish():void {
        val a = activity();
        if (null == a.finishStack)
            a.finishStack = new Stack[FinishState]();
        a.finishStack.push(new RootFinish());
    }

    public static def startLocalFinish():void {
        val a = activity();
        if (null == a.finishStack)
            a.finishStack = new Stack[FinishState]();
        val r = new LocalRootFinish();
        a.finishStack.push(r);
    }

    public static def startSimpleFinish():void {
        val a = activity();
        if (null == a.finishStack)
            a.finishStack = new Stack[FinishState]();
        val r = new SimpleRootFinish();
        a.finishStack.push(r);
    }

    /**
     * Suspend until all activities spawned during this finish
     * operation have terminated. Throw an exception if any
     * async terminated abruptly. Otherwise continue normally.
     * Should only be called by the thread executing the current activity.
     */
    public static def stopFinish():void {
        val a = activity();
        val finishState = a.finishStack.pop();
        finishState.notifyActivityTermination();
        finishState.waitForFinish(safe());
    }

    /**
     * Push the exception thrown while executing s in a finish s,
     * onto the finish state.
     */
    public static def pushException(t:Throwable):void  {
        currentState().pushException(t);
    }

    private static def safe():Boolean {
        val a = activity();
        return a.safe && (null == a.clockPhases);
    }


    static def scan(random:Random, latch:Latch, block:Boolean):Activity {
        return runtime().pool.scan(random, latch, block);
    }


    // submit an activity to the pool
    private static def execute(activity:Activity):void {
        worker().push(activity);
    }

    // notify the pool a worker is about to execute a blocking operation
    static def increaseParallelism():void {
        if (!STATIC_THREADS) {
            runtime().pool.increase();
        }
    }

    // notify the pool a worker resumed execution after a blocking operation
    static def decreaseParallelism(n:Int) {
        if (!STATIC_THREADS) {
            runtime().pool.decrease(n);
        }
    }

    // park current thread
    static def park() {
        if (!STATIC_THREADS) {
            Thread.park();
        } else {
            event_probe();
        }
    }

    // unpark given thread
    static def unpark(thread:Thread) {
        if (!STATIC_THREADS) {
            thread.unpark();
        }
    }
    //Collecting Finish Implementation
    // All these methods should be moved to Pool.
    @Pinned public static class CollectingFinish[T] {
        //Exposed API
    	// should become startFinish(r:Reducible[T])
        public def this(r:Reducible[T]) {
        	val a = activity();
        	if (null == a.finishStack)
        		a.finishStack = new Stack[FinishState]();
        	a.finishStack.push(new RootCollectingFinish[T](r));

        }

        public static def offer[T](t:T) {
            val thisWorker = worker();
            val id = thisWorker.workerId;
            val state = currentState();
	    //	    Console.OUT.println("Place(" + here.id + ") Runtime.offer: received " + t);
            if (here.equals(state.home())) {
                (state as RootCollectingFinish[T]).accept(t,id);
            } else {
                (Runtime.proxy(state as RootFinish) as RemoteCollectingFinish[T]).accept(t,id);
            }
       }
        public def stopFinishExpr():T {
        	 val thisWorker = worker();
             val id = thisWorker.workerId;
             val state = currentState();
             (state as RootCollectingFinish[T]).notifyActivityTermination();                       
             //assert here.equals(home);
             val result = (state as RootCollectingFinish[T]).waitForFinishExpr(true);
             val a = activity();
             a.finishStack.pop();  
             return result;
            
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
