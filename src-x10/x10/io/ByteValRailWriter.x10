package x10.io;

public value ByteValRailWriter extends ByteWriter[ValRail[T]] {
    public def this() { super(new ValRailBuilder()); }
    public def toValRail() = result();
}


