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
import x10.util.concurrent.SimpleLatch;

/*
 * Sample implemenation of Resilient Finish
 */
class FinishResilientSample extends FinishResilient {
    private static val verbose = FinishResilient.verbose;
    def this(latch:SimpleLatch) = this(currentFS(), latch);
    def makeChildFinish() = new FinishResilientSample(this, null);
    private def this(parent:FinishState, latch:SimpleLatch) {
        if (verbose>=2) debug("constructor called, parent=" + parent + " latch=" + latch);
    }
    def notifySubActivitySpawn(place:Place):void {
        if (verbose>=2) debug("notifySubActivitySpawn called, place.id=" + place.id);
    }
    def notifyActivityCreation(srcPlace:Place):Boolean {
        if (verbose>=2) debug("notifySubActivitySpawn called, srcPlace.id=" + srcPlace.id);
        return true;
    }
    def notifyActivityTermination():void {
        if (verbose>=2) debug("notifySubActivityTermination called");
    }
    def pushException(t:Exception):void {
        if (verbose>=2) debug("pushException called, t=" + t);
    }
    def waitForFinish():void {
        if (verbose>=2) debug("waitForFiish called");
    }
    static def notifyPlaceDeath():void {
        if (verbose>=2) debug("notifyPlaceDeath called");
    }
}
