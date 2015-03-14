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
package x10.xrx.managed;

import x10.util.concurrent.SimpleLatch;
import x10.util.GrowableRail;
import x10.util.ArrayList;
import x10.array.Array_3;

import com.hazelcast.core.IMap;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.core.EntryListener;
import java.util.Map;

import x10.xrx.*;

public
class FinishResilientHCopt extends FinishResilientBridge {
  private var f:FinishResilientHC;

  private def this(parent:Any, latch:SimpleLatch) {
  f = FinishResilientHC.make(parent, latch);
  }

  static def make(parent:Any, latch:SimpleLatch):FinishResilientHCopt {
    return new FinishResilientHCopt(parent, latch);
  }

  public def notifySubActivitySpawn(place:Place):void { f.notifySubActivitySpawn(place); }
  public def notifyRemoteContinuationCreated():void { f.notifyRemoteContinuationCreated(); }
  public def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean = f.notifyActivityCreation(srcPlace, activity);
  public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean = f.notifyActivityCreationBlocking(srcPlace, activity);
  public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void { f.notifyActivityCreationFailed(srcPlace, t); }
  public def notifyActivityCreatedAndTerminated(srcPlace:Place):void { f.notifyActivityCreatedAndTerminated(srcPlace); }
  public def notifyActivityTermination():void { f.notifyActivityTermination(); }
  public def pushException(t:CheckedThrowable):void { f.pushException(t); }
  public def waitForFinish():void { f.waitForFinish(); }
  public def joinFinish(latch:SimpleLatch):void { f.joinFinish(latch); }
}