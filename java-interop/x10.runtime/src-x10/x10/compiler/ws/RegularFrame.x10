package x10.compiler.ws;

import x10.compiler.Abort;
import x10.compiler.Header;
import x10.compiler.Ifdef;
import x10.compiler.Ifndef;
import x10.compiler.Inline;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;

public abstract class RegularFrame extends Frame {
    public val ff:FinishFrame;

    @Header public def this(up:Frame, ff:FinishFrame) {
        super(up);
        this.ff = ff;
    }

    @Ifdef("__CPP__")
    public def this(Int, o:RegularFrame) {
        super(o.up.realloc());
        this.ff = o.ff.redirect;
    }

    @Ifdef("__CPP__")
    public abstract def remap():RegularFrame;

    @Inline public final def push(worker:Worker) {
        worker.deque.push(this);
    }

    @NoInline @NoReturn public final def continueLater(worker:Worker):void {
        worker.migrate();
        var k:RegularFrame = this;
        @Ifdef("__CPP__") {
            k = k.remap();
        }
        Runtime.wsBlock(k);
        throw Abort.ABORT;
    }

    @NoInline @NoReturn public final def continueNow(worker:Worker):void {
        worker.migrate();
        var k:RegularFrame = this;
        @Ifdef("__CPP__") {
            k = k.remap();
        }
        worker.fifo.push(k);
        throw Abort.ABORT;
    }
}
