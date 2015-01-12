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

package x10.glb;

import x10.compiler.*;
import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Random;
import x10.xrx.Runtime;

/**
 * The local runner for the GLB framework. An instance of this class runs at each
 * place and provides the context within which user-specified tasks execute and
 * are load balanced across all places.
 * @param <Queue> Concrete TaskQueue type
 * @param <R> Result type.
 */
final class Worker[Queue, R]{Queue<:TaskQueue[Queue, R]} {
    /** TaskQueue, responsible for crunching numbers */
    val queue:Queue;
    
    /** Read as I am the "lifeline buddy" of my "lifelineThieves" */
    val lifelineThieves:FixedSizeStack[Long];
    
    
    /** Thieves that send stealing requests*/
    val thieves:FixedSizeStack[Long];
    
    /** Lifeline buddies */
    val lifelines:Rail[Long];
    
    /** The data structure to keep a key invariant: 
     * At any time, at most one message has been sent on an
     * outgoing lifeline (and hence at most one message has
     * been received on an incoming lifeline).*/
    val lifelinesActivated:Rail[Boolean];
    
    /** The granularity of tasks to run in a batch before starts to probe network to respond to work-stealing 
     * requests. The smaller n is, the more responsive to the work-stealing requests; on the other hand, less focused
     * on computation */
    val n:Int;
    
    /** Number of random victims to probe before sending requests to lifeline buddy*/
    val w:Int;
    
    /** Maximum number of random victims */
    val m:Int;
    
    /** Random number, used when picking a non-lifeline victim/buddy. Important to seed with place id, otherwise
      BG/Q, the random sequence will be exactly same at different places*/
    val random = new Random(Runtime.hereInt());
    
    /** Random buddies, a runner first probes its random buddy, only when none of the random buddies responds
     *  it starts to probe its lifeline buddies */
    val victims:Rail[Long];
    
    /** Logger to record the work-stealing status */
    val logger:Logger;
    
    /** Variables used for synchronization, made sure not to be optimized out by the compiler */
    @x10.compiler.Volatile transient var active:Boolean = false;
    @x10.compiler.Volatile transient var empty:Boolean = true;
    @x10.compiler.Volatile transient var waiting:Boolean = false;
    
    /* Number of places.*/
    val P = Place.numPlaces();
    
    /*Context object accessible to user code, which can be used to yield.*/
    var context:Context[Queue, R];
    
    /**
     * Class constructor
     * @param init function closure to init the local {@link TaskQueue}
     * @param n same to this.n
     * @param w same to this.w
     * @param m same to this.m
     * @param l power of lifeline graph
     * @param z base of lifeline graph
     * @param tree true if the workload is dynamically generated, false if the workload can be statically generated
     */
    public def this(init:()=>Queue, n:Int, w:Int, l:Int, z:Int, m:Int, tree:Boolean) {
        this.n = n;
        this.w = w;
        this.m = m;
        this.lifelines = new Rail[Long](z, -1);
        
        val h = Runtime.hereLong();
        
        victims = new Rail[Long](m);
        if (P>1) for (var i:Long=0; i<m; i++) {
            while ((victims(i) = random.nextLong(P)) == h);
        }
        
        // lifelines
        var x:Long = 1;
        var t:Long = 0;
        for (var j:Long=0; j<z; j++) {
            var v:Long = h;
            for (var k:Long=1; k<l; k++) {
                v = v - v%(x*l) + (v+x*l-x)%(x*l);
                if (v<P) {
                    lifelines(t++) = v;
                    break;
                }
            }
            x *= l;
        }
        
        queue = init();
        lifelineThieves = new FixedSizeStack[Long](lifelines.size+3);
        thieves = new FixedSizeStack[Long](P);
        lifelinesActivated = new Rail[Boolean](P);
        
        if (tree) {
            // 1st wave
            if (3*h+1 < P) lifelineThieves.push(3*h+1);
            if (3*h+2 < P) lifelineThieves.push(3*h+2);
            if (3*h+3 < P) lifelineThieves.push(3*h+3);
            if (h > 0) lifelinesActivated((h-1)/3) = true;
        }
        
        logger = new Logger(true);
    }
    
    /**
     * Main process function of Worker. It does 4 things:
     * (1) execute at most n tasks 
     * (2) respond to stealing requests
     * (3) when not worth sharing tasks, reject the stealing requests 
     * (4) when running out of tasks, steal from others
     * @param st the place local handle of Worker
     */
    final def processStack(st:PlaceLocalHandle[Worker[Queue, R]]){Queue<:TaskQueue[Queue, R]} {
        do {
            while (queue.process(n, context)) {
                Runtime.probe();
                distribute(st);
                reject(st);
            }
            reject(st);
        } while (steal(st));
    }
    
    /**
     * Send out the workload to thieves. At this point, either thieves or lifelinetheives 
     * is non-empty (or both are non-empty). Note sending workload to the lifeline thieve
     * is the only place that uses async (instead of uncounted async as in other places),
     * which means when only all lifeline requests are responded can the framework be terminated.
     * @param st place local handle of LJR
     * @param loot the taskbag(aka workload) to send out
     */
    @Inline def give(st:PlaceLocalHandle[Worker[Queue, R]], loot:TaskBag) {
        val victim = Runtime.hereLong();
        logger.nodesGiven += loot.size();
        if (thieves.size() > 0) {
            val thief = thieves.pop();
            if (thief >= 0) {
                ++logger.lifelineStealsSuffered;
                at (Place(thief)) @Uncounted async { st().deal(st, loot, victim); st().waiting = false; }
            } else {
                ++logger.stealsSuffered;
                at (Place(-thief-1)) @Uncounted async { st().deal(st, loot, -1); st().waiting = false; }
            }
        } else {
            ++logger.lifelineStealsSuffered;
            val thief = lifelineThieves.pop();
            at (Place(thief)) async st().deal(st, loot, victim);
        }
    }
    
    /**
     * Distribute works to (lifeline) thieves by calling the 
     * {@link #give(st:PlaceLocalHandle[Worker[Queue, R]], loot:TaskBag) method
     * @param st place local handle of Worker
     */
    @Inline def distribute(st:PlaceLocalHandle[Worker[Queue, R]]) {
        var loot:TaskBag;
        while (((thieves.size() > 0) || (lifelineThieves.size() > 0)) && (loot = queue.split()) != null) {
            give(st, loot);
        }
    }
    
    /**
     * Rejecting thieves when no task to share (or worth sharing). Note, never reject lifeline thief,
     * instead put it into the lifelineThieves stack,
     * @param st place local handle of Worker
     */
    @Inline def reject(st:PlaceLocalHandle[Worker[Queue, R]]) {
        while (thieves.size() > 0) {
            val thief = thieves.pop();
            if (thief >= 0) {
                lifelineThieves.push(thief);
                at (Place(thief)) @Uncounted async { st().waiting = false; }
            } else {
                at (Place(-thief-1)) @Uncounted async { st().waiting = false; }
            }
        }
    }
    
    /**
     * Send out steal requests.
     * It does following things:
     * (1) Probes w random victims and send out stealing requests by calling into 
     * {@link #request(st:PlaceLocalHandle[Worker[Queue, R]], thief:Long, lifeline:Boolean)}
     * (2) If probing random victims fails, resort to lifeline buddies
     * In both case, it sends out the request and wait on the thieves' response, which either comes from
     * (i){@link #reject(PlaceLocalHandle[Worker[Queue, R]])} when victim has no workload to share
     * or (ii) {@link #give(PlaceLocalHandle[Worker[Queue, R]]],TaskBag)} when victim gives the workload
     * 
     * @param st PHL for Worker
     */
    def steal(st:PlaceLocalHandle[Worker[Queue, R]]) {
        if (P == 1) return false;
        val p = Runtime.hereLong();
        empty = true;
        for (var i:Long=0; i < w && empty; ++i) {
            ++logger.stealsAttempted;
            waiting = true;
            logger.stopLive();
            val v = victims(random.nextInt(m));
            at (Place(v)) @Uncounted async st().request(st, p, false);
            while (waiting) Runtime.probe();
            logger.startLive();
        }
        for (var i:Long=0; (i<lifelines.size) && empty && (0<=lifelines(i)); ++i) {
            val lifeline = lifelines(i);
            if (!lifelinesActivated(lifeline)) {
                ++logger.lifelineStealsAttempted;
                lifelinesActivated(lifeline) = true;
                waiting = true;
                logger.stopLive();
                at (Place(lifeline)) @Uncounted async st().request(st, p, true);
                while (waiting) Runtime.probe();
                logger.startLive();
            }
        }
        return !empty;
    }
    
    /**
     * Remote thief sending requests to local LJR. When empty or waiting for more work,
     * reject non-lifeline thief right away. Note, never reject lifeline thief.
     * @param st PLH for Woker
     * @param thief place id of thief
     * @param lifeline if I am the lifeline buddy of the remote thief
     */
    def request(st:PlaceLocalHandle[Worker[Queue, R]], thief:Long, lifeline:Boolean) {
        try {
            if (lifeline) ++logger.lifelineStealsReceived; else ++logger.stealsReceived;
            if (empty || waiting) {
                if (lifeline) lifelineThieves.push(thief);
                at (Place(thief)) @Uncounted async { st().waiting = false; }
            } else {
                if (lifeline) thieves.push(thief); else thieves.push(-thief-1);
            }
        } catch (v:CheckedThrowable) {
            error(v);
        }
    }
    
    /**
     * Merge current Worker's taskbag with incoming task bag.
     * @param loot task bag to merge
     * @param lifeline if it is from a lifeline buddy
     */
    @Inline final def processLoot(loot:TaskBag, lifeline:Boolean) {
        val n = loot.size();
        if (lifeline) {
            ++logger.lifelineStealsPerpetrated;
            logger.lifelineNodesReceived += n;
        } else {
            ++logger.stealsPerpetrated;
            logger.nodesReceived += n;
        }
        queue.merge(loot);
    }
    
    /**
     * Deal workload to the theif. If the thief is active already, simply merge the taskbag. If the thief is inactive,
     * the thief gets reactiveated again.
     * @param st: PLH for Worker
     * @param loot Task to share
     * @param source victim id
     */
    def deal(st:PlaceLocalHandle[Worker[Queue, R]], loot:TaskBag, source:Long) {
        try {
            val lifeline = source >= 0;
            if (lifeline) lifelinesActivated(source) = false;
            empty = false;
            if (active) {
                processLoot(loot, lifeline);
            } else {
                active = true;
                logger.startLive();
                processLoot(loot, lifeline);
                //distribute(st);
                processStack(st);
                logger.stopLive();
                active = false;
                logger.nodesCount = queue.count();
            }
        } catch (v:CheckedThrowable) {
            error(v);
        }
    }
    
    /**
     * Entry point when workload is only known dynamically . The workflow is terminated when 
     * (1) No one has work to do
     * (2) Lifeline steals are responded
     * @param place local handle for Worker
     * @param start init method used in {@link TaskQueue}, note the workload is not allocated, because
     * the workload can only be self-generated.
     */
    def main(st:PlaceLocalHandle[Worker[Queue, R]], start:()=>void) {
        @Pragma(Pragma.FINISH_DENSE) finish {
            try {
                empty = false;
                active = true;
                logger.startLive();
                start();
                processStack(st);
                logger.stopLive();
                active = false;
                logger.nodesCount = queue.count();
            } catch (v:CheckedThrowable) {
                error(v);
            }
        } 
    }
    
    /**
     * Entry point when workload can be known statically. The workflow is terminated when 
     * (1) No one has work to do
     * (2) Lifeline steals are responded
     * @param place local handle for Worker. Note the workload is assumed to be allocated already in the {@link TaskQueue}
     * constructor.
     */
    def main(st:PlaceLocalHandle[Worker[Queue, R]]) {
        try {
            empty = false;
            active = true;
            logger.startLive();
            processStack(st);
            logger.stopLive();
            active = false;
            logger.nodesCount = queue.count();
        } catch (v:CheckedThrowable) {
            error(v);
        }
    }
    
    /**
     * Print exceptions
     * @param v exeception
     */
    static def error(v:CheckedThrowable) {
        Runtime.println("Exception at " + here);
        v.printStackTrace();
    }
    
    /**
     * Min helper function
     */
    @Inline static def min(i:Long, j:Long) = i < j ? i : j;
    
    
    // static def stats[Queue, R](st:PlaceLocalHandle[Worker[Queue, R]], verbose:Boolean){Queue<:TaskQueue[Queue, R]} {
    //     val P = Place.numPlaces();
    //     val logs:Rail[Logger];
    //     if (P >= 1024) {
    //         logs = new Rail[Logger](P/32, (i:Long)=>at (Place(i*32)) {
    //             val h = Runtime.hereLong();
    //             val n = min(32, P-h);
    //             val logs = new Rail[Logger](n, (i:Long)=>at (Place(h+i)) st().logger.get(verbose));
    //             val log = new Logger(false);
    //             log.collect(logs);
    //             return log;
    //         });
    //     } else {
    //         logs = new Rail[Logger](P, (i:Long)=>at (Place(i)) st().logger.get(verbose));
    //     }
    //     val log = new Logger(false);
    //     log.collect(logs);
    //     log.stats();
    //     return log.nodesCount;
    // }

    /**
     * Internal method used by {@link GLB} to start Worker at each place when the 
     * workload is known statically.
     * @param st PLH of Worker
     */
    static def broadcast[Queue, R](st:PlaceLocalHandle[Worker[Queue, R]]){Queue<:TaskQueue[Queue, R]} {
        val P = Place.numPlaces();
        @Pragma(Pragma.FINISH_DENSE) finish {
            if (P < 256) {
                for(var i:Long=0; i<P; i++) {
                    at (Place(i)) async st().main(st);
                }
            } else {
                for(var i:Long=P-1; i>=0; i-=32) {
                    at (Place(i)) async {
                        val max = Runtime.hereLong();
                        val min = Math.max(max-31, 0);
                        for (var j:Long=min; j<=max; ++j) {
                            at (Place(j)) async st().main(st);
                        }
                    }
                }
            }
        }
    }
    /**
     * Initialize Context object at every place
     * @param st: PLH of Worker
     */
    static def initContexts[Queue,R](st:PlaceLocalHandle[Worker[Queue, R]]){Queue<:TaskQueue[Queue, R]}{
        val P = Place.numPlaces();
        @Pragma(Pragma.FINISH_DENSE) finish {
            if (P < 256) {
                for(var i:Long=0; i<P; i++) {
                    at (Place(i)) async st().setContext(st);
                }
            } else {
                for(var i:Long=P-1; i>=0; i-=32) {
                    at (Place(i)) async {
                        val max = Runtime.hereLong();
                        val min = Math.max(max-31, 0);
                        for (var j:Long=min; j<=max; ++j) {
                            at (Place(j)) async st().setContext(st);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Returns yield point
     */
    @Inline public def getYieldPoint(){
        return (st:PlaceLocalHandle[Worker[Queue, R]])=>{Runtime.probe();distribute(st);reject(st);};
    }
    
    /**
     * Set the context object
     * @param st PLH of Worker
     */
    protected def setContext(st:PlaceLocalHandle[Worker[Queue, R]]){Queue<:TaskQueue[Queue, R]}{
    
        this.context = new Context(st);
    }
}
