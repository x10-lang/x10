package x10.io;

import x10.util.ValRailBuilder;

public value ByteValRailWriter extends ByteWriter[ValRail[Byte]] {
    public def this() { super(new ValRailBuilder[Byte]()); }
    public def toValRail() = result();
}


