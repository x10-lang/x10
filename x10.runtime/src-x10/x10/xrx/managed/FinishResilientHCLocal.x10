/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
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

/**
 * This class manages the local (non-resilient) state of a
 * Hazelcast resilient finish. The key idea is the same as
 * with Place0 finish:  resiliently count all place-shifting
 * activities and summarize the local children of a place-shifting
 * activity non-resiliently and upon local quiescence report to
 * the resilient store that the place-shifting activity has terminated.
 */
public class FinishResilientHCLocal extends FinishResilientBridge implements x10.io.CustomSerialization {
  private var f:FinishResilientHC;

  // for all instances
  private transient var local:Int; // local task count
  private transient val latch:SimpleLatch; // used as a lock if not root instamce

  // for root instance
  private transient val parent:Any; // parent finish
  private transient var strictFinish:Boolean;
//  private transient var exceptions:GrowableRail[CheckedThrowable](); // TODO

  static def make(parent:Any):FinishResilientHCLocal {
    return new FinishResilientHCLocal(parent, new SimpleLatch());
  }

  private def this(parent:Any, latch:SimpleLatch) {
    this.parent = parent;
    this.latch = latch;
    this.strictFinish = false;
  }

  public def serialize(s:x10.io.Serializer) {
    init();
    s.writeAny(f);
  }

  private def this(ds:x10.io.Deserializer) {
    this.f = ds.readAny() as FinishResilientHC;
    this.parent = 42;
    this.latch = new SimpleLatch();
    this.strictFinish = true;
  }

  private def init() {
    latch.lock();
    strictFinish = true;
    if (f == null) {
      var exposedParent:Any = parent;
      if (parent instanceof FinishResilientHCLocal) {
        val optParent = (parent as FinishResilientHCLocal);
        optParent.init();
        exposedParent = optParent.f;
      }
      f = FinishResilientHC.make(exposedParent);
    }
    latch.unlock();
  }
  
  public def notifySubActivitySpawn(place:Place):void {
    if (place.equals(here)) {
      latch.lock();
      local++;
      latch.unlock();
    } else {
      init();
      f.notifySubActivitySpawn(place);
    }
  }

  public def notifyRemoteContinuationCreated():void {
    strictFinish = true;
  }

  public def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
    return srcPlace.equals(here) || f.notifyActivityCreation(srcPlace, activity);
  }

  public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
    init();
    f.notifyActivityCreationFailed(srcPlace, t);
  }

  public def notifyActivityCreatedAndTerminated(srcPlace:Place):void {
    if (srcPlace.equals(here)) {
      notifyActivityTermination();
    } else {
      init();
      f.notifyActivityCreatedAndTerminated(srcPlace);
    }
  }

  public def notifyShiftedActivitySpawn(dstPlace:Place):void {
    init();
    f.notifyShiftedActivitySpawn(dstPlace);
  }

  public def notifyShiftedActivityCreation(srcPlace:Place):Boolean {
    init();
    return f.notifyShiftedActivityCreation(srcPlace);
  }

  public def notifyShiftedActivityCompletion():void {
    init();
    f.notifyShiftedActivityCompletion();
  }

  public def notifyActivityTermination():void {
    latch.lock();
    if (--local >= 0) {
      latch.unlock();
      return;
    }
    val b = f != null && 42.equals(parent);
    latch.unlock();
    if (b) f.notifyActivityTermination();
    latch.release();
  }

  public def pushException(t:CheckedThrowable):void {
    init();
    f.pushException(t);
  }
  
  public def waitForFinish():void {
    notifyActivityTermination();
    if ((!Runtime.STRICT_FINISH) && (!strictFinish)) {
      joinFinish(latch);
    }
    latch.await();
    latch.lock();
    val b = f != null;
    latch.unlock();
    if (b) f.waitForFinish();
  }
}
