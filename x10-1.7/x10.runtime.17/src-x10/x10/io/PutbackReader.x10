package x10.io;

import x10.util.GrowableRail;

public value PutbackReader extends FilterReader {
    val putback: GrowableRail[Byte];

    public def this(r: Reader) {
        super(r);
        putback = new GrowableRail[Byte]();
    }

    public def read() throws IOException {
       if (putback.length() > 0) {
           val p = putback.apply(putback.length()-1);
           putback.removeLast();
           return p;
       }
       return super.read();
    }

    public def putback(p: Byte) {
       putback.add(p);
    }
}
