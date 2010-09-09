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

public class FilterWriter extends Writer {
    global val w: Writer;
    
    protected global def inner(): Writer = w;

    public def this(w: Writer) { this.w = w; }

    public global def close(): Void throws IOException = w.close();
    public global def flush(): Void throws IOException = w.flush();

    public global def write(b: Byte): Void throws IOException = w.write(b);
}
