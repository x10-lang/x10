/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.io;

import x10.util.StringBuilder;

public class StringWriter extends Writer {
    val b:StringBuilder;

    public def this() { 
    	this.b = new StringBuilder(); 
    }

    public def write(x:Byte):void { 
        b.add(x as Char);
    }

    public def write(s:String):void { 
        b.addString(s);
    }

    public def write(x:Rail[Byte], off:Long, len:Long):void { 
        for (var i:Long = off; i<off+len; i++) {
            b.add(x(i) as Char);
        }
    }

    public def size() = b.length();

    public def result() = b.result();
    
    public def flush():void { }
    public def close():void { }
}

