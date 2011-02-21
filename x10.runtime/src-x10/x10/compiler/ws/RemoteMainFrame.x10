package x10.compiler.ws;

import x10.compiler.Header;

abstract public class RemoteMainFrame extends RegularFrame {
    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up, ff);
    }

    public def this(Int, o:RemoteMainFrame) {
        super(-1, o);
    }

    public abstract def fast(worker:Worker):void;
}
