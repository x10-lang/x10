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
import x10.util.concurrent.AtomicLong;
import x10.util.concurrent.AtomicBoolean;

public class ResilientStorePlaceZero {

    static me = new ResilientStorePlaceZero();


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
            while (!c().get()) Runtime.probe();
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
            while (c().get()==-1l) Runtime.probe();
            return c().get();
        }
    }

    private static class State {

        val id : Long;
        val parent : State;
        val transit : Rail[Int];
        val live : Rail[Int];
        val homeId : Long;
        var adopted : Boolean;
        var multipleExceptions : GrowableRail[Exception] = null;

        private def ensureMultipleExceptions() {
            if (multipleExceptions == null) multipleExceptions = new GrowableRail[Exception]();
            return multipleExceptions;
        }

        public def this(pfs:State, homeId:Long, id:Long) {
            this.id = id;
            this.parent = pfs;
            this.transit = new Rail[Int](Place.MAX_PLACES * Place.MAX_PLACES, 0);
            this.live = new Rail[Int](Place.MAX_PLACES, 0);
            this.homeId = homeId;
            this.adopted = false;
        }

        def findFirstNonDeadParent() : State {
            if (!Place.isDead(parent.homeId)) return parent;
            return parent.findFirstNonDeadParent();
        }

        def stealCounters(child:State) : void {
            for (i in 0..(Place.MAX_PLACES-1)) {
                live(i) += child.live(i);
                for (j in 0..(Place.MAX_PLACES-1)) {
                    transit(j + i*Place.MAX_PLACES) += child.transit(j + i*Place.MAX_PLACES);
                }
            }
            child.adopted = true;
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


    static def make(homeId:Long, parentId:Long) : Long {
        return lowLevelAtExprLong(() => {
            atomic {
                val pfs = parentId==-1l ? null : me.states(parentId);
                val fs = new State(pfs, homeId, me.states.size());
                me.states.add(fs);
                return fs.id;
            }
        });
    }

    static def notifySubActivitySpawn(id:Long, srcId:Long, dstId:Long) {
        //Runtime.println("notifySubActivitySpawn("+id+", "+srcId+", "+dstId+")");
        lowLevelAt(() => { atomic {
            val fs = me.states(id);
            fs.transit(srcId + dstId*Place.MAX_PLACES)++;
            //Runtime.println("transit("+srcId+","+dstId+") == "+fs.transit(srcId + dstId*Place.MAX_PLACES));
        } });
    }

    static def notifyActivityCreation(id:Long, srcId:Long, dstId:Long) {
        //Runtime.println("notifyActivityCreation("+id+", "+srcId+", "+dstId+")");
        return 1l==lowLevelAtExprLong(() => { atomic {
            if (Place(srcId).isDead()) return 0l;
            val fs = me.states(id);
            fs.live(dstId)++;
            fs.transit(srcId + dstId*Place.MAX_PLACES)--;
            //Runtime.println("live("+dstId+") == "+fs.live(dstId));
            //Runtime.println("transit("+srcId+","+dstId+") == "+fs.transit(srcId + dstId*Place.MAX_PLACES));
            return 1l;
        } });
    }

    static def notifyActivityTermination(id:Long, dstId:Long) {
        //Runtime.println("notifyActivityTermination("+id+", "+dstId+")");
        lowLevelAt(() => { atomic {
            val fs = me.states(id);
            fs.live(dstId)--;
            //Runtime.println("live("+dstId+") == "+fs.live(dstId));
        } });
    }

    static def pushException(id:Long, t:Exception) {
        lowLevelAt(() => { atomic {
            val fs = me.states(id);
            //Runtime.println("pushException("+id+", "+t+")");
            fs.ensureMultipleExceptions().add(t);
        } });
    }

    def quiescent(fs:State) : Boolean {
        val nd = Place.numDead();
        if (nd != me.numDead) {
            numDead = nd;
            pushUp();
        }

        // overwrite counters with 0 if places have died, accumuluate exceptions
        for (i in 0..(Place.MAX_PLACES-1)) {
            if (Place.isDead(i)) {
                for (unused in 1..fs.live(i)) {
                    fs.addDeadPlaceException(Place(i));
                }
                fs.live(i) = 0;

                // kill horizontal and vertical lines in transit matrix
                for (j in 0..(Place.MAX_PLACES-1)) {
                    for (unused in 1..fs.transit(i + j*Place.MAX_PLACES)) {
                        fs.addDeadPlaceException(Place(i));
                    }
                    fs.transit(i + j*Place.MAX_PLACES) = 0;

                    for (unused in 1..fs.transit(j + i*Place.MAX_PLACES)) {
                        fs.addDeadPlaceException(Place(i));
                    }
                    fs.transit(j + i*Place.MAX_PLACES) = 0;
                }
            }
        }

        for (i in 0..(Place.MAX_PLACES-1)) {
            if (fs.live(i)!=0) {
                //Runtime.println("Live at "+i);
                return false;
            }
            for (j in 0..(Place.MAX_PLACES-1)) {
                if (fs.transit(i + j*Place.MAX_PLACES)!=0) {
                    //Runtime.println("In transit from "+i+" -> "+j);
                    return false;
                }
            }
        }

        return true;
    }

    /** Grandfather activities under a dead finish into the nearest parent finish at a place that is still alive. */
    private def pushUp() : void {
        atomic {
            for (i in 0..(states.size()-1)) {
                val fs = states(i);
                if (fs.adopted) continue;
                if (Place.isDead(fs.homeId)) {
                    val pfs = fs.findFirstNonDeadParent();
                    pfs.stealCounters(fs);
                }
            }
        }
    }

    static def waitForFinish(id:Long) {
        //Runtime.println("waitForFinish()");
        try {
            lowLevelAt(() => {
                val s : State;
                atomic {
                    s = me.states(id);
                }
                when (me.quiescent(s)) { }
                if (s.multipleExceptions != null) {
                    throw new MultipleExceptions(s.multipleExceptions);
                }
            });
        } finally {
            //Runtime.println("waitForFinish() done waiting");
        }
    }
}


