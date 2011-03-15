package x10.compiler.ws;

import x10.compiler.Header;
import x10.compiler.Inline;

public abstract class RegularFrame extends Frame {
    public val ff:FinishFrame;

    // constructor
    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up);
        this.ff = ff;
    }

    // copy constructor
    public def this(Int, o:RegularFrame) {
        super(o.up.realloc());
        this.ff = o.ff.redirect;
    }

    // copy methods
    public abstract def remap():RegularFrame;

    @Inline public final def push(worker:Worker) {
        worker.deque.push(this);
    }

    @Inline public final def redo(worker:Worker):void {
        worker.migrate();
        worker.fifo.push(remap());
        throw Stolen.STOLEN;
    }
}
