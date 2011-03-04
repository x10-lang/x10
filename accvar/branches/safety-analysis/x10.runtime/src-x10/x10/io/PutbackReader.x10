/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

import x10.util.GrowableRail;

public class PutbackReader extends FilterReader {
    global val putback: GrowableRail[Byte];

    global def putback() = putback as GrowableRail[Byte]!;
    public def this(r: Reader) {
        super(r);
        putback = new GrowableRail[Byte]();
    }

    public global def read() throws IOException {
       if (putback().length() > 0) {
           val p = putback().apply(putback().length()-1);
           putback().removeLast();
           return p;
       }
       return super.read();
    }

    public global def putback(p: Byte) {
       putback().add(p);
    }
}
