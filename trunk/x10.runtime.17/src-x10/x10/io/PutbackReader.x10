package x10.io;

public value PutbackReader extends FilterReader {
    val putback: GrowableRail[Byte];

    def this() {
        putback = new GrowableRail[Byte]();
    }
        
    def read() {
       if (putback.length() > 0) {
           val p = putback.apply(putback.length()-1);
           putback.removeLast();
           return p;
       }
       return super.read()   
    }
    
    def putback(p: Byte) {
       putback.add(p);
    }
}
