package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Inline;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;

public abstract class RegularFrame extends Frame {
    public val ff:FinishFrame;

    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up);
        this.ff = ff;
    }

    public def this(Int, o:RegularFrame) {
        super(o.up.realloc());
        throwable = null;
        this.ff = o.ff.redirect;
    }

    public abstract def remap():RegularFrame;

    @Inline public final def push(worker:Worker) {
        worker.deque.push(this);
    }

    @NoInline @NoReturn public final def continueLater(worker:Worker):void {
        worker.migrate();
        Runtime.wsBlock(remap());
        throw Abort.ABORT;
    }

    @NoInline @NoReturn public final def continueNow(worker:Worker):void {
        worker.migrate();
        worker.fifo.push(remap());
        throw Abort.ABORT;
    }
}
