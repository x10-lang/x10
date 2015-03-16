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
class FinishResilientHCopt extends FinishResilientBridge implements x10.io.CustomSerialization {
  private var f:FinishResilientHC;

  // for all instances
  private transient var local:Int; // local task count
  private transient val latch:SimpleLatch; // used as a lock if not root instamce

  // for root instance
  private transient val parent:Any; // parent finish
//  private transient var exceptions:GrowableRail[CheckedThrowable](); // TODO

  static def make(parent:Any, latch:SimpleLatch):FinishResilientHCopt {
    return new FinishResilientHCopt(parent, latch);
  }

  private def this(parent:Any, latch:SimpleLatch) {
    this.parent = parent;
    this.latch = latch;
  }

  public def serialize(s:x10.io.Serializer) {
    init();
    s.writeAny(f);
  }

  private def this(ds:x10.io.Deserializer) {
    this.f = ds.readAny() as FinishResilientHC;
    this.parent = 42;
    this.latch = new SimpleLatch();
  }

  private def init() {
    latch.lock();
    if (f == null) {
      if (parent instanceof FinishResilientHCopt) {
        (parent as FinishResilientHCopt).init();
      }
      f = FinishResilientHC.make(parent, new SimpleLatch());
    }
    latch.unlock();
  }
  
  public def notifySubActivitySpawn(place:Place):void {
    if (place.equals(here)) {
      latch.lock();
      local++;
      latch.unlock();
    } else {
      f.notifySubActivitySpawn(place);
    }
  }

  public def notifyRemoteContinuationCreated():void {
    init();
  }

  public def notifyActivityCreation(srcPlace:Place, activity:Activity):Boolean {
    return srcPlace.equals(here) || f.notifyActivityCreation(srcPlace, activity);
  }

  public def notifyActivityCreationBlocking(srcPlace:Place, activity:Activity):Boolean {
    return notifyActivityCreation(srcPlace, activity);
  }

  public def notifyActivityCreationFailed(srcPlace:Place, t:CheckedThrowable):void {
    init();
    f.notifyActivityCreationFailed(srcPlace, t);
  }

  public def notifyActivityCreatedAndTerminated(srcPlace:Place):void {
    init();
    f.notifyActivityCreatedAndTerminated(srcPlace);
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
    if ((!Runtime.STRICT_FINISH) && (f == null)) {
      joinFinish(latch);
    }
    latch.await();
    latch.lock();
    val b = f != null;
    latch.unlock();
    if (b) f.waitForFinish();
  }
}
