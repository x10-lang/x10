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

import x10.util.Pair;
import x10.util.HashMap;
import x10.util.GrowableRail;
import x10.util.concurrent.AtomicLong;
import x10.util.concurrent.AtomicBoolean;
import x10.util.concurrent.SimpleLatch;

class ResilientStorePlaceZero {

    static me = new ResilientStorePlaceZero();

    // Turn this on to debug deadlocks within the finish implementation
    // moved VERBOSE flag to x10.lang.FinishState
    //static VERBOSE = false;


    /** Simply utility function to send a message to place zero at the x10rt level.
      * Propagates back the exception, if thrown.
      */
    private static def lowLevelAt(cl:()=>void) {
        if (here.id == 0l) {
            cl();
        } else {
            val exc = new GlobalRef(new Cell[Exception](null));
            val c = new GlobalRef(new AtomicBoolean());
            Runtime.x10rtSendMessage(0, () => @RemoteInvocation("low_level_at_out") {
                try {
                    cl();
                } catch (t:Exception) {
                    Runtime.x10rtSendMessage(c.home.id, () => @RemoteInvocation("low_level_at_back_exc") {
                        // [DC] assume that the write barrier on c is enough to see update on exc
                        exc.getLocalOrCopy()(t);
                        c.getLocalOrCopy().getAndSet(true);
                    }, null);
                }
                Runtime.x10rtSendMessage(c.home.id, () => @RemoteInvocation("low_level_at_back") {
                    c.getLocalOrCopy().getAndSet(true);
                }, null);
            }, null);
            // while (!c().get()) Runtime.probe();
            if (!c().get()) { // Fix for XTENLANG-3303/3305
                Runtime.increaseParallelism();
                do { Runtime.x10rtProbe();
                } while (!c().get());
                Runtime.decreaseParallelism(1n);
            }
            if (exc()() != null) throw exc()();
        }
    }

    /** Simply utility function to send a message to place zero, that returns an Int (-1 used internally), at the x10rt level. */
    private static def lowLevelAtExprLong(cl:()=>Long) : Long {
        if (here.id == 0l) {
            return cl();
        } else {
            val c = new GlobalRef(new AtomicLong(-1l));
            Runtime.x10rtSendMessage(0, () => @RemoteInvocation("low_level_at_int_out") {
                val r = cl();
                Runtime.x10rtSendMessage(c.home.id, () => @RemoteInvocation("low_level_at_int_back") {
                    c.getLocalOrCopy().set(r);
                }, null);
            }, null);
            // while (c().get()==-1l) Runtime.probe();
            if (c().get()==-1l) { // Fix for XTENLANG-3303/3305
                Runtime.increaseParallelism();
                do { Runtime.x10rtProbe();
                } while (c().get()==-1l);
                Runtime.decreaseParallelism(1n);
            }
            return c().get();
        }
    }

    private static class State {

        val id : Long;
        val parent : State;
        //val transit : Rail[Int];
        val transit : HashMap[Pair[Long,Long], Int];
        val live : Rail[Int];
        //val transitAdopted : Rail[Int];
        val transitAdopted : HashMap[Pair[Long,Long], Int];
        val liveAdopted : Rail[Int];
        val homeId : Long;
        var adopted : Boolean;
        var adoptedParent : Long;
        var multipleExceptions : GrowableRail[Exception] = null;
        val latch : SimpleLatch;
        var totalCounter : Long;

        private def recalculateTotal() {
            totalCounter = 0;
            for (i in 0..(Place.MAX_PLACES-1)) {
                totalCounter += live(i);
                totalCounter += liveAdopted(i);
            }
            for (ent in transit.entries()) {
                totalCounter += ent.getValue();
            }
            for (ent in transitAdopted.entries()) {
                totalCounter += ent.getValue();
            }
        }

        private def ensureMultipleExceptions() {
            if (multipleExceptions == null) multipleExceptions = new GrowableRail[Exception]();
            return multipleExceptions;
        }

        public def this(pfs:State, homeId:Long, id:Long, latch:SimpleLatch) {
            this.id = id;
            this.parent = pfs;
            //this.transit = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0n);
            this.transit = new HashMap[Pair[Long,Long],Int]();
            this.live = new Rail[Int](Place.MAX_PLACES, 0n);
            //this.transitAdopted = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0n);
            this.transitAdopted = new HashMap[Pair[Long,Long],Int]();
            this.liveAdopted = new Rail[Int](Place.MAX_PLACES, 0n);
            this.live(homeId) = 1n;
            if (FinishState.VERBOSE) Runtime.println("    initial live("+homeId+") == 1");
            this.totalCounter = 1;
            this.homeId = homeId;
            this.adopted = false;
            this.latch = latch;
        }

        //def transitInc(src:Long, dst:Long, v:Int) { transit(src + dst*Place.MAX_PLACES) += v; }
        //def transitDec(src:Long, dst:Long) { transit(src + dst*Place.MAX_PLACES)--; }
        //def transitGet(src:Long, dst:Long) = transit(src + dst*Place.MAX_PLACES);
        //def transitSet(src:Long, dst:Long, v:Int) { transit(src + dst*Place.MAX_PLACES) = v; }
        def transitInc(src:Long, dst:Long, v:Int) {
            if (v==0n) return;
            val p = Pair[Long, Long](src, dst);
            val old = transit.getOrElse(p, 0n);
            transit.put(p, old+v);
        }
        def transitDec(src:Long, dst:Long) {
            val p = Pair[Long, Long](src, dst);
            val old = transit.getOrElse(p, 0n);
            transit.put(p, old-1n);
        }
        def transitGet(src:Long, dst:Long) : Int { 
            val p = Pair[Long, Long](src, dst);
            return transit.getOrElse(p, 0n);
        }
        def transitSet(src:Long, dst:Long, v:Int) {
            val p = Pair[Long, Long](src, dst);
            transit.put(p, v);
        }
        //def transitAdoptedInc(src:Long, dst:Long, v:Int) { transitAdopted(src + dst*Place.MAX_PLACES) += v; }
        //def transitAdoptedDec(src:Long, dst:Long) { transitAdopted(src + dst*Place.MAX_PLACES)--; }
        //def transitAdoptedGet(src:Long, dst:Long) = transitAdopted(src + dst*Place.MAX_PLACES);
        //def transitAdoptedSet(src:Long, dst:Long, v:Int) { transitAdopted(src + dst*Place.MAX_PLACES) = v; }
        def transitAdoptedInc(src:Long, dst:Long, v:Int) {
            if (v==0n) return;
            val p = Pair[Long, Long](src, dst);
            val old = transitAdopted.getOrElse(p, 0n);
            transitAdopted.put(p, old+v);
        }
        def transitAdoptedDec(src:Long, dst:Long) {
            val p = Pair[Long, Long](src, dst);
            val old = transitAdopted.getOrElse(p, 0n);
            transitAdopted.put(p, old-1n);
        }
        def transitAdoptedGet(src:Long, dst:Long) : Int { 
            val p = Pair[Long, Long](src, dst);
            return transitAdopted.getOrElse(p, 0n);
        }
        def transitAdoptedSet(src:Long, dst:Long, v:Int) {
            val p = Pair[Long, Long](src, dst);
            transitAdopted.put(p, v);
        }

        def transitInc(src:Long, dst:Long) { transitInc(src,dst,1n); }
        def transitAdoptedInc(src:Long, dst:Long) { transitAdoptedInc(src,dst,1n); }

        def findFirstNonDeadParent() : State {
            if (!Place.isDead(parent.homeId)) return parent;
            return parent.findFirstNonDeadParent();
        }

        def adopt(child:State) : void {
            for (i in 0..(Place.MAX_PLACES-1)) {
                liveAdopted(i) += child.live(i);
                liveAdopted(i) += child.liveAdopted(i);
                for (j in 0..(Place.MAX_PLACES-1)) {
                    transitAdoptedInc(j, i, child.transitGet(j, i));
                    transitAdoptedInc(j, i, child.transitAdoptedGet(j, i));
                }
            }
            recalculateTotal();
            child.adopted = true;
            child.adoptedParent = id;
            child.latch.release(); // stop blocked activities building up
        }

        def addDeadPlaceException(p:Place) {
            val e = new DeadPlaceException(p);
            e.fillInStackTrace();
            ensureMultipleExceptions().add(e);
        }

    }

    // TODO: freelist to reuse ids (maybe also states)
    private val states = new GrowableRail[State]();

    private var numDead : Long = 0;


    static def make(homeId:Long, parentId:Long, latch:SimpleLatch) : Long {
        return lowLevelAtExprLong(() => {
            atomic {
                val pfs = parentId==-1l ? null : me.states(parentId);
                val id = me.states.size();
                if (FinishState.VERBOSE) Runtime.println("make("+parentId+","+id+") @ "+homeId);
                val fs = new State(pfs, homeId, id, latch == null ? new SimpleLatch() : latch);
                me.states.add(fs);
                return fs.id;
            }
        });
    }

    static def getStateAccountingForAdoption(id:Long) {
        var fs:State = me.states(id);
        var adopted : Boolean = false;
        while (fs.adopted) {
            adopted = true;
            fs = me.states(fs.adoptedParent);
        }
        return Pair(fs, adopted);
    }

    static def notifySubActivitySpawn(id:Long, srcId:Long, dstId:Long) {
        lowLevelAt(() => { atomic {
            if (FinishState.VERBOSE) Runtime.println("notifySubActivitySpawn("+id+", "+srcId+", "+dstId+")");
            val pair = getStateAccountingForAdoption(id);
            val fs = pair.first;
            val adopted = pair.second;
            if (adopted) {
                fs.transitAdoptedInc(srcId, dstId);
                if (FinishState.VERBOSE) Runtime.println("    transitAdopted("+srcId+","+dstId+") == "+fs.transitAdoptedGet(srcId, dstId));
            } else {
                fs.transitInc(srcId, dstId);
                if (FinishState.VERBOSE) Runtime.println("    transit("+srcId+","+dstId+") == "+fs.transitGet(srcId, dstId));
            }
            fs.totalCounter++;
        } });
    }

    static def notifyActivityCreation(id:Long, srcId:Long, dstId:Long) {
        return 1l==lowLevelAtExprLong(() => { atomic {
            if (FinishState.VERBOSE) Runtime.println("notifyActivityCreation("+id+", "+srcId+", "+dstId+")");
            if (Place(srcId).isDead()) return 0l;
            val pair = getStateAccountingForAdoption(id);
            val fs = pair.first;
            val adopted = pair.second;
            if (adopted) {
                fs.liveAdopted(dstId)++;
                fs.transitAdoptedDec(srcId, dstId);
                if (FinishState.VERBOSE) Runtime.println("    liveAdopted("+dstId+") == "+fs.liveAdopted(dstId));
                if (FinishState.VERBOSE) Runtime.println("    transitAdopted("+srcId+","+dstId+") == "+fs.transitAdoptedGet(srcId, dstId));
            } else {
                fs.live(dstId)++;
                fs.transitDec(srcId, dstId);
                if (FinishState.VERBOSE) Runtime.println("    live("+dstId+") == "+fs.live(dstId));
                if (FinishState.VERBOSE) Runtime.println("    transit("+srcId+","+dstId+") == "+fs.transitGet(srcId, dstId));
            }
            return 1l;
        } });
    }

    static def notifyActivityTermination(id:Long, dstId:Long) {
        lowLevelAt(() => { atomic {
            if (FinishState.VERBOSE) Runtime.println("notifyActivityTermination("+id+", "+dstId+")");
            val pair = getStateAccountingForAdoption(id);
            val fs = pair.first;
            val adopted = pair.second;
            if (adopted) {
                fs.liveAdopted(dstId)--;
            } else {
                fs.live(dstId)--;
            }
            fs.totalCounter--;
            if (FinishState.VERBOSE) Runtime.println("    live("+dstId+") == "+fs.live(dstId));
            if (me.quiescent(fs)) {
                if (FinishState.VERBOSE) Runtime.println("    Releasing latch...");
                fs.latch.release();
            }
        } });
    }

    static def notifyPlaceDeath(root_id:Long) {
        assert here == Place.FIRST_PLACE;
        me.pushUp();
    }

    static def pushException(id:Long, t:Exception) {
        lowLevelAt(() => { atomic {
            val fs = me.states(id);
            if (fs.adopted) {
                // ignoring exception since finish is dead
                if (FinishState.VERBOSE) Runtime.println("pushException("+id+", "+t+") dropped due to dead finish");
            } else {
                if (FinishState.VERBOSE) Runtime.println("pushException("+id+", "+t+")");
                fs.ensureMultipleExceptions().add(t);
            }
        } });
    }

    def quiescent(fs:State) : Boolean {

        // There is actually a race condition here (despite quiescent being called in an atomic section
        // The Place.isDead() can go to false between the pushUp() and the code after it, causing
        // a finish to 
        // TODO: store dead places in an array, use the same data to drive pushUp() and DPE generation

        val nd = Place.numDead();
        if (nd != me.numDead) {
            numDead = nd;
            pushUp();
        }

        // overwrite counters with 0 if places have died, accumuluate exceptions
        var recalc_needed : Boolean = false;
        for (i in 0..(Place.MAX_PLACES-1)) {
            if (Place.isDead(i)) {
                for (unused in 1..fs.live(i)) {
                    fs.addDeadPlaceException(Place(i));
                }
                fs.live(i) = 0n;
                fs.liveAdopted(i) = 0n;

                // kill horizontal and vertical lines in transit matrix
                for (j in 0..(Place.MAX_PLACES-1)) {
                    // do not generate DPEs for these guys, they were technically never sent!
                    fs.transitSet(i, j, 0n);
                    fs.transitAdoptedSet(i, j, 0n);

                    for (unused in 1..fs.transitGet(j, i)) {
                        fs.addDeadPlaceException(Place(i));
                    }
                    fs.transitSet(j, i, 0n);
                    fs.transitAdoptedSet(j, i, 0n);
                }
                recalc_needed = true;
            }
        }
        if (recalc_needed) fs.recalculateTotal();

        // note that counter can go below 0 due to quirky use of top level finish
        if (fs.totalCounter <= 0) return true;


        if (FinishState.VERBOSE) {
            Runtime.println("quiescent("+fs.id+")");
            for (i in 0..(Place.MAX_PLACES-1)) {
                if (fs.live(i)>0) {
                    if (FinishState.VERBOSE) Runtime.println("    "+fs.id+" Live at "+i);
                    return false;
                }
            }
            for (ent in fs.transit.entries()) {
                if (ent.getValue()>0) {
                    val pair = ent.getKey();
                    if (FinishState.VERBOSE) Runtime.println("    "+fs.id+" In transit from "+pair.first+" -> "+pair.second);
                    return false;
                }
            }
            for (i in 0..(Place.MAX_PLACES-1)) {
                if (fs.liveAdopted(i)>0) {
                    if (FinishState.VERBOSE) Runtime.println("    "+fs.id+" Live (adopted) at "+i);
                    return false;
                }
            }
            for (ent in fs.transitAdopted.entries()) {
                if (ent.getValue()>0) {
                    val pair = ent.getKey();
                    if (FinishState.VERBOSE) Runtime.println("    "+fs.id+" In transit (adopted) from "+pair.first+" -> "+pair.second);
                    return false;
                }
            }
        }

        return false;
    }

    /** Grandfather activities under a dead finish into the nearest parent finish at a place that is still alive. */
    private def pushUp() : void {
        atomic {
            for (i in 0..(states.size()-1)) {
                val fs = states(i);
                if (fs==null || fs.adopted) continue;
                if (Place.isDead(fs.homeId)) {
                    val pfs = fs.findFirstNonDeadParent();
                    if (FinishState.VERBOSE) Runtime.println("Finish has died ("+fs.id+"), adopting activities into ("+pfs.id+")");
                    pfs.adopt(fs);
                }
            }
            // have to do two traversals, since quiescent may go from true to false after adopting more activities
            for (i in 0..(states.size()-1)) {
                val fs = states(i);
                if (fs==null || fs.adopted) continue;
                if (me.quiescent(fs)) fs.latch.release();
            }
        }
    }

    static def waitForFinish(id:Long) {
        lowLevelAt(() => {
            if (FinishState.VERBOSE) Runtime.println("waitForFinish("+id+")");
            val s : State;
            atomic {
                s = me.states(id);
            }
            notifyActivityTermination(id, s.homeId);
            // if (!Runtime.STRICT_FINISH) Runtime.worker().join(s.latch); // removed as a tentative fix for XTENLANG-3304
            s.latch.await();
            // atomic {
            //     me.states(id) = null;
            // }
            if (!s.adopted) {
                atomic { me.states(id) = null; } // delete the state only if it finishes without being adopted (XTENLANG-3323)
                if (s.multipleExceptions != null) {
                    if (FinishState.VERBOSE) Runtime.println("waitForFinish("+id+") done waiting (throwing exceptions)");
                    throw new MultipleExceptions(s.multipleExceptions);
                }
                if (FinishState.VERBOSE) Runtime.println("waitForFinish("+id+") done waiting");
            } else {
                if (FinishState.VERBOSE) Runtime.println("waitForFinish("+id+") done waiting, finish was dead (cleaning up)");
            }
        });
    }
}


