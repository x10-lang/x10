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
package x10.lang;
import x10.util.concurrent.SimpleLatch;

/*
 * Place0-based Resilient Finish + Various Optimizations
 * (to be filled in)
 */
class FinishResilientPlace0opt extends FinishResilient {
    static def make(parent:FinishState, latch:SimpleLatch):FinishResilientPlace0opt { throw new UnsupportedOperationException(); }
    static def notifyPlaceDeath():void { throw new UnsupportedOperationException(); }
    def notifySubActivitySpawn(place:Place):void { throw new UnsupportedOperationException(); }
    def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean { throw new UnsupportedOperationException(); }
    def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean { throw new UnsupportedOperationException(); }
    def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void { throw new UnsupportedOperationException(); }
    def notifyActivityCreatedAndTerminated(srcPlace:Place):void { throw new UnsupportedOperationException(); }
    def notifyActivityTermination():void { throw new UnsupportedOperationException(); }
    def pushException(t:CheckedThrowable):void { throw new UnsupportedOperationException(); }
    def waitForFinish():void { throw new UnsupportedOperationException(); }
}
