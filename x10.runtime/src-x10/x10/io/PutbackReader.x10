/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

import x10.util.GrowableRail;

public value PutbackReader extends FilterReader {
    val putback: GrowableRail[Byte];

    def putback() = putback as GrowableRail[Byte]{self.at(here)};
    public def this(r: Reader) {
        super(r);
        putback = new GrowableRail[Byte]();
    }

    public def read() throws IOException {
       if (putback().length() > 0) {
           val p = putback().apply(putback().length()-1);
           putback().removeLast();
           return p;
       }
       return super.read();
    }

    public def putback(p: Byte) {
       putback().add(p);
    }
}
