/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
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

