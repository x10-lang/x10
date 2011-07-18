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

package x10.io;

/**
 * Data container used 	in the CustomSerialization protocol.
 * 
 * @see CustomSerialization
 */
public final class SerialData {
  public val data:Any;
  public val superclassData:SerialData;
  public def this(data:Any, superclassData:SerialData) {
    this.data = data;
    this.superclassData = superclassData;
  }
  
  public def toString():String = "SerialData(" + data + "," + superclassData + ")";
}
