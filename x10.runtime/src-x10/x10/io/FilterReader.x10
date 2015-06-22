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

package x10.io;

public class FilterReader extends Reader {
    val r:Reader;
    
    protected def inner():Reader = r;

    public def this(r:Reader) { 
        this.r = r; 
    }

    public def close():void { 
        r.close(); 
    }
    public def read():Byte = r.read();

    public def read(r:Rail[Byte], off:Long, len:Long):void {
        this.r.read(r,off,len); 
    }
    
    public def available():Long = r.available();

    public def skip(off:Long):void {
        r.skip(off); 
    }

    public def mark(off:Long):void  { 
        r.mark(off); 
    }

    public def reset():void { 
        r.reset(); 
    }

    public def markSupported():Boolean = r.markSupported();
}
