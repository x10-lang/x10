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

public class FilterWriter extends Writer {
    val w: Writer;
    
    protected def inner(): Writer = w;

    public def this(w: Writer) { 
        this.w = w; 
    }

    public def close():void { 
        w.close(); 
    }

    public def flush():void { 
        w.flush(); 
    }

    public def write(b:Byte):void {
        w.write(b); 
    }

    public def write(s:String):void {
        w.write(s); 
    }

    public def write(x:Rail[Byte], off:Long, len:Long):void {
        w.write(x, off, len);
    }
}
