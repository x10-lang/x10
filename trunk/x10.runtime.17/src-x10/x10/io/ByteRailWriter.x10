package x10.io;

public class ByteRailWriter extends ByteWriter[Rail[T]] {
    public def this() { super(new RailBuilder()); }
    public def toRail() = result();
}

