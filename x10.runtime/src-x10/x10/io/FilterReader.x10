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
    val r: Reader;
    
    protected def inner(): Reader = r;

    public def this(r: Reader) { this.r = r; }
    public def close(): void //throws IOException 
    { r.close(); }
    public def read(): Byte //throws IOException 
    = r.read();
    public def read(r:Rail[Byte], off:Long, len:Long): void //throws IOException 
    { this.r.read(r,off,len); }
    
    public def available(): Int //throws IOException 
    = r.available();
    
    public def skip(off: Int): void //throws IOException 
    { r.skip(off); }

    public def mark(off: Int): void //throws IOException 
    { r.mark(off); }
    public def reset(): void //throws IOException 
    { r.reset(); }
    public def markSupported(): Boolean = r.markSupported();
    
}
