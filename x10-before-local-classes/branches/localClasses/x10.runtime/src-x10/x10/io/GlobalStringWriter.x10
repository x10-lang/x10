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

// TODO: This class is of questionable utility, but I ported it
//       over so we would have something that matched the semantics
//       of the 2.0.x StringWriter class.
//       I think we actually should remove this class.  --dave Sep 3, 2010.
public class GlobalStringWriter extends Writer {
    val b:GlobalRef[StringBuilder];
    public def this() { this.b = GlobalRef[StringBuilder](new StringBuilder()); }

    public def write(x:Byte): Void { 
        at (b) {
       	    b().add((x as Byte) as Char);
        }
    }

    public def size() = at (b) b().length();
    public def result() = at (b) b().result(); 
    
    public def flush(): Void { }
    public def close(): Void { }
}

