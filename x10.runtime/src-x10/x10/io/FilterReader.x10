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

public class FilterReader extends Reader {
    global val r: Reader;
    
    protected global def inner(): Reader = r;

    public def this(r: Reader) { this.r = r; }
    public global def close(): Void throws IOException = r.close();
    public global def read(): Byte throws IOException = r.read();
    
    public global def available(): Int throws IOException = r.available();
    
    public global def skip(off: Int): Void throws IOException = r.skip(off);

    public global def mark(off: Int): Void throws IOException = r.mark(off);
    public global def reset(): Void throws IOException = r.reset();
    public global def markSupported(): Boolean = r.markSupported();
    
}
