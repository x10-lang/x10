package x10.io;

import x10.util.ValRailBuilder;

public class ByteValRailWriter extends ByteWriter[ValRail[Byte]] {
    public def this() { super(new ValRailBuilder[Byte]()); }
    public global def toValRail() = result();
}


