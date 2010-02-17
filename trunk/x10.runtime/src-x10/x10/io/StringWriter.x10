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

import x10.util.StringBuilder;

public class StringWriter extends Writer {
    global val b:StringBuilder;
    public def this() { this.b = new StringBuilder(); }

    public global def write(x:Byte): Void { 
        at(b) { b.add((x as Byte) as Char); }
    }

    public global def size() = at (b) b.length();
    public global def result() = at (b) b.result(); 
    
    public global def flush(): Void { }
    public global def close(): Void { }
}

