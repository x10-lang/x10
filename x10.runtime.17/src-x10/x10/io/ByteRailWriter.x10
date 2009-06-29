package x10.io;

import x10.util.RailBuilder;

public /*value*/ class ByteRailWriter extends ByteWriter[Rail[Byte]] {
    public def this() { super(new RailBuilder[Byte]()); }
    public def toRail() = result();
}

