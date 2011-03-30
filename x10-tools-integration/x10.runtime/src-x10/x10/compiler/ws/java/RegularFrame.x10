package x10.compiler.ws.java;

import x10.compiler.Abort;
import x10.compiler.Inline;

public abstract class RegularFrame extends Frame {
    public val ff:FinishFrame;

    public def this(up:Frame, ff:FinishFrame) {
        super(up);
        this.ff = ff;
    }

    @Inline public final def push(worker:Worker) {
        worker.deque.push(this);
    }

    @Inline public final def redo(worker:Worker):void {
        worker.migrate();
        worker.fifo.push(this);
        throw Abort.ABORT;
    }
}
