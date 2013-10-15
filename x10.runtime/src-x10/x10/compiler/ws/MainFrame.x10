package x10.compiler.ws;

import x10.compiler.Header;
import x10.compiler.Ifdef;

abstract public class MainFrame extends RegularFrame {
    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up, ff);
    }

    @Ifdef("__CPP__")
    public def this(Int, o:MainFrame) {
        super(-1n, o);
    }

    public abstract def fast(worker:Worker):void;
}
