package x10.io;

public class ByteValRailWriter extends ByteWriter[ValRail[T]] {
    public def this() { super(new ValRailBuilder()); }
    public def toValRail() = result();
}


