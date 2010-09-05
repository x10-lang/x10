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

import x10.util.Builder;

public class ByteWriter[T] /*extends Writer*/ {
    val b:GlobalRef[Builder[Byte,T]];

    public def this(b: Builder[Byte,T]) { this.b = new GlobalRef(b); }

    public def write(x: Byte): Void { at (b) b().add(x); }
    public incomplete def size() : Long;
    public safe def toString() { 
      if (here == b.home) {
          return b().toString();
      } else {
          b.toString();
      }
    }
    public def result() = at (b) b().result(); 
    
    public def flush(): Void { }
    public def close(): Void { }
}

