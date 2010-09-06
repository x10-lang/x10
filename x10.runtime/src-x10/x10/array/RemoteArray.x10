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

package x10.array;

import x10.compiler.Global;
import x10.util.IndexedMemoryChunk;

/**
 * A class that encapsulates sufficient information about a remote
 * array to enable DMA opertations via Array.copyTo and Array.copyFrom
 * to be performed on the encapsulated Array.<p>
 *
 * Because copyTo and copyFrom are low-level interfaces that do not necessarily require
 * the source and destination arrays to have equivalent regions, the Region of the
 * array is not cached directly in the RemoteArray object (to minimize serialized bytes).
 * If the Region is actually needed, it can be retreived by using the array GlobalRef
 * to return to the referenced array's home location and access its region.
 */
public class RemoteArray[T] {
  val array:GlobalRef[Array[T]];
  val rawData:IndexedMemoryChunk[T];
  val rawLength:int;

  public property home:Place = array.home;

  public def this(a:Array[T]) {
    array = GlobalRef[Array[T]](a);
    rawData = a.raw();
    rawLength = a.rawLength;  
  }

  public safe def equals(other:Any) {
    if (!(other instanceof RemoteArray[T])) return false;
    val oRA = other as RemoteArray[T];
    return oRA.array.equals(array);
  }

  public safe def hashCode() = array.hashCode();
}

