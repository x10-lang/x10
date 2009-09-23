/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

import x10.util.StringBuilder;

// Reconsider whether this needs to be a value.

public value StringWriter extends Writer {
    val b: StringBuilder;
    def b() = b as StringBuilder!;
    public def this() { this.b = new StringBuilder(); }

    public def write(x: Byte): Void { b().add((x as Byte) as Char); }

    public def size() = b().length();
    public def toString() = b().toString();
    public def result() = b().result(); 
    
    public def flush(): Void { }
    public def close(): Void { }
}

