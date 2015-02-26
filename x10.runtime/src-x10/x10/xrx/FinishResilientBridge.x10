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

import x10.util.concurrent.SimpleLatch;

/*
 * This class is used to make some interfaces in FinishResilient accessible
 * from classes in a different package (e.g. x10.lang.managed.FinishResilientHC)
 */
public
class FinishResilientBridge extends FinishResilient {
    public static val verbose = FinishResilient.verbose;
    public static def debug(msg:String) { FinishResilient.debug(msg); }
    
    public def notifySubActivitySpawn(place:Place):void { throw new UnsupportedOperationException(); }
    public def notifyRemoteContinuationCreated():void { throw new UnsupportedOperationException(); }
    public def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean { throw new UnsupportedOperationException(); }
    public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean { throw new UnsupportedOperationException(); }
    public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void { throw new UnsupportedOperationException(); }
    public def notifyActivityCreatedAndTerminated(srcPlace:Place):void { throw new UnsupportedOperationException(); }
    public def notifyActivityTermination():void { throw new UnsupportedOperationException(); }
    public def pushException(t:CheckedThrowable):void { throw new UnsupportedOperationException(); }
    public def waitForFinish():void { throw new UnsupportedOperationException(); }

    public def joinFinish(latch:SimpleLatch):void {
       Runtime.worker().join(latch);
    }
}
