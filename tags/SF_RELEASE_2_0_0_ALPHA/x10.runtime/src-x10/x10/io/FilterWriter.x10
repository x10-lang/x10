/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
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
